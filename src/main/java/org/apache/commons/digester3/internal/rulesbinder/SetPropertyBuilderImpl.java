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
import org.apache.commons.digester3.SetPropertyRule;
import org.apache.commons.digester3.rulesbinder.SetPropertyBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#setProperty(String)}.
 */
final class SetPropertyBuilderImpl extends AbstractBackToLinkedRuleBuilder<SetPropertyRule> implements SetPropertyBuilder {

    private final String attributePropertyName;

    private String valueAttributeName;

    public SetPropertyBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder,
            String attributePropertyName) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
        this.attributePropertyName = attributePropertyName;
    }

    /**
     * {@inheritDoc}
     */
    public SetPropertyBuilder extractingValueFromAttribute(String valueAttributeName) {
        if (attributePropertyName == null || attributePropertyName.length() == 0) {
            this.reportError(
                    String.format("setProperty(\"%s\").extractingValueFromAttribute(String)}", this.attributePropertyName),
                    "empty 'valueAttributeName' not allowed");
        }

        this.valueAttributeName = valueAttributeName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SetPropertyRule createRule() {
        return new SetPropertyRule(this.attributePropertyName, this.valueAttributeName);
    }

}
