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
package org.apache.commons.digester.annotations.rules;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.commons.digester.PathCallParamRule;
import org.apache.commons.digester.annotations.DigesterRule;
import org.apache.commons.digester.annotations.DigesterRuleList;
import org.apache.commons.digester.annotations.providers.PathCallParamRuleProvider;

/**
 * Methods arguments annotated with {@code PathCallParam} will be bound
 * with {@code PathCallParamRule} digester rule.
 *
 * @see org.apache.commons.digester.Digester#addCallParamPath(String,int)
 * @since 2.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@DigesterRule(
        reflectsRule = PathCallParamRule.class,
        providedBy = PathCallParamRuleProvider.class
)
public @interface PathCallParam {

    /**
     * The element matching pattern.
     *
     * @return the element matching pattern.
     */
    String pattern();

    /**
     * Defines several {@code @PathCallParam} annotations on the same element.
     *
     * @see PathCallParam
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @DigesterRuleList
    @interface List {
        PathCallParam[] value();
    }

}
