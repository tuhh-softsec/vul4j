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
package org.apache.commons.digester3.internal.rulesbinder;

import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rule.FactoryCreateRule;
import org.apache.commons.digester3.rulesbinder.FactoryCreateBuilder;
import org.apache.commons.digester3.spi.ObjectCreationFactory;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#factoryCreate(String)}.
 */
final class FactoryCreateBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<FactoryCreateRule>
        implements FactoryCreateBuilder {

    private final ClassLoader classLoader;

    private Class<? extends ObjectCreationFactory<?>> type;

    private String attributeName;

    private boolean ignoreCreateExceptions;

    private ObjectCreationFactory<?> creationFactory;

    public FactoryCreateBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder,
            ClassLoader classLoader) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
        this.classLoader = classLoader;
    }

    /**
     * {@inheritDoc}
     */
    public FactoryCreateBuilder ofType(String className) {
        if (className == null) {
            this.reportError("factoryCreate().ofType(Class<?>)", "NULL Java type not allowed");
        }

        try {
            Class<?> type = this.classLoader.loadClass(className);
            if (!ObjectCreationFactory.class.isAssignableFrom(type)) {
                this.reportError("factoryCreate().ofType(Class<?>)", "NULL Java type not allowed");
                return this;
            }

            this.type = (Class<? extends ObjectCreationFactory<?>>) type;
        } catch (ClassNotFoundException e) {
            this.reportError("factoryCreate().ofType(String)", String.format("class '%s' cannot be load", className));
        }

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public <T> FactoryCreateBuilder ofType(Class<? extends ObjectCreationFactory<T>> type) {
        if (type == null) {
            this.reportError("factoryCreate().ofType(Class<?>)", "NULL Java type not allowed");
        }

        this.type = type;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public <T> FactoryCreateBuilder usingFactory(/* @Nullable */ObjectCreationFactory<T> creationFactory) {
        this.creationFactory = creationFactory;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FactoryCreateBuilder overriddenByAttribute(/* @Nullable */String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public FactoryCreateBuilder ignoreCreateExceptions(boolean ignoreCreateExceptions) {
        this.ignoreCreateExceptions = ignoreCreateExceptions;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected FactoryCreateRule createRule() {
        if (type == null && attributeName == null && creationFactory == null) {
            this.reportError("factoryCreate()",
                    "at least one between 'className' ar 'attributeName' or 'creationFactory' has to be specified");
        }

        return new FactoryCreateRule(this.type, this.attributeName, this.creationFactory, this.ignoreCreateExceptions);
    }

}
