/*
 *   Copyright (c) 2021-2026. caoccao.com Sam Cao
 *   All rights reserved.

 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at

 *   http://www.apache.org/licenses/LICENSE-2.0

 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

"use strict";

// Self-contained probe for the RunTillNoMoreTasksDeep patch.
//
// WebAssembly.instantiate() compiles on a V8 background thread and posts its
// completion callback back to the isolate as a foreground task. The nested
// async chain below (IIFE -> loadWasm -> WebAssembly.instantiate) leaves
// globalThis.wasmResult === null until that callback runs and every layer of
// microtask continuation has drained.
//
// javet.v8.awaitDeep() is called ONCE, inline, right after kicking off the
// async work. If the Deep patch works, wasmResult is already 5 when logged.
// If the race is still present, it logs null.
//
// Do NOT call awaitDeep() twice: the original bug is "null on first call,
// value on second", so a second await would mask exactly what we test.

globalThis.wasmResult = null;

// Nested async: the outer IIFE defines an inner async function, then awaits it.
// This deepens the Promise chain so the background-thread completion callback
// has more microtask continuations to drain before wasmResult is written,
// stressing the "two consecutive empty rounds" exit condition harder than a
// single flat await.
(async () => {
  async function loadWasm() {
    // Minimal module: export function add(a, b) { return a + b; }
    const bytes = Uint8Array.from([
      0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00, // magic + version
      0x01, 0x07, 0x01, 0x60, 0x02, 0x7f, 0x7f, 0x01, 0x7f, // type: (i32,i32)->i32
      0x03, 0x02, 0x01, 0x00, // function: 1 func of type 0
      0x07, 0x07, 0x01, 0x03, 0x61, 0x64, 0x64, 0x00, 0x00, // export "add" -> func 0
      0x0a, 0x09, 0x01, 0x07, 0x00, 0x20, 0x00, 0x20, 0x01, 0x6a, 0x0b, // code: local.get 0/1; i32.add
    ]);
    const { instance } = await WebAssembly.instantiate(bytes);
    return instance.exports.add(2, 3); // expected: 5
  }

  globalThis.wasmResult = await loadWasm();
})();

// Single, inline deep await. No polling, no second call.
javet.v8.awaitDeep();

// Deep patch working => 5. Race still present => null.
console.log(globalThis.wasmResult);
