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

import java.util.StringTokenizer;

import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rulesbinder.CallMethodBuilder;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;
import org.xml.sax.Attributes;

/**
 * 
 */
final class CallMethodRule extends AbstractXmlRule {

    public CallMethodRule(RulesBinder targetRulesBinder, PatternStack patternStack) {
        super(targetRulesBinder, patternStack);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void bindRule(LinkedRuleBuilder linkedRuleBuilder, Attributes attributes) throws Exception {
        CallMethodBuilder builder = linkedRuleBuilder.callMethod(attributes.getValue("methodname"));

        // Select which element is to be the target. Default to zero,
        // ie the top object on the stack.
        int targetOffset = 0;
        String targetOffsetStr = attributes.getValue("targetoffset");
        if (targetOffsetStr != null) {
            targetOffset = Integer.parseInt(targetOffsetStr);
            builder.withTargetOffset(targetOffset);
        }

        if ("true".equalsIgnoreCase(attributes.getValue("usingElementBodyAsArgument"))) {
            builder.usingElementBodyAsArgument();
            return; // this takes advantage
        }

        builder.useExactMatch("true".equalsIgnoreCase(attributes.getValue("useExactMatch")));

        String paramCountStr = attributes.getValue("paramcount");
        if (paramCountStr != null) {
            int paramCount = Integer.parseInt(attributes.getValue("paramcount"));

            builder.withParamCount(paramCount);
        }

        String paramTypesStr = attributes.getValue("paramtypes");
        if (paramTypesStr != null && paramTypesStr.length() > 0) {
            StringTokenizer tokens = new StringTokenizer(paramTypesStr, " \t\n\r,");
            String[] paramTypeNames = new String[tokens.countTokens()];
            int counter = 0;
            while (tokens.hasMoreTokens()) {
                paramTypeNames[counter++] = tokens.nextToken();
            }
            builder.withParamTypes(paramTypeNames);
        }
    }

}
