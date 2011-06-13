package org.apache.commons.digester3.binder;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.digester3.plugins.PluginCreateRule;
import org.apache.commons.digester3.plugins.RuleLoader;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#createPlugin()}.
 *
 * @since 3.0
 */
public final class PluginCreateRuleBuilder
    extends AbstractBackToLinkedRuleBuilder<PluginCreateRule>
{

    private final Map<String, String> pluginClassAttributes = new LinkedHashMap<String, String>();

    private final Map<String, String> pluginIdAttributes = new LinkedHashMap<String, String>();

    private Class<?> baseClass;

    private Class<?> dfltPluginClass;

    private RuleLoader dfltPluginRuleLoader;

    PluginCreateRuleBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder,
                                    LinkedRuleBuilder mainBuilder )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
    }

    /**
     * Set the class which any specified plugin <i>must</i> be descended from.
     *
     * @param <T> Any Java type
     * @param type the class which any specified plugin <i>must</i> be descended from
     * @return this builder instance
     */
    public <T> PluginCreateRuleBuilder ofType( Class<T> type )
    {
        if ( type == null )
        {
            reportError( "createPlugin().ofType( Class<?> )", "NULL Java type not allowed" );
            return this;
        }

        this.baseClass = type;

        return this;
    }

    /**
     * Set the class which will be used if the user doesn't specify any plugin-class or plugin-id.
     *
     * @param <T> Any Java type
     * @param type the class which will be used if the user doesn't specify any plugin-class or plugin-id.
     * @return this builder instance
     */
    public <T> PluginCreateRuleBuilder usingDefaultPluginClass( /* @Nullable */Class<T> type )
    {
        this.dfltPluginClass = type;
        return this;
    }

    /**
     * Set RuleLoader instance which knows how to load the custom rules associated with the default plugin.
     *
     * @param <RL> Any {@link RuleLoader} extension.
     * @param ruleLoader the RuleLoader instance which knows how to load the custom rules associated with
     *        the default plugin.
     * @return this builder instance
     */
    public <RL extends RuleLoader> PluginCreateRuleBuilder usingRuleLoader( /* @Nullable */RL ruleLoader )
    {
        this.dfltPluginRuleLoader = ruleLoader;
        return this;
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a
     * PluginCreateRule which class should be instantiated.
     *
     * @param attrName the xml attribute which the input xml uses to indicate to a
     *                 PluginCreateRule which class should be instantiated.
     * @return this builder instance
     */
    public PluginCreateRuleBuilder setPluginClassAttribute( String attrName )
    {
        if ( attrName == null )
        {
            reportError( "createPlugin().setPluginClassAttribute( String )", "NULL attribute name not allowed" );
            return this;
        }

        return this.setPluginClassAttribute( null, attrName );
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a
     * PluginCreateRule which class should be instantiated.
     *
     * @param namespaceUri The attribute NameSpace
     * @param attrName The attribute name
     * @return this builder instance
     */
    public PluginCreateRuleBuilder setPluginClassAttribute( /* @Nullable */String namespaceUri, String attrName )
    {
        if ( attrName == null )
        {
            reportError( "createPlugin().setPluginClassAttribute( String, String )",
                         "NULL attribute name not allowed" );
            return this;
        }

        return addToMap( pluginClassAttributes, namespaceUri, attrName );
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which plugin declaration is being referenced.
     *
     * @param attrName The attribute name
     * @return this builder instance
     */
    public PluginCreateRuleBuilder setPluginIdAttribute( String attrName )
    {
        if ( attrName == null )
        {
            reportError( "createPlugin().setPluginIdAttribute( String )", "NULL attribute name not allowed" );
            return this;
        }

        return setPluginIdAttribute( null, attrName );
    }

    /**
     * Sets the xml attribute which the input xml uses to indicate to a 
     * PluginCreateRule which plugin declaration is being referenced.
     *
     * @param namespaceUri The attribute NameSpace
     * @param attrName The attribute name
     * @return this builder instance
     */
    public PluginCreateRuleBuilder setPluginIdAttribute( /* @Nullable */String namespaceUri, String attrName )
    {
        if ( attrName == null )
        {
            reportError( "createPlugin().setPluginIdAttribute( String, String )", "NULL attribute name not allowed" );
            return this;
        }

        return addToMap( pluginIdAttributes, namespaceUri, attrName );
    }

    /**
     * Private internal method to set values to a {@link Map} instance and return the current builder.
     *
     * @param map The target {@link Map}
     * @param namespaceUri The attribute NameSpace
     * @param attrName The attribute name
     * @return this builder instance
     */
    private PluginCreateRuleBuilder addToMap( Map<String, String> map, String namespaceUri, String attrName )
    {
        map.put( namespaceUri, attrName );
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected PluginCreateRule createRule()
    {
        if ( baseClass == null )
        {
            reportError( "createPlugin()", "'baseClass' has to be specified" );
        }

        PluginCreateRule rule;
        if ( dfltPluginClass != null )
        {
            if ( dfltPluginRuleLoader != null )
            {
                rule = new PluginCreateRule( baseClass, dfltPluginClass, dfltPluginRuleLoader );
            }
            else
            {
                rule = new PluginCreateRule( baseClass, dfltPluginClass );
            }
        }
        else
        {
            rule = new PluginCreateRule( baseClass );
        }

        for ( Entry<String, String> entry : pluginClassAttributes.entrySet() )
        {
            rule.setPluginClassAttribute( entry.getKey(), entry.getValue() );
        }

        for ( Entry<String, String> entry : pluginIdAttributes.entrySet() )
        {
            rule.setPluginIdAttribute( entry.getKey(), entry.getValue() );
        }

        return rule;
    }

}
