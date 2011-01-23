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
package org.apache.directory.shared.ldap.schema.normalizers;


import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.model.entry.StringValue;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.name.Dn;
import org.apache.directory.shared.ldap.schema.Normalizer;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.util.Strings;


/**
 * A noirmalizer for UniqueMember
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UniqueMemberNormalizer extends Normalizer
{
    // The serial UID
    private static final long serialVersionUID = 1L;

    /** A reference to the schema manager used to normalize the Dn */
    private SchemaManager schemaManager;
    
    
    public UniqueMemberNormalizer()
    {
        super( SchemaConstants.UNIQUE_MEMBER_MATCH_MR_OID );
    }
    

    public Value<?> normalize( Value<?> value ) throws LdapException
    {
        String nameAndUid = value.getString();
            
        if ( nameAndUid.length() == 0 )
        {
            return null;
        }
        
        // Let's see if we have an UID part
        int sharpPos = nameAndUid.lastIndexOf( '#' );
        
        if ( sharpPos != -1 )
        {
            // Now, check that we don't have another '#'
            if ( nameAndUid.indexOf( '#' ) != sharpPos )
            {
                // Yes, we have one : this is not allowed, it should have been
                // escaped.
                return null;
            }
            
            // This is an UID if the '#' is immediately
            // followed by a BitString, except if the '#' is
            // on the last position
            String uid = nameAndUid.substring( sharpPos + 1 );
            
            if ( sharpPos > 0 )
            {
                Dn dn = new Dn( nameAndUid.substring( 0, sharpPos ), schemaManager );
                
                return new StringValue( dn.getNormName() + '#' + uid );
            }
            else
            {
                throw new IllegalStateException( I18n.err( I18n.ERR_04226, value.getClass() ) );
            }
        }
        else
        {
            // No UID, the strValue is a Dn
            // Return the normalized Dn
            return new StringValue( new Dn( nameAndUid ).getNormName() );
        }
    }


    public String normalize( String value ) throws LdapException
    {
        if ( Strings.isEmpty(value) )
        {
            return null;
        }
        
        // Let's see if we have an UID part
        int sharpPos = value.lastIndexOf( '#' );
        
        if ( sharpPos != -1 )
        {
            // Now, check that we don't have another '#'
            if ( value.indexOf( '#' ) != sharpPos )
            {
                // Yes, we have one : this is not allowed, it should have been
                // escaped.
                return null;
            }
            
            // This is an UID if the '#' is immediatly
            // followed by a BitString, except if the '#' is
            // on the last position
            String uid = value.substring( sharpPos + 1 );
            
            if ( sharpPos > 0 )
            {
                Dn dn = new Dn( value.substring( 0, sharpPos ), schemaManager );
                
                return dn.getNormName() + '#' + uid;
            }
            else
            {
                throw new IllegalStateException( I18n.err( I18n.ERR_04226, value.getClass() ) );
            }
        }
        else
        {
            // No UID, the strValue is a Dn
            // Return the normalized Dn
            return new Dn( value, schemaManager ).getNormName();
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setSchemaManager( SchemaManager schemaManager )
    {
        this.schemaManager = schemaManager;
    }
}
