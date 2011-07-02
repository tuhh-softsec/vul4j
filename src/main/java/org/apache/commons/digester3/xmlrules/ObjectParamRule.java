package org.apache.commons.digester3.xmlrules;

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

import static org.apache.commons.beanutils.ConvertUtils.convert;

import org.apache.commons.digester3.binder.LinkedRuleBuilder;
import org.apache.commons.digester3.binder.ObjectParamBuilder;
import org.apache.commons.digester3.binder.RulesBinder;
import org.xml.sax.Attributes;

/**
 * 
 */
final class ObjectParamRule
    extends AbstractXmlRule
{

    public ObjectParamRule( RulesBinder targetRulesBinder, PatternStack patternStack )
    {
        super( targetRulesBinder, patternStack );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void bindRule( LinkedRuleBuilder linkedRuleBuilder, Attributes attributes )
        throws Exception
    {
        // create callparamrule
        int paramIndex = Integer.parseInt( attributes.getValue( "paramnumber" ) );

        String attributeName = attributes.getValue( "attrname" );
        String type = attributes.getValue( "type" );
        String value = attributes.getValue( "value" );

        // type name is requried
        if ( type == null )
        {
            throw new RuntimeException( "Attribute 'type' is required." );
        }

        // create object instance
        Object param = null;
        Class<?> clazz = Class.forName( type );
        if ( value == null )
        {
            param = clazz.newInstance();
        }
        else
        {
            param = convert( value, clazz );
        }

        ObjectParamBuilder<?> builder = linkedRuleBuilder.objectParam( param ).ofIndex( paramIndex );
        if ( attributeName != null )
        {
            builder.matchingAttribute( attributeName );
        }
    }

}
