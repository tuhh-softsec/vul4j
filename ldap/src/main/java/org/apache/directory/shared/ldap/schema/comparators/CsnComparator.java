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


import org.apache.directory.shared.ldap.entry.StringValue;
import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A comparator for CSN.
 *
 * The CSN are ordered depending on an evaluation of its component, in this order :
 * - time, 
 * - changeCount,
 * - sid
 * - modifierNumber
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CsnComparator extends LdapComparator<Object>
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( CsnComparator.class );

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * The CsnComparator constructor. Its OID is the CsnMatch matching
     * rule OID.
     */
    public CsnComparator( String oid )
    {
        super( oid );
    }


    /**
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare( Object csnObj1, Object csnObj2 )
    {
        LOG.debug( "comparing CSN objects '{}' with '{}'", csnObj1, csnObj2 );
        
        if ( csnObj1 == csnObj2 )
        {
            return 0;
        }

        // -------------------------------------------------------------------
        // Handle some basis cases
        // -------------------------------------------------------------------
        if ( csnObj1 == null )
        {
            return ( csnObj2 == null ) ? 0 : -1;
        }
        
        if ( csnObj2 == null )
        {
            return 1;
        }
        
        String csnStr1 = null;
        String csnStr2 = null;
        
        if ( csnObj1 instanceof StringValue )
        {
            csnStr1 = ( ( StringValue ) csnObj1 ).get();
        }
        else
        {
            csnStr1 = csnObj1.toString();
        }

        if ( csnObj2 instanceof StringValue )
        {
            csnStr2 = ( ( StringValue ) csnObj2 ).get();
        }
        else
        {
            csnStr2 = csnObj2.toString();
        }
        
        return csnStr1.compareTo( csnStr2 );
    }
}
