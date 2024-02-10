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

package com.caoccao.javet.interfaces;

import com.caoccao.javet.interop.proxy.IJavetNonProxy;

/**
 * The interface Javet entity object.
 *
 * @param <T> the type parameter
 * @since 3.0.4
 */
public interface IJavetEntityObject<T> extends IJavetNonProxy {
    /**
     * Gets value.
     *
     * @return the value
     * @since 3.0.4
     */
    T getValue();

    /**
     * Sets value.
     *
     * @param value the value
     * @since 3.0.4
     */
    void setValue(T value);
}
