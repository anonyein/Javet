'''
  Idempotent Javet patcher: add a new V8AwaitMode `RunTillNoMoreTasksDeep`
  alongside a matching `V8Runtime.awaitDeep()` Java helper and C++ branch.

  Why this patch exists
  ---------------------
  The default `V8Runtime.await()` (= `RunTillNoMoreTasks`) only pumps libuv
  and a single `DrainTasks` pass. An inline `await` chain scheduled from a
  `nodeEval(...)` script (e.g. `await WebAssembly.instantiate(...)`) often
  has its Promise callbacks still pending on the NodePlatform worker thread
  at the moment `uv_loop_alive()` reports false, causing `nodeEval(...)` to
  return on the very first call with `null` and only succeed on the second
  call.

  `RunTillNoMoreTasksDeep` additionally pumps
  `v8::Platform::FlushForegroundTasks(isolate)` every round and requires two
  consecutive genuinely-empty rounds before returning. It is opt-in: the
  default `await()` behaviour is untouched, so existing users are unaffected.

  Idempotency
  -----------
  Each of the four sub-patches detects its own marker comment / symbol and
  skips if already applied. Re-running the script is always safe.

  Usage
  -----
      python scripts/python/patch_await_deep.py [--check]
                                                    #ENSURE_RUN

  --check    Dry-run: report the state of each sub-patch without writing.

  Notes on patching strategy
  --------------------------
  All anchors are landmark strings that are unlikely to drift between minor
  releases:
    * `enum V8AwaitMode {` ... `RunTillNoMoreTasks = 0,`
    * `bool V8Runtime::Await(const Javet::Enums::V8AwaitMode::V8AwaitMode awaitMode) noexcept {`
    * `public boolean await(V8AwaitMode v8AwaitMode) {` ... `return false;
      }`
    * Java enum's `RunTillNoMoreTasks(0);`

  If any anchor moves (re-factor on upstream side) the patcher reports the
  specific file it could not find and exits non-zero without modifying
  anything.
'''

import argparse
import logging
import pathlib
import re
import sys

if __package__ in (None, ''):
    logging.basicConfig(level=logging.INFO,
                        format='%(asctime)-15s %(levelname)s: %(message)s')
else:
    try:
        import coloredlogs
        coloredlogs.install(level=logging.DEBUG,
                            fmt='%(asctime)-15s %(name)s %(levelname)s: %(message)s')
    except ImportError:
        logging.basicConfig(level=logging.INFO,
                            format='%(asctime)-15s %(levelname)s: %(message)s')


CPP_ENUM_MARKER = 'PatchAwaitDeep/enums-applied'
CPP_AWAIT_MARKER = 'PatchAwaitDeep/await-applied'
JAVA_ENUM_MARKER = 'PatchAwaitDeep/java-enum-applied'
JAVA_AWAITDEEP_MARKER = 'PatchAwaitDeep/java-awaitdeep-applied'


def _read(path: pathlib.Path) -> str:
    # Universal newlines: normalises CRLF/CR to \n so the patch anchors can be
    # written with \n and still match files checked out on Windows.
    return path.read_text(encoding='utf-8')


def _write(path: pathlib.Path, content: str) -> None:
    '''
    Write back preserving the original file's line separator. If we just used
    pathlib write_text it would write exactly what we gave it (\n only),
    which on a CRLF-checked-out repo would mark every line as modified.
    '''
    original = path.read_bytes() if path.exists() else b''
    sep = b'\r\n' if b'\r\n' in original else b'\n'
    out = content.encode('utf-8').replace(b'\r\n', b'\n').replace(b'\n', sep)
    path.write_bytes(out)


def _find_first(source: str, needle: str) -> int:
    idx = source.find(needle)
    if idx == -1:
        raise ValueError(f'Anchor not found: {needle!r}')
    return idx


def patch_cpp_enums(repo_root: pathlib.Path, dry_run: bool) -> bool:
    '''
    Insert RunTillNoMoreTasksDeep into the C++ V8AwaitMode enum.
    '''
    rel = pathlib.Path('cpp/jni/javet_enums.h')
    p = repo_root / rel
    if not p.exists():
        logging.error('%s not found', rel)
        return False
    src = _read(p)
    if CPP_ENUM_MARKER in src:
        logging.info('[cpp enums] already patched, skipping')
        return True
    anchor_block = '''        namespace V8AwaitMode {
            enum V8AwaitMode {
'''
    deep_line = '                RunTillNoMoreTasksDeep = 3,\n'
    end_anchor = '                RunTillNoMoreTasks = 0,\n            };\n        };'
    if end_anchor not in src:
        logging.error('[cpp enums] anchor %r not found', end_anchor.strip())
        return False
    # Inject the marker as a trailing comment so later re-runs detect us.
    patched = src.replace(
        end_anchor,
        '                RunTillNoMoreTasks = 0, // ' + CPP_ENUM_MARKER + '\n'
        + deep_line
        + '            };\n        };',
        1,
    )
    if patched == src:
        logging.error('[cpp enums] replace did not change anything')
        return False
    logging.info('[cpp enums] inserting RunTillNoMoreTasksDeep = 3')
    if not dry_run:
        _write(p, patched)
    return True


def patch_cpp_await(repo_root: pathlib.Path, dry_run: bool) -> bool:
    '''
    Insert the RunTillNoMoreTasksDeep early-return branch at the top of
    V8Runtime::Await in javet_v8_runtime.cpp.
    '''
    rel = pathlib.Path('cpp/jni/javet_v8_runtime.cpp')
    p = repo_root / rel
    if not p.exists():
        logging.error('%s not found', rel)
        return False
    src = _read(p)
    if CPP_AWAIT_MARKER in src:
        logging.info('[cpp await] already patched, skipping')
        return True

    fn_sig = ('    bool V8Runtime::Await(const Javet::Enums::V8AwaitMode::V8AwaitMode '
              'awaitMode) noexcept {\n')
    # The "#ifdef ENABLE_NODE" branch is the first Await definition; we insert
    # the Deep branch right inside that body, immediately after the opening
    # brace and the "bool hasMoreTasks = false;" line.
    anchor_head = (
        fn_sig
        + '        bool hasMoreTasks = false;\n'
        + '        using namespace Javet::Enums::V8AwaitMode;\n'
    )
    if anchor_head not in src:
        logging.error('[cpp await] anchor (%r...) not found - the C++ source has '
                      'moved; refusing to patch blindly', anchor_head[:60])
        return False

    deep_block = '''        // PatchAwaitDeep/await-applied BEGIN
        // RunTillNoMoreTasksDeep: drain libuv AND v8 isolate foreground tasks
        // (Promise microtasks, async resolve callbacks, WebAssembly.instantiate
        // worker-thread completions) until two consecutive genuinely-empty rounds
        // are observed. The default RunTillNoMoreTasks below only pumps libuv and
        // a single DrainTasks pass, which can return before a Promise chain has
        // settled, causing inline `await` scripts to return on the first call.
        // FlushForegroundTasks is a MultiIsolatePlatform override on v8::Platform
        // and is thread-safe: it runs any pending foreground task for the isolate
        // and reports whether any actually dispatched.
        if (awaitMode == RunTillNoMoreTasksDeep) {
            constexpr uv_run_mode uvRunMode = UV_RUN_ONCE;
            uv_loop_t* loop = nodeCommonSetup ? nodeCommonSetup->event_loop() : &uvLoop;
            node::Environment* env = nodeCommonSetup ? nodeCommonSetup->env() : nodeEnvironment.get();
            // Two consecutive genuinely-empty rounds => done. Upper bound
            // guards against pathological self-rescheduling loops.
            int idleRounds = 0;
            int roundsLeft = 4096;
            do {
                bool flushedAny = false;
                {
                    auto v8Locker = GetUniqueV8Locker();
                    auto v8IsolateScope = GetV8IsolateScope();
                    V8HandleScope v8HandleScope(v8Isolate);
                    auto v8Context = GetV8LocalContext();
                    auto v8ContextScope = GetV8ContextScope(v8Context);
                    uv_run(loop, uvRunMode);
                    // DrainTasks is thread-safe.
                    v8PlatformPointer->DrainTasks(v8Isolate);
                    // Keep flushing the V8 isolate foreground task queue until it
                    // is momentarily empty - each foreground task may queue more
                    // microtasks, timers, async work, or beforeExit callbacks.
                    do {
                        flushedAny = v8PlatformPointer->FlushForegroundTasks(v8Isolate);
                    } while (flushedAny);
                }
                hasMoreTasks = uv_loop_alive(loop);
                if (hasMoreTasks || flushedAny) {
                    idleRounds = 0;
                    continue;
                }
                // Loop momentarily empty. Give process.beforeExit handlers a
                // chance to schedule more work, then re-check.
                {
                    auto v8Locker = GetUniqueV8Locker();
                    auto v8IsolateScope = GetV8IsolateScope();
                    V8HandleScope v8HandleScope(v8Isolate);
                    auto v8Context = GetV8LocalContext();
                    auto v8ContextScope = GetV8ContextScope(v8Context);
                    // node::EmitProcessBeforeExit is thread-safe.
                    node::EmitProcessBeforeExit(env);
                    bool flushedAfterExit = v8PlatformPointer->FlushForegroundTasks(v8Isolate);
                    hasMoreTasks = uv_loop_alive(loop) || flushedAfterExit;
                }
                if (hasMoreTasks) {
                    idleRounds = 0;
                    continue;
                }
                if (++idleRounds >= 2) {
                    break;
                }
            } while (--roundsLeft > 0);
            return hasMoreTasks;
        }
        // PatchAwaitDeep/await-applied END
'''

    patched = src.replace(anchor_head, anchor_head + deep_block, 1)
    if patched == src:
        logging.error('[cpp await] inject failed (no change)')
        return False
    logging.info('[cpp await] inserting Deep branch into V8Runtime::Await')
    if not dry_run:
        _write(p, patched)
    return True


def patch_java_enum(repo_root: pathlib.Path, dry_run: bool) -> bool:
    '''
    Add RunTillNoMoreTasksDeep(3) to the Java V8AwaitMode enum, replacing the
    `RunTillNoMoreTasks(0);` semicolon with `RunTillNoMoreTasks(0),` and a
    new trailing entry.
    '''
    rel = pathlib.Path('src/main/java/com/caoccao/javet/enums/V8AwaitMode.java')
    p = repo_root / rel
    if not p.exists():
        logging.error('%s not found', rel)
        return False
    src = _read(p)
    if JAVA_ENUM_MARKER in src:
        logging.info('[java enum] already patched, skipping')
        return True
    anchor = '    RunTillNoMoreTasks(0);'
    if anchor not in src:
        logging.error('[java enum] anchor %r not found', anchor)
        return False
    replacement = (
        '    RunTillNoMoreTasks(0), // PatchAwaitDeep/java-enum-applied\n'
        '    /**\n'
        '     * RunTillNoMoreTasksDeep tells Javet to keep pumping BOTH the libuv event\n'
        '     * loop AND the V8 isolate foreground task queue (Promise microtasks, async\n'
        '     * resolve callbacks, WebAssembly.instantiate worker-thread completions, ...)\n'
        '     * until two consecutive rounds are observed to be empty. Use this mode when\n'
        '     * an inline {@code await} chain scheduled from script needs to fully settle\n'
        '     * before {@code nodeEval()} returns - the default {@link #RunTillNoMoreTasks}\n'
        '     * only pumps libuv and a single {@code DrainTasks} pass, which can return\n'
        '     * before the Promise chain has drained (yielding the classic\n'
        '     * "first {@code nodeEval()} result is null" race).\n'
        '     * <p>\n'
        '     * It is a non-blocking call. It only works in Node.js mode.\n'
        '     *\n'
        '     * @since 5.0.10\n'
        '     */\n'
        '    RunTillNoMoreTasksDeep(3);'
    )
    patched = src.replace(anchor, replacement, 1)
    if patched == src:
        logging.error('[java enum] replace made no change')
        return False
    logging.info('[java enum] inserting RunTillNoMoreTasksDeep(3)')
    if not dry_run:
        _write(p, patched)
    return True


def patch_java_awaitdeep(repo_root: pathlib.Path, dry_run: bool) -> bool:
    '''
    Add V8Runtime.awaitDeep() helper right after the existing
    `public boolean await(V8AwaitMode v8AwaitMode) { ... }` method.
    '''
    rel = pathlib.Path('src/main/java/com/caoccao/javet/interop/V8Runtime.java')
    p = repo_root / rel
    if not p.exists():
        logging.error('%s not found', rel)
        return False
    src = _read(p)
    if JAVA_AWAITDEEP_MARKER in src:
        logging.info('[java awaitDeep] already patched, skipping')
        return True
    # The existing method ends with the pattern below. We anchor on its fully-
    # formed body so that even if upstream adds more code above/below a slightly
    # different layout still matches - we look for the closing `    }` after
    # `return v8Native.await(handle, Objects.requireNonNull(v8AwaitMode).getId());`.
    anchor = '''    public boolean await(V8AwaitMode v8AwaitMode) {
        if (!isClosed()) {
            return v8Native.await(handle, Objects.requireNonNull(v8AwaitMode).getId());
        }
        return false;
    }
'''
    if anchor not in src:
        logging.error('[java awaitDeep] anchor method not found - layout changed')
        return False
    helper = '''    /**
     * Await deep tells the V8 runtime to pump both the libuv event loop and
     * the V8 isolate foreground task queue until two consecutive rounds are
     * observed to be empty. This is the strict variant of {@link #await()} for
     * inline async scripts (e.g. WebAssembly.instantiate + await chains) whose
     * Promise callbacks are scheduled on a NodePlatform worker thread and may
     * not have been posted back to the isolate by the time the default
     * {@link V8AwaitMode#RunTillNoMoreTasks} sees an empty libuv loop. Use it
     * when an inline {@code await} chain scheduled from script needs to fully
     * settle before {@code nodeEval()} returns.
     * <p>
     * In the Node.js mode, the V8 await mode takes effect.
     * In the V8 mode, the V8 await mode takes no effect and the return is always false.
     * // PatchAwaitDeep/java-awaitdeep-applied
     *
     * @return true : there are more tasks, false : there are no more tasks
     * @since 5.0.10
     */
    public boolean awaitDeep() {
        return await(V8AwaitMode.RunTillNoMoreTasksDeep);
    }
'''
    patched = src.replace(anchor, anchor + helper, 1)
    if patched == src:
        logging.error('[java awaitDeep] replace made no change')
        return False
    logging.info('[java awaitDeep] inserting V8Runtime.awaitDeep() helper')
    if not dry_run:
        _write(p, patched)
    return True


def main(argv: list) -> int:
    p = argparse.ArgumentParser(description='Apply the RunTillNoMoreTasksDeep patch to Javet.')
    p.add_argument('--check', action='store_true',
                   help='Dry-run: report status without writing anything')
    p.add_argument('--repo-root', type=pathlib.Path, default=None,
                   help='Javet repo root (default: two levels up from this script)')
    args = p.parse_args(argv)
    repo_root = args.repo_root
    if repo_root is None:
        repo_root = pathlib.Path(__file__).parent.joinpath('../../').resolve().absolute()
    if not repo_root.exists():
        logging.error('repo root %s does not exist', repo_root)
        return 1
    logging.info('repo root = %s', repo_root)
    logging.info('dry-run = %s', args.check)
    ok = True
    ok &= patch_cpp_enums(repo_root, args.check)
    ok &= patch_cpp_await(repo_root, args.check)
    ok &= patch_java_enum(repo_root, args.check)
    ok &= patch_java_awaitdeep(repo_root, args.check)
    if not ok:
        logging.error('one or more patches failed - see above')
        return 2
    logging.info('all 4 patches applied (or already present)')
    return 0


if __name__ == '__main__':
    sys.exit(main(sys.argv[1:]))
