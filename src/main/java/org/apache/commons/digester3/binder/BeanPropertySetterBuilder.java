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

import org.apache.commons.digester3.BeanPropertySetterRule;

/**
 * Builder chained when invoking {@link LinkedRuleBuilder#setBeanProperty()}.
 */
public final class BeanPropertySetterBuilder
    extends AbstractBackToLinkedRuleBuilder<BeanPropertySetterRule>
{

    private String propertyName;

    private String attribute;

    BeanPropertySetterBuilder( String keyPattern, String namespaceURI, RulesBinder mainBinder,
                               LinkedRuleBuilder mainBuilder )
    {
        super( keyPattern, namespaceURI, mainBinder, mainBuilder );
    }

    /**
     * Sets the name of property to set.
     *
     * @param propertyName The name of property to set
     * @return this builder instance
     */
    public BeanPropertySetterBuilder withName( /* @Nullable */String propertyName )
    {
        this.propertyName = propertyName;
        return this;
    }

    /**
     * Sets the attribute name from which the property name has to be extracted.
     *
     * @param attribute The attribute name from which extracting the name of property to set
     * @return this builder instance
     */
    public BeanPropertySetterBuilder extractPropertyNameFromAttribute( String attribute )
    {
        if ( attribute == null )
        {
            reportError( "setBeanProperty().extractPropertyNameFromAttribute( String )",
                         "Attribute name can not be null" );
        }
        this.attribute = attribute;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanPropertySetterRule createRule()
    {
        BeanPropertySetterRule rule = new BeanPropertySetterRule( propertyName );
        rule.setPropertyNameFromAttribute( attribute );
        return rule;
    }

}
