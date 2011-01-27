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
package org.apache.directory.shared.ldap.codec.decorators;


import org.apache.directory.shared.ldap.model.message.LdapResult;
import org.apache.directory.shared.ldap.model.message.Referral;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.model.name.Dn;


/**
 * A decorator for the LdapResultResponse message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapResultDecorator implements LdapResult
{
    /** The decorated LdapResult */
    private final LdapResult decoratedLdapResult;

    /** Temporary storage for message bytes */
    private byte[] errorMessageBytes;

    /** Temporary storage of the byte[] representing the matchedDN */
    private byte[] matchedDnBytes;


    /**
     * Makes a LdapResult encodable.
     *
     * @param decoratedLdapResult the decorated LdapResult
     */
    public LdapResultDecorator( LdapResult decoratedLdapResult )
    {
        this.decoratedLdapResult = decoratedLdapResult;
    }


    /**
     * @return The decorated LdapResult
     */
    public LdapResult getLdapResult()
    {
        return decoratedLdapResult;
    }


    /**
     * @return The encoded Error message
     */
    public  byte[] getErrorMessageBytes()
    {
        return errorMessageBytes;
    }


    /**
     * Set the encoded message's bytes
     * @param errorMessageBytes The encoded bytes
     */
    public void setErrorMessageBytes( byte[] errorMessageBytes )
    {
        this.errorMessageBytes = errorMessageBytes;
    }


    /**
     * Sets the encoded value for MatchedDn
     *
     * @param matchedDnBytes The encoded MatchedDN
     */
    public void setMatchedDnBytes( byte[] matchedDnBytes )
    {
        this.matchedDnBytes = matchedDnBytes;
    }


    /**
     * @return the encoded MatchedDN
     */
    public byte[] getMatchedDnBytes()
    {
        return matchedDnBytes;
    }


    //-------------------------------------------------------------------------
    // The LdapResult methods
    //-------------------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public ResultCodeEnum getResultCode()
    {
        return decoratedLdapResult.getResultCode();
    }


    /**
     * {@inheritDoc}
     */
    public void setResultCode( ResultCodeEnum resultCode )
    {
        decoratedLdapResult.setResultCode( resultCode );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getMatchedDn()
    {
        return decoratedLdapResult.getMatchedDn();
    }


    /**
     * {@inheritDoc}
     */
    public void setMatchedDn( Dn dn )
    {
        decoratedLdapResult.setMatchedDn( dn );
    }


    /**
     * {@inheritDoc}
     */
    public String getErrorMessage()
    {
        return decoratedLdapResult.getErrorMessage();
    }


    /**
     * {@inheritDoc}
     */
    public void setErrorMessage( String errorMessage )
    {
        decoratedLdapResult.setErrorMessage( errorMessage );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isReferral()
    {
        return decoratedLdapResult.isReferral();
    }


    /**
     * {@inheritDoc}
     */
    public Referral getReferral()
    {
        return decoratedLdapResult.getReferral();
    }


    /**
     * {@inheritDoc}
     */
    public void setReferral( Referral referral )
    {
        decoratedLdapResult.setReferral( referral );
    }

    
    /**
     * {@inheritDoc}
     */
    public String toString()
    {
        return decoratedLdapResult.toString();
    }
}
