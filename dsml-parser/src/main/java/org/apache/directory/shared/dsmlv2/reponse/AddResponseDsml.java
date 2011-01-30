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

package org.apache.directory.shared.dsmlv2.reponse;


import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.AddResponse;
import org.apache.directory.shared.ldap.model.message.AddResponseImpl;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.dom4j.Element;


/**
 * DSML Decorator for AddResponse
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class AddResponseDsml extends AbstractResultResponseDsml<AddResponse>
{
    /**
     * Creates a new getDecoratedMessage() of AddResponseDsml.
     */
    public AddResponseDsml( ILdapCodecService codec )
    {
        super( codec, new AddResponseImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of AddResponseDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public AddResponseDsml( ILdapCodecService codec, AddResponse ldapMessage )
    {
        super( codec, ldapMessage );
    }


    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getType()
    {
        return getDecorated().getType();
    }


    /**
     * {@inheritDoc}
     */
    public Element toDsml( Element root )
    {
        Element element = root.addElement( "addResponse" );

        LdapResultDsml ldapResultDsml = new LdapResultDsml( ( ( AddResponse ) getDecorated() ).getLdapResult(), getDecorated() );
        ldapResultDsml.toDsml( element );
        return element;
    }
}
