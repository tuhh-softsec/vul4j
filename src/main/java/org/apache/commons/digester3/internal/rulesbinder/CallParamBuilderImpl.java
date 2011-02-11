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

import org.apache.commons.digester3.CallParamRule;
import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rulesbinder.CallParamBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#callParam(int)}.
 */
final class CallParamBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<CallParamRule>
        implements CallParamBuilder {

    private int paramIndex = 0;

    private int stackIndex = 0;

    private boolean fromStack = false;

    private String attributeName;

    public CallParamBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
    }

    /**
     * Sets the zero-relative parameter number.
     *
     * @param paramIndex The zero-relative parameter number
     * @return this builder instance
     */
    public CallParamBuilderImpl ofIndex(int paramIndex) {
        if (paramIndex < 0) {
            this.reportError("callParam().ofIndex(int)", "negative index argument not allowed");
        }

        this.paramIndex = paramIndex;
        return this;
    }

    /**
     * Sets the attribute from which to save the parameter value.
     *
     * @param attributeName The attribute from which to save the parameter value
     * @return this builder instance
     */
    public CallParamBuilderImpl fromAttribute(/* @Nullable */String attributeName) {
        this.attributeName = attributeName;
        return this;
    }

    /**
     * Flags the parameter to be set from the stack.
     *
     * @param fromStack the parameter flag to be set from the stack
     * @return this builder instance
     */
    public CallParamBuilderImpl fromStack(boolean fromStack) {
        this.fromStack = fromStack;
        return this;
    }

    /**
     * Sets the position of the object from the top of the stack.
     *
     * @param stackIndex The position of the object from the top of the stack
     * @return this builder instance
     */
    public CallParamBuilderImpl withStackIndex(int stackIndex) {
        this.stackIndex = stackIndex;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CallParamRule createRule() {
        return new CallParamRule(this.paramIndex, this.fromStack, this.stackIndex, this.attributeName);
    }

}
