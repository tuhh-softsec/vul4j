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
import org.apache.commons.digester3.rule.PathCallParamRule;
import org.apache.commons.digester3.rulesbinder.PathCallParamBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#callParam(int)}.
 */
final class PathCallParamBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<PathCallParamRule>
        implements PathCallParamBuilder {

    private int paramIndex = 0;

    public PathCallParamBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
    }

    /**
     * {@inheritDoc}
     */
    public PathCallParamBuilderImpl ofIndex(int paramIndex) {
        if (paramIndex < 0) {
            this.reportError("callParamPath().ofIndex(int)", "negative index argument not allowed");
        }

        this.paramIndex = paramIndex;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PathCallParamRule createRule() {
        return new PathCallParamRule(this.paramIndex);
    }

}
