package org.apache.commons.digester3;

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

import static java.lang.String.format;
import static org.apache.commons.beanutils.BeanUtils.setProperty;
import static org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptor;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.xml.sax.Attributes;

/**
 * Rule implementation that sets an individual property on the object at the top of the stack, based on attributes with
 * specified names.
 */
public class SetPropertyRule
    extends Rule
{

    // ----------------------------------------------------------- Constructors

    /**
     * Construct a "set property" rule with the specified name and value attributes.
     * 
     * @param name Name of the attribute that will contain the name of the property to be set
     * @param value Name of the attribute that will contain the value to which the property should be set
     */
    public SetPropertyRule( String name, String value )
    {
        this.name = name;
        this.value = value;
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * The attribute that will contain the property name.
     */
    protected String name = null;

    /**
     * The attribute that will contain the property value.
     */
    protected String value = null;

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        if ( attributes.getLength() == 0 )
        {
            return;
        }

        // Identify the actual property name and value to be used
        String actualName = null;
        String actualValue = null;
        for ( int i = 0; i < attributes.getLength(); i++ )
        {
            String attributeName = attributes.getLocalName( i );
            if ( "".equals( attributeName ) )
            {
                attributeName = attributes.getQName( i );
            }
            String value = attributes.getValue( i );
            if ( attributeName.equals( this.name ) )
            {
                actualName = value;
            }
            else if ( attributeName.equals( this.value ) )
            {
                actualValue = value;
            }
        }

        // Get a reference to the top object
        Object top = getDigester().peek();

        // Log some debugging information
        if ( getDigester().getLogger().isDebugEnabled() )
        {
            getDigester().getLogger().debug( format( "[SetPropertiesRule]{%s} Set %s property %s to %s",
                                                     getDigester().getMatch(),
                                                     top.getClass().getName(),
                                                     actualName,
                                                     actualValue ) );
        }

        // Force an exception if the property does not exist
        // (BeanUtils.setProperty() silently returns in this case)
        //
        // This code should probably use PropertyUtils.isWriteable(),
        // like SetPropertiesRule does.
        if ( top instanceof DynaBean )
        {
            DynaProperty desc = ( (DynaBean) top ).getDynaClass().getDynaProperty( actualName );
            if ( desc == null )
            {
                throw new NoSuchMethodException( "Bean has no property named " + actualName );
            }
        }
        else
        /* this is a standard JavaBean */
        {
            PropertyDescriptor desc = getPropertyDescriptor( top, actualName );
            if ( desc == null )
            {
                throw new NoSuchMethodException( "Bean has no property named " + actualName );
            }
        }

        // Set the property (with conversion as necessary)
        setProperty( top, actualName, actualValue );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "SetPropertyRule[name=%s, value=%s]", name, value );
    }

}
