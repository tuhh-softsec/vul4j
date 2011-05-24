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

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;
import org.apache.commons.digester3.rulesbinder.ParamTypeBuilder;
import org.xml.sax.Attributes;

/**
 * 
 */
abstract class AbstractXmlMethodRule extends AbstractXmlRule {

    public AbstractXmlMethodRule(RulesBinder targetRulesBinder, PatternStack patternStack) {
        super(targetRulesBinder, patternStack);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected final void bindRule(LinkedRuleBuilder linkedRuleBuilder, Attributes attributes) throws Exception {
        String methodName = attributes.getValue("methodname");
        String paramType = attributes.getValue("paramtype");

        ParamTypeBuilder<? extends Rule> builder = this.createBuilder(linkedRuleBuilder, methodName);
        if (paramType != null && paramType.length() > 0) {
            builder.withParameterType(paramType);
        }
    }

    /**
     * 
     * @param methodName
     * @return
     */
    protected abstract ParamTypeBuilder<? extends Rule> createBuilder(LinkedRuleBuilder linkedRuleBuilder, String methodName);

}
