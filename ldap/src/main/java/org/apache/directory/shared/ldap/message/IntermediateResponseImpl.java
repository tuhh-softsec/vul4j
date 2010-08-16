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


import java.util.Arrays;

import org.apache.directory.shared.ldap.message.internal.InternalAbstractResultResponse;
import org.apache.directory.shared.ldap.message.internal.IntermediateResponse;
import org.apache.directory.shared.ldap.util.StringTools;


/**
 * IntermediateResponse implementation
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 */
public class IntermediateResponseImpl extends InternalAbstractResultResponse implements IntermediateResponse
{
    static final long serialVersionUID = -6646752766410531060L;

    /** ResponseName for the intermediate response */
    protected String responseName;

    /** The response name as a byte[] */
    private byte[] responseNameBytes;

    /** Response Value for the intermediate response */
    protected byte[] responseValue;

    /** The encoded intermediateResponse length */
    private int intermediateResponseLength;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    public IntermediateResponseImpl( int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // IntermediateResponse Interface Method Implementations
    // ------------------------------------------------------------------------

    /**
     * Gets the reponseName specific encoded
     * 
     * @return the response value
     */
    public byte[] getResponseValue()
    {
        if ( responseValue == null )
        {
            return null;
        }

        final byte[] copy = new byte[responseValue.length];
        System.arraycopy( responseValue, 0, copy, 0, responseValue.length );
        return copy;
    }


    /**
     * Sets the response value
     * 
     * @param value the response value.
     */
    public void setResponseValue( byte[] value )
    {
        if ( value != null )
        {
            this.responseValue = new byte[value.length];
            System.arraycopy( value, 0, this.responseValue, 0, value.length );
        }
        else
        {
            this.responseValue = null;
        }
    }


    /**
     * Gets the OID uniquely identifying this Intermediate response (a.k.a. its
     * name).
     * 
     * @return the OID of the Intermediate response type.
     */
    public String getResponseName()
    {
        return ( ( responseName == null ) ? "" : responseName );
    }


    /**
     * Gets the ResponseName bytes
     * 
     * @return the ResponseName bytes of the Intermediate response type.
     */
    /* No qualifier */byte[] getResponseNameBytes()
    {
        return responseNameBytes;
    }


    /**
     * Sets the OID uniquely identifying this Intermediate response (a.k.a. its
     * name).
     * 
     * @param oid the OID of the Intermediate response type.
     */
    public void setResponseName( String oid )
    {
        this.responseName = oid;
    }


    /**
     * Sets the ResponseName bytes
     * 
     * @param oid the ResponseName bytes of the Intermediate response type.
     */
    /* No qualifier */void setResponseNameBytes( byte[] responseNameBytes )
    {
        this.responseNameBytes = responseNameBytes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        if ( responseName != null )
        {
            hash = hash * 17 + responseName.hashCode();
        }
        if ( responseValue != null )
        {
            hash = hash * 17 + Arrays.hashCode( responseValue );
        }
        hash = hash * 17 + super.hashCode();

        return hash;
    }


    /**
     * Checks to see if an object equals this IntemediateResponse.
     * 
     * @param obj the object to be checked for equality
     * @return true if the obj equals this IntemediateResponse, false otherwise
     */
    public boolean equals( Object obj )
    {
        if ( obj == this )
        {
            return true;
        }

        if ( !super.equals( obj ) )
        {
            return false;
        }

        if ( !( obj instanceof IntermediateResponse ) )
        {
            return false;
        }

        IntermediateResponse resp = ( IntermediateResponse ) obj;

        if ( ( responseName != null ) && ( resp.getResponseName() == null ) )
        {
            return false;
        }

        if ( ( responseName == null ) && ( resp.getResponseName() != null ) )
        {
            return false;
        }

        if ( ( responseName != null ) && ( resp.getResponseName() != null )
            && !responseName.equals( resp.getResponseName() ) )
        {
            return false;
        }

        if ( ( responseValue != null ) && ( resp.getResponseValue() == null ) )
        {
            return false;
        }

        if ( ( responseValue == null ) && ( resp.getResponseValue() != null ) )
        {
            return false;
        }

        if ( ( responseValue != null ) && ( resp.getResponseValue() != null )
            && !Arrays.equals( responseValue, resp.getResponseValue() ) )
        {
            return false;
        }

        return true;
    }


    /**
     * Stores the encoded length for the IntermediateResponse
     * 
     * @param intermediateResponseLength The encoded length
     */
    /* No qualifier*/void setIntermediateResponseLength( int intermediateResponseLength )
    {
        this.intermediateResponseLength = intermediateResponseLength;
    }


    /**
     * @return The encoded IntermediateResponse's length
     */
    /* No qualifier*/int getIntermediateResponseLength()
    {
        return intermediateResponseLength;
    }


    /**
     * Get a String representation of an IntermediateResponse
     * 
     * @return An IntermediateResponse String
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "    Intermediate Response\n" );

        if ( responseName != null )
        {
            sb.append( "        Response name :'" ).append( responseName ).append( "'\n" );
        }

        if ( responseValue != null )
        {
            sb.append( "        ResponseValue :'" );
            sb.append( StringTools.dumpBytes( responseValue ) );
            sb.append( "'\n" );
        }

        sb.append( super.toString() );

        return sb.toString();
    }
}
