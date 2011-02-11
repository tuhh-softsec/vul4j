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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.rule.RulesBinder;
import org.apache.commons.digester3.rule.SetNestedPropertiesRule;
import org.apache.commons.digester3.rulesbinder.NestedPropertiesBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#setNestedProperties()}.
 */
final class NestedPropertiesBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<SetNestedPropertiesRule>
        implements NestedPropertiesBuilder {

    private final Map<String, String> elementNames = new HashMap<String, String>();

    private boolean trimData = true;

    private boolean allowUnknownChildElements = false;

    public NestedPropertiesBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
    }

    /**
     * Allows ignore a matching element.
     *
     * @param elementName The child xml element to be ignored
     * @return this builder instance
     */
    public NestedPropertiesBuilderImpl ignoreElement(String elementName) {
        if (elementName == null) {
            this.reportError("setNestedProperties().ignoreElement(String)", "empty 'elementName' not allowed");
        } else {
            this.elementNames.put(elementName, null);
        }
        return this;
    }

    /**
     * Allows element2property mapping to be overridden.
     *
     * @param elementName The child xml element to match
     * @param propertyName The java bean property to be assigned the value
     * @return this builder instance
     */
    public NestedPropertiesBuilderImpl addAlias(String elementName, String propertyName) {
        if (elementName == null) {
            this.reportError("setNestedProperties().addAlias(String,String)", "empty 'elementName' not allowed");
        } else {
            if (propertyName == null) {
                this.reportError("setNestedProperties().addAlias(String,String)", "empty 'propertyName' not allowed");
            } else {
                this.elementNames.put(elementName, propertyName);
            }
        }
        return this;
    }

    /**
     * When set to true, any text within child elements will have leading
     * and trailing whitespace removed before assignment to the target
     * object.
     *
     * @param trimData
     * @return this builder instance
     */
    public NestedPropertiesBuilderImpl trimData(boolean trimData) {
        this.trimData = trimData;
        return this;
    }

    /**
     * 
     *
     * @param allowUnknownChildElements
     * @return
     */
    public NestedPropertiesBuilderImpl allowUnknownChildElements(boolean allowUnknownChildElements) {
        this.allowUnknownChildElements = allowUnknownChildElements;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SetNestedPropertiesRule createRule() {
        return new SetNestedPropertiesRule(this.elementNames, this.trimData, this.allowUnknownChildElements);
    }

}
