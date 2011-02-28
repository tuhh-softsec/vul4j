/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.extras.extended.ads_impl;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestDecorator;
import org.apache.directory.shared.ldap.codec.api.ExtendedRequestFactory;
import org.apache.directory.shared.ldap.codec.api.ExtendedResponseDecorator;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.extended.GracefulShutdownRequestImpl;
import org.apache.directory.shared.ldap.extras.extended.GracefulShutdownResponseImpl;
import org.apache.directory.shared.ldap.extras.extended.GracefulShutdownRequest;
import org.apache.directory.shared.ldap.extras.extended.GracefulShutdownResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedRequest;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;


/**
 * An {@link ExtendedRequestFactory} for creating cancel extended request response 
 * pairs.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class GracefulShutdownFactory 
    implements ExtendedRequestFactory<GracefulShutdownRequest, GracefulShutdownResponse>
{
    private LdapCodecService codec;
    
    
    public GracefulShutdownFactory( LdapCodecService codec )
    {
        this.codec = codec;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public String getOid()
    {
        return GracefulShutdownRequest.EXTENSION_OID;
    }

    
    /**
     * {@inheritDoc}
     */
    public GracefulShutdownRequest newRequest()
    {
        return new GracefulShutdownRequestDecorator( codec, new GracefulShutdownRequestImpl() );
    }


    /**
     * {@inheritDoc}
     */
    public GracefulShutdownResponse newResponse( byte[] encodedValue ) throws DecoderException
    {
        GracefulShutdownResponseDecorator response = new GracefulShutdownResponseDecorator( 
            codec, new GracefulShutdownResponseImpl() );
        response.setResponseValue( encodedValue );
        return response;
    }


    /**
     * {@inheritDoc}
     */
    public GracefulShutdownRequest newRequest( byte[] value )
    {
        GracefulShutdownRequestDecorator req = new GracefulShutdownRequestDecorator( codec, new GracefulShutdownRequestImpl() );
        req.setRequestValue( value );
        return req;
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedRequestDecorator<GracefulShutdownRequest, GracefulShutdownResponse> decorate(
        ExtendedRequest<?> modelRequest )
    {
        if ( modelRequest instanceof GracefulShutdownRequestDecorator )
        {
            return ( GracefulShutdownRequestDecorator ) modelRequest;
        }

        return new GracefulShutdownRequestDecorator( codec, ( GracefulShutdownRequest ) modelRequest );
    }


    /**
     * {@inheritDoc}
     */
    public ExtendedResponseDecorator<GracefulShutdownResponse> decorate( ExtendedResponse decoratedMessage )
    {
        if ( decoratedMessage instanceof GracefulShutdownResponseDecorator )
        {
            return ( GracefulShutdownResponseDecorator ) decoratedMessage;
        }

        return new GracefulShutdownResponseDecorator( codec, ( GracefulShutdownResponse ) decoratedMessage );
    }
}
