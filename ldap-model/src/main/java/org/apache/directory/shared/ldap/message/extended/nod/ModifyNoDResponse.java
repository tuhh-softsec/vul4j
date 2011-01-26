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
package org.apache.directory.shared.ldap.message.extended.nod;


import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.message.ModifyResponseImpl;
import org.apache.directory.shared.ldap.model.message.ResultCodeEnum;
import org.apache.directory.shared.ldap.message.extended.NoticeOfDisconnect;


/**
 * An extended operation intended for notifying clients of upcoming
 * disconnection for the Modify response. 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyNoDResponse extends ModifyResponseImpl
{
    /** The OID of the NotiveOfDisconnect extended operation. */
    public static final String EXTENSION_OID = NoticeOfDisconnect.EXTENSION_OID;

    /** The empty response */
    private static final byte[] EMPTY_RESPONSE = new byte[0];

    /** The single instance with unavailable result code. */
    public static final ModifyNoDResponse UNAVAILABLE = new ModifyNoDResponse( ResultCodeEnum.UNAVAILABLE );

    /** The single instance with protocolError result code. */
    public static final ModifyNoDResponse PROTOCOLERROR = new ModifyNoDResponse( ResultCodeEnum.PROTOCOL_ERROR );

    /** The single instance with strongAuthRequired result code. */
    public static final ModifyNoDResponse STRONGAUTHREQUIRED = new ModifyNoDResponse(
        ResultCodeEnum.STRONG_AUTH_REQUIRED );


    /**
     * Creates a new instance of NoticeOfDisconnect.
     */
    private ModifyNoDResponse( ResultCodeEnum rcode )
    {
        super();

        switch ( rcode )
        {
            case UNAVAILABLE:
                break;

            case PROTOCOL_ERROR:
                break;

            case STRONG_AUTH_REQUIRED:
                break;

            default:
                throw new IllegalArgumentException( I18n.err( I18n.ERR_04166, ResultCodeEnum.UNAVAILABLE,
                    ResultCodeEnum.PROTOCOL_ERROR, ResultCodeEnum.STRONG_AUTH_REQUIRED ) );
        }

        super.getLdapResult().setErrorMessage( rcode.toString() + ": The server will disconnect!" );
        super.getLdapResult().setMatchedDn( null );
        super.getLdapResult().setResultCode( rcode );
    }


    // ------------------------------------------------------------------------
    // ExtendedResponse Interface Method Implementations
    // ------------------------------------------------------------------------
    /**
     * Gets the reponse OID specific encoded response values.
     * 
     * @return the response specific encoded response values.
     */
    public byte[] getResponse()
    {
        return EMPTY_RESPONSE;
    }


    /**
     * Gets the OID uniquely identifying this extended response (a.k.a. its
     * name).
     * 
     * @return the OID of the extended response type.
     */
    public String getResponseName()
    {
        return EXTENSION_OID;
    }
}
