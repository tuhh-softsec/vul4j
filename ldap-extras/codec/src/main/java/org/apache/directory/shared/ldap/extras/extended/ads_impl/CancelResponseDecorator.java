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


import org.apache.directory.shared.ldap.codec.api.ExtendedResponseDecorator;
import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.extras.extended.CancelResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;


/**
 * A Decorator for CancelResponses.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CancelResponseDecorator extends ExtendedResponseDecorator implements CancelResponse
{
    /**
     * Creates a new instance of CancelResponseDecorator.
     *
     * @param codec
     * @param decoratedMessage
     */
    public CancelResponseDecorator( LdapCodecService codec, ExtendedResponse decoratedMessage )
    {
        super( codec, decoratedMessage );
    }
}
