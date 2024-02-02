/*
 * Copyright (c) 2024-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.interop.proxy.polyfill;

import com.caoccao.javet.enums.V8ValueErrorType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.exceptions.V8ErrorTemplate;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.V8Scope;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.interop.proxy.JavetProxySymbolIterableConverter;
import com.caoccao.javet.utils.*;
import com.caoccao.javet.values.V8Value;
import com.caoccao.javet.values.reference.IV8ValueArray;
import com.caoccao.javet.values.reference.V8ValueArray;
import com.caoccao.javet.values.reference.V8ValueFunction;
import com.caoccao.javet.values.reference.V8ValueObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Javet proxy polyfill list.
 *
 * @since 3.0.4
 */
@SuppressWarnings("unchecked")
public final class JavetProxyPolyfillList {
    private static final String AT = "at";
    private static final String CONCAT = "concat";
    private static final String COPY_WITHIN = "copyWithin";
    private static final String ENTRIES = "entries";
    private static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST =
            "Target object must be an instance of List.";
    private static final String EVERY = "every";
    private static final String FILL = "fill";
    private static final String FILTER = "filter";
    private static final String FIND = "find";
    private static final String FIND_INDEX = "findIndex";
    private static final String FIND_LAST = "findLast";
    private static final String FIND_LAST_INDEX = "findLastIndex";
    private static final String FOR_EACH = "forEach";
    private static final String INCLUDES = "includes";
    private static final String INDEX_OF = "indexOf";
    private static final String JOIN = "join";
    private static final String KEYS = "keys";
    private static final String LAST_INDEX_OF = "lastIndexOf";
    private static final String LENGTH = "length";
    private static final String MAP = "map";
    private static final String POP = "pop";
    private static final String PUSH = "push";
    private static final String REDUCE = "reduce";
    private static final String REDUCE_RIGHT = "reduceRight";
    private static final String REVERSE = "reverse";
    private static final String SHIFT = "shift";
    private static final String SIZE = "size";
    private static final String SLICE = "slice";
    private static final String SOME = "some";
    private static final String SORT = "sort";
    private static final String SPLICE = "splice";
    private static final String TO_JSON = "toJSON";
    private static final String TO_REVERSED = "toReversed";
    private static final String TO_SORTED = "toSorted";
    private static final String TO_SPLICED = "toSpliced";
    private static final String UNSHIFT = "unshift";
    private static final String VALUES = "values";
    private static final String WITH = "with";
    private static final Map<String, IJavetProxyPolyfillFunction<?, ?>> functionMap;

    static {
        functionMap = new HashMap<>();
        functionMap.put(AT, JavetProxyPolyfillList::at);
        functionMap.put(CONCAT, JavetProxyPolyfillList::concat);
        functionMap.put(COPY_WITHIN, JavetProxyPolyfillList::copyWithin);
        functionMap.put(ENTRIES, JavetProxyPolyfillList::entries);
        functionMap.put(EVERY, JavetProxyPolyfillList::every);
        functionMap.put(FILL, JavetProxyPolyfillList::fill);
        functionMap.put(FILTER, JavetProxyPolyfillList::filter);
        functionMap.put(FIND, JavetProxyPolyfillList::find);
        functionMap.put(FIND_INDEX, JavetProxyPolyfillList::findIndex);
        functionMap.put(FIND_LAST, JavetProxyPolyfillList::findLast);
        functionMap.put(FIND_LAST_INDEX, JavetProxyPolyfillList::findLastIndex);
        functionMap.put(FOR_EACH, JavetProxyPolyfillList::forEach);
        functionMap.put(INCLUDES, JavetProxyPolyfillList::includes);
        functionMap.put(INDEX_OF, JavetProxyPolyfillList::indexOf);
        functionMap.put(JOIN, JavetProxyPolyfillList::join);
        functionMap.put(KEYS, JavetProxyPolyfillList::keys);
        functionMap.put(LAST_INDEX_OF, JavetProxyPolyfillList::lastIndexOf);
        functionMap.put(LENGTH, JavetProxyPolyfillList::length);
        functionMap.put(MAP, JavetProxyPolyfillList::map);
        functionMap.put(POP, JavetProxyPolyfillList::pop);
        functionMap.put(PUSH, JavetProxyPolyfillList::push);
        functionMap.put(REDUCE, JavetProxyPolyfillList::reduce);
        functionMap.put(REDUCE_RIGHT, JavetProxyPolyfillList::reduceRight);
        functionMap.put(REVERSE, JavetProxyPolyfillList::reverse);
        functionMap.put(SHIFT, JavetProxyPolyfillList::shift);
        functionMap.put(SIZE, JavetProxyPolyfillList::length);
        functionMap.put(SLICE, JavetProxyPolyfillList::slice);
        functionMap.put(SOME, JavetProxyPolyfillList::some);
        functionMap.put(SORT, JavetProxyPolyfillList::sort);
        functionMap.put(SPLICE, JavetProxyPolyfillList::splice);
        functionMap.put(TO_JSON, JavetProxyPolyfillList::toJSON);
        functionMap.put(TO_REVERSED, JavetProxyPolyfillList::toReversed);
        functionMap.put(TO_SORTED, JavetProxyPolyfillList::toSorted);
        functionMap.put(TO_SPLICED, JavetProxyPolyfillList::toSpliced);
        functionMap.put(UNSHIFT, JavetProxyPolyfillList::unshift);
        functionMap.put(VALUES, JavetProxyPolyfillList::values);
        functionMap.put(WITH, JavetProxyPolyfillList::with);
    }

    private JavetProxyPolyfillList() {
    }

    /**
     * Polyfill Array.prototype.at().
     * The at() method of Array instances takes an integer value and returns the item at that index,
     * allowing for positive and negative integers. Negative integers count back from the last item in the array.
     * <p>
     * Parameters
     * index
     * Zero-based index of the array element to be returned, converted to an integer.
     * Negative index counts back from the end of the array — if index &lt; 0, index + array.length is accessed.
     * <p>
     * Return value
     * The element in the array matching the given index.
     * Always returns undefined if index &lt; -array.length or index &gt;= array.length
     * without attempting to access the corresponding property.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value at(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                AT, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    final int size = list.size();
                    int index = V8ValueUtils.asInt(v8Values, 0);
                    if (index < 0) {
                        index += size;
                    }
                    if (index >= 0 && index < size) {
                        return v8Runtime.toV8Value(list.get(index));
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Array.prototype.concat().
     * The concat() method of Array instances is used to merge two or more arrays.
     * This method does not change the existing arrays, but instead returns a new array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value concat(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                CONCAT, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    try (V8Scope v8Scope = v8Runtime.getV8Scope()) {
                        V8ValueArray v8ValueArray = v8Scope.createV8ValueArray();
                        if (!list.isEmpty()) {
                            v8ValueArray.push(list.toArray());
                        }
                        if (ArrayUtils.isNotEmpty(v8Values)) {
                            List<Object> objects = new ArrayList<>(v8Values.length);
                            for (V8Value v8Value : v8Values) {
                                if (v8Value instanceof IV8ValueArray) {
                                    IV8ValueArray iV8ValueArray = (IV8ValueArray) v8Value;
                                    V8Value[] items = new V8Value[iV8ValueArray.getLength()];
                                    if (ArrayUtils.isNotEmpty(items)) {
                                        iV8ValueArray.batchGet(items, 0, items.length);
                                        Collections.addAll(objects, items);
                                    }
                                } else {
                                    Object object = v8Runtime.toObject(v8Value);
                                    if (object instanceof Collection) {
                                        Collections.addAll(objects, (Collection<?>) object);
                                    } else {
                                        objects.add(v8Value);
                                    }
                                }
                            }
                            if (!objects.isEmpty()) {
                                v8ValueArray.push(objects.toArray());
                            }
                        }
                        v8Scope.setEscapable();
                        return v8ValueArray;
                    }
                }));
    }

    /**
     * Polyfill Array.prototype.copyWithin().
     * The copyWithin() method of Array instances shallow copies part of this array to another location
     * in the same array and returns this array without modifying its length.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value copyWithin(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<Object> list = (List<Object>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                COPY_WITHIN, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    Object[] objects = list.toArray();
                    if (!list.isEmpty() && ArrayUtils.isNotEmpty(v8Values)) {
                        final int length = list.size();
                        int targetIndex = V8ValueUtils.asInt(v8Values, 0);
                        if (targetIndex < 0) {
                            targetIndex += length;
                            if (targetIndex < 0) {
                                targetIndex = 0;
                            }
                        }
                        int startIndex = V8ValueUtils.asInt(v8Values, 1);
                        if (startIndex < 0) {
                            startIndex += length;
                            if (startIndex < 0) {
                                startIndex = 0;
                            }
                        }
                        int endIndex = V8ValueUtils.asInt(v8Values, 2);
                        if (endIndex < 0) {
                            endIndex += length;
                            if (endIndex < 0) {
                                endIndex = 0;
                            }
                        }
                        if (endIndex > length) {
                            endIndex = length;
                        }
                        if (endIndex == 0) {
                            endIndex = length;
                        }
                        if (targetIndex < length && startIndex < length && endIndex > startIndex) {
                            if (targetIndex + endIndex - startIndex > length) {
                                endIndex = length + startIndex - targetIndex;
                            }
                            for (int i = startIndex; i < endIndex; ++i) {
                                list.set(targetIndex + i - startIndex, objects[i]);
                            }
                        }
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Array.prototype.entries().
     * The entries() method of Array instances returns a new array iterator object
     * that contains the key/value pairs for each index in the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value entries(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        final int length = list.size();
        List<List<Object>> entries = new ArrayList<>(length);
        int index = 0;
        for (Object object : list) {
            entries.add(SimpleList.of(index, object));
            ++index;
        }
        return new JavetProxySymbolIterableConverter<>(v8Runtime, entries).getV8ValueFunction();
    }

    /**
     * Polyfill Array.prototype.every().
     * The every() method of Array instances tests whether all elements in the array pass the test implemented
     * by the provided function. It returns a Boolean value.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value every(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                EVERY, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = 0;
                        for (Object object : list) {
                            try (V8Value result = v8ValueFunction.call(v8ValueObject, object, index, thisObject)) {
                                if (!result.ifTrue()) {
                                    return v8Runtime.createV8ValueBoolean(false);
                                }
                            }
                            ++index;
                        }
                        return v8Runtime.createV8ValueBoolean(true);
                    }
                    return v8Runtime.createV8ValueBoolean(false);
                }));
    }

    /**
     * Polyfill Array.prototype.fill()
     * The fill() method of Array instances changes all elements within a range of indices
     * in an array to a static value. It returns the modified array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value fill(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FILL, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    if (!list.isEmpty() && ArrayUtils.isNotEmpty(v8Values)) {
                        final int length = list.size();
                        V8Value v8Value = v8Values[0];
                        int startIndex = V8ValueUtils.asInt(v8Values, 1);
                        if (startIndex < 0) {
                            startIndex += length;
                            if (startIndex < 0) {
                                startIndex = 0;
                            }
                        }
                        int endIndex = V8ValueUtils.asInt(v8Values, 2);
                        if (endIndex < 0) {
                            endIndex += length;
                            if (endIndex < 0) {
                                endIndex = 0;
                            }
                        }
                        if (endIndex == 0) {
                            endIndex = length;
                        }
                        if (startIndex < length && endIndex > startIndex) {
                            for (int i = startIndex; i < endIndex; ++i) {
                                list.set(i, v8Runtime.toObject(v8Value));
                            }
                        }
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Array.prototype.filter().
     * The filter() method of Array instances creates a shallow copy of a portion of a given array,
     * filtered down to just the elements from the given array that pass the test implemented by the provided function.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value filter(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FILTER, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    List<Object> results = new ArrayList<>(list.size());
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = 0;
                        for (Object object : list) {
                            try (V8Value v8ValueResult = v8ValueFunction.call(v8ValueObject, object, index, thisObject)) {
                                if (v8ValueResult.ifTrue()) {
                                    results.add(object);
                                }
                            }
                            ++index;
                        }
                    }
                    return V8ValueUtils.createV8ValueArray(v8Runtime, results.toArray());
                }));
    }

    /**
     * Polyfill Array.prototype.find().
     * The find() method of Array instances returns the first element in the provided array
     * that satisfies the provided testing function. If no values satisfy the testing function, undefined is returned.
     * <p>
     * If you need the index of the found element in the array, use findIndex().
     * If you need to find the index of a value, use indexOf(). (It's similar to findIndex(),
     * but checks each element for equality with the value instead of using a testing function.)
     * If you need to find if a value exists in an array, use includes().
     * Again, it checks each element for equality with the value instead of using a testing function.
     * If you need to find if any element satisfies the provided testing function, use some().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value find(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FIND, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = 0;
                        for (Object object : list) {
                            try (V8Value result = v8ValueFunction.call(v8ValueObject, object, index, thisObject)) {
                                if (result.ifTrue()) {
                                    return v8Runtime.toV8Value(object);
                                }
                            }
                            ++index;
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Array.prototype.findIndex().
     * The findIndex() method of Array instances returns the index of the first element in an array
     * that satisfies the provided testing function. If no elements satisfy the testing function, -1 is returned.
     * <p>
     * See also the find() method,
     * which returns the first element that satisfies the testing function (rather than its index).
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value findIndex(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FIND_INDEX, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = 0;
                        for (Object object : list) {
                            try (V8Value result = v8ValueFunction.call(v8ValueObject, object, index, thisObject)) {
                                if (result.ifTrue()) {
                                    return v8Runtime.createV8ValueInteger(index);
                                }
                            }
                            ++index;
                        }
                    }
                    return v8Runtime.createV8ValueInteger(-1);
                }));
    }

    /**
     * Polyfill Array.prototype.findLast().
     * The findLast() method of Array instances iterates the array in reverse order
     * and returns the value of the first element that satisfies the provided testing function.
     * If no elements satisfy the testing function, undefined is returned.
     * <p>
     * If you need to find:
     * <p>
     * the first element that matches, use find().
     * the index of the last matching element in the array, use findLastIndex().
     * the index of a value, use indexOf(). (It's similar to findIndex(),
     * but checks each element for equality with the value instead of using a testing function.)
     * whether a value exists in an array, use includes().
     * Again, it checks each element for equality with the value instead of using a testing function.
     * if any element satisfies the provided testing function, use some().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value findLast(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FIND_LAST, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = list.size() - 1;
                        ListIterator<?> listIterator = list.listIterator(list.size());
                        while (listIterator.hasPrevious()) {
                            Object object = listIterator.previous();
                            try (V8Value result = v8ValueFunction.call(v8ValueObject, object, index, thisObject)) {
                                if (result.ifTrue()) {
                                    return v8Runtime.toV8Value(object);
                                }
                            }
                            --index;
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Array.prototype.findLastIndex().
     * The findLastIndex() method of Array instances iterates the array in reverse order
     * and returns the index of the first element that satisfies the provided testing function.
     * If no elements satisfy the testing function, -1 is returned.
     * <p>
     * See also the findLast() method,
     * which returns the value of last element that satisfies the testing function (rather than its index).
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value findLastIndex(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FIND_LAST_INDEX, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = list.size() - 1;
                        ListIterator<?> listIterator = list.listIterator(list.size());
                        while (listIterator.hasPrevious()) {
                            try (V8Value result = v8ValueFunction.call(v8ValueObject, listIterator.previous(), index, thisObject)) {
                                if (result.ifTrue()) {
                                    return v8Runtime.createV8ValueInteger(index);
                                }
                            }
                            --index;
                        }
                    }
                    return v8Runtime.createV8ValueInteger(-1);
                }));
    }

    /**
     * Polyfill Array.prototype.forEach().
     * The forEach() method of Array instances executes a provided function once for each array element.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value forEach(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                FOR_EACH, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = 0;
                        for (Object object : list) {
                            try (V8Value v8ValueResult = v8ValueFunction.call(v8ValueObject, object, index, thisObject)) {
                            }
                            ++index;
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Gets function.
     *
     * @param name the name
     * @return the function
     * @since 3.0.4
     */
    public static IJavetProxyPolyfillFunction<?, ?> getFunction(String name) {
        return functionMap.get(name);
    }

    /**
     * Polyfill Array.prototype.includes().
     * The includes() method of Array instances determines whether an array includes a certain value among its entries,
     * returning true or false as appropriate.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value includes(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                INCLUDES, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    boolean included = false;
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        Object object = v8Runtime.toObject(v8Values[0]);
                        int fromIndex = V8ValueUtils.asInt(v8Values, 1);
                        final int length = list.size();
                        if (fromIndex < 0) {
                            fromIndex += length;
                        }
                        if (fromIndex < 0) {
                            fromIndex = 0;
                        }
                        if (fromIndex < length) {
                            included = ListUtils.includes((List<Object>) list, object, fromIndex);
                        }
                    }
                    return v8Runtime.createV8ValueBoolean(included);
                }));
    }

    /**
     * Polyfill Array.prototype.indexOf().
     * The indexOf() method of Array instances returns the first index
     * at which a given element can be found in the array, or -1 if it is not present.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value indexOf(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                INDEX_OF, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    int index = -1;
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        Object object = v8Runtime.toObject(v8Values[0]);
                        int fromIndex = V8ValueUtils.asInt(v8Values, 1);
                        final int length = list.size();
                        if (fromIndex < 0) {
                            fromIndex += length;
                        }
                        if (fromIndex < 0) {
                            fromIndex = 0;
                        }
                        if (fromIndex < length) {
                            index = ListUtils.indexOf((List<Object>) list, object, fromIndex);
                        }
                    }
                    return v8Runtime.createV8ValueInteger(index);
                }));
    }

    /**
     * Polyfill Array.prototype.join().
     * The join() method of Array instances creates and returns a new string by concatenating all of the elements
     * in this array, separated by commas or a specified separator string. If the array has only one item,
     * then that item will be returned without using the separator.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value join(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                JOIN, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    String delimiter = V8ValueUtils.asString(v8Values, 0, StringUtils.EMPTY);
                    String result = list.stream().map(Object::toString).collect(Collectors.joining(delimiter));
                    return v8Runtime.createV8ValueString(result);
                }));
    }

    /**
     * Polyfill Array.prototype.keys().
     * The keys() method of Array instances returns a new array iterator object
     * that contains the keys for each index in the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value keys(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                KEYS, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    final int size = list.size();
                    Object[] indexes = new Object[size];
                    for (int i = 0; i < size; ++i) {
                        indexes[i] = v8Runtime.createV8ValueInteger(i);
                    }
                    return V8ValueUtils.createV8ValueArray(v8Runtime, indexes);
                }));
    }

    /**
     * Polyfill Array.prototype.lastIndexOf().
     * The lastIndexOf() method of Array instances returns the last index
     * at which a given element can be found in the array, or -1 if it is not present.
     * The array is searched backwards, starting at fromIndex.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value lastIndexOf(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                LAST_INDEX_OF, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    int index = -1;
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        Object object = v8Runtime.toObject(v8Values[0]);
                        final int length = list.size();
                        int fromIndex = V8ValueUtils.asInt(v8Values, 1, length - 1);
                        if (fromIndex < 0) {
                            fromIndex += length;
                        }
                        if (fromIndex < 0) {
                            fromIndex = length - 1;
                        }
                        if (fromIndex < length) {
                            index = ListUtils.lastIndexOf((List<Object>) list, object, fromIndex);
                        }
                    }
                    return v8Runtime.createV8ValueInteger(index);
                }));
    }

    /**
     * Polyfill Array.length.
     * The length data property of an Array instance represents the number of elements in that array.
     * The value is an unsigned, 32-bit integer that is always numerically greater than the highest index in the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value length(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueInteger(list.size());
    }

    /**
     * Polyfill Array.prototype.map().
     * The map() method of Array instances creates a new array populated with the results of
     * calling a provided function on every element in the calling array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value map(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                MAP, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    List<V8Value> results = new ArrayList<>(list.size());
                    try {
                        V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                        if (v8ValueFunction != null) {
                            V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                            int index = 0;
                            for (Object object : list) {
                                results.add(v8ValueFunction.call(v8ValueObject, object, index, thisObject));
                                ++index;
                            }
                        }
                        return V8ValueUtils.createV8ValueArray(v8Runtime, results.toArray());
                    } finally {
                        JavetResourceUtils.safeClose(results);
                    }
                }));
    }

    /**
     * Polyfill Array.prototype.pop().
     * The pop() method of Array instances removes the last element from an array and returns that element.
     * This method changes the length of the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value pop(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                POP, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (list.isEmpty()) {
                        return v8Runtime.createV8ValueUndefined();
                    }
                    return v8Runtime.toV8Value(ListUtils.pop(list));
                }));
    }

    /**
     * Polyfill Array.prototype.push().
     * The push() method of Array instances adds the specified elements to the end of an array
     * and returns the new length of the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value push(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                PUSH, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueInteger(
                                ListUtils.push((List<Object>) list, V8ValueUtils.toArray(v8Runtime, v8Values)))));
    }

    /**
     * Polyfill Array.prototype.reduce().
     * The reduce() method of Array instances executes a user-supplied "reducer" callback function on each element
     * of the array, in order, passing in the return value from the calculation on the preceding element.
     * The final result of running the reducer across all elements of the array is a single value.
     * <p>
     * The first time that the callback is run there is no "return value of the previous calculation".
     * If supplied, an initial value may be used in its place. Otherwise the array element at index 0 is used
     * as the initial value and iteration starts from the next element (index 1 instead of index 0).
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value reduce(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                REDUCE, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8Value initialValue = V8ValueUtils.asV8Value(v8Values, 1);
                        final int length = list.size();
                        if (initialValue == null) {
                            if (length == 0) {
                                v8Runtime.throwError(
                                        V8ValueErrorType.TypeError,
                                        V8ErrorTemplate.typeErrorReduceOfEmptyArrayWithNoInitialValue());
                            } else if (length == 1) {
                                return v8Runtime.toV8Value(list.get(0));
                            } else {
                                /**
                                 * If initialValue is not specified, accumulator is initialized
                                 * to the first value in the array, and callbackFn starts executing
                                 * with the second value in the array as currentValue.
                                 */
                                V8Value accumulator = v8Runtime.toV8Value(list.get(0));
                                int index = 0;
                                for (Object object : list) {
                                    if (index == 0) {
                                        ++index;
                                        continue;
                                    }
                                    V8Value result;
                                    try (V8Value currentValue = v8Runtime.toV8Value(object)) {
                                        result = v8ValueFunction.call(
                                                null, accumulator, currentValue, index, thisObject);
                                    } finally {
                                        JavetResourceUtils.safeClose(accumulator);
                                    }
                                    accumulator = result;
                                    ++index;
                                }
                                return accumulator;
                            }
                        } else {
                            if (length == 0) {
                                return initialValue;
                            } else {
                                V8Value accumulator = initialValue.toClone();
                                int index = 0;
                                for (Object object : list) {
                                    V8Value result;
                                    try (V8Value currentValue = v8Runtime.toV8Value(object)) {
                                        result = v8ValueFunction.call(
                                                null, accumulator, currentValue, index, thisObject);
                                    } finally {
                                        JavetResourceUtils.safeClose(accumulator);
                                    }
                                    accumulator = result;
                                    ++index;
                                }
                                return accumulator;
                            }
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Array.prototype.reduceRight()
     * The reduceRight() method of Array instances applies a function against an accumulator and each value
     * of the array (from right-to-left) to reduce it to a single value.
     * <p>
     * See also Array.prototype.reduce() for left-to-right.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value reduceRight(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                REDUCE, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8Value initialValue = V8ValueUtils.asV8Value(v8Values, 1);
                        final int length = list.size();
                        if (initialValue == null) {
                            if (length == 0) {
                                v8Runtime.throwError(
                                        V8ValueErrorType.TypeError,
                                        V8ErrorTemplate.typeErrorReduceOfEmptyArrayWithNoInitialValue());
                            } else if (length == 1) {
                                return v8Runtime.toV8Value(list.get(0));
                            } else {
                                /**
                                 * If initialValue is not specified, accumulator is initialized
                                 * to the first value in the array, and callbackFn starts executing
                                 * with the second value in the array as currentValue.
                                 */
                                V8Value accumulator = v8Runtime.toV8Value(list.get(length - 1));
                                int index = length - 1;
                                ListIterator<?> listIterator = list.listIterator(length);
                                while (listIterator.hasPrevious()) {
                                    if (index == length - 1) {
                                        listIterator.previous();
                                        --index;
                                        continue;
                                    }
                                    V8Value result;
                                    try (V8Value currentValue = v8Runtime.toV8Value(listIterator.previous())) {
                                        result = v8ValueFunction.call(
                                                null, accumulator, currentValue, index, thisObject);
                                    } finally {
                                        JavetResourceUtils.safeClose(accumulator);
                                    }
                                    accumulator = result;
                                    --index;
                                }
                                return accumulator;
                            }
                        } else {
                            if (length == 0) {
                                return initialValue;
                            } else {
                                V8Value accumulator = initialValue.toClone();
                                int index = length - 1;
                                ListIterator<?> listIterator = list.listIterator(length);
                                while (listIterator.hasPrevious()) {
                                    V8Value result;
                                    try (V8Value currentValue = v8Runtime.toV8Value(listIterator.previous())) {
                                        result = v8ValueFunction.call(
                                                null, accumulator, currentValue, index, thisObject);
                                    } finally {
                                        JavetResourceUtils.safeClose(accumulator);
                                    }
                                    accumulator = result;
                                    --index;
                                }
                                return accumulator;
                            }
                        }
                    }
                    return v8Runtime.createV8ValueUndefined();
                }));
    }

    /**
     * Polyfill Array.prototype.reverse().
     * The reverse() method of Array instances reverses an array in place and returns the reference to the same array,
     * the first array element now becoming the last, and the last array element becoming the first. In other words,
     * elements order in the array will be turned towards the direction opposite to that previously stated.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value reverse(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                REVERSE, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    if (!list.isEmpty()) {
                        Collections.reverse(list);
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Array.prototype.shift().
     * The shift() method of Array instances removes the first element from an array and returns that removed element.
     * This method changes the length of the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value shift(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                SHIFT, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    if (list.isEmpty()) {
                        return v8Runtime.createV8ValueUndefined();
                    }
                    return v8Runtime.toV8Value(ListUtils.shift(list));
                }));
    }

    /**
     * Polyfill Array.prototype.slice().
     * The slice() method of Array instances returns a shallow copy of a portion of an array
     * into a new array object selected from start to end (end not included) where start
     * and end represent the index of items in that array. The original array will not be modified.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value slice(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                SLICE, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    List<Object> results = new ArrayList<>();
                    if (!list.isEmpty()) {
                        final int length = list.size();
                        int startIndex = V8ValueUtils.asInt(v8Values, 0);
                        if (startIndex < 0) {
                            startIndex += length;
                        }
                        if (startIndex < 0) {
                            startIndex = 0;
                        }
                        int endIndex = V8ValueUtils.asInt(v8Values, 1, length);
                        if (endIndex < 0) {
                            endIndex += length;
                        }
                        if (endIndex < 0) {
                            endIndex = 0;
                        }
                        if (endIndex > length) {
                            endIndex = length;
                        }
                        if (startIndex < endIndex) {
                            results.addAll(list.subList(startIndex, endIndex));
                        }
                    }
                    return V8ValueUtils.createV8ValueArray(v8Runtime, results.toArray());
                }));
    }

    /**
     * Polyfill Array.prototype.some().
     * The some() method of Array instances tests whether at least one element in the array passes
     * the test implemented by the provided function. It returns true if, in the array,
     * it finds an element for which the provided function returns true; otherwise it returns false.
     * It doesn't modify the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value some(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                SOME, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunctionWithError(v8Runtime, v8Values, 0);
                    if (v8ValueFunction != null) {
                        V8ValueObject v8ValueObject = V8ValueUtils.asV8ValueObject(v8Values, 1);
                        int index = 0;
                        for (Object object : list) {
                            try (V8Value result = v8ValueFunction.call(v8ValueObject, object, index, thisObject)) {
                                if (result.ifTrue()) {
                                    return v8Runtime.createV8ValueBoolean(true);
                                }
                            }
                            ++index;
                        }
                    }
                    return v8Runtime.createV8ValueBoolean(false);
                }));
    }

    /**
     * Polyfill Array.prototype.sort().
     * The sort() method of Array instances sorts the elements of an array in place
     * and returns the reference to the same array, now sorted.
     * The default sort order is ascending, built upon converting the elements into strings,
     * then comparing their sequences of UTF-16 code units values.
     * <p>
     * The time and space complexity of the sort cannot be guaranteed as it depends on the implementation.
     * <p>
     * To sort the elements in an array without mutating the original array, use toSorted().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value sort(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                SORT, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    final int length = list.size();
                    if (length > 1) {
                        V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunction(v8Values, 0);
                        if (v8ValueFunction == null) {
                            list.sort((o1, o2) -> ((Comparator<String>) Comparator.naturalOrder()).compare(
                                    String.valueOf(o1), String.valueOf(o2)));
                        } else {
                            try {
                                list.sort((o1, o2) -> {
                                    try (V8Value v8Value = v8ValueFunction.call(null, o1, o2)) {
                                        return v8Value.asInt();
                                    } catch (JavetException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            } catch (Throwable t) {
                                v8Runtime.throwError(V8ValueErrorType.Error, t.getMessage());
                            }
                        }
                    }
                    return thisObject;
                }));
    }

    /**
     * Polyfill Array.prototype.splice().
     * The splice() method of Array instances changes the contents of an array by removing
     * or replacing existing elements and/or adding new elements in place.
     * <p>
     * To create a new array with a segment removed and/or replaced without mutating the original array,
     * use toSpliced(). To access part of an array without modifying it, see slice().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value splice(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<Object> list = (List<Object>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                SPLICE, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    List<Object> results = new ArrayList<>();
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        final int length = list.size();
                        int startIndex = V8ValueUtils.asInt(v8Values, 0);
                        if (startIndex < 0) {
                            startIndex += length;
                        }
                        if (startIndex < 0) {
                            startIndex = 0;
                        }
                        if (startIndex >= length) {
                            v8Runtime.throwError(
                                    V8ValueErrorType.RangeError,
                                    V8ErrorTemplate.rangeErrorStartIsOutOfRange(startIndex));
                        } else {
                            int deleteCount = V8ValueUtils.asInt(v8Values, 1);
                            deleteCount = Math.min(deleteCount, length - startIndex);
                            if (deleteCount > 0) {
                                List<?> subList = list.subList(startIndex, startIndex + deleteCount);
                                results.addAll(subList);
                                subList.clear();
                            }
                            if (v8Values.length > 2) {
                                List<Object> toBeAddedList = new ArrayList<>();
                                for (int i = 2; i < v8Values.length; ++i) {
                                    toBeAddedList.add(v8Runtime.toObject(v8Values[i]));
                                }
                                list.addAll(startIndex, toBeAddedList);
                            }
                        }
                    }
                    return V8ValueUtils.createV8ValueArray(v8Runtime, results.toArray());
                }));
    }

    /**
     * Polyfill List.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value toJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        V8ValueUtils.createV8ValueArray(v8Runtime, list.toArray())));
    }

    /**
     * Polyfill Array.prototype.toReversed().
     * The toReversed() method of Array instances is the copying counterpart of the reverse() method.
     * It returns a new array with the elements in reversed order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value toReversed(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_REVERSED, targetObject, JavetCallbackType.DirectCallThisAndResult,
                (IJavetDirectCallable.ThisAndResult<Exception>) (thisObject, v8Values) -> {
                    List<Object> reversedList = new ArrayList<>(list);
                    Collections.reverse(reversedList);
                    return V8ValueUtils.createV8ValueArray(v8Runtime, reversedList.toArray());
                }));
    }

    /**
     * Polyfill Array.prototype.toSorted()
     * The toSorted() method of Array instances is the copying version of the sort() method.
     * It returns a new array with the elements sorted in ascending order.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value toSorted(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_SORTED, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    List<?> results = new ArrayList<>(list);
                    final int length = list.size();
                    if (length > 1) {
                        V8ValueFunction v8ValueFunction = V8ValueUtils.asV8ValueFunction(v8Values, 0);
                        if (v8ValueFunction == null) {
                            results.sort((o1, o2) -> ((Comparator<String>) Comparator.naturalOrder()).compare(
                                    String.valueOf(o1), String.valueOf(o2)));
                        } else {
                            try {
                                results.sort((o1, o2) -> {
                                    try (V8Value v8Value = v8ValueFunction.call(null, o1, o2)) {
                                        return v8Value.asInt();
                                    } catch (JavetException e) {
                                        throw new RuntimeException(e);
                                    }
                                });
                            } catch (Throwable t) {
                                v8Runtime.throwError(V8ValueErrorType.Error, t.getMessage());
                            }
                        }
                    }
                    return V8ValueUtils.createV8ValueArray(v8Runtime, results.toArray());
                }));
    }

    /**
     * Polyfill Array.prototype.toSpliced()
     * The toSpliced() method of Array instances is the copying version of the splice() method.
     * It returns a new array with some elements removed and/or replaced at a given index.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value toSpliced(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<Object> list = (List<Object>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_SPLICED, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    List<Object> results = new ArrayList<>(list);
                    if (ArrayUtils.isNotEmpty(v8Values)) {
                        final int length = list.size();
                        int startIndex = V8ValueUtils.asInt(v8Values, 0);
                        if (startIndex < 0) {
                            startIndex += length;
                        }
                        if (startIndex < 0) {
                            startIndex = 0;
                        }
                        if (startIndex >= length) {
                            v8Runtime.throwError(
                                    V8ValueErrorType.RangeError,
                                    V8ErrorTemplate.rangeErrorStartIsOutOfRange(startIndex));
                        } else {
                            int deleteCount = V8ValueUtils.asInt(v8Values, 1);
                            deleteCount = Math.min(deleteCount, length - startIndex);
                            if (deleteCount > 0) {
                                results.subList(startIndex, startIndex + deleteCount).clear();
                            }
                            if (v8Values.length > 2) {
                                List<Object> toBeAddedList = new ArrayList<>();
                                for (int i = 2; i < v8Values.length; ++i) {
                                    toBeAddedList.add(v8Runtime.toObject(v8Values[i]));
                                }
                                results.addAll(startIndex, toBeAddedList);
                            }
                        }
                    }
                    return V8ValueUtils.createV8ValueArray(v8Runtime, results.toArray());
                }));
    }

    /**
     * Polyfill Array.prototype.unshift().
     * The unshift() method of Array instances adds the specified elements to the beginning of an array
     * and returns the new length of the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value unshift(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                UNSHIFT, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueInteger(
                                ListUtils.unshift((List<Object>) list, V8ValueUtils.toArray(v8Runtime, v8Values)))));
    }

    /**
     * Polyfill Array.prototype.values().
     * The values() method of Array instances returns a new array iterator object that iterates the value
     * of each item in the array.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value values(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        return new JavetProxySymbolIterableConverter<>(v8Runtime, targetObject).getV8ValueFunction();
    }

    /**
     * Polyfill Array.prototype.with().
     * The with() method of Array instances is the copying version of using the bracket notation to change the value
     * of a given index. It returns a new array with the element at the given index replaced with the given value.
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public static V8Value with(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof List : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LIST;
        final List<?> list = (List<?>) Objects.requireNonNull(targetObject);
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                WITH, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> {
                    Object[] objects = list.toArray();
                    int index = V8ValueUtils.asInt(v8Values, 0);
                    if (index >= 0 && index < objects.length) {
                        objects[index] = v8Values.length > 1
                                ? v8Values[1]
                                : v8Runtime.createV8ValueUndefined();
                    } else {
                        v8Runtime.throwError(
                                V8ValueErrorType.RangeError,
                                V8ErrorTemplate.rangeErrorInvalidIndex(index));
                    }
                    return V8ValueUtils.createV8ValueArray(v8Runtime, objects);
                }));
    }
}
