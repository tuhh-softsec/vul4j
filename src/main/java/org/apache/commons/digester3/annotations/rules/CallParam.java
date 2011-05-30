package org.apache.commons.digester3.annotations.rules;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.digester3.CallParamRule;
import org.apache.commons.digester3.annotations.DigesterRule;
import org.apache.commons.digester3.annotations.DigesterRuleList;
import org.apache.commons.digester3.annotations.handlers.CallParamHandler;

/**
 * Methods arguments annotated with {@code CallParam} will be bound with {@code CallParamRule} digester rule.
 * 
 * @see org.apache.commons.digester3.Digester#addCallParam(String,int)
 * @since 2.1
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.PARAMETER )
@DigesterRule( reflectsRule = CallParamRule.class, handledBy = CallParamHandler.class )
public @interface CallParam
{

    /**
     * The element matching pattern.
     */
    String pattern();

    /**
     * The namespace URI for which this Rule is relevant, if any.
     *
     * @since 3.0
     */
    String namespaceURI() default "";

    /**
     * The attribute from which to save the parameter value.
     *
     * @since 3.0
     */
    String attributeName() default "";

    /**
     * Flags the parameter to be set from the stack.
     *
     * @since 3.0
     */
    boolean fromStack() default false;

    /**
     * Sets the position of the object from the top of the stack.
     *
     * @since 3.0
     */
    int stackIndex() default 0;

    /**
     * Defines several {@code @CallParam} annotations on the same element.
     * 
     * @see CallParam
     */
    @Documented
    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.TYPE )
    @DigesterRuleList
    @interface List
    {
        CallParam[] value();
    }

}
