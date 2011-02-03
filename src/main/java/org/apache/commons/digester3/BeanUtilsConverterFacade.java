/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3;

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.digester3.spi.TypeConverter;

/**
 * Façade for Apache BeanUtils' {@code org.apache.commons.beanutils.Converter}.
 *
 * IMPLEMENTATION NOTE: this class is for internal use only, must be deleted for final version.
 */
final class BeanUtilsConverterFacade implements Converter {

    /**
     * The wrapped {@link TypeConverter}.
     */
    private final TypeConverter<?> wrappedConverter;

    /**
     * Creates a new {@code org.apache.commons.beanutils.Converter} façade
     * wrapping an existing {@link TypeConverter}.
     *
     * @param <T> Whatever type is fine
     * @param wrappedConverter The wrapped {@link TypeConverter}
     */
    public <T> BeanUtilsConverterFacade(TypeConverter<T> wrappedConverter) {
        this.wrappedConverter = wrappedConverter;
    }

    /**
     * Return the wrapped {@link TypeConverter}.
     *
     * @return The wrapped {@link TypeConverter}
     */
    public TypeConverter<?> getWrappedConverter() {
        return wrappedConverter;
    }

    /**
     * {@inheritDoc}
     */
    public Object convert(Class type, Object value) {
        if (value == null) {
            return null;
        }

        if (String.class != value.getClass()) {
            throw new ConversionException("Only java.lang.String supported in this version!");
        }

        return this.wrappedConverter.convert((String) value);
    }

}
