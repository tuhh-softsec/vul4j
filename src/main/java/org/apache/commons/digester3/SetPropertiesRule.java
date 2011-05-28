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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.xml.sax.Attributes;

/**
 * <p>
 * Rule implementation that sets properties on the object at the top of the stack, based on attributes with
 * corresponding names.
 * </p>
 * <p>
 * This rule supports custom mapping of attribute names to property names. The default mapping for particular attributes
 * can be overridden by using {@link #SetPropertiesRule(String[] attributeNames, String[] propertyNames)}. This allows
 * attributes to be mapped to properties with different names. Certain attributes can also be marked to be ignored.
 * </p>
 */
public class SetPropertiesRule
    extends Rule
{

    // ----------------------------------------------------------- Constructors

    /**
     * Base constructor.
     */
    public SetPropertiesRule()
    {
        // nothing to set up
    }

    /**
     * <p>
     * Convenience constructor overrides the mapping for just one property.
     * </p>
     * <p>
     * For details about how this works, see {@link #SetPropertiesRule(String[] attributeNames, String[] propertyNames)}
     * .
     * </p>
     * 
     * @param attributeName map this attribute
     * @param propertyName to a property with this name
     */
    public SetPropertiesRule( String attributeName, String propertyName )
    {
        aliases.put( attributeName, propertyName );
    }

    /**
     * <p>
     * Constructor allows attribute->property mapping to be overriden.
     * </p>
     * <p>
     * Two arrays are passed in. One contains the attribute names and the other the property names. The attribute name /
     * property name pairs are match by position In order words, the first string in the attribute name list matches to
     * the first string in the property name list and so on.
     * </p>
     * <p>
     * If a property name is null or the attribute name has no matching property name, then this indicates that the
     * attibute should be ignored.
     * </p>
     * <h5>Example One</h5>
     * <p>
     * The following constructs a rule that maps the <code>alt-city</code> attribute to the <code>city</code> property
     * and the <code>alt-state</code> to the <code>state</code> property. All other attributes are mapped as usual using
     * exact name matching. <code><pre>
     *      SetPropertiesRule(
     *                new String[] {"alt-city", "alt-state"}, 
     *                new String[] {"city", "state"});
     * </pre></code>
     * <h5>Example Two</h5>
     * <p>
     * The following constructs a rule that maps the <code>class</code> attribute to the <code>className</code>
     * property. The attribute <code>ignore-me</code> is not mapped. All other attributes are mapped as usual using
     * exact name matching. <code><pre>
     *      SetPropertiesRule(
     *                new String[] {"class", "ignore-me"}, 
     *                new String[] {"className"});
     * </pre></code>
     * 
     * @param attributeNames names of attributes to map
     * @param propertyNames names of properties mapped to
     */
    public SetPropertiesRule( String[] attributeNames, String[] propertyNames )
    {
        for ( int i = 0, size = attributeNames.length; i < size; i++ )
        {
            String propName = null;
            if ( i < propertyNames.length )
            {
                propName = propertyNames[i];
            }

            aliases.put( attributeNames[i], propName );
        }
    }

    /**
     * Constructor allows attribute->property mapping to be overriden.
     *
     * @param aliases attribute->property mapping
     * @since 3.0
     */
    public SetPropertiesRule( Map<String, String> aliases )
    {
        if ( aliases != null && !aliases.isEmpty() )
        {
            this.aliases.putAll( aliases );
        }
    }

    // ----------------------------------------------------- Instance Variables

    private final Map<String, String> aliases = new HashMap<String, String>();

    /**
     * Used to determine whether the parsing should fail if an property specified in the XML is missing from the bean.
     * Default is true for backward compatibility.
     */
    private boolean ignoreMissingProperty = true;

    // --------------------------------------------------------- Public Methods

    /**
     * {@inheritDoc}
     */
    @Override
    public void begin( String namespace, String name, Attributes attributes )
        throws Exception
    {
        // Build a set of attribute names and corresponding values
        Map<String, String> values = new HashMap<String, String>();

        for ( int i = 0; i < attributes.getLength(); i++ )
        {
            String attributeName = attributes.getLocalName( i );
            if ( "".equals( attributeName ) )
            {
                attributeName = attributes.getQName( i );
            }
            String value = attributes.getValue( i );

            // alias lookup has complexity O(1)
            if ( aliases.containsKey( attributeName ) )
            {
                attributeName = aliases.get( attributeName );
            }

            if ( getDigester().getLogger().isDebugEnabled() )
            {
                getDigester().getLogger().debug( "[SetPropertiesRule]{"
                                                 + getDigester().getMatch()
                                                 + "} Setting property '"
                                                 + attributeName
                                                 + "' to '"
                                                 + value
                                                 + "'" );
            }

            if ( ( !ignoreMissingProperty ) && ( attributeName != null ) )
            {
                // The BeanUtils.populate method silently ignores items in
                // the map (ie xml entities) which have no corresponding
                // setter method, so here we check whether each xml attribute
                // does have a corresponding property before calling the
                // BeanUtils.populate method.
                //
                // Yes having the test and set as separate steps is ugly and
                // inefficient. But BeanUtils.populate doesn't provide the
                // functionality we need here, and changing the algorithm which
                // determines the appropriate setter method to invoke is
                // considered too risky.
                //
                // Using two different classes (PropertyUtils vs BeanUtils) to
                // do the test and the set is also ugly; the codepaths
                // are different which could potentially lead to trouble.
                // However the BeanUtils/ProperyUtils code has been carefully
                // compared and the PropertyUtils functionality does appear
                // compatible so we'll accept the risk here.

                Object top = getDigester().peek();
                boolean test = PropertyUtils.isWriteable( top, attributeName );
                if ( !test )
                {
                    throw new NoSuchMethodException( "Property " + attributeName + " can't be set" );
                }
            }

            if ( attributeName != null )
            {
                values.put( attributeName, value );
            }
        }

        // Populate the corresponding properties of the top object
        Object top = getDigester().peek();
        if ( getDigester().getLogger().isDebugEnabled() )
        {
            if ( top != null )
            {
                getDigester().getLogger().debug( "[SetPropertiesRule]{" + getDigester().getMatch() + "} Set "
                                                     + top.getClass().getName() + " properties" );
            }
            else
            {
                getDigester().getLogger().debug( "[SetPropertiesRule]{" + getDigester().getMatch()
                                                     + "} Set NULL properties" );
            }
        }
        BeanUtils.populate( top, values );
    }

    /**
     * Add an additional attribute name to property name mapping. This is intended to be used from the xml rules.
     *
     * @param attributeName the attribute name has to be mapped
     * @param propertyName the target property name
     */
    public void addAlias( String attributeName, String propertyName )
    {
        aliases.put( attributeName, propertyName );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder( "SetPropertiesRule[" );
        sb.append( "]" );
        return ( sb.toString() );
    }

    /**
     * <p>
     * Are attributes found in the xml without matching properties to be ignored?
     * </p>
     * <p>
     * If false, the parsing will interrupt with an <code>NoSuchMethodException</code> if a property specified in the
     * XML is not found. The default is true.
     * </p>
     * 
     * @return true if skipping the unmatched attributes.
     */
    public boolean isIgnoreMissingProperty()
    {
        return this.ignoreMissingProperty;
    }

    /**
     * Sets whether attributes found in the xml without matching properties should be ignored. If set to false, the
     * parsing will throw an <code>NoSuchMethodException</code> if an unmatched attribute is found. This allows to trap
     * misspellings in the XML file.
     * 
     * @param ignoreMissingProperty false to stop the parsing on unmatched attributes.
     */
    public void setIgnoreMissingProperty( boolean ignoreMissingProperty )
    {
        this.ignoreMissingProperty = ignoreMissingProperty;
    }

}
