/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.shared.ldap.model.ldif;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntrySerializer;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationSerializer;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.DnSerializer;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;
import org.apache.directory.shared.util.Unicode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A helper class which serialize and deserialize a LdifEntry.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdifEntrySerializer
{
    /** The LoggerFactory used by this class */
    protected static final Logger LOG = LoggerFactory.getLogger( LdifEntrySerializer.class );

    /**
     * Private constructor.
     */
    private LdifEntrySerializer()
    {
    }

    
    /**
     * Serializes a LdifEntry instance.
     * 
     * @param principal The LdifEntry instance to serialize
     * @param out The stream into which we will write the serialized instance
     * @throws IOException If the stream can't be written
     */
    public static void serialize( LdifEntry ldifEntry, ObjectOutput out ) throws IOException
    {
        // The changeType
        out.writeInt( ldifEntry.getChangeType().getChangeType() );

        switch ( ldifEntry.getChangeType() )
        {
            case Add :
                // We write the entry
                EntrySerializer.serialize( ldifEntry.getEntry(), out );
                
                break;
                
            case Delete :
                // We just have to store the deleted DN
                DnSerializer.serialize( ldifEntry.getDn(), out );
                
                break;
                
            case ModDn :
            case ModRdn :
                DnSerializer.serialize( ldifEntry.getDn(), out );
                out.writeBoolean( ldifEntry.isDeleteOldRdn() );

                if ( ldifEntry.getNewRdn() != null )
                {
                    out.writeBoolean( true );
                    Unicode.writeUTF( out, ldifEntry.getNewRdn() );
                }
                else
                {
                    out.writeBoolean( false );
                }

                if ( ldifEntry.getNewSuperior() != null )
                {
                    out.writeBoolean( true );
                    Unicode.writeUTF( out, ldifEntry.getNewSuperior() );
                }
                else
                {
                    out.writeBoolean( false );
                }

                break;
                
            case Modify :
                DnSerializer.serialize( ldifEntry.getDn(), out );
                // Read the modification
                out.writeInt( ldifEntry.getModificationItems().size() );

                for ( Modification modification : ldifEntry.getModificationItems() )
                {
                    ModificationSerializer.serialize( modification, out );
                }
                
                break;
        }

        
        // The controls
        if ( ldifEntry.hasControls() )
        {
            // Write the controls
            out.writeInt( ldifEntry.getControls().size() );

            for ( LdifControl ldifControl : ldifEntry.getControls().values() )
            {
                LdifControlSerializer.serialize( ldifControl, out );
            }
        }
        else
        {
            out.writeInt( 0 );
        }
        
        out.flush();
    }
    
    
    /**
     * Deserializes a LdifEntry instance.
     * 
     * @param schemaManager The SchemaManager (can be null)
     * @param in The input stream from which the LdifEntry is read
     * @return a deserialized LdifEntry
     * @throws IOException If the stream can't be read
     */
    public static LdifEntry deserialize( SchemaManager schemaManager, ObjectInput in )
        throws IOException, LdapInvalidDnException
    {
        // The ChangeType 
        ChangeType changeType = ChangeType.getChangeType( in.readInt() );
        
        LdifEntry ldifEntry = null;
        
        switch ( changeType )
        {
            case Add :
                Entry entry = EntrySerializer.deserialize( schemaManager, in );
                ldifEntry = new LdifEntry( entry );
                ldifEntry.setChangeType( changeType );
                
                break;
                
            case Delete :
                Dn dn = DnSerializer.deserialize( schemaManager, in );
                ldifEntry = new LdifEntry();
                ldifEntry.setChangeType( changeType );
                ldifEntry.setDn( dn );
                
                break;
                
            case ModDn :
            case ModRdn :
                ldifEntry = new LdifEntry();

                dn = DnSerializer.deserialize( schemaManager, in );
                ldifEntry.setDn( dn );
                boolean deleteOldRdn = in.readBoolean();
                ldifEntry.setChangeType( changeType );
                ldifEntry.setDeleteOldRdn( deleteOldRdn );

                // The newRDN
                if ( in.readBoolean() )
                {
                    String newRdn = Unicode.readUTF(in);
                    ldifEntry.setNewRdn( newRdn );
                }

                // The newSuperior
                if ( in.readBoolean() )
                {
                    String newSuperior = Unicode.readUTF(in);
                    ldifEntry.setNewSuperior( newSuperior );
                }
                
                break;
                
            case Modify :
                ldifEntry = new LdifEntry();
                dn = DnSerializer.deserialize( schemaManager, in );
                ldifEntry.setDn( dn );

                // Read the modification
                int nbModifs = in.readInt();
                ldifEntry.setChangeType( changeType );

                for ( int i = 0; i < nbModifs; i++ )
                {
                    Modification modification = ModificationSerializer.deserialize( schemaManager, in );

                    ldifEntry.addModificationItem( modification );
                }
                
                break;
        }
        
        // The controls
        int nbControls = in.readInt();
        
        if ( nbControls > 0 )
        {
            for ( int i = 0; i < nbControls; i++ )
            {
                LdifControl ldifControl = LdifControlSerializer.deserialize( in );
                
                ldifEntry.addControl( ldifControl );
            }
        }

        return ldifEntry;
    }
}
