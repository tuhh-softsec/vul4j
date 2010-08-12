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
import org.apache.directory.shared.ldap.message.internal.InternalExtendedResponse;


/**
 * ExtendedResponse implementation
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 */
public class ExtendedResponseImpl extends InternalAbstractResultResponse implements InternalExtendedResponse
{
    static final long serialVersionUID = -6646752766410531060L;

    /** Object identifier for the extended response */
    protected String oid;

    /** The response name as a byte[] */
    private byte[] oidBytes;

    /** Values encoded in the extended response payload */
    protected byte[] value;

    /** The encoded extendedResponse length */
    private int extendedResponseLength;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------

    /**
     * Creates an ExtendedResponse as a reply to an ExtendedRequest.
     * 
     * @param id the session unique message id
     */
    public ExtendedResponseImpl( final int id, String oid )
    {
        super( id, TYPE );
        this.oid = oid;
    }


    public ExtendedResponseImpl( int id )
    {
        super( id, TYPE );
    }


    // ------------------------------------------------------------------------
    // ExtendedResponse Interface Method Implementations
    // ------------------------------------------------------------------------
    /**
     * Sets the response OID specific encoded response values.
     * 
     * @param value
     *            the response specific encoded response values.
     */
    public void setEncodedValue( byte[] value )
    {
        if ( value != null )
        {
            this.value = new byte[value.length];
            System.arraycopy( value, 0, this.value, 0, value.length );
        }
        else
        {
            this.value = null;
        }
    }


    /**
     * Gets the OID bytes.
     * 
     * @return the OID bytes of the extended response type.
     */
    public byte[] getIDBytes()
    {
        return oidBytes;
    }


    /**
     * Sets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @param oid the OID of the extended response type.
     */
    public void setID( String oid )
    {
        this.oid = oid;
    }


    /**
     * Sets the OID bytes.
     * 
     * @param oidBytes the OID bytes of the extended response type.
     */
    public void setIDBytes( byte[] oidBytes )
    {
        this.oidBytes = oidBytes;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {
        int hash = 37;
        if ( oid != null )
        {
            hash = hash * 17 + oid.hashCode();
        }
        if ( value != null )
        {
            hash = hash * 17 + Arrays.hashCode( value );
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

        if ( !( obj instanceof InternalExtendedResponse ) )
        {
            return false;
        }

        InternalExtendedResponse resp = ( InternalExtendedResponse ) obj;

        if ( ( oid != null ) && ( resp.getID() == null ) )
        {
            return false;
        }

        if ( ( oid == null ) && ( resp.getID() != null ) )
        {
            return false;
        }

        if ( ( oid != null ) && ( resp.getID() != null ) && !oid.equals( resp.getID() ) )
        {
            return false;
        }

        if ( ( value != null ) && ( resp.getEncodedValue() == null ) )
        {
            return false;
        }

        if ( ( value == null ) && ( resp.getEncodedValue() != null ) )
        {
            return false;
        }

        if ( ( value != null ) && ( resp.getEncodedValue() != null ) && !Arrays.equals( value, resp.getEncodedValue() ) )
        {
            return false;
        }

        return true;
    }


    /**
     * Gets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @return the OID of the extended response type.
     */
    public String getID()
    {
        return oid;
    }


    /**
     * Gets the reponse OID specific encoded response values.
     * 
     * @return the response specific encoded response values.
     */
    public byte[] getEncodedValue()
    {
        if ( value == null )
        {
            return null;
        }

        final byte[] copy = new byte[value.length];
        System.arraycopy( value, 0, copy, 0, value.length );
        return copy;
    }


    /**
     * {@inheritDoc}
     */
    public void setExtendedResponseLength( int extendedResponseLength )
    {
        this.extendedResponseLength = extendedResponseLength;
    }


    /**
     * {@inheritDoc}
     */
    public int getExtendedResponseLength()
    {
        return extendedResponseLength;
    }
}
