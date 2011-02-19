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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import org.apache.commons.digester3.RulesBinder;
import org.apache.commons.digester3.plugins.PluginCreateRule;
import org.apache.commons.digester3.plugins.RuleLoader;
import org.apache.commons.digester3.rulesbinder.LinkedRuleBuilder;
import org.apache.commons.digester3.rulesbinder.PluginCreateRuleBuilder;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#createPlugin()}.
 */
final class PluginCreateRuleBuilderImpl
        extends AbstractBackToLinkedRuleBuilder<PluginCreateRule>
        implements PluginCreateRuleBuilder {

    private final Collection<Entry<String, String>> pluginClassAttributes = new ArrayList<Entry<String, String>>();

    private final Collection<Entry<String, String>> pluginIdAttributes = new ArrayList<Entry<String, String>>();

    private Class<?> baseClass;

    private Class<?> dfltPluginClass;

    private RuleLoader dfltPluginRuleLoader;

    public PluginCreateRuleBuilderImpl(String keyPattern,
            String namespaceURI,
            RulesBinder mainBinder,
            LinkedRuleBuilderImpl mainBuilder) {
        super(keyPattern, namespaceURI, mainBinder, mainBuilder);
    }

    /**
     * {@inheritDoc}
     */
    public <T> PluginCreateRuleBuilder ofType(Class<T> type) {
        if (type == null) {
            this.reportError("createPlugin().ofType(Class<?>)", "NULL Java type not allowed");
            return this;
        }

        this.baseClass = type;

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public <T> PluginCreateRuleBuilder usingDefaultPluginClass(/* @Nullable */ Class<T> type) {
        this.dfltPluginClass = type;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public <RL extends RuleLoader> PluginCreateRuleBuilder usingRuleLoader(/* @Nullable */ RL ruleLoader) {
        this.dfltPluginRuleLoader = ruleLoader;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public PluginCreateRuleBuilder setPluginClassAttribute(String attrName) {
        if (attrName == null) {
            this.reportError("createPlugin().setPluginClassAttribute(String)", "NULL attribute name not allowed");
            return this;
        }

        return this.setPluginClassAttribute(null, attrName);
    }

    /**
     * {@inheritDoc}
     */
    public PluginCreateRuleBuilder setPluginClassAttribute(/* @Nullable */ String namespaceUri, String attrName) {
        if (attrName == null) {
            this.reportError("createPlugin().setPluginClassAttribute(String,String)", "NULL attribute name not allowed");
            return this;
        }

        return this.addToCollection(this.pluginClassAttributes, namespaceUri, attrName);
    }

    /**
     * {@inheritDoc}
     */
    public PluginCreateRuleBuilder setPluginIdAttribute(String attrName) {
        if (attrName == null) {
            this.reportError("createPlugin().setPluginIdAttribute(String)", "NULL attribute name not allowed");
            return this;
        }

        return this.setPluginIdAttribute(null, attrName);
    }

    /**
     * {@inheritDoc}
     */
    public PluginCreateRuleBuilder setPluginIdAttribute(/* @Nullable */ String namespaceUri, String attrName) {
        if (attrName == null) {
            this.reportError("createPlugin().setPluginIdAttribute(String,String)", "NULL attribute name not allowed");
            return this;
        }

        return this.addToCollection(this.pluginIdAttributes, namespaceUri, attrName);
    }

    public PluginCreateRuleBuilder addToCollection(Collection<Entry<String, String>> collection,
            final String namespaceUri,
            final String attrName) {
        collection.add(new Entry<String, String>() {

            public String setValue(String value) {
                // not needed
                return null;
            }

            public String getValue() {
                return attrName;
            }

            public String getKey() {
                return namespaceUri;
            }

        });
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PluginCreateRule createRule() {
        if (this.baseClass == null) {
            this.reportError("createPlugin()",
                    "'baseClass' has to be specified");
        }

        PluginCreateRule rule;
        if (this.dfltPluginClass != null) {
            if (this.dfltPluginRuleLoader != null) {
                rule = new PluginCreateRule(this.baseClass, this.dfltPluginClass, this.dfltPluginRuleLoader);
            } else {
                rule = new PluginCreateRule(this.baseClass, this.dfltPluginClass);
            }
        } else {
            rule = new PluginCreateRule(this.baseClass);
        }

        for (Entry<String, String> entry : this.pluginClassAttributes) {
            rule.setPluginClassAttribute(entry.getKey(), entry.getValue());
        }

        for (Entry<String, String> entry : this.pluginIdAttributes) {
            rule.setPluginIdAttribute(entry.getKey(), entry.getValue());
        }

        return rule;
    }

}
