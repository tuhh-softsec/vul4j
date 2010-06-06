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


import org.apache.directory.shared.ldap.schema.LdapComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A class for the bitStringMatch matchingRule (RFC 4517, par. 4.2.1)
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BitStringComparator extends LdapComparator<String>
{
    /** A logger for this class */
    private static final Logger LOG = LoggerFactory.getLogger( BitStringComparator.class );

    /** The serialVersionUID */
    private static final long serialVersionUID = 1L;


    /**
     * The BitStringComparator constructor. Its OID is the IntegerOrderingMatch matching
     * rule OID.
     */
    public BitStringComparator( String oid )
    {
        super( oid );
    }


    /**
     * Implementation of the Compare method
     */
    public int compare( String bs1, String bs2 )
    {
        LOG.debug( "comparing BitString objects '{}' with '{}'", bs1, bs2 );

        // First, shortcut the process by comparing
        // references. If they are equals, then bs1 and bs2
        // reference the same object
        if ( bs1 == bs2 )
        {
            return 0;
        }

        // Then, deal with one of bs1 or bs2 being null
        // Both can't be null, because then they would 
        // have been caught by the previous test
        if ( ( bs1 == null ) || ( bs2 == null ) )
        {
            return ( bs1 == null ? -1 : 1 );
        }

        // We have to get rid of 0 from left of each BitString
        char[] array1 = bs1.toCharArray();
        char[] array2 = bs2.toCharArray();

        int pos1 = bs1.indexOf( '1' );
        int pos2 = bs2.indexOf( '1' );

        if ( pos1 == -1 )
        {
            if ( pos2 == -1 )
            {
                return 0;
            }
            else
            {
                return -1;
            }
        }
        else if ( pos2 == -1 )
        {
            return 1;
        }

        int length1 = array1.length - pos1;
        int length2 = array2.length - pos2;

        if ( length1 == length2 )
        {
            for ( int i = 0; i < length1; i++ )
            {
                int i1 = i + pos1;
                int i2 = i + pos2;

                if ( array1[i1] < array2[i2] )
                {
                    return -1;
                }
                else if ( array1[i1] > array2[i2] )
                {
                    return 1;
                }
            }

            return 0;
        }

        if ( length1 < length2 )
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }
}
