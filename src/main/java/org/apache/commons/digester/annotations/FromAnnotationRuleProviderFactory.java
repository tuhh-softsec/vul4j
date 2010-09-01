/*
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
package org.apache.commons.digester.annotations;

import org.apache.commons.digester.annotations.spi.AnnotationRuleProviderFactory;
import org.apache.commons.digester.annotations.spi.DigesterLoaderHandlerFactory;

/**
 * {@link DigesterLoader} builder implementation.
 *
 * @version $Id$
 * @since 2.1
 */
public final class FromAnnotationRuleProviderFactory {

    /**
     * The {@link AnnotationRuleProviderFactory} selected in the previous
     * chained builder.
     */
    private final AnnotationRuleProviderFactory annotationRuleProviderFactory;

    /**
     * {@link DigesterLoader} builder implementation.
     *
     * @param annotationRuleProviderFactory the
     *        {@link AnnotationRuleProviderFactory} selected in the previous
     *        chained builder.
     */
    protected FromAnnotationRuleProviderFactory(
            AnnotationRuleProviderFactory annotationRuleProviderFactory) {
        this.annotationRuleProviderFactory = annotationRuleProviderFactory;
    }

    /**
     * Builds a new {@link DigesterLoader} using the default
     * {@link DigesterLoaderHandlerFactory} implementation.
     *
     * @return the {@link DigesterLoader}.
     */
    public DigesterLoader useDefaultDigesterLoaderHandlerFactory() {
        return this.useDigesterLoaderHandlerFactory(new DefaultDigesterLoaderHandlerFactory());
    }

    /**
     * Builds a new {@link DigesterLoader} using the user defined
     * {@link DigesterLoaderHandlerFactory} implementation.
     *
     * @param digesterLoaderHandlerFactory
     * @return the {@link DigesterLoader}.
     */
    public DigesterLoader useDigesterLoaderHandlerFactory(DigesterLoaderHandlerFactory digesterLoaderHandlerFactory) {
        if (digesterLoaderHandlerFactory == null) {
            throw new IllegalArgumentException("Parameter 'digesterLoaderHandlerFactory' must be not null");
        }
        return new DigesterLoader(this.annotationRuleProviderFactory, digesterLoaderHandlerFactory);
    }

}
