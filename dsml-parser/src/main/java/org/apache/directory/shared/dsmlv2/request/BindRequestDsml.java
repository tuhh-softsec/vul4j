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
package org.apache.directory.shared.dsmlv2.request;


import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.model.message.BindRequest;
import org.apache.directory.shared.ldap.model.message.BindRequestImpl;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.dom4j.Element;


/**
 * DSML Decorator for BindRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class BindRequestDsml 
    extends AbstractResultResponseRequestDsml<BindRequest>
    implements BindRequest
{
    /**
     * Creates a new getDecoratedMessage() of AuthRequestDsml.
     */
    public BindRequestDsml( LdapCodecService codec )
    {
        super( codec, new BindRequestImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of AuthRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public BindRequestDsml( LdapCodecService codec, BindRequest ldapMessage )
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
        Element element = super.toDsml( root );

        BindRequest request = ( BindRequest ) getDecorated();

        // AbandonID
        String name = request.getName().getName();
        if ( ( name != null ) && ( !"".equals( name ) ) )
        {
            element.addAttribute( "principal", name );
        }

        return element;
    }


    /**
     * {@inheritDoc}
     */
    public MessageTypeEnum getResponseType()
    {
        return getDecorated().getResponseType();
    }


    /**
     * {@inheritDoc}
     */
    public boolean isSimple()
    {
        return getDecorated().isSimple();
    }


    /**
     * {@inheritDoc}
     */
    public boolean getSimple()
    {
        return getDecorated().getSimple();
    }


    /**
     * {@inheritDoc}
     */
    public void setSimple( boolean isSimple )
    {
        getDecorated().setSimple( isSimple );
    }


    /**
     * {@inheritDoc}
     */
    public byte[] getCredentials()
    {
        return getDecorated().getCredentials();
    }


    /**
     * {@inheritDoc}
     */
    public void setCredentials( String credentials )
    {
        getDecorated().setCredentials( credentials );
    }


    /**
     * {@inheritDoc}
     */
    public void setCredentials( byte[] credentials )
    {
        getDecorated().setCredentials( credentials );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getDecorated().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getDecorated().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isVersion3()
    {
        return getDecorated().isVersion3();
    }


    /**
     * {@inheritDoc}
     */
    public boolean getVersion3()
    {
        return getDecorated().getVersion3();
    }


    /**
     * {@inheritDoc}
     */
    public void setVersion3( boolean isVersion3 )
    {
        getDecorated().setVersion3( isVersion3 );
    }


    /**
     * {@inheritDoc}
     */
    public String getSaslMechanism()
    {
        return getDecorated().getSaslMechanism();
    }


    /**
     * {@inheritDoc}
     */
    public void setSaslMechanism( String saslMechanism )
    {
        getDecorated().setSaslMechanism( saslMechanism );
    }
}
