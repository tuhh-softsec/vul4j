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

import org.apache.commons.digester3.SetTopRule;
import org.apache.commons.digester3.annotations.DigesterRule;
import org.apache.commons.digester3.annotations.DigesterRuleList;
import org.apache.commons.digester3.annotations.handlers.SetTopHandler;

/**
 * Methods annotated with {@code SetTop} will be bound with {@code SetTopRule} digester rule.
 * 
 * @see org.apache.commons.digester3.Digester#addSetTop(String,String,String)
 * @since 2.1
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
@DigesterRule( reflectsRule = SetTopRule.class, handledBy = SetTopHandler.class )
public @interface SetTop
{

    /**
     * The element matching pattern.
     */
    String pattern();

    /**
     * Marks the rule be invoked when {@code begin} or {@code end} events match.
     */
    boolean fireOnBegin() default false;

    /**
     * The namespace URI for which this Rule is relevant, if any.
     *
     * @since 3.0
     */
    String namespaceURI() default "";

    /**
     * Defines several {@code @SetTop} annotations on the same element
     * 
     * @see SetTop
     */
    @Documented
    @Retention( RetentionPolicy.RUNTIME )
    @Target( ElementType.TYPE )
    @DigesterRuleList
    @interface List
    {
        SetTop[] value();
    }

}
