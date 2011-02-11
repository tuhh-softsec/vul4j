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

import org.apache.commons.digester3.ObjectCreateRule;
import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rulesbinder.ObjectCreateBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#objectCreate(String)}.
 */
final class ObjectCreateBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<ObjectCreateRule>
        implements ObjectCreateBuilder {

    private final ClassLoader classLoader;

    private Class<?> type;

    private String attributeName;

    public ObjectCreateBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder,
            ClassLoader classLoader) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
        this.classLoader = classLoader;
    }

    /**
     * Construct an object with the specified class name.
     *
     * @param className Java class name of the object to be created
     * @return this builder instance
     */
    public ObjectCreateBuilderImpl ofType(String className) {
        if (className == null) {
            this.reportError("createObject().ofType(String)", "NULL Java type not allowed");
            return this;
        }

        try {
            return this.ofType(this.classLoader.loadClass(className));
        } catch (ClassNotFoundException e) {
            this.reportError("createObject().ofType(String)", String.format("class '%s' cannot be load", className));
            return this;
        }
    }

    /**
     * Construct an object with the specified class.
     *
     * @param type Java class of the object to be created
     * @return this builder instance
     */
    public <T> ObjectCreateBuilderImpl ofType(Class<T> type) {
        if (type == null) {
            this.reportError("createObject().ofType(Class<?>)", "NULL Java type not allowed");
            return this;
        }

        this.type = type;

        return this;
    }

    /**
     * Allows specify the attribute containing an override class name if it is present.
     *
     * @param attributeName The attribute containing an override class name if it is present
     * @return this builder instance
     */
    public ObjectCreateBuilderImpl ofTypeSpecifiedByAttribute(/* @Nullable */String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ObjectCreateRule createRule() {
        return new ObjectCreateRule(this.type, this.attributeName);
    }

}
