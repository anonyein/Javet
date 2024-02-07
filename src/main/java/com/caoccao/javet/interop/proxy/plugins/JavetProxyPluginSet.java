/*
 * Copyright (c) 2024. caoccao.com Sam Cao
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caoccao.javet.interop.proxy.plugins;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.converters.JavetConverterConfig;
import com.caoccao.javet.interop.proxy.JavetProxySymbolIterableConverter;
import com.caoccao.javet.utils.ArrayUtils;
import com.caoccao.javet.utils.SimpleList;
import com.caoccao.javet.utils.V8ValueUtils;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The type Javet proxy plugin set.
 *
 * @since 3.0.4
 */
@SuppressWarnings("unchecked")
public class JavetProxyPluginSet extends BaseJavetProxyPluginSingle {
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = Set.class.getName();
    protected static final String ADD = "add";
    protected static final String CLEAR = "clear";
    protected static final String DELETE = "delete";
    protected static final String ENTRIES = "entries";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET =
            "Target object must be an instance of Set.";
    protected static final String FOR_EACH = "forEach";
    protected static final String HAS = "has";
    protected static final String KEYS = "keys";
    protected static final String OBJECT_SET = "[object Set]";
    protected static final String SIZE = "size";
    protected static final String VALUES = "values";
    private static final JavetProxyPluginSet instance = new JavetProxyPluginSet();

    protected JavetProxyPluginSet() {
        super();
        proxyGetByStringMap.put(ADD, this::add);
        proxyGetByStringMap.put(CLEAR, this::clear);
        proxyGetByStringMap.put(DELETE, this::delete);
        proxyGetByStringMap.put(ENTRIES, this::entries);
        proxyGetByStringMap.put(FOR_EACH, this::forEach);
        proxyGetByStringMap.put(HAS, this::has);
        proxyGetByStringMap.put(KEYS, this::values);
        proxyGetByStringMap.put(SIZE, this::size);
        proxyGetByStringMap.put(TO_JSON, this::toJSON);
        proxyGetByStringMap.put(TO_STRING, this::toString);
        proxyGetByStringMap.put(VALUES, this::values);
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 3.0.4
     */
    public static JavetProxyPluginSet getInstance() {
        return instance;
    }

    /**
     * Polyfill Set.prototype.add().
     * The add() method of Set instances inserts a new element with a specified value in to this set,
     * if there isn't an element with the same value already in this set
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value add(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                ADD, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        set.add(v8Runtime.toObject(v8Values[0]));
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Set.prototype.clear().
     * The clear() method of Set instances removes all elements from this set.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value clear(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                CLEAR, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    set.clear();
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Set.prototype.delete().
     * The delete() method of Set instances removes a specified value from this set, if it is in the set.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value delete(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                DELETE, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean removed = false;
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        removed = set.remove(v8Runtime.toObject(v8Values[0]));
                    }
                    return v8Runtime.createV8ValueBoolean(removed);
                }));
    }

    /**
     * Polyfill Set.prototype.entries().
     * The entries() method of Set instances returns a new set iterator object that contains an array of
     * [value, value] for each element in this set, in insertion order.
     * For Set objects there is no key like in Map objects. However, to keep the API similar to the Map object,
     * each entry has the same value for its key and value here, so that an array [value, value] is returned.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value entries(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) targetObject;
        final List<List<?>> entries = set.stream().map(o -> SimpleList.of(o, o)).collect(Collectors.toList());
        return new JavetProxySymbolIterableConverter<>(v8Runtime, entries).getV8ValueFunction();
    }

    /**
     * Polyfill Set.prototype.forEach()
     * The forEach() method of Set instances executes a provided function once for each value in this set,
     * in insertion order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value forEach(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FOR_EACH, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        for (Object key : set) {
                            try (V8Value v8ValueResult = v8ValueFunction.call(v8ValueObject, key, key, thisObject)) {
                            }
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Set<String> getOverrideMethods(JavetConverterConfig<?> config) {
        return config.getProxySetOverrideMethods();
    }

    @Override
    public Object[] getOwnKeys(Object targetObject) {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        return ((Set<?>) targetObject).toArray();
    }

    /**
     * Polyfill Set.prototype.has().
     * The has() method of Set instances returns a boolean indicating whether an element
     * with the specified value exists in this set or not.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value has(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) targetObject;
        return v8Runtime.createV8ValueFunction(new JavetCallbackContext(
                HAS, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean result = false;
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        result = set.contains(v8Runtime.toObject(v8Values[0]));
                    }
                    return v8Runtime.createV8ValueBoolean(result);
                }));
    }

    @Override
    public boolean hasByObject(Object targetObject, Object propertyKey) {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        return ((Set<?>) targetObject).contains(propertyKey);
    }

    @Override
    public boolean isHasSupported() {
        return true;
    }

    @Override
    public boolean isProxyable(Class<?> targetClass) {
        return targetClass != null && Set.class.isAssignableFrom(targetClass);
    }

    @Override
    public boolean isUniqueKeySupported() {
        return true;
    }

    @Override
    public void populateUniqueKeys(Set<String> uniqueKeySet, Object targetObject) {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        ((Set<Object>) targetObject).stream()
                .map(Object::toString)
                .filter(Objects::nonNull)
                .forEach(Objects.requireNonNull(uniqueKeySet)::add);
    }

    /**
     * Polyfill Set.prototype.size
     * The size accessor property of Set instances returns the number of (unique) elements in this set.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value size(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        final Set<?> set = (Set<?>) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueInteger(set.size());
    }

    /**
     * Polyfill Set.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value toJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueObject()));
    }

    /**
     * Polyfill Object.prototype.toString()
     * The toString() method of Set instances always returns [object Set].
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value toString(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_STRING, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueString(OBJECT_SET)));
    }

    /**
     * Polyfill Set.prototype.values().
     * The values() method of Set instances returns a new set iterator object that contains the values
     * for each element in this set in insertion order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value values(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Set : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SET;
        return new JavetProxySymbolIterableConverter<>(v8Runtime, targetObject).getV8ValueFunction();
    }
}
