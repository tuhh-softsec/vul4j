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

import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.rule.SetPropertiesRule;
import org.apache.commons.digester3.rulesbinder.SetPropertiesBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilderImpl#setNestedProperties()}.
 */
final class SetPropertiesBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<SetPropertiesRule>
        implements SetPropertiesBuilder {

    private final Map<String, String> aliases = new HashMap<String, String>();

    private boolean ignoreMissingProperty = true;

    public SetPropertiesBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
    }

    /**
     * Add an attribute name to the ignore list.
     *
     * @param attributeName The attribute to match has to be ignored
     * @return this builder instance
     */
    public SetPropertiesBuilderImpl ignoreAttribute(String attributeName) {
        if (attributeName == null) {
            this.reportError("setProperties().ignoreAttribute(String)}", "empty 'attributeName' not allowed");
        } else {
            this.aliases.put(attributeName, null);
        }
        return this;
    }

    /**
     * Add an additional attribute name to property name mapping.
     *
     * @param attributeName The attribute to match
     * @param propertyName The java bean property to be assigned the value
     * @return this builder instance
     */
    public SetPropertiesBuilderImpl addAlias(String attributeName, String propertyName) {
        if (attributeName == null) {
            this.reportError("setProperties().addAlias(String,String)}", "empty 'attributeName' not allowed");
        } else {
            if (propertyName == null) {
                this.reportError("setProperties().addAlias(String,String)}", "empty 'propertyName' not allowed");
            } else {
                this.aliases.put(attributeName, propertyName);
            }
        }
        return this;
    }

    /**
     * Sets whether attributes found in the XML without matching properties should be ignored.
     * 
     * If set to false, the parsing will throw an {@code NoSuchMethodException}
     * if an unmatched attribute is found.
     * This allows to trap misspellings in the XML file.
     *
     * @param ignoreMissingProperty false to stop the parsing on unmatched attributes
     * @return this builder instance
     */
    public SetPropertiesBuilderImpl ignoreMissingProperty(boolean ignoreMissingProperty) {
        this.ignoreMissingProperty = ignoreMissingProperty;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SetPropertiesRule createRule() {
        return new SetPropertiesRule(this.aliases, this.ignoreMissingProperty);
    }

}
