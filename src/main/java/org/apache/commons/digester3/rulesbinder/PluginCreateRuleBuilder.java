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

import org.apache.commons.digester3.plugins.PluginCreateRule;
import org.apache.commons.digester3.plugins.RuleLoader;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#createPlugin()}.
 */
public interface PluginCreateRuleBuilder extends BackToLinkedRuleBuilder<PluginCreateRule> {

    /**
     * 
     *
     * @param <T>
     * @param type The class which any specified plugin <i>must</i> be descended from
     * @return this builder instance
     */
    <T> PluginCreateRuleBuilder ofType(Class<T> type);

    /**
     * 
     *
     * @param <T>
     * @return this builder instance
     */
    <T> PluginCreateRuleBuilder usingDefaultPluginClass(Class<T> type);

    /**
     * 
     *
     * @param <RL>
     * @param ruleLoader
     * @return this builder instance
     */
    <RL extends RuleLoader> PluginCreateRuleBuilder usingRuleLoader(RL ruleLoader);

    /**
     * Sets the xml attribute which the input xml uses to indicate to a
     * PluginCreateRule which class should be instantiated.
     *
     * @param attrName
     * @return
     */
    PluginCreateRuleBuilder setPluginClassAttribute(String attrName);

    /**
     * Sets the xml attribute which the input xml uses to indicate to a
     * PluginCreateRule which class should be instantiated.
     *
     * @param namespaceUri
     * @param attrName
     * @return
     */
    PluginCreateRuleBuilder setPluginClassAttribute(String namespaceUri, String attrName);

    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which plugin declaration is being referenced.
     *
     * @param namespaceUri
     * @param attrName
     * @return
     */
    PluginCreateRuleBuilder setPluginIdAttribute(String attrName);

    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which plugin declaration is being referenced.
     *
     * @param attrName
     * @return
     */
    PluginCreateRuleBuilder setPluginIdAttribute(String namespaceUri, String attrName);

}
