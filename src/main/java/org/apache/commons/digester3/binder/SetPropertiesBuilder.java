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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.SetPropertiesRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#setProperties()}.
 */
public final class SetPropertiesBuilder
    extends AbstractBackToLinkedRuleBuilder<SetPropertiesRule>
{

    private final Map<String, String> aliases = new HashMap<String, String>();

    private boolean ignoreMissingProperty = true;

    SetPropertiesBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder,
                          LinkedRuleBuilder mainBuilder )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
    }

    /**
     * Add an additional attribute name to property name mapping.
     *
     * @param attributeName The attribute to match
     * @param propertyName The java bean property to be assigned the value
     * @return this builder instance
     */
    public SetPropertiesBuilder addAlias( String attributeName, String propertyName )
    {
        if ( attributeName == null )
        {
            reportError( "setProperties().addAlias( String,String )", "empty 'attributeName' not allowed" );
        }
        else
        {
            aliases.put( attributeName, propertyName );
        }
        return this;
    }

    /**
     * Add an attribute name to the ignore list.
     *
     * @param attributeName The attribute to match has to be ignored
     * @return this builder instance
     */
    public SetPropertiesBuilder ignoreAttribute( String attributeName )
    {
        if ( attributeName == null )
        {
            reportError( "setProperties().ignoreAttribute( String )", "empty 'attributeName' not allowed" );
        }
        else
        {
            addAlias( attributeName, null );
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
    public SetPropertiesBuilder ignoreMissingProperty( boolean ignoreMissingProperty )
    {
        this.ignoreMissingProperty = ignoreMissingProperty;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SetPropertiesRule createRule()
    {
        SetPropertiesRule rule = new SetPropertiesRule( aliases );
        rule.setIgnoreMissingProperty( ignoreMissingProperty );
        return rule;
    }

}
