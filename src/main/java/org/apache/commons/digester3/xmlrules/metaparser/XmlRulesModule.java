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
package org.apache.commons.digester3.xmlrules.metaparser;

import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.RulesModule;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;

/**
 * 
 */
public final class XmlRulesModule implements RulesModule {

    private final RulesBinder targetRulesBinder;

    private WithMemoryRulesBinder memoryRulesBinder;

    public XmlRulesModule(final RulesBinder targetRulesBinder) {
        this.targetRulesBinder = targetRulesBinder;
    }

    /**
     * {@inheritDoc}
     */
    public void configure(RulesBinder rulesBinder) {
        if (rulesBinder instanceof WithMemoryRulesBinder) {
            this.memoryRulesBinder = (WithMemoryRulesBinder) rulesBinder;
        } else {
            this.memoryRulesBinder = new WithMemoryRulesBinder(rulesBinder);
        }

        PatternStack patternStack = this.memoryRulesBinder.getPatternStack();

        try {
            forPattern("*/pattern")
                .addRule(new PatternRule(patternStack));
            forPattern("*/include")
                .addRule(new IncludeRule(this.memoryRulesBinder, this.targetRulesBinder));

            forPattern("*/bean-property-setter-rule")
                .addRule(new BeanPropertySetterRule(this.targetRulesBinder, patternStack));

            forPattern("*/call-method-rule")
                .addRule(new CallMethodRule(this.targetRulesBinder, patternStack));
            forPattern("*/call-param-rule")
                .addRule(new CallParamRule(this.targetRulesBinder, patternStack));
            forPattern("*/object-param-rule")
                .addRule(new ObjectParamRule(this.targetRulesBinder, patternStack));

            forPattern("*/factory-create-rule")
                .addRule(new FactoryCreateRule(this.targetRulesBinder, patternStack));
            forPattern("*/object-create-rule")
                .addRule(new ObjectCreateRule(this.targetRulesBinder, patternStack));

            forPattern("*/set-properties-rule")
                .addRule(new SetPropertiesRule(this.targetRulesBinder, patternStack));
            forPattern("*/set-properties-rule/alias")
                .addRule(new SetPropertiesAliasRule(this.targetRulesBinder, patternStack));

            forPattern("*/set-property-rule")
                .addRule(new SetPropertyRule(this.targetRulesBinder, patternStack));

            forPattern("*/set-nested-properties-rule")
                .addRule(new SetNestedPropertiesRule(this.targetRulesBinder, patternStack));
            forPattern("*/set-nested-properties-rule/alias")
                .addRule(new SetNestedPropertiesAliasRule(this.targetRulesBinder, patternStack));

            forPattern("*/set-top-rule")
                .addRule(new SetTopRule(this.targetRulesBinder, patternStack));
            forPattern("*/set-next-rule")
                .addRule(new SetNextRule(this.targetRulesBinder, patternStack));
            forPattern("*/set-root-rule")
                .addRule(new SetRootRule(this.targetRulesBinder, patternStack));
        } finally {
            this.memoryRulesBinder = null;
        }
    }

    /**
     * 
     * @param pattern
     * @return
     */
    protected LinkedRuleBuilder forPattern(String pattern) {
        return this.memoryRulesBinder.forPattern(pattern);
    }

}
