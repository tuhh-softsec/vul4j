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
package org.apache.commons.digester3.annotations.providers;

import org.apache.commons.digester3.CallParamRule;
import org.apache.commons.digester3.annotations.AnnotationRuleProvider;
import org.apache.commons.digester3.annotations.reflect.MethodArgument;
import org.apache.commons.digester3.annotations.rules.AttributeCallParam;

/**
 * Provides instances of {@link CallParamRule}.
 *
 * @see CallParamRule#CallParamRule(int,String)
 * @since 2.1
 */
public final class AttributeCallParamRuleProvider
        implements AnnotationRuleProvider<AttributeCallParam, MethodArgument, CallParamRule> {

    private String attribute;

    private int index;

    /**
     * {@inheritDoc}
     */
    public void init(AttributeCallParam annotation, MethodArgument element) {
        this.attribute = annotation.attribute();
        this.index = element.getIndex();
    }

    /**
     * {@inheritDoc}
     */
    public CallParamRule get() {
        return new CallParamRule(this.index, this.attribute);
    }

}
