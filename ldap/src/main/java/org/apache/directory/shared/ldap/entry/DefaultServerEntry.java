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
package org.apache.directory.shared.ldap.entry;


import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.entry.client.DefaultClientEntry;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.name.RDN;
import org.apache.directory.shared.ldap.name.RdnSerializer;
import org.apache.directory.shared.ldap.schema.AttributeType;
import org.apache.directory.shared.ldap.schema.SchemaManager;
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
public final class DefaultServerEntry extends DefaultClientEntry implements ServerEntry
{
    /** Used for serialization */
    private static final long serialVersionUID = 2L;
    
    /** The logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( DefaultServerEntry.class );

    /** A mutex to manage synchronization*/
    private static transient Object MUTEX = new Object();

    //-------------------------------------------------------------------------
    // Helper methods
    //-------------------------------------------------------------------------
    /**
     * This method is used to initialize the OBJECT_CLASS_AT attributeType.
     * 
     * We want to do it only once, so it's a synchronized method. Note that
     * the alternative would be to call the lookup() every time, but this won't
     * be very efficient, as it will get the AT from a map, which is also
     * synchronized, so here, we have a very minimal cost.
     * 
     * We can't do it once as a static part in the body of this class, because
     * the access to the registries is mandatory to get back the AttributeType.
     */
    private void initObjectClassAT( SchemaManager schemaManager )
    {
        try
        {
            if ( OBJECT_CLASS_AT == null )
            {
                synchronized ( MUTEX )
                {
                    OBJECT_CLASS_AT = schemaManager.lookupAttributeTypeRegistry( SchemaConstants.OBJECT_CLASS_AT );
                }
            }
        }
        catch ( LdapException ne )
        {
            // do nothing...
        }
    }

    
    //-------------------------------------------------------------------------
    // Constructors
    //-------------------------------------------------------------------------
    /**
     * <p>
     * Creates a new instance of DefaultServerEntry.
     * </p> 
     * <p>
     * This entry <b>must</b> be initialized before being used !
     * </p>
     */
    public DefaultServerEntry()
    {
        schemaManager = null;
        dn = DN.EMPTY_DN;
    }


    /**
     * <p>
     * Creates a new instance of DefaultServerEntry, with registries. 
     * </p>
     * <p>
     * No attributes will be created.
     * </p> 
     * 
     * @param registries The reference to the global registries
     */
    public DefaultServerEntry( SchemaManager schemaManager )
    {
        this.schemaManager = schemaManager;
        dn = DN.EMPTY_DN;

        // Initialize the ObjectClass object
        initObjectClassAT( schemaManager );
    }


    /**
     * <p>
     * Creates a new instance of DefaultServerEntry, copying 
     * another entry, which can be a ClientEntry. 
     * </p>
     * <p>
     * No attributes will be created.
     * </p> 
     * 
     * @param registries The reference to the global registries
     * @param entry the entry to copy
     */
    public DefaultServerEntry( SchemaManager schemaManager, Entry entry )
    {
        this.schemaManager = schemaManager;

        // Initialize the ObjectClass object
        initObjectClassAT( schemaManager );

        // We will clone the existing entry, because it may be normalized
        if ( entry.getDn() != null )
        {
            dn = (DN)entry.getDn().clone();
        }
        else
        {
            dn = DN.EMPTY_DN;
        }
        
        if ( !dn.isNormalized( ) )
        {
            try
            {
                // The dn must be normalized
                dn.normalize( schemaManager.getNormalizerMapping() );
            }
            catch ( LdapException ne )
            {
                LOG.warn( "The DN '" + entry.getDn() + "' cannot be normalized" );
            }
        }
        
        // Init the attributes map
        attributes = new HashMap<String, EntryAttribute>( entry.size() );
        
        // and copy all the attributes
        for ( EntryAttribute attribute:entry )
        {
            try
            {
                // First get the AttributeType
                AttributeType attributeType = attribute.getAttributeType();

                if ( attributeType == null )
                {
                    attributeType = schemaManager.lookupAttributeTypeRegistry( attribute.getId() );
                }
                
                // Create a new ServerAttribute.
                EntryAttribute serverAttribute = new DefaultEntryAttribute( attributeType, attribute );
                
                // And store it
                add( serverAttribute );
            }
            catch ( LdapException ne )
            {
                // Just log a warning
                LOG.warn( "The attribute '" + attribute.getId() + "' cannot be stored" );
            }
        }
    }


    /**
     * <p>
     * Creates a new instance of DefaultServerEntry, with a 
     * DN and registries. 
     * </p>
     * <p>
     * No attributes will be created.
     * </p> 
     * 
     * @param registries The reference to the global registries
     * @param dn The DN for this serverEntry. Can be null.
     */
    public DefaultServerEntry( SchemaManager schemaManager, DN dn )
    {
        if ( dn == null )
        {
            dn = DN.EMPTY_DN;
        }
        else
        {
            this.dn = dn;
        }
        
        this.schemaManager = schemaManager;

        // Initialize the ObjectClass object
        initObjectClassAT( schemaManager );
    }


    /**
     * <p>
     * Creates a new instance of DefaultServerEntry, with a 
     * DN, registries and a list of attributeTypes. 
     * </p>
     * <p>
     * The newly created entry is fed with the list of attributeTypes. No
     * values are associated with those attributeTypes.
     * </p>
     * <p>
     * If any of the AttributeType does not exist, they it's simply discarded.
     * </p>
     * 
     * @param registries The reference to the global registries
     * @param dn The DN for this serverEntry. Can be null.
     * @param attributeTypes The list of attributes to create, without value.
     */
    public DefaultServerEntry( SchemaManager schemaManager, DN dn, AttributeType... attributeTypes )
    {
        if ( dn == null )
        {
            dn = DN.EMPTY_DN;
        }
        else
        {
            this.dn = dn;
        }

        this.schemaManager = schemaManager;

        // Initialize the ObjectClass object
        initObjectClassAT( schemaManager );

        // Add the attributeTypes
        set( attributeTypes );
    }

    
    /**
     * <p>
     * Creates a new instance of DefaultServerEntry, with a 
     * DN, registries and an attributeType with the user provided ID. 
     * </p>
     * <p>
     * The newly created entry is fed with the given attributeType. No
     * values are associated with this attributeType.
     * </p>
     * <p>
     * If the AttributeType does not exist, they it's simply discarded.
     * </p>
     * <p>
     * We also check that the normalized upID equals the AttributeType ID
     * </p>
     * 
     * @param registries The reference to the global registries
     * @param dn The DN for this serverEntry. Can be null.
     * @param attributeType The attribute to create, without value.
     * @param upId The User Provided ID fro this AttributeType
     */
    public DefaultServerEntry( SchemaManager schemaManager, DN dn, AttributeType attributeType, String upId )
    {
        if ( dn == null )
        {
            dn = DN.EMPTY_DN;
        }
        else
        {
            this.dn = dn;
        }
        
        this.schemaManager = schemaManager;
        // Initialize the ObjectClass object

        // Initialize the ObjectClass object
        initObjectClassAT( schemaManager );

        try
        {
            put( upId, attributeType, (String)null );
        }
        catch ( LdapException ne )
        {
            // Just discard the AttributeType
            LOG.error( I18n.err( I18n.ERR_04459, upId, ne.getLocalizedMessage() ) );
        }
    }
    
    
    /**
     * Creates a new instance of DefaultServerEntry, with a 
     * DN, registries and a list of IDs. 
     * <p>
     * No attributes will be created except the ObjectClass attribute,
     * which will contains "top". 
     * <p>
     * If any of the AttributeType does not exist, they are simply discarded.
     * 
     * @param registries The reference to the global registries
     * @param dn The DN for this serverEntry. Can be null.
     * @param upIds The list of attributes to create.
     */
    public DefaultServerEntry( SchemaManager schemaManager, DN dn, String... upIds )
    {
        if ( dn == null )
        {
            dn = DN.EMPTY_DN;
        }
        else
        {
            this.dn = dn;
        }
        
        this.schemaManager = schemaManager;

        initObjectClassAT( schemaManager );

        set( upIds );
    }

    
    /**
     * Creates a new instance of DefaultServerEntry, with a 
     * DN, registries and a list of ServerAttributes. 
     * <p>
     * No attributes will be created except the ObjectClass attribute,
     * which will contains "top". 
     * <p>
     * If any of the AttributeType does not exist, they are simply discarded.
     * 
     * @param registries The reference to the global registries
     * @param dn The DN for this serverEntry. Can be null
     * @param attributes The list of attributes to create
     */
    public DefaultServerEntry( SchemaManager schemaManager, DN dn, EntryAttribute... attributes )
    {
        if ( dn == null )
        {
            dn = DN.EMPTY_DN;
        }
        else
        {
            this.dn = dn;
        }
        
        this.schemaManager = schemaManager;

        initObjectClassAT( schemaManager );

        for ( EntryAttribute attribute:attributes )
        {
            // Store a new ServerAttribute
            try
            {
                put( attribute );
            }
            catch ( LdapException ne )
            {
                LOG.warn( "The ServerAttribute '{}' does not exist. It has been discarded", attribute );
            }
        }
    }

    
    //-------------------------------------------------------------------------
    // API
    //-------------------------------------------------------------------------
    //-------------------------------------------------------------------------
    // Object methods
    //-------------------------------------------------------------------------
    /**
     * Clone an entry. All the element are duplicated, so a modification on
     * the original object won't affect the cloned object, as a modification
     * on the cloned object has no impact on the original object
     */
    public Entry clone()
    {
        // First, clone the structure
        DefaultServerEntry clone = (DefaultServerEntry)super.clone();
        
        // A serverEntry has a DN, an ObjectClass attribute
        // and many attributes.
        // Clone the DN  first.
        if ( dn != null )
        {
            clone.dn = (DN)dn.clone();
        }
        
        // clone the ServerAttribute Map
        clone.attributes = (Map<String, EntryAttribute>)(((HashMap<String, EntryAttribute>)attributes).clone());
        
        // now clone all the servrAttributes
        clone.attributes.clear();
        
        for ( EntryAttribute entryAttribute : attributes.values() )
        {
            EntryAttribute value = (EntryAttribute)entryAttribute.clone();
            clone.attributes.put( value.getAttributeType().getOid(), value );
        }
        
        // We are done !
        return clone;
    }
    

    /**
     * Serialize a server entry.
     * 
     * The structure is the following :
     * <b>[a byte] : if the DN is empty 0 will be written else 1
     * <b>[RDN]</b> : The entry's RDN.
     * <b>[numberAttr]</b> : the bumber of attributes. Can be 0 
     * <b>[attribute's oid]*</b> : The attribute's OID to get back 
     * the attributeType on deserialization
     * <b>[Attribute]*</b> The attribute
     * 
     * @param out the buffer in which the data will be serialized
     * @throws IOException if the serialization failed
     */
    public void serialize( ObjectOutput out ) throws IOException
    {
        // First, the DN
        // Write the RDN of the DN
        
        if( dn.getRdn() == null )
        {
            out.writeByte( 0 );
        }
        else
        {
            out.writeByte( 1 );
            RdnSerializer.serialize( dn.getRdn(), out );
        }
        
        // Then the attributes.
        out.writeInt( attributes.size() );
        
        // Iterate through the keys. We store the Attribute
        // here, to be able to restore it in the readExternal :
        // we need access to the registries, which are not available
        // in the ServerAttribute class.
        for ( AttributeType attributeType:getAttributeTypes() )
        {
            // Write the oid to be able to restore the AttributeType when deserializing
            // the attribute
            String oid = attributeType.getOid();
            
            out.writeUTF( oid );
            
            // Get the attribute
            DefaultEntryAttribute attribute = (DefaultEntryAttribute)attributes.get( attributeType.getOid() );

            // Write the attribute
            attribute.serialize( out );
        }
    }

    
    /**
     * Deserialize a server entry. 
     * 
     * @param in The buffer containing the serialized serverEntry
     * @throws IOException if there was a problem when deserializing
     * @throws ClassNotFoundException if we can't deserialize an expected object
     */
    public void deserialize( ObjectInput in ) throws IOException, ClassNotFoundException
    {
        // Read the DN
        dn = new DN();

        byte b = in.readByte();
        if( b == 1 )
        {
            RDN rdn = RdnSerializer.deserialize( in );
            dn.add( rdn );
        }
        
        // Read the number of attributes
        int nbAttributes = in.readInt();
        
        // Read the attributes
        for ( int i = 0; i < nbAttributes; i++ )
        {
            // Read the attribute's OID
            String oid = in.readUTF();
            
            try
            {
                AttributeType attributeType = schemaManager.lookupAttributeTypeRegistry( oid );
                
                // Create the attribute we will read
                EntryAttribute attribute = new DefaultEntryAttribute( attributeType );
                
                // Read the attribute
                attribute.deserialize( in );
                
                attributes.put( attributeType.getOid(), attribute );
            }
            catch ( LdapException ne )
            {
                // We weren't able to find the OID. The attribute will not be added
                LOG.warn( I18n.err( I18n.ERR_04470, oid ) );
                
            }
        }
    }
}
