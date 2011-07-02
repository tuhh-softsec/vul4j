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
 * <p>
 * Rule implements sets a bean property on the top object to the body text.
 * </p>
 * <p>
 * The property set:
 * </p>
 * <ul>
 * <li>can be specified when the rule is created</li>
 * <li>or can match the current element when the rule is called.</li>
 * </ul>
 * <p>
 * Using the second method and the {@link ExtendedBaseRules} child match pattern, all the child elements can be
 * automatically mapped to properties on the parent object.
 * </p>
 */
public class BeanPropertySetterRule
    extends Rule
{

    // ----------------------------------------------------------- Constructors

    /**
     * <p>
     * Construct rule that sets the given property from the body text.
     * </p>
     * 
     * @param propertyName name of property to set
     */
    public BeanPropertySetterRule( String propertyName )
    {
        this.propertyName = propertyName;
    }

    /**
     * <p>
     * Construct rule that automatically sets a property from the body text.
     * <p>
     * This construct creates a rule that sets the property on the top object named the same as the current element.
     */
    public BeanPropertySetterRule()
    {
        this( null );
    }

    // ----------------------------------------------------- Instance Variables

    /**
     * Set this property on the top object.
     */
    private String propertyName;

    /**
     * Extract the property name from attribute
     */
    private String propertyNameFromAttribute;

    /**
     * The body text used to set the property.
     */
    private String bodyText = null;

    // --------------------------------------------------------- Public Methods

    /**
     * Returns the property name associated to this setter rule.
     *
     * @return The property name associated to this setter rule
     */
    public String getPropertyName()
    {
        return propertyName;
    }

    /**
     * Sets the attribute name from which the property name has to be extracted.
     *
     * @param propertyNameFromAttribute the attribute name from which the property name has to be extracted.
     * @since 3.0
     */
    public void setPropertyNameFromAttribute( String propertyNameFromAttribute )
    {
        this.propertyNameFromAttribute = propertyNameFromAttribute;
    }

    /**
     * Returns the body text used to set the property.
     *
     * @return The body text used to set the property
     */
    protected String getBodyText()
    {
        return bodyText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        if ( propertyNameFromAttribute != null )
        {
            propertyName = attributes.getValue( propertyNameFromAttribute );

            getDigester().getLogger().warn( format( "[BeanPropertySetterRule]{%s} Attribute '%s' not found in matching element '%s'",
                                                    getDigester().getMatch(), propertyNameFromAttribute, name ) );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void body( String namespace, String name, String text )
        throws Exception
    {
        // log some debugging information
        if ( getDigester().getLogger().isDebugEnabled() )
        {
            getDigester().getLogger().debug( format( "[BeanPropertySetterRule]{%s} Called with text '%s'",
                                                     getDigester().getMatch(),
                                                     text ) );
        }

        bodyText = text.trim();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void end( String namespace, String name )
        throws Exception
    {
        String property = propertyName;

        if ( property == null )
        {
            // If we don't have a specific property name,
            // use the element name.
            property = name;
        }

        // Get a reference to the top object
        Object top = getDigester().peek();

        // log some debugging information
        if ( getDigester().getLogger().isDebugEnabled() )
        {
            getDigester().getLogger().debug( format( "[BeanPropertySetterRule]{%s} Set %s property %s with text %s",
                                                     getDigester().getMatch(),
                                                     top.getClass().getName(),
                                                     property,
                                                     bodyText ) );
        }

        // Force an exception if the property does not exist
        // (BeanUtils.setProperty() silently returns in this case)
        if ( top instanceof DynaBean )
        {
            DynaProperty desc = ( (DynaBean) top ).getDynaClass().getDynaProperty( property );
            if ( desc == null )
            {
                throw new NoSuchMethodException( "Bean has no property named " + property );
            }
        }
        else
        /* this is a standard JavaBean */
        {
            PropertyDescriptor desc = getPropertyDescriptor( top, property );
            if ( desc == null )
            {
                throw new NoSuchMethodException( "Bean has no property named " + property );
            }
        }

        // Set the property (with conversion as necessary)
        setProperty( top, property, bodyText );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void finish()
        throws Exception
    {
        bodyText = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return format( "BeanPropertySetterRule[propertyName=%s]", propertyName );
    }

}
