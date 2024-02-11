/*
 * Copyright (c) 2021-2024. caoccao.com Sam Cao
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

package com.caoccao.javet.values.primitive;

import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.V8Runtime;

/**
 * The type V8 value long.
 *
 * @since 0.7.0
 */
@SuppressWarnings("unchecked")
public final class V8ValueLong extends V8ValuePrimitive<Long> {
    /**
     * Instantiates a new V8 value long.
     *
     * @param v8Runtime the V8 runtime
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueLong(V8Runtime v8Runtime) throws JavetException {
        this(v8Runtime, 0L);
    }

    /**
     * Instantiates a new V8 value long.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueLong(V8Runtime v8Runtime, long value) throws JavetException {
        super(v8Runtime, value);
    }

    /**
     * Instantiates a new V8 value long.
     *
     * @param v8Runtime the V8 runtime
     * @param value     the value
     * @throws JavetException the javet exception
     * @since 0.7.0
     */
    public V8ValueLong(V8Runtime v8Runtime, String value) throws JavetException {
        this(v8Runtime, Long.parseLong(value));
    }

    @Override
    public double asDouble() {
        return value.doubleValue();
    }

    @Override
    public int asInt() {
        return value.intValue();
    }

    @Override
    public long asLong() throws JavetException {
        return value;
    }

    @Override
    public boolean ifTrue() {
        return value != 0L;
    }

    @Override
    public V8ValueLong toClone(boolean referenceCopy) throws JavetException {
        return this;
    }

    /**
     * To primitive long.
     *
     * @return the long
     * @since 0.7.0
     */
    public long toPrimitive() {
        return value;
    }
}
