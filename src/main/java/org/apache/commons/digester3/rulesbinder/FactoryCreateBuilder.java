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
package org.apache.commons.digester3.rulesbinder;

import org.apache.commons.digester3.FactoryCreateRule;
import org.apache.commons.digester3.spi.ObjectCreationFactory;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#factoryCreate(String)}.
 */
public interface FactoryCreateBuilder extends BackToLinkedRuleBuilder<FactoryCreateRule> {

    /**
     * Construct a factory create rule that will use the specified class name to create an {@link ObjectCreationFactory}
     * which will then be used to create an object and push it on the stack.
     *
     * @param className Java class name of the object creation factory class
     * @return this builder instance
     */
    FactoryCreateBuilder ofType(String className);

    /**
     * Construct a factory create rule that will use the specified class to create an {@link ObjectCreationFactory}
     * which will then be used to create an object and push it on the stack.
     *
     * @param type Java class of the object creation factory class
     * @return this builder instance
     */
    FactoryCreateBuilder ofType(Class<?> type);

    /**
     * Construct a factory create rule using the given, already instantiated, {@link ObjectCreationFactory}.
     *
     * @param <T> the type of created object by the given factory
     * @param creationFactory called on to create the object
     * @return this builder instance
     */
    <T> FactoryCreateBuilder usingFactory(ObjectCreationFactory<T> creationFactory);

    /**
     * Allows specify the attribute containing an override class name if it is present.
     *
     * @param attributeName The attribute containing an override class name if it is present
     * @return this builder instance
     */
    FactoryCreateBuilder overriddenByAttribute(String attributeName);

    /**
     * Exceptions thrown by the object creation factory will be ignored or not.
     *
     * @param ignoreCreateExceptions if true, exceptions thrown by the object creation factory will be ignored
     * @return this builder instance
     */
    FactoryCreateBuilder ignoreCreateExceptions(boolean ignoreCreateExceptions);

}
