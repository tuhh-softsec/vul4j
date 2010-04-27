/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.directory.shared.ldap.entry.client;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.directory.shared.ldap.NotImplementedException;
import org.apache.directory.shared.ldap.exception.LdapException;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.entry.DefaultEntryAttribute;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.EntryAttribute;
import org.apache.directory.shared.ldap.entry.Value;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A default implementation of a ServerEntry which should suite most
 * use cases.
 * 
 * This class is final, it should not be extended.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class DefaultClientEntry implements Entry
{
    /** Used for serialization */
    private static final long serialVersionUID = 2L;
    
    /** The logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultClientEntry.class );

    /** The DN for this entry */
    protected DN dn;
    
    /** A map containing all the attributes for this entry */
    protected Map<String, EntryAttribute> attributes = new HashMap<String, EntryAttribute>();
    
    /** A speedup to get the ObjectClass attribute */
    protected static transient AttributeType OBJECT_CLASS_AT;

    /** The SchemaManager */
    protected SchemaManager schemaManager;

    /** The computed hashcode. We don't want to compute it each time the hashcode() method is called */
    private volatile int h;

    
    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------
    /**
     * Creates a new instance of DefaultClientEntry. 
     * <p>
     * This entry <b>must</b> be initialized before being used !
     */
    public DefaultClientEntry()
    {
        dn = DN.EMPTY_DN;
    }


    /**
     * Creates a new instance of DefaultServerEntry, with a 
     * DN. 
     * 
     * @param dn The DN for this serverEntry. Can be null.
     */
    public DefaultClientEntry( DN dn )
    {
        this.dn = dn;
    }


    /**
     * Creates a new instance of DefaultServerEntry, with a 
     * DN and a list of IDs. 
     * 
     * @param dn The DN for this serverEntry. Can be null.
     * @param upIds The list of attributes to create.
     */
    public DefaultClientEntry( DN dn, String... upIds )
    {
        this.dn = dn;

        for ( String upId:upIds )
        {
            // Add a new AttributeType without value
            set( upId );
        }
    }

    
    /**
     * <p>
     * Creates a new instance of DefaultClientEntry, with a 
     * DN and a list of EntryAttributes.
     * </p> 
     * 
     * @param dn The DN for this serverEntry. Can be null
     * @param attributes The list of attributes to create
     */
    public DefaultClientEntry( DN dn, EntryAttribute... attributes )
    {
        this.dn = dn;

        for ( EntryAttribute attribute:attributes )
        {
            if ( attribute == null )
            {
                continue;
            }
            
            // Store a new ClientAttribute
            this.attributes.put( attribute.getId(), attribute );
        }
    }

    
    //-------------------------------------------------------------------------
    // Helper methods
    //-------------------------------------------------------------------------
    private String getId( String upId ) throws IllegalArgumentException
    {
        String id = StringTools.trim( StringTools.toLowerCase( upId ) );
        
        // If empty, throw an error
        if ( ( id == null ) || ( id.length() == 0 ) ) 
        {
            String message = I18n.err( I18n.ERR_04133 );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        return id;
    }

    
    /**
     * Get the UpId if it was null.
     */
    public static String getUpId( String upId, AttributeType attributeType )
    {
        String normUpId = StringTools.trim( upId );

        if ( ( attributeType == null ) )
        {
            if ( StringTools.isEmpty( normUpId ) )
            {
                String message = I18n.err( I18n.ERR_04458 );
                LOG.error( message );
                throw new IllegalArgumentException( message );
            }
        }
        else if ( StringTools.isEmpty( normUpId ) )
        {
            upId = attributeType.getName();
            
            if ( StringTools.isEmpty( upId ) )
            {
                upId = attributeType.getOid();
            }
        }
        
        return upId;
    }

    
    /**
     * Add a new EntryAttribute, with its upId. If the upId is null,
     * default to the AttributeType name.
     * 
     * Updates the AttributeMap.
     */
    protected void createAttribute( String upId, AttributeType attributeType, byte[]... values ) 
    {
        EntryAttribute attribute = new DefaultEntryAttribute( attributeType, values );
        attribute.setUpId( upId, attributeType );
        attributes.put( attributeType.getOid(), attribute );
    }
    
    
    /**
     * Add a new EntryAttribute, with its upId. If the upId is null,
     * default to the AttributeType name.
     * 
     * Updates the AttributeMap.
     */
    protected void createAttribute( String upId, AttributeType attributeType, String... values ) 
    {
        EntryAttribute attribute = new DefaultEntryAttribute( attributeType, values );
        attribute.setUpId( upId, attributeType );
        attributes.put( attributeType.getOid(), attribute );
    }
    
    
    /**
     * Add a new EntryAttribute, with its upId. If the upId is null,
     * default to the AttributeType name.
     * 
     * Updates the AttributeMap.
     */
    protected void createAttribute( String upId, AttributeType attributeType, Value<?>... values ) 
    {
        EntryAttribute attribute = new DefaultEntryAttribute( attributeType, values );
        attribute.setUpId( upId, attributeType );
        attributes.put( attributeType.getOid(), attribute );
    }
    
    
    /**
     * Returns the attributeType from an Attribute ID.
     */
    protected AttributeType getAttributeType( String upId ) throws LdapException
    {
        if ( StringTools.isEmpty( StringTools.trim( upId ) ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        return schemaManager.lookupAttributeTypeRegistry( upId );
    }

    
    //-------------------------------------------------------------------------
    // Entry methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void add( AttributeType attributeType, byte[]... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            String message = I18n.err( I18n.ERR_04460_ATTRIBUTE_TYPE_NULL_NOT_ALLOWED );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        // ObjectClass with binary values are not allowed
        if ( attributeType.equals( OBJECT_CLASS_AT ) )
        {
            String message = I18n.err( I18n.ERR_04461 );
            LOG.error(  message  );
            throw new UnsupportedOperationException( message );
        }

        EntryAttribute attribute = attributes.get( attributeType.getOid() );
        
        if ( attribute != null )
        {
            // This Attribute already exist, we add the values 
            // into it
            attribute.add( values );
        }
        else
        {
            // We have to create a new Attribute and set the values.
            // The upId, which is set to null, will be setup by the 
            // createAttribute method
            createAttribute( null, attributeType, values );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void add( AttributeType attributeType, String... values ) throws LdapException
    {    
        if ( attributeType == null )
        {
            String message = I18n.err( I18n.ERR_04460_ATTRIBUTE_TYPE_NULL_NOT_ALLOWED );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        EntryAttribute attribute = attributes.get( attributeType.getOid() );
        
        if ( attribute != null )
        {
            // This Attribute already exist, we add the values 
            // into it
            attribute.add( values );
        }
        else
        {
            // We have to create a new Attribute and set the values.
            // The upId, which is set to null, will be setup by the 
            // createAttribute method
            createAttribute( null, attributeType, values );
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public void add( AttributeType attributeType, Value<?>... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            String message = I18n.err( I18n.ERR_04460_ATTRIBUTE_TYPE_NULL_NOT_ALLOWED );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        EntryAttribute attribute = attributes.get( attributeType.getOid() );
    
        if ( attribute != null )
        {
            // This Attribute already exist, we add the values 
            // into it
            attribute.add( values );
        }
        else
        {
            // We have to create a new Attribute and set the values.
            // The upId, which is set to null, will be setup by the 
            // createAttribute method
            createAttribute( null, attributeType, values );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void add( String upId, AttributeType attributeType, byte[]... values ) throws LdapException
    {
        // ObjectClass with binary values are not allowed
        if ( attributeType.equals( OBJECT_CLASS_AT ) )
        {
            String message = I18n.err( I18n.ERR_04461 );
            LOG.error(  message  );
            throw new UnsupportedOperationException( message );
        }

        EntryAttribute attribute = (EntryAttribute)attributes.get( attributeType.getOid() );
        
        upId = getUpId( upId, attributeType );
        
        if ( attribute != null )
        {
            // This Attribute already exist, we add the values 
            // into it
            attribute.add( values );
            attribute.setUpId( upId, attributeType );
        }
        else
        {
            // We have to create a new Attribute and set the values
            // and the upId
            createAttribute( upId, attributeType, values );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void add( String upId, AttributeType attributeType, Value<?>... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            String message = I18n.err( I18n.ERR_04460_ATTRIBUTE_TYPE_NULL_NOT_ALLOWED );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        upId = getUpId( upId, attributeType );
        
        EntryAttribute attribute = (EntryAttribute)attributes.get( attributeType.getOid() );
    
        if ( attribute != null )
        {
            // This Attribute already exist, we add the values 
            // into it
            attribute.add( values );
            attribute.setUpId( upId, attributeType );
        }
        else
        {
            createAttribute( upId, attributeType, values );
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public void add( String upId, AttributeType attributeType, String... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            String message = I18n.err( I18n.ERR_04460_ATTRIBUTE_TYPE_NULL_NOT_ALLOWED );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        upId = getUpId( upId, attributeType );

        EntryAttribute attribute = (EntryAttribute)attributes.get( attributeType.getOid() );
        
        if ( attribute != null )
        {
            // This Attribute already exist, we add the values 
            // into it
            attribute.add( values );
            attribute.setUpId( upId, attributeType );
        }
        else
        {
            // We have to create a new Attribute and set the values
            // and the upId
            createAttribute( upId, attributeType, values );
        }
    }


    /**
     * {@inheritDoc}
     */
    public void add( EntryAttribute... attributes ) throws LdapException
    {
        // Loop on all the added attributes
        for ( EntryAttribute attribute:attributes )
        {
            AttributeType attributeType = attribute.getAttributeType();
            
            if ( attributeType != null )
            {
                String oid = attributeType.getOid();
                
                if ( this.attributes.containsKey( oid ) )
                {
                    // We already have an attribute with the same AttributeType
                    // Just add the new values into it.
                    EntryAttribute existingAttribute = this.attributes.get( oid );
                    
                    for ( Value<?> value:attribute )
                    {
                        existingAttribute.add( value );
                    }
                    
                    // And update the upId
                    existingAttribute.setUpId( attribute.getUpId() );
                }
                else
                {
                    // The attributeType does not exist, add it
                    this.attributes.put( oid, attribute );
                }
            }
            else
            {
                // If the attribute already exist, we will add the new values.
                if ( contains( attribute ) )
                {
                    EntryAttribute existingAttribute = get( attribute.getId() );
                    
                    // Loop on all the values, and add them to the existing attribute
                    for ( Value<?> value:attribute )
                    {
                        existingAttribute.add( value );
                    }
                }
                else
                {
                    // Stores the attribute into the entry
                    this.attributes.put( attribute.getId(), attribute );
                }
            }
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public void add( String upId, byte[]... values ) throws LdapException
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        // First, transform the upID to a valid ID
        String id = getId( upId );
        
        if ( schemaManager != null )
        {
            add( upId, schemaManager.lookupAttributeTypeRegistry( id ), values );
        }
        else
        {
            // Now, check to see if we already have such an attribute
            EntryAttribute attribute = attributes.get( id );
            
            if ( attribute != null )
            {
                // This Attribute already exist, we add the values 
                // into it. (If the values already exists, they will
                // not be added, but this is done in the add() method)
                attribute.add( values );
                attribute.setUpId( upId );
            }
            else
            {
                // We have to create a new Attribute and set the values
                // and the upId
                attributes.put( id, new DefaultEntryAttribute( upId, values ) );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void add( String upId, String... values ) throws LdapException
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        // First, transform the upID to a valid ID
        String id = getId( upId );

        if ( schemaManager != null )
        {
            add( upId, schemaManager.lookupAttributeTypeRegistry( upId ), values );
        }
        else
        {
            // Now, check to see if we already have such an attribute
            EntryAttribute attribute = attributes.get( id );
            
            if ( attribute != null )
            {
                // This Attribute already exist, we add the values 
                // into it. (If the values already exists, they will
                // not be added, but this is done in the add() method)
                attribute.add( values );
                attribute.setUpId( upId );
            }
            else
            {
                // We have to create a new Attribute and set the values
                // and the upId
                attributes.put( id, new DefaultEntryAttribute( upId, values ) );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public void add( String upId, Value<?>... values ) throws LdapException
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        // First, transform the upID to a valid ID
        String id = getId( upId );

        if ( schemaManager != null )
        {
            add( upId, schemaManager.lookupAttributeTypeRegistry( upId ), values );
        }
        else
        {
            // Now, check to see if we already have such an attribute
            EntryAttribute attribute = attributes.get( id );
            
            if ( attribute != null )
            {
                // This Attribute already exist, we add the values 
                // into it. (If the values already exists, they will
                // not be added, but this is done in the add() method)
                attribute.add( values );
                attribute.setUpId( upId );
            }
            else
            {
                // We have to create a new Attribute and set the values
                // and the upId
                attributes.put( id, new DefaultEntryAttribute( upId, values ) );
            }
        }
    }


    /**
     * Clone an entry. All the element are duplicated, so a modification on
     * the original object won't affect the cloned object, as a modification
     * on the cloned object has no impact on the original object
     */
    public Entry clone()
    {
        try
        {
            // First, clone the structure
            DefaultClientEntry clone = (DefaultClientEntry)super.clone();
            
            // Just in case ... Should *never* happen
            if ( clone == null )
            {
                return null;
            }
            
            // An Entry has a DN and many attributes.
            // First, clone the DN, if not null.
            if ( dn != null )
            {
                clone.setDn( (DN)dn.clone() );
            }
            
            // then clone the ClientAttribute Map.
            clone.attributes = (Map<String, EntryAttribute>)(((HashMap<String, EntryAttribute>)attributes).clone());
            
            // now clone all the attributes
            clone.attributes.clear();
            
            for ( EntryAttribute attribute:attributes.values() )
            {
                clone.attributes.put( attribute.getId(), attribute.clone() );
            }
            
            // We are done !
            return clone;
        }
        catch ( CloneNotSupportedException cnse )
        {
            return null;
        }
    }
    

    /**
     * {@inheritDoc}
     */
    public boolean contains( EntryAttribute... attributes ) throws LdapException
    {
        if ( schemaManager == null )
        {
            for ( EntryAttribute attribute:attributes )
            {
                if ( attribute == null )
                {
                    return this.attributes.size() == 0;
                }
                
                if ( !this.attributes.containsKey( attribute.getId() ) )
                {
                    return false;
                }
            }
        }
        else
        {
            for ( EntryAttribute entryAttribute:attributes )
            {
                if ( entryAttribute == null )
                {
                    return this.attributes.size() == 0;
                }
                
                AttributeType attributeType = entryAttribute.getAttributeType();
                
                if ( ( entryAttribute == null ) || !this.attributes.containsKey( attributeType.getOid() ) )
                {
                    return false;
                }
            }
        }
        
        return true;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean contains( String upId ) throws LdapException
    {
        if ( StringTools.isEmpty( upId ) )
        {
            return false;
        }
        
        String id = getId( upId );
        
        if ( schemaManager != null )
        {
            try
            {
                return containsAttribute( schemaManager.lookupAttributeTypeRegistry( id ) );
            }
            catch ( LdapException le )
            {
                return false;
            }
        }
        
        return attributes.containsKey( id );
    }


    /**
     * {@inheritDoc}
     */
    public boolean containsAttribute( String... attributes )
    {
        if ( schemaManager == null )
        {
            for ( String attribute:attributes )
            {
                String id = getId( attribute );
        
                if ( !this.attributes.containsKey( id ) )
                {
                    return false;
                }
            }
            
            return true;
        }
        else
        {
            for ( String attribute:attributes )
            {
                try
                {
                    if ( !containsAttribute( schemaManager.lookupAttributeTypeRegistry( attribute ) ) )
                    {
                        return false;
                    }
                }
                catch ( LdapException ne )
                {
                    return false;
                }
            }
        
            return true;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean containsAttribute( AttributeType attributeType )
    {
        if ( attributeType == null )
        {
            return false;
        }
        
        return attributes.containsKey( attributeType.getOid() );
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean contains( AttributeType attributeType, byte[]... values )
    {
        if ( attributeType == null )
        {
            return false;
        }
        
        EntryAttribute attribute = attributes.get( attributeType.getOid() );
        
        if ( attribute != null )
        {
            return attribute.contains( values );
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean contains( AttributeType attributeType, String... values )
    {
        if ( attributeType == null )
        {
            return false;
        }
        
        EntryAttribute attribute = attributes.get( attributeType.getOid() );
        
        if ( attribute != null )
        {
            return attribute.contains( values );
        }
        else
        {
            return false;
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean contains( AttributeType attributeType, Value<?>... values )
    {
        if ( attributeType == null )
        {
            return false;
        }
        
        EntryAttribute attribute = attributes.get( attributeType.getOid() );
        
        if ( attribute != null )
        {
            return attribute.contains( values );
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean contains( String upId, byte[]... values )
    {
        if ( StringTools.isEmpty( upId ) )
        {
            return false;
        }
        
        String id = getId( upId );
        
        if ( schemaManager != null )
        {
            try
            {
                return contains( schemaManager.lookupAttributeTypeRegistry( id ), values );
            }
            catch ( LdapException le )
            {
                return false;
            }
        }
        
        EntryAttribute attribute = attributes.get( id );
        
        if ( attribute == null )
        {
            return false;
        }
        
        return attribute.contains( values );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean contains( String upId, String... values )
    {
        if ( StringTools.isEmpty( upId ) )
        {
            return false;
        }
        
        String id = getId( upId );

        if ( schemaManager != null )
        {
            try
            {
                return contains( schemaManager.lookupAttributeTypeRegistry( id ), values );
            }
            catch ( LdapException le )
            {
                return false;
            }
        }
        
        
        EntryAttribute attribute = attributes.get( id );
        
        if ( attribute == null )
        {
            return false;
        }
        
        return attribute.contains( values );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean contains( String upId, Value<?>... values )
    {
        if ( StringTools.isEmpty( upId ) )
        {
            return false;
        }
        
        String id = getId( upId );

        if ( schemaManager != null )
        {
            try
            {
                return contains( schemaManager.lookupAttributeTypeRegistry( id ), values );
            }
            catch ( LdapException le )
            {
                return false;
            }
        }
        
        
        EntryAttribute attribute = attributes.get( id );
        
        if ( attribute == null )
        {
            return false;
        }
        
        return attribute.contains( values );
    }
    
    
    /**
     * {@inheritDoc}
     */
    public EntryAttribute get( String alias )
    {
        try
        {
            String id = getId( alias );
            
            if ( schemaManager != null )
            {
                try
                {
                    AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( id );
                    
                    return attributes.get( attributeType.getOid() );
                }
                catch ( LdapException ne )
                {
                    String message = ne.getLocalizedMessage();
                    LOG.error( message );
                    return null;
                }
            }
            else
            {
                return attributes.get( id );
            }
        }
        catch( IllegalArgumentException iea )
        {
            LOG.error( I18n.err( I18n.ERR_04134, alias ) );
            return null;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public EntryAttribute get( AttributeType attributeType )
    {
        if ( attributeType != null )
        {
            return attributes.get( attributeType.getOid() );
        }
        else
        {
            return null;
        }
    }


    /**
     * {@inheritDoc}
     */
    public Set<AttributeType> getAttributeTypes()
    {
        Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
        
        for ( EntryAttribute attribute:attributes.values() )
        {
            if ( attribute.getAttributeType() != null )
            { 
                attributeTypes.add( attribute.getAttributeType() );
            }
        }
        
        return attributeTypes;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( String upId, byte[]... values )
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        if ( schemaManager == null )
        {
            // Get the normalized form of the ID
            String id = getId( upId );

            // Create a new attribute
            EntryAttribute clientAttribute = new DefaultEntryAttribute( upId, values );
    
            // Replace the previous one, and return it back
            return attributes.put( id, clientAttribute );
        }
        else
        {
            try
            {
                return put( upId, getAttributeType( upId ), values );
            }
            catch ( LdapException ne )
            {
                String message = I18n.err( I18n.ERR_04464, upId, ne.getLocalizedMessage() );
                LOG.error( message );
                throw new IllegalArgumentException( message );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( String upId, String... values )
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        if ( schemaManager == null )
        {
            // Get the normalized form of the ID
            String id = getId( upId );
            
            // Create a new attribute
            EntryAttribute clientAttribute = new DefaultEntryAttribute( upId, values );
    
            // Replace the previous one, and return it back
            return attributes.put( id, clientAttribute );
        }
        else
        {
            try
            {
                return put( upId, getAttributeType( upId ), values );
            }
            catch ( LdapException ne )
            {
                String message = I18n.err( I18n.ERR_04464, upId, ne.getLocalizedMessage() );
                LOG.error( message );
                throw new IllegalArgumentException( message );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( String upId, Value<?>... values )
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        if ( schemaManager == null )
        {
            // Get the normalized form of the ID
            String id = getId( upId );
            
            // Create a new attribute
            EntryAttribute clientAttribute = new DefaultEntryAttribute( upId, values );
    
            // Replace the previous one, and return it back
            return attributes.put( id, clientAttribute );
        }
        else
        {
            try
            {
                return put( upId, getAttributeType( upId ), values );
            }
            catch ( LdapException ne )
            {
                String message = I18n.err( I18n.ERR_04464, upId, ne.getLocalizedMessage() );
                LOG.error( message );
                throw new IllegalArgumentException( message );
            }
        }
    }


    /**
     * {@inheritDoc}
     */
    public List<EntryAttribute> set( String... upIds )
    {
        if ( ( upIds == null ) || ( upIds.length == 0 ) )
        {
            String message = I18n.err( I18n.ERR_04135 );
            LOG.error( message );
            throw new IllegalArgumentException( message );
        }
        
        List<EntryAttribute> removed = new ArrayList<EntryAttribute>();
        boolean added = false;
        
        if ( schemaManager == null )
        {
            // Now, loop on all the attributeType to add
            for ( String upId:upIds )
            {
                if ( upId == null )
                {
                    String message = I18n.err( I18n.ERR_04135 );
                    LOG.info( message );
                    throw new IllegalArgumentException( message );
                }
                
                String id = getId( upId );

                if ( attributes.containsKey( id ) )
                {
                    // Add the removed serverAttribute to the list
                    removed.add( attributes.remove( id ) );
                }
    
                EntryAttribute newAttribute = new DefaultEntryAttribute( upId );
                attributes.put( id, newAttribute );
                added = true;
            }
        }
        else
        {
            for ( String upId:upIds )
            {
                if ( upId == null )
                {
                    String message = I18n.err( I18n.ERR_04135 );
                    LOG.info( message );
                    throw new IllegalArgumentException( message );
                }
                
                // Search for the corresponding AttributeType, based on the upID 
                AttributeType attributeType = null;
                
                try
                {
                    attributeType = getAttributeType( upId );
                }
                catch ( LdapException ne )
                {
                    LOG.warn( "Trying to add a bad attribute type '{}', error : ", upId, ne.getLocalizedMessage() );
                    continue;
                }
                catch ( IllegalArgumentException iae )
                {
                    LOG.warn( "Trying to add a bad attribute type '{}', error : ", upId, iae.getLocalizedMessage() );
                    continue;
                }
                
                String oid = attributeType.getOid();
                
                if ( attributes.containsKey( oid ) )
                {
                    removed.add( attributes.get( oid ) );
                }
                
                attributes.put( oid, new DefaultEntryAttribute( upId, attributeType ) );
                added = true;
            }
        }
        
        if ( ( !added ) || ( removed.size() == 0 ) ) 
        {
            return null;
        }
        
        return removed;
    }

    
    /**
     * {@inheritDoc}     
     **/
    public List<EntryAttribute> set( AttributeType... attributeTypes )
    {
        List<EntryAttribute> removed = new ArrayList<EntryAttribute>();
        
        // Now, loop on all the attributeType to add
        for ( AttributeType attributeType:attributeTypes )
        {
            if ( attributeType == null )
            {
                String message = I18n.err( I18n.ERR_04467 );
                LOG.error( message );
                continue;
            }
            
            EntryAttribute attribute = attributes.put( attributeType.getOid(), new DefaultEntryAttribute( attributeType ) );

            if ( attribute != null )
            {
                removed.add( attribute );
            }
        }
        
        if ( removed.size() == 0 )
        {
            return null;
        }
        else
        {
            return removed;
        }
    }

    
    /**
     * {@inheritDoc}
     */
    public List<EntryAttribute> put( EntryAttribute... attributes ) throws LdapException
    {
        // First, get the existing attributes
        List<EntryAttribute> previous = new ArrayList<EntryAttribute>();
        
        if ( schemaManager == null )
        {
            for ( EntryAttribute attribute:attributes )
            {
                String id = attribute.getId();
                
                if ( contains( id ) )
                {
                    // Store the attribute and remove it from the list
                    previous.add( get( id ) );
                    this.attributes.remove( id );
                }
                
                // add the new one
                this.attributes.put( id, (EntryAttribute)attribute );            
            }
        }
        else
        {
            for ( EntryAttribute attribute:attributes )
            {
                if ( attribute == null )
                {
                    String message = I18n.err( I18n.ERR_04462 );
                    LOG.error( message );
                    throw new IllegalArgumentException( message );
                }
                
                EntryAttribute removed = this.attributes.put( attribute.getAttributeType().getOid(), attribute );
                
                if ( removed != null )
                {
                    previous.add( removed );
                }
            }
        }
        
        // return the previous attributes
        return previous;
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( AttributeType attributeType, byte[]... values ) throws LdapException
    {
        return put( null, attributeType, values );
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( AttributeType attributeType, String... values ) throws LdapException
    {
        return put( null, attributeType, values );
    }

    
    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( AttributeType attributeType, Value<?>... values ) throws LdapException
    {
        return put( null, attributeType, values );
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( String upId, AttributeType attributeType, byte[]... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            try
            {
                attributeType = getAttributeType( upId );
            }
            catch ( Exception e )
            {
                String message = I18n.err( I18n.ERR_04477_NO_VALID_AT_FOR_THIS_ID );
                LOG.error( message );
                throw new IllegalArgumentException( message );
            }
        }
        else
        {
            if ( !StringTools.isEmpty( upId ) )
            {
                AttributeType tempAT = getAttributeType( upId );
            
                if ( !tempAT.equals( attributeType ) )
                {
                    String message = I18n.err( I18n.ERR_04463, upId, attributeType );
                    LOG.error( message );
                    throw new IllegalArgumentException( message );
                }
            }
            else
            {
                upId = getUpId( upId, attributeType );
            }
        }
        
        if ( attributeType.equals( OBJECT_CLASS_AT ) )
        {
            String message = I18n.err( I18n.ERR_04461 );
            LOG.error( message );
            throw new UnsupportedOperationException( message );
        }

        EntryAttribute attribute = new DefaultEntryAttribute( upId, attributeType, values );
        
        return attributes.put( attributeType.getOid(), attribute );
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( String upId, AttributeType attributeType, String... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            try
            {
                attributeType = getAttributeType( upId );
            }
            catch ( Exception e )
            {
                String message = I18n.err( I18n.ERR_04477_NO_VALID_AT_FOR_THIS_ID );
                LOG.error( message );
                throw new IllegalArgumentException( message );
            }
        }
        else
        {
            if ( !StringTools.isEmpty( upId ) )
            {
                AttributeType tempAT = getAttributeType( upId );
            
                if ( !tempAT.equals( attributeType ) )
                {
                    String message = I18n.err( I18n.ERR_04463, upId, attributeType );
                    LOG.error( message );
                    throw new IllegalArgumentException( message );
                }
            }
            else
            {
                upId = getUpId( upId, attributeType );
            }
        }
        
        EntryAttribute attribute = new DefaultEntryAttribute( upId, attributeType, values );
        
        return attributes.put( attributeType.getOid(), attribute );
    }


    /**
     * {@inheritDoc}
     */
    public EntryAttribute put( String upId, AttributeType attributeType, Value<?>... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            try
            {
                attributeType = getAttributeType( upId );
            }
            catch ( Exception e )
            {
                String message = I18n.err( I18n.ERR_04477_NO_VALID_AT_FOR_THIS_ID );
                LOG.error( message );
                throw new IllegalArgumentException( message );
            }
        }
        else
        {
            if ( !StringTools.isEmpty( upId ) )
            {
                AttributeType tempAT = getAttributeType( upId );
            
                if ( !tempAT.equals( attributeType ) )
                {
                    String message = I18n.err( I18n.ERR_04463, upId, attributeType );
                    LOG.error( message );
                    throw new IllegalArgumentException( message );
                }
            }
            else
            {
                upId = getUpId( upId, attributeType );
            }
        }
        
        EntryAttribute attribute = new DefaultEntryAttribute( upId, attributeType, values );
        
        return attributes.put( attributeType.getOid(), attribute );
    }


    /**
     * {@inheritDoc}
     */
    public List<EntryAttribute> remove( EntryAttribute... attributes ) throws LdapException
    {
        List<EntryAttribute> removedAttributes = new ArrayList<EntryAttribute>();
        
        if ( schemaManager == null )
        {
            for ( EntryAttribute attribute:attributes )
            {
                if ( contains( attribute.getId() ) )
                {
                    this.attributes.remove( attribute.getId() );
                    removedAttributes.add( attribute );
                }
            }
        }
        else
        {
            for ( EntryAttribute attribute:attributes )
            {
                AttributeType attributeType = attribute.getAttributeType();
                
                if ( attributeType == null )
                {
                    String message = I18n.err( I18n.ERR_04460_ATTRIBUTE_TYPE_NULL_NOT_ALLOWED );
                    LOG.error( message );
                    throw new IllegalArgumentException( message );
                }
                
                if ( this.attributes.containsKey( attributeType.getOid() ) )
                {
                    this.attributes.remove( attributeType.getOid() );
                    removedAttributes.add( attribute );
                }
            }
        }
        
        return removedAttributes;
    }


    /**
     * {@inheritDoc}
     */
    public boolean remove( AttributeType attributeType, byte[]... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            return false;
        }
        
        try
        {
            EntryAttribute attribute = attributes.get( attributeType.getOid() );
            
            if ( attribute == null )
            {
                // Can't remove values from a not existing attribute !
                return false;
            }
            
            int nbOldValues = attribute.size();
            
            // Remove the values
            attribute.remove( values );
            
            if ( attribute.size() == 0 )
            {
                // No mare values, remove the attribute
                attributes.remove( attributeType.getOid() );
                
                return true;
            }
            
            if ( nbOldValues != attribute.size() )
            {
                // At least one value have been removed, return true.
                return true;
            }
            else
            {
                // No values have been removed, return false.
                return false;
            }
        }
        catch ( IllegalArgumentException iae )
        {
            LOG.error( I18n.err( I18n.ERR_04465, attributeType ) );
            return false;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean remove( AttributeType attributeType, String... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            return false;
        }
        
        try
        {
            EntryAttribute attribute = attributes.get( attributeType.getOid() );
            
            if ( attribute == null )
            {
                // Can't remove values from a not existing attribute !
                return false;
            }
            
            int nbOldValues = attribute.size();
            
            // Remove the values
            attribute.remove( values );
            
            if ( attribute.size() == 0 )
            {
                // No mare values, remove the attribute
                attributes.remove( attributeType.getOid() );
                
                return true;
            }
            
            if ( nbOldValues != attribute.size() )
            {
                // At least one value have been removed, return true.
                return true;
            }
            else
            {
                // No values have been removed, return false.
                return false;
            }
        }
        catch ( IllegalArgumentException iae )
        {
            LOG.error( I18n.err( I18n.ERR_04465, attributeType ) );
            return false;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public boolean remove( AttributeType attributeType, Value<?>... values ) throws LdapException
    {
        if ( attributeType == null )
        {
            return false;
        }
        
        try
        {
            EntryAttribute attribute = attributes.get( attributeType.getOid() );
            
            if ( attribute == null )
            {
                // Can't remove values from a not existing attribute !
                return false;
            }
            
            int nbOldValues = attribute.size();
            
            // Remove the values
            attribute.remove( values );
            
            if ( attribute.size() == 0 )
            {
                // No mare values, remove the attribute
                attributes.remove( attributeType.getOid() );
                
                return true;
            }
            
            if ( nbOldValues != attribute.size() )
            {
                // At least one value have been removed, return true.
                return true;
            }
            else
            {
                // No values have been removed, return false.
                return false;
            }
        }
        catch ( IllegalArgumentException iae )
        {
            LOG.error( I18n.err( I18n.ERR_04465, attributeType ) );
            return false;
        }
    }
    
    
    /**
     * <p>
     * Removes the attribute with the specified AttributeTypes. 
     * </p>
     * <p>
     * The removed attribute are returned by this method. 
     * </p>
     * <p>
     * If there is no attribute with the specified AttributeTypes,
     * the return value is <code>null</code>.
     * </p>
     *
     * @param attributes the AttributeTypes to be removed
     * @return the removed attributes, if any, as a list; otherwise <code>null</code>
     */
    public List<EntryAttribute> removeAttributes( AttributeType... attributes )
    {
        if ( ( attributes == null ) || ( attributes.length == 0 ) || ( schemaManager == null ) )
        {
            return null;
        }
        
        List<EntryAttribute> removed = new ArrayList<EntryAttribute>( attributes.length );
        
        for ( AttributeType attributeType:attributes )
        {
            if ( attributeType == null )
            {
                continue;
            }
            
            EntryAttribute attr = this.attributes.remove( attributeType.getOid() );
            
            if ( attr != null )
            {
                removed.add( attr );
            }
        }
        
        if ( removed.size() == 0 )
        {
            return null;
        }
        else
        {
            return removed;
        }
    }
    
    
    /**
     * {@inheritDoc}
     */
    public List<EntryAttribute> removeAttributes( String... attributes )
    {
        if ( attributes.length == 0 )
        {
            return null;
        }
        
        List<EntryAttribute> removed = new ArrayList<EntryAttribute>( attributes.length );
        
        if ( schemaManager == null )
        {
            for ( String attribute:attributes )
            {
                EntryAttribute attr = get( attribute );
                
                if ( attr != null )
                {
                    removed.add( this.attributes.remove( attr.getId() ) );
                }
                else
                {
                    String message = I18n.err( I18n.ERR_04137, attribute );
                    LOG.warn( message );
                    continue;
                }
            }
        }
        else
        {
            for ( String attribute:attributes )
            {
                AttributeType attributeType = null;
                
                try
                {
                    attributeType = schemaManager.lookupAttributeTypeRegistry( attribute );
                }
                catch ( LdapException ne )
                {
                    String message = "The attribute '" + attribute + "' does not exist in the entry";
                    LOG.warn( message );
                    continue;
                }
        
                EntryAttribute attr = this.attributes.remove( attributeType.getOid() );
                
                if ( attr != null )
                {
                    removed.add( attr );
                }
            }
        }
        
        if ( removed.size() == 0 )
        {
            return null;
        }
        else
        {
            return removed;
        }
    }


    /**
     * <p>
     * Removes the specified binary values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param upId The attribute ID  
     * @param values the values to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    public boolean remove( String upId, byte[]... values ) throws LdapException
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.info( message );
            return false;
        }
        
        if ( schemaManager == null )
        {
            String id = getId( upId );

            EntryAttribute attribute = get( id );

            if ( attribute == null )
            {
                // Can't remove values from a not existing attribute !
                return false;
            }
            

            int nbOldValues = attribute.size();
            
            // Remove the values
            attribute.remove( values );
            
            if ( attribute.size() == 0 )
            {
                // No mare values, remove the attribute
                attributes.remove( id );
                
                return true;
            }
            
            if ( nbOldValues != attribute.size() )
            {
                // At least one value have been removed, return true.
                return true;
            }
            else
            {
                // No values have been removed, return false.
                return false;
            }
        }
        else
        {
            try
            {
                AttributeType attributeType = getAttributeType( upId );

                return remove( attributeType, values );
            }
            catch ( LdapException ne )
            {
                LOG.error( I18n.err( I18n.ERR_04465, upId ) );
                return false;
            }
            catch ( IllegalArgumentException iae )
            {
                LOG.error( I18n.err( I18n.ERR_04466, upId ) );
                return false;
            }
        }
            
    }


    /**
     * <p>
     * Removes the specified String values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param upId The attribute ID  
     * @param attributes the attributes to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    public boolean remove( String upId, String... values ) throws LdapException
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.info( message );
            return false;
        }

        if ( schemaManager == null )
        {
            String id = getId( upId );
            
            EntryAttribute attribute = get( id );
            
            if ( attribute == null )
            {
                // Can't remove values from a not existing attribute !
                return false;
            }
            
            int nbOldValues = attribute.size();
            
            // Remove the values
            attribute.remove( values );
            
            if ( attribute.size() == 0 )
            {
                // No mare values, remove the attribute
                attributes.remove( id );
                
                return true;
            }
            
            if ( nbOldValues != attribute.size() )
            {
                // At least one value have been removed, return true.
                return true;
            }
            else
            {
                // No values have been removed, return false.
                return false;
            }
        }
        else
        {
            try
            {
                AttributeType attributeType = getAttributeType( upId );

                return remove( attributeType, values );
            }
            catch ( LdapException ne )
            {
                LOG.error( I18n.err( I18n.ERR_04465, upId ) );
                return false;
            }
            catch ( IllegalArgumentException iae )
            {
                LOG.error( I18n.err( I18n.ERR_04466, upId ) );
                return false;
            }
        }
    }


    /**
     * <p>
     * Removes the specified values from an attribute.
     * </p>
     * <p>
     * If at least one value is removed, this method returns <code>true</code>.
     * </p>
     * <p>
     * If there is no more value after having removed the values, the attribute
     * will be removed too.
     * </p>
     * <p>
     * If the attribute does not exist, nothing is done and the method returns 
     * <code>false</code>
     * </p> 
     *
     * @param upId The attribute ID  
     * @param attributes the attributes to be removed
     * @return <code>true</code> if at least a value is removed, <code>false</code>
     * if not all the values have been removed or if the attribute does not exist. 
     */
    public boolean remove( String upId, Value<?>... values ) throws LdapException
    {
        if ( StringTools.isEmpty( upId ) )
        {
            String message = I18n.err( I18n.ERR_04457_NULL_ATTRIBUTE_ID );
            LOG.info( message );
            return false;
        }

        if ( schemaManager == null )
        {
            String id = getId( upId );
            
            EntryAttribute attribute = get( id );
            
            if ( attribute == null )
            {
                // Can't remove values from a not existing attribute !
                return false;
            }
            
            int nbOldValues = attribute.size();
            
            // Remove the values
            attribute.remove( values );
            
            if ( attribute.size() == 0 )
            {
                // No mare values, remove the attribute
                attributes.remove( id );
                
                return true;
            }
            
            if ( nbOldValues != attribute.size() )
            {
                // At least one value have been removed, return true.
                return true;
            }
            else
            {
                // No values have been removed, return false.
                return false;
            }
        }
        else
        {
            try
            {
                AttributeType attributeType = getAttributeType( upId );

                return remove( attributeType, values );
            }
            catch ( LdapException ne )
            {
                LOG.error( I18n.err( I18n.ERR_04465, upId ) );
                return false;
            }
            catch ( IllegalArgumentException iae )
            {
                LOG.error( I18n.err( I18n.ERR_04466, upId ) );
                return false;
            }
        }
    }


    /**
     * Get this entry's DN.
     *
     * @return The entry's DN
     */
    public DN getDn()
    {
        return dn;
    }


    /**
     * Set this entry's DN.
     *
     * @param dn The DN associated with this entry
     */
    public void setDn( DN dn )
    {
        this.dn = dn;
        
        // Resash the object
        rehash();
    }
    
    
    /**
     * Remove all the attributes for this entry. The DN is not reset
     */
    public void clear()
    {
        attributes.clear();
    }
    
    
    /**
     * Returns an enumeration containing the zero or more attributes in the
     * collection. The behavior of the enumeration is not specified if the
     * attribute collection is changed.
     *
     * @return an enumeration of all contained attributes
     */
    public Iterator<EntryAttribute> iterator()
    {
        return Collections.unmodifiableMap( attributes ).values().iterator();
    }
    

    /**
     * {@inheritDoc}
     */
    public boolean isValid()
    {
        // @TODO Implement me !
        throw new NotImplementedException();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isValid( EntryAttribute objectClass )
    {
        // @TODO Implement me !
        throw new NotImplementedException();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isValid( String objectClass )
    {
        // @TODO Implement me !
        throw new NotImplementedException();
    }


    /**
     * Returns the number of attributes.
     *
     * @return the number of attributes
     */
    public int size()
    {
        return attributes.size();
    }
    
    
    /**
     * @see Externalizable#writeExternal(ObjectOutput)<p>
     * 
     * This is the place where we serialize entries, and all theirs
     * elements.
     * <p>
     * The structure used to store the entry is the following :
     * <li>
     * <b>[DN]</b> : If it's null, stores an empty DN
     * </li>
     * <li>
     * <b>[attributes number]</b> : the number of attributes.
     * </li>
     * <li>
     * <b>[attribute]*</b> : each attribute, if we have some
     * </li>
     */
    public void writeExternal( ObjectOutput out ) throws IOException
    {
        // First, the DN
        if ( dn == null )
        {
            // Write an empty DN
            out.writeObject( DN.EMPTY_DN );
        }
        else
        {
            // Write the DN
            out.writeObject( dn );
        }
        
        // Then the attributes. 
        // Store the attributes' nulber first
        out.writeInt( attributes.size() );
        
        // Iterate through the keys.
        for ( EntryAttribute attribute:attributes.values() )
        {
            // Store the attribute
            out.writeObject( attribute );
        }
        
        out.flush();
    }

    
    /**
     * @see Externalizable#readExternal(ObjectInput)
     */
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException
    {
        // Read the DN
        dn = (DN)in.readObject();
        
        // Read the number of attributes
        int nbAttributes = in.readInt();
        
        // Read the attributes
        for ( int i = 0; i < nbAttributes; i++ )
        {
            // Read each attribute
            EntryAttribute attribute = (DefaultEntryAttribute)in.readObject();
            
            if ( schemaManager != null )
            {
                try
                {
                    AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( attribute.getId() );

                    attributes.put( attributeType.getOid(), attribute );
                }
                catch (LdapException le  )
                {
                    String message = le.getLocalizedMessage();
                    LOG.error( message );
                    throw new IOException( message );
                }
            }
            else
            {
                attributes.put( attribute.getId(), attribute );
            }
        }
    }
    
    
    private void rehash()
    {
        h = 37;
        h = h*17 + dn.hashCode();
        
        /*
        // We have to sort the Attributes if we want to compare two entries
        SortedMap<String, EntryAttribute> sortedMap = new TreeMap<String, EntryAttribute>();
        
        for ( String id:attributes.keySet() )
        {
            sortedMap.put( id, attributes.get( id ) );
        }
        
        for ( String id:sortedMap.keySet() )
        {
            h = h*17 + sortedMap.get( id ).hashCode();
        }
        */
    }
    
    /**
     * Get the hash code of this ClientEntry. The Attributes will be sorted
     * before the comparison can be done.
     *
     * @see java.lang.Object#hashCode()
     * @return the instance's hash code 
     */
    public int hashCode()
    {
        if ( h == 0 )
        {
            rehash();
        }
        
        return h;
    }

    
    /**
     * {@inheritDoc}
     */
    public boolean hasObjectClass( String objectClass )
    {
        if ( schemaManager != null )
        {
            return contains( OBJECT_CLASS_AT.getOid(), objectClass );

        }
        else
        {
            return contains( "objectclass", objectClass );
        }
    }


    /**
     * {@inheritDoc}
     */
    public boolean hasObjectClass( EntryAttribute objectClass )
    {
        if ( objectClass == null )
        {
            return false;
        }
        
        // We have to check that we are checking the ObjectClass attributeType
        if ( !objectClass.getAttributeType().equals( OBJECT_CLASS_AT ) )
        {
            return false;
        }
        
        EntryAttribute attribute = attributes.get( OBJECT_CLASS_AT.getOid() );
        
        if ( attribute == null )
        {
            // The entry does not have an ObjectClass attribute
            return false;
        }
        
        for ( Value<?> value:objectClass )
        {
            // Loop on all the values, and check if they are present
            if ( !attribute.contains( value.getString() ) )
            {
                return false;
            }
        }
        
        return true;
    }


    /**
     * @see Object#equals(Object)
     */
    public boolean equals( Object o )
    {
        // Short circuit

        if ( this == o )
        {
            return true;
        }
        
        if ( ! ( o instanceof DefaultClientEntry ) )
        {
            return false;
        }
        
        DefaultClientEntry other = (DefaultClientEntry)o;
        
        // Both DN must be equal
        if ( dn == null )
        {
            if ( other.getDn() != null )
            {
                return false;
            }
        }
        else
        {
            if ( !dn.equals( other.getDn() ) )
            {
                return false;
            }
        }
        
        // They must have the same number of attributes
        if ( size() != other.size() )
        {
            return false;
        }
        
        // Each attribute must be equal
        for ( EntryAttribute attribute:other )
        {
            if ( !attribute.equals( this.get( attribute.getId() ) ) )
            {
                return false;
            }
        }
        
        return true;
    }
        

    /**
     * @see Object#toString()
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        sb.append( "Entry\n" );
        sb.append( "    dn" );

        if ( dn.isNormalized() )
        {
            sb.append( "[n]" );
        }
        
        sb.append( ": " );
        sb.append( dn.getName() );
        sb.append( '\n' );
        
        // First dump the ObjectClass attribute
        if ( schemaManager != null )
        {
            // First dump the ObjectClass attribute
            if ( containsAttribute( OBJECT_CLASS_AT.getOid() ) )
            {
                EntryAttribute objectClass = get( OBJECT_CLASS_AT );
                
                sb.append( objectClass );
            }
        }
        else
        {
            if ( containsAttribute( "objectClass" ) )
            {
                EntryAttribute objectClass = get( "objectclass" );
                
                sb.append( objectClass );
            }
        }
        
        if ( attributes.size() != 0 )
        {
            for ( EntryAttribute attribute:attributes.values() )
            {
                if ( attribute.getAttributeType() != OBJECT_CLASS_AT )
                {
                    sb.append( attribute );
                }
                else if ( !attribute.getId().equals( "objectclass" ) )
                {
                    sb.append( attribute );
                }
            }
        }
        
        return sb.toString();
    }
}
