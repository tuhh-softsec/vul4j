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

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.rule.RulesBinder;
import org.apache.commons.digester3.rulesbinder.ParamTypeBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#setNext(String)},
 * {@link LinkedRuleBuilderImpl#setRoot(String)} or {@link LinkedRuleBuilderImpl#setTop(String)}.
 */
abstract class AbstractParamTypeBuilder<R extends Rule>
        extends AbstractBackToLinkedRuleBuilder<R>
        implements ParamTypeBuilder<R> {

    private final String methodName;

    private boolean useExactMatch = false;

    private String paramType;

    public AbstractParamTypeBuilder(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder,
            String methodName) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
        this.methodName = methodName;
    }

    /**
     * {@inheritDoc}
     */
    public final ParamTypeBuilder<R> withParameterType(Class<?> paramType) {
        if (paramType == null) {
            this.reportError(String.format(".%s.withParameterType(Class<?>)", this.methodName),
                    "NULL Java type not allowed");
            return this;
        }
        return this.withParameterType(paramType.getName());
    }

    /**
     * {@inheritDoc}
     */
    public final ParamTypeBuilder<R> withParameterType(String paramType) {
        this.paramType = paramType;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public final ParamTypeBuilder<R> useExactMatch(boolean useExactMatch) {
        this.useExactMatch = useExactMatch;
        return this;
    }

    protected final String getMethodName() {
        return this.methodName;
    }

    protected final String getParamType() {
        return this.paramType;
    }

    protected final boolean isUseExactMatch() {
        return this.useExactMatch;
    }

}
