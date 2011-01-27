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
package org.apache.directory.shared.ldap.codec.search.controls.pagedSearch;


import org.apache.directory.shared.ldap.codec.controls.BasicControlImpl;
import org.apache.directory.shared.util.StringConstants;
import org.apache.directory.shared.util.Strings;

import java.util.Arrays;


/**
 * A request/response control used to implement a simple paging of search
 * results. This is an implementation of RFC 2696 :
 * <a href="http://www.faqs.org/rfcs/rfc2696.html">LDAP Control Extension for Simple Paged Results Manipulation</a>
 * <br/>
 * <pre>
 *    This control is included in the searchRequest and searchResultDone
 *    messages as part of the controls field of the LDAPMessage, as defined
 *    in Section 4.1.12 of [LDAPv3]. The structure of this control is as
 *    follows:
 *
 * pagedResultsControl ::= SEQUENCE {
 *         controlType     1.2.840.113556.1.4.319,
 *         criticality     BOOLEAN DEFAULT FALSE,
 *         controlValue    searchControlValue
 * }
 * 
 * The searchControlValue is an OCTET STRING wrapping the BER-encoded
 * version of the following SEQUENCE:
 * 
 * realSearchControlValue ::= SEQUENCE {
 *         size            INTEGER (0..maxInt),
 *                                 -- requested page size from client
 *                                 -- result set size estimate from server
 *         cookie          OCTET STRING
 * }
 * 
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SimplePagedResults extends BasicControlImpl implements PagedResults
{

    /** The number of entries to return, or returned */
    private int size;

    /** The exchanged cookie */
    private byte[] cookie = StringConstants.EMPTY_BYTES;


    /**
     * Creates a new instance of PagedResultsDecorator.
     */
    public SimplePagedResults()
    {
        super( OID );
    }


    public int getSize()
    {
        return size;
    }


    public void setSize( int size )
    {
        this.size = size;
    }


    public byte[] getCookie()
    {
        return cookie;
    }


    public void setCookie( byte[] cookie )
    {
        this.cookie = cookie;
    }


    public int getCookieValue()
    {
        int value = 0;

        switch ( cookie.length )
        {
            case 1:
                value = cookie[0] & 0x00FF;
                break;

            case 2:
                value = ( ( cookie[0] & 0x00FF ) << 8 ) + ( cookie[1] & 0x00FF );
                break;

            case 3:
                value = ( ( cookie[0] & 0x00FF ) << 16 ) + ( ( cookie[1] & 0x00FF ) << 8 ) + ( cookie[2] & 0x00FF );
                break;

            case 4:
                value = ( ( cookie[0] & 0x00FF ) << 24 ) + ( ( cookie[1] & 0x00FF ) << 16 )
                    + ( ( cookie[2] & 0x00FF ) << 8 ) + ( cookie[3] & 0x00FF );
                break;

        }

        return value;
    }


    /**
     * @see Object#equals(Object)
     */
    @SuppressWarnings( { "EqualsWhichDoesntCheckParameterClass" } )
    @Override
    public boolean equals( Object o )
    {
        if ( !super.equals( o ) )
        {
            return false;
        }

        PagedResults otherControl = ( PagedResults ) o;

        return ( size == otherControl.getSize() ) && Arrays.equals( cookie, otherControl.getCookie() );
    }


    /**
     * Return a String representing this PagedSearchControl.
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();

        sb.append( "    Paged Search Control\n" );
        sb.append( "        oid : " ).append( getOid() ).append( '\n' );
        sb.append( "        critical : " ).append( isCritical() ).append( '\n' );
        sb.append( "        size   : '" ).append( size ).append( "'\n" );
        sb.append( "        cookie   : '" ).append( Strings.dumpBytes(cookie) ).append( "'\n" );

        return sb.toString();
    }
}
