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


import java.util.Arrays;

import org.apache.directory.shared.util.Strings;


/**
 * ExtendedResponse implementation
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 */
public class ExtendedResponseImpl extends AbstractResultResponse implements ExtendedResponse
{
    static final long serialVersionUID = -6646752766410531060L;

    /** Object identifier for the extended response */
    protected String responseName;

    /** Value encoded in the extended response payload */
    protected byte[] responseValue;


    /**
     * Creates an ExtendedResponse as a reply to an ExtendedRequest.
     * 
     * @param responseName the ExtendedResponse's name
     */
    public ExtendedResponseImpl( String responseName )
    {
        super( -1, TYPE );
        this.responseName = responseName;
    }


    /**
     * Creates an ExtendedResponse as a reply to an ExtendedRequest.
     * 
     * @param id the session unique message id
     * @param responseName the ExtendedResponse's name
     */
    public ExtendedResponseImpl( final int id, String responseName )
    {
        super( id, TYPE );
        this.responseName = responseName;
    }


    /**
     * Creates an ExtendedResponse as a reply to an ExtendedRequest.
     * 
     * @param id the session unique message id
     */
    public ExtendedResponseImpl( int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // ExtendedResponse Interface Method Implementations
    // ------------------------------------------------------------------------
    /**
     * Sets the response OID specific encoded response value.
     * 
     * @param responseValue the response specific encoded response values.
     */
    public void setResponseValue( byte[] responseValue )
    {
        if ( responseValue != null )
        {
            this.responseValue = new byte[responseValue.length];
            System.arraycopy( responseValue, 0, this.responseValue, 0, responseValue.length );
        }
        else
        {
            this.responseValue = null;
        }
    }


    /**
     * Sets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @param responseName the OID of the extended response type.
     */
    public void setResponseName( String responseName )
    {
        this.responseName = responseName;
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
     * Checks to see if an object equals this ExtendedRequest.
     * 
     * @param obj
     *            the object to be checked for equality
     * @return true if the obj equals this ExtendedRequest, false otherwise
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

        if ( !( obj instanceof ExtendedResponse ) )
        {
            return false;
        }

        ExtendedResponse resp = ( ExtendedResponse ) obj;

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

        return ( ( responseValue == null ) || ( resp.getResponseValue() == null )
            || Arrays.equals( responseValue, resp.getResponseValue() ) );
    }


    /**
     * Gets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @return the responseName of the extended response
     */
    public String getResponseName()
    {
        return ( ( responseName == null ) ? "" : responseName.toString() );
    }


    /**
     * Gets the response OID specific encoded response values.
     * 
     * @return the response specific encoded response value
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
     * {@inheritDoc}
     * @deprecated Use the {@link #getResponseValue()} method
     */
    public byte[] getEncodedValue()
    {
        return getResponseValue();
    }


    /**
     * {@inheritDoc}
     * @deprecated Use the {@link #getResponseName()} method
     */
    public String getID()
    {
        return getResponseName();
    }


    /**
     * Get a String representation of an ExtendedResponse
     * 
     * @return An ExtendedResponse String
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append( "    Extended Response\n" );

        if ( responseName != null )
        {
            sb.append( "        ResponseName :'" ).append( responseName ).append( "'\n" );
        }

        if ( responseValue != null )
        {
            sb.append( "        ResponseValue :'" ).append( Strings.dumpBytes(responseValue) ).append( "'\n" );
        }

        sb.append( super.toString() );

        return sb.toString();
    }
}
