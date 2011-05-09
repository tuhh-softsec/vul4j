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
package org.apache.commons.digester.annotations.providers;

import org.apache.commons.digester.FactoryCreateRule;
import org.apache.commons.digester.annotations.AnnotationRuleProvider;
import org.apache.commons.digester.annotations.rules.FactoryCreate;

/**
 * Provides instances of {@link FactoryCreateRule}.
 *
 * @since 2.1
 */
public final class FactoryCreateRuleProvider
        implements AnnotationRuleProvider<FactoryCreate, Class<?>, FactoryCreateRule> {

    private Class<?> factoryClass;

    private boolean ignoreCreateExceptions;

    /**
     * {@inheritDoc}
     */
    public void init(FactoryCreate annotation, Class<?> element) {
        this.factoryClass = annotation.factoryClass();
        this.ignoreCreateExceptions = annotation.ignoreCreateExceptions();
    }

    /**
     * {@inheritDoc}
     */
    public FactoryCreateRule get() {
        return new FactoryCreateRule(this.factoryClass,
                this.ignoreCreateExceptions);
    }

}
