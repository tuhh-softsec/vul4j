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
package org.apache.directory.shared.ldap.model.schema.comparators;


import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.schema.LdapComparator;
import org.apache.directory.shared.ldap.model.schema.SchemaManager;


/**
 * A comparator that sorts OIDs based on their numeric id value.  Needs a 
 * OidRegistry to properly do it's job.  Public method to set the oid 
 * registry will be used by the server after instantiation in deserialization.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class UniqueMemberComparator extends LdapComparator<String>
{
    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** A reference to the schema manager */
    private transient SchemaManager schemaManager;


    /**
     * The IntegerComparator constructor. Its OID is the IntegerOrderingMatch matching
     * rule OID.
     */
    public UniqueMemberComparator( String oid )
    {
        super( oid );
    }


    /**
     * Implementation of the Compare method
     */
    public int compare( String dnstr1, String dnstr2 )
    {
        int dash1 = dnstr1.lastIndexOf( '#' );
        int dash2 = dnstr2.lastIndexOf( '#' );

        if ( ( dash1 == -1 ) && ( dash2 == -1 ) )
        {
            // no UID part
            try
            {
                Dn dn1 = getDn( dnstr1 );
                Dn dn2 = getDn( dnstr2 );
                
                if ( dn1.equals( dn2 ) )
                {
                    return 0;
                }
                else
                {
                    return -1;
                }
            }
            catch ( LdapInvalidDnException ne )
            {
                return -1;
            }
        }
        else
        {
            // Now, check that we don't have another '#'
            if ( dnstr1.indexOf( '#' ) != dash1 )
            {
                // Yes, we have one : this is not allowed, it should have been
                // escaped.
                return -1;
            }

            if ( dnstr2.indexOf( '#' ) != dash1 )
            {
                // Yes, we have one : this is not allowed, it should have been
                // escaped.
                return 1;
            }

            Dn dn1 = null;
            Dn dn2 = null;

            // This is an UID if the '#' is immediatly
            // followed by a BitString, except if the '#' is
            // on the last position
            String uid1 = dnstr1.substring( dash1 + 1 );

            if ( dash1 > 0 )
            {
                try
                {
                    dn1 = new Dn( dnstr1.substring( 0, dash1 ) );
                }
                catch ( LdapException ne )
                {
                    return -1;
                }
            }
            else
            {
                return -1;
            }

            // This is an UID if the '#' is immediatly
            // followed by a BitString, except if the '#' is
            // on the last position
            String uid2 = dnstr2.substring( dash2 + 1 );

            if ( dash2 > 0 )
            {
                try
                {
                    dn2 = new Dn( dnstr1.substring( 0, dash2 ) );
                }
                catch ( LdapException ne )
                {
                    return 1;
                }
            }
            else
            {
                return 1;
            }

            if ( dn1.equals( dn2 ) )
            {
                return uid1.compareTo( uid2 );
            }

            return -1;
        }
    }


    /**
     * {@inheritDoc}
     */
    public void setSchemaManager( SchemaManager schemaManager )
    {
        this.schemaManager = schemaManager;
    }


    public Dn getDn( Object obj ) throws LdapInvalidDnException
    {
        Dn dn = null;

        if ( obj instanceof Dn)
        {
            dn = (Dn) obj;

            dn = ( dn.isNormalized() ? dn : dn.normalize( schemaManager ) );
        }
        else if ( obj instanceof String )
        {
            dn = new Dn( schemaManager, ( String ) obj );
        }
        else
        {
            throw new IllegalStateException( I18n.err( I18n.ERR_04218, ( obj == null ? null : obj.getClass() ) ) );
        }

        return dn;
    }
}
