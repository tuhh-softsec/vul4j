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
package org.apache.directory.shared.ldap.schema.comparators;


import org.apache.directory.shared.ldap.constants.SchemaConstants;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.apache.directory.shared.ldap.schema.registries.Registries;
import org.apache.directory.shared.ldap.util.StringTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A comparator that sorts OIDs based on their numeric id value.  Needs a 
 * OidRegistry to properly do it's job.  Public method to set the oid 
 * registry will be used by the server after instantiation in deserialization.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class NameOrNumericIdComparator extends LdapComparator<String>
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( NameOrNumericIdComparator.class );

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /** A reference to the global registries */ 
    private transient Registries registries;

    
    /**
     * The IntegerOrderingComparator constructor. Its OID is the IntegerOrderingMatch matching
     * rule OID.
     */
    public NameOrNumericIdComparator( Registries registries )
    {
        super( SchemaConstants.NAME_OR_NUMERIC_ID_MATCH_OID );
        this.registries = registries;
    }
    
    
    /**
     * Implementation of the Compare method
     */
    public int compare( String o1, String o2 )
    {
        String s1 = getNumericIdString( o1 );
        String s2 = getNumericIdString( o2 );

        if ( s1 == null && s2 == null )
        {
            return 0;
        }
        
        if ( s1 == null )
        {
            return -1;
        }
        
        if ( s2 == null )
        {
            return 1;
        }
        
        return s1.compareTo( s2 );
    }
    
    
    public void setRegistries( Registries registries )
    {
        this.registries = registries;
    }
    
    
    String getNumericIdString( Object obj )
    {
        String strValue;

        if ( obj == null )
        {
            return null;
        }
        
        if ( obj instanceof String )
        {
            strValue = ( String ) obj;
        }
        else if ( obj instanceof byte[] )
        {
            strValue = StringTools.utf8ToString( ( byte[] ) obj ); 
        }
        else
        {
            strValue = obj.toString();
        }
        
        if ( strValue.length() == 0 )
        {
            return "";
        }

        String oid = registries.getOid( strValue );
        
        if ( oid != null )
        {
            return oid;
        }
        
        String msg =  "Failed to lookup OID for " + strValue;
        LOG.warn( msg );
        throw new RuntimeException( msg );
    }
}
