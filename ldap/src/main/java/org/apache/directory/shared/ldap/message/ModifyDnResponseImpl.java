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
package org.apache.directory.shared.ldap.message;


import org.apache.directory.shared.ldap.message.internal.InternalAbstractResultResponse;
import org.apache.directory.shared.ldap.message.internal.InternalModifyDnResponse;


/**
 * ModifyDnResponse implementation
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 */
public class ModifyDnResponseImpl extends InternalAbstractResultResponse implements InternalModifyDnResponse
{
    /** The encoded modifyDnResponse length */
    private int modifyDnResponseLength;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    static final long serialVersionUID = 996870775343263543L;


    /**
     * Creates a ModifyDnResponse as a reply to an ModifyDnRequest.
     * 
     * @param id the sequence if of this response
     */
    public ModifyDnResponseImpl( final int id )
    {
        super( id, TYPE );
    }


    /**
     * @return The encoded ModifyDnResponse's length
     */
    /* No Qualifier*/void setModifyDnResponseLength( int modifyDnResponseLength )
    {
        this.modifyDnResponseLength = modifyDnResponseLength;
    }


    /**
     * Stores the encoded length for the ModifyDnResponse
     * @param modifyDnResponseLength The encoded length
     */
    /* No Qualifier*/int getModifyDnResponseLength()
    {
        return modifyDnResponseLength;
    }


    /**
     * Get a String representation of a ModifyDNResponse
     * 
     * @return A ModifyDNResponse String
     */
    public String toString()
    {

        StringBuilder sb = new StringBuilder();

        sb.append( "    Modify DN Response\n" );
        sb.append( super.toString() );

        return sb.toString();
    }
}
