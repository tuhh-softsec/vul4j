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

import org.apache.commons.digester3.rule.CallParamRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#callParam(int)}.
 */
public interface CallParamBuilder extends BackToLinkedRuleBuilder<CallParamRule> {

    /**
     * Sets the zero-relative parameter number.
     *
     * @param paramIndex The zero-relative parameter number
     * @return this builder instance
     */
    CallParamBuilder ofIndex(int paramIndex);

    /**
     * Sets the attribute from which to save the parameter value.
     *
     * @param attributeName The attribute from which to save the parameter value
     * @return this builder instance
     */
    CallParamBuilder fromAttribute(String attributeName);

    /**
     * Flags the parameter to be set from the stack.
     *
     * @param fromStack the parameter flag to be set from the stack
     * @return this builder instance
     */
    CallParamBuilder fromStack(boolean fromStack);

    /**
     * Sets the position of the object from the top of the stack.
     *
     * @param stackIndex The position of the object from the top of the stack
     * @return this builder instance
     */
    CallParamBuilder withStackIndex(int stackIndex);

}
