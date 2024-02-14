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

package com.caoccao.javet.interop.proxy.plugins;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;
import com.caoccao.javet.interop.binding.IClassProxyPluginFunction;
import com.caoccao.javet.interop.callback.IJavetDirectCallable;
import com.caoccao.javet.interop.callback.JavetCallbackContext;
import com.caoccao.javet.interop.callback.JavetCallbackType;
import com.caoccao.javet.values.V8Value;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The type Javet proxy plugin primitive.
 *
 * @since 3.0.4
 */
public class JavetProxyPluginDefault extends BaseJavetProxyPluginMultiple {
    /**
     * The constant NAME.
     *
     * @since 3.0.4
     */
    public static final String NAME = Object.class.getName();
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BIG_INTEGER =
            "Target object must be an instance of BigInteger.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BOOLEAN =
            "Target object must be an instance of Boolean.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BYTE =
            "Target object must be an instance of Byte.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_CHARACTER =
            "Target object must be an instance of Character.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_DOUBLE =
            "Target object must be an instance of Double.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_FLOAT =
            "Target object must be an instance of Float.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_INTEGER =
            "Target object must be an instance of Integer.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LONG =
            "Target object must be an instance of Long.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SHORT =
            "Target object must be an instance of Short.";
    protected static final String ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING =
            "Target object must be an instance of String.";
    private static final JavetProxyPluginDefault instance = new JavetProxyPluginDefault();

    public JavetProxyPluginDefault() {
        super();
        {
            // java.math.BigInteger
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::bigIntegerToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(BigInteger.class, polyfillFunctionMap);
        }
        {
            // java.lang.Boolean
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::booleanToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Boolean.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Boolean.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueBooleanObject((Boolean) targetObject));
        }
        {
            // java.lang.Byte
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::byteToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Byte.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Byte.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueIntegerObject((Byte) targetObject));
        }
        {
            // java.lang.Character
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::characterToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Character.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Character.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueStringObject(String.valueOf(targetObject)));
        }
        {
            // java.lang.Double
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::doubleToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Double.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Double.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueDoubleObject((Double) targetObject));
        }
        {
            // java.lang.Float
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::floatToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Float.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Float.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueDoubleObject((Float) targetObject));
        }
        {
            // java.lang.Integer
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::integerToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Integer.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Integer.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueIntegerObject((Integer) targetObject));
        }
        {
            // java.lang.Long
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::longToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Long.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Long.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueLongObject((Long) targetObject));
        }
        {
            // java.lang.Short
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::shortToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(Short.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    Short.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueIntegerObject((Short) targetObject));
        }
        {
            // java.lang.String
            Map<String, IClassProxyPluginFunction<?>> polyfillFunctionMap = new HashMap<>();
            polyfillFunctionMap.put(TO_JSON, this::stringToJSON);
            polyfillFunctionMap.put(VALUE_OF, this::valueOf);
            proxyGetByStringMap.put(String.class, polyfillFunctionMap);
            targetObjectConstructorMap.put(
                    String.class,
                    (v8Runtime, targetObject) -> v8Runtime.createV8ValueStringObject((String) targetObject));
        }
    }

    /**
     * Gets instance.
     *
     * @return the instance
     * @since 3.0.4
     */
    public static JavetProxyPluginDefault getInstance() {
        return instance;
    }

    /**
     * Polyfill BigInteger.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value bigIntegerToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof BigInteger : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BIG_INTEGER;
        final BigInteger value = (BigInteger) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueBigInteger(value)));
    }

    /**
     * Polyfill Boolean.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value booleanToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Boolean : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BOOLEAN;
        final Boolean value = (Boolean) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueBoolean(value)));
    }

    /**
     * Polyfill Byte.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value byteToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Byte : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_BYTE;
        final Byte value = (Byte) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueInteger(value.intValue())));
    }

    /**
     * Polyfill Character.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value characterToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Character : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_CHARACTER;
        final Character value = (Character) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueString(value.toString())));
    }

    /**
     * Polyfill Double.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value doubleToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Double : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_DOUBLE;
        final Double value = (Double) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueDouble(value)));
    }

    /**
     * Polyfill Float.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value floatToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Float : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_FLOAT;
        final Float value = (Float) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueDouble(value.doubleValue())));
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Polyfill Integer.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value integerToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Integer : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_INTEGER;
        final Integer value = (Integer) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueInteger(value)));
    }

    @Override
    public boolean isMethodProxyable(String methodName) {
        return false;
    }

    @Override
    public boolean isProxyable(Class<?> targetClass) {
        return targetClass != null;
    }

    /**
     * Polyfill Long.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value longToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Long : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_LONG;
        final Long value = (Long) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueLong(value)));
    }

    /**
     * Polyfill Short.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value shortToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof Short : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_SHORT;
        final Short value = (Short) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        v8Runtime.createV8ValueInteger(value.intValue())));
    }

    /**
     * Polyfill String.toJSON().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value stringToJSON(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        assert targetObject instanceof String : ERROR_TARGET_OBJECT_MUST_BE_AN_INSTANCE_OF_STRING;
        final String value = (String) targetObject;
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                TO_JSON, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) -> v8Runtime.createV8ValueString(value)));
    }

    /**
     * Polyfill valueOf().
     *
     * @param v8Runtime    the V8 runtime
     * @param targetObject the target object
     * @return the V8 value
     * @throws JavetException the javet exception
     * @since 3.0.4
     */
    public V8Value valueOf(V8Runtime v8Runtime, Object targetObject) throws JavetException {
        return Objects.requireNonNull(v8Runtime).createV8ValueFunction(new JavetCallbackContext(
                VALUE_OF, targetObject, JavetCallbackType.DirectCallNoThisAndResult,
                (IJavetDirectCallable.NoThisAndResult<Exception>) (v8Values) ->
                        OBJECT_CONVERTER.toV8Value(v8Runtime, targetObject)));
    }
}
