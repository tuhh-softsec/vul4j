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

import org.apache.commons.digester3.SetNestedPropertiesRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#setNestedProperties()}.
 *
 * @since 3.0
 */
public final class NestedPropertiesBuilder
    extends AbstractBackToLinkedRuleBuilder<SetNestedPropertiesRule>
{

    private final Map<String, String> elementNames = new HashMap<String, String>();

    private boolean trimData = true;

    private boolean allowUnknownChildElements = false;

    NestedPropertiesBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder,
                                    LinkedRuleBuilder mainBuilder )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
    }

    /**
     * Allows ignore a matching element.
     *
     * @param elementName The child xml element to be ignored
     * @return this builder instance
     */
    public NestedPropertiesBuilder ignoreElement( String elementName )
    {
        if ( elementName == null )
        {
            reportError( "setNestedProperties().ignoreElement( String )", "empty 'elementName' not allowed" );
        }
        else
        {
            addAlias( elementName, null );
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
    public NestedPropertiesBuilder addAlias( String elementName, String propertyName )
    {
        if ( elementName == null )
        {
            reportError( "setNestedProperties().addAlias( String,String )", "empty 'elementName' not allowed" );
        }
        else
        {
            elementNames.put( elementName, propertyName );
        }
        return this;
    }

    /**
     * When set to true, any text within child elements will have leading
     * and trailing whitespace removed before assignment to the target
     * object.
     *
     * @param trimData Flag to set any text within child elements will have leading
     *                 and trailing whitespace removed
     * @return this builder instance
     */
    public NestedPropertiesBuilder trimData( boolean trimData )
    {
        this.trimData = trimData;
        return this;
    }

    /**
     * Determines whether an error is reported when a nested element is encountered for which there is no corresponding
     * property-setter method.
     *
     * @param allowUnknownChildElements flag to ignore any child element for which there is no corresponding
     *        object property
     * @return this builder instance
     */
    public NestedPropertiesBuilder allowUnknownChildElements( boolean allowUnknownChildElements )
    {
        this.allowUnknownChildElements = allowUnknownChildElements;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected SetNestedPropertiesRule createRule()
    {
        SetNestedPropertiesRule rule = new SetNestedPropertiesRule( elementNames );
        rule.setTrimData( trimData );
        rule.setAllowUnknownChildElements( allowUnknownChildElements );
        return rule;
    }

}
