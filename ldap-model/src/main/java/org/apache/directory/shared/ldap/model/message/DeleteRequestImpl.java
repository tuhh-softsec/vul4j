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
package org.apache.directory.shared.ldap.model.message;


import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * Delete request implementation.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class DeleteRequestImpl extends AbstractAbandonableRequest implements DeleteRequest
{
    static final long serialVersionUID = 3187847454305567542L;

    /** The distinguished name of the entry to delete */
    private Dn name;

    /** The deleteResponse associated with this request */
    private DeleteResponse response;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates a DeleteRequest implementing object used to delete a
     * leaf entry from the DIT.
     */
    public DeleteRequestImpl()
    {
        super( -1, TYPE );
    }


    /**
     * Creates a DeleteRequest implementing object used to delete a
     * leaf entry from the DIT.
     * 
     * @param id the sequential message identifier
     */
    public DeleteRequestImpl( final int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // DeleteRequest Interface Method Implementations
    // ------------------------------------------------------------------------

    /**
     * Gets the distinguished name of the leaf entry to be deleted by this
     * request.
     * 
     * @return the Dn of the leaf entry to delete.
     */
    public Dn getName()
    {
        return name;
    }


    /**
     * Sets the distinguished name of the leaf entry to be deleted by this
     * request.
     * 
     * @param name the Dn of the leaf entry to delete.
     */
    public void setName( Dn name )
    {
        this.name = name;
    }


    // ------------------------------------------------------------------------
    // SingleReplyRequest Interface Method Implementations
    // ------------------------------------------------------------------------

    /**
     * Gets the protocol response message type for this request which produces
     * at least one response.
     * 
     * @return the message type of the response.
     */
    public MessageTypeEnum getResponseType()
    {
        return RESP_TYPE;
    }


    /**
     * The result containing response for this request.
     * 
     * @return the result containing response for this request
     */
    public ResultResponse getResultResponse()
    {
        if ( response == null )
        {
            response = new DeleteResponseImpl( getMessageId() );
        }

        return response;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;

        if ( name != null )
        {
            hash = hash * 17 + name.hashCode();
        }

        hash = hash * 17 + super.hashCode();

        return hash;
    }


    /**
     * Checks to see if an object is equivalent to this DeleteRequest. First
     * there's a quick test to see if the obj is the same object as this one -
     * if so true is returned. Next if the super method fails false is returned.
     * Then the name of the entry is compared - if not the same false is
     * returned. Finally the method exists returning true.
     * 
     * @param obj the object to test for equality to this
     * @return true if the obj is equal to this DeleteRequest, false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( this == obj )
        {
            return true;
        }

        if ( !super.equals( obj ) )
        {
            return false;
        }

        DeleteRequest req = ( DeleteRequest ) obj;

        if ( name != null && req.getName() == null )
        {
            return false;
        }

        if ( name == null && req.getName() != null )
        {
            return false;
        }

        if ( ( name != null ) && ( req.getName() != null ) && !name.equals( req.getName() ) )
        {
            return false;
        }

        return true;
    }


    /**
     * Return a String representing a DelRequest
     * 
     * @return A DelRequest String
     */
    public String toString()
    {

        StringBuilder sb = new StringBuilder();

        sb.append( "    Del request\n" );
        sb.append( "        Entry : '" ).append( name.toString() ).append( "'\n" );
        sb.append( super.toString() );

        return sb.toString();
    }
}
