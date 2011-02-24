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
package org.apache.commons.digester3.annotations.rules;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.digester3.annotations.DigesterRule;
import org.apache.commons.digester3.annotations.DigesterRuleList;
import org.apache.commons.digester3.annotations.handlers.FactoryCreateHandler;
import org.apache.commons.digester3.rule.AbstractObjectCreationFactory;
import org.apache.commons.digester3.rule.FactoryCreateRule;
import org.apache.commons.digester3.spi.ObjectCreationFactory;
import org.xml.sax.Attributes;

/**
 * Classes annotated with {@code FactoryCreate} will be bound with
 * {@code FactoryCreateRule} digester rule.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@CreationRule
@DigesterRule(
    reflectsRule = FactoryCreateRule.class,
    handledBy = FactoryCreateHandler.class
)
public @interface FactoryCreate {

    /**
     * The Java class of the object creation factory class.
     *
     * @return the Java class of the object creation factory class
     */
    Class<? extends ObjectCreationFactory<?>> factoryClass() default DefaultObjectCreationFactory.class;

    /**
     * Allows specify the attribute containing an override class name if it is present.
     *
     * @return The attribute containing an override class name if it is present
     */
    String attributeName() default "";

    /**
     * The element matching pattern.
     *
     * @return the element matching pattern
     */
    String pattern();

    /**
     * The namespace URI for which this Rule is relevant, if any.
     *
     * @return The namespace URI for which this Rule is relevant, if any
     */
    String namespaceURI() default "";

    /**
     * When true any exceptions thrown during object creation will be ignored.
     *
     * @return when true any exceptions thrown during object creation will be
     *         ignored.
     */
    boolean ignoreCreateExceptions() default false;

    /**
     * Defines several {@code @FactoryCreate} annotations on the same element.
     *
     * @see FactoryCreate
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @DigesterRuleList
    @interface List {
        FactoryCreate[] value();
    }

    /**
     * Dummy ObjectCreationFactory type only for annotation value type purposes.
     */
    public static final class DefaultObjectCreationFactory extends AbstractObjectCreationFactory<Object> {

        /**
         * {@inheritDoc}
         */
        @Override
        public Object createObject(Attributes attributes) throws Exception {
            // do nothing
            return null;
        }

    }

}
