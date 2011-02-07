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


import org.apache.directory.shared.ldap.codec.api.LdapCodecService;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.message.CompareRequest;
import org.apache.directory.shared.ldap.model.message.CompareRequestImpl;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.dom4j.Element;


/**
 * DSML Decorator for CompareRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompareRequestDsml 
    extends AbstractResultResponseRequestDsml<CompareRequest>
    implements CompareRequest
{
    /**
     * Creates a new getDecoratedMessage() of CompareRequestDsml.
     */
    public CompareRequestDsml( LdapCodecService codec )
    {
        super( codec, new CompareRequestImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of CompareRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public CompareRequestDsml( LdapCodecService codec, CompareRequest ldapMessage )
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

        CompareRequest request = ( CompareRequest ) getDecorated();

        // Dn
        if ( request.getName() != null )
        {
            element.addAttribute( "dn", request.getName().getName() );
        }

        // Assertion
        Element assertionElement = element.addElement( "assertion" );
        if ( request.getAttributeId() != null )
        {
            assertionElement.addAttribute( "name", request.getAttributeId() );
        }
        if ( request.getAssertionValue() != null )
        {
            assertionElement.addElement( "value" ).setText( request.getAssertionValue().getString() );
        }

        return element;
    }


    /**
     * Get the entry to be compared
     * 
     * @return Returns the entry.
     */
    public Dn getName()
    {
        return getDecorated().getName();
    }


    /**
     * Set the entry to be compared
     * 
     * @param entry The entry to set.
     */
    public void setName( Dn entry )
    {
        getDecorated().setName( entry );
    }


    /**
     * Set the assertion value
     * 
     * @param assertionValue The assertionValue to set.
     */
    public void setAssertionValue( Object assertionValue )
    {
        if ( assertionValue instanceof String )
        {
            getDecorated().setAssertionValue( ( String ) assertionValue );
        }
        else
        {
            getDecorated().setAssertionValue( ( byte[] ) assertionValue );
        }
    }


    /**
     * Get the attribute description
     * 
     * @return Returns the attributeDesc.
     */
    public String getAttributeDesc()
    {
        return getDecorated().getAttributeId();
    }


    /**
     * Set the attribute description
     * 
     * @param attributeDesc The attributeDesc to set.
     */
    public void setAttributeDesc( String attributeDesc )
    {
        getDecorated().setAttributeId( attributeDesc );
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
    public void setAssertionValue( String value )
    {
        getDecorated().setAssertionValue( value );
    }


    /**
     * {@inheritDoc}
     */
    public void setAssertionValue( byte[] value )
    {
        getDecorated().setAssertionValue( value );
    }


    /**
     * {@inheritDoc}
     */
    public String getAttributeId()
    {
        return getDecorated().getAttributeId();
    }


    /**
     * {@inheritDoc}
     */
    public void setAttributeId( String attrId )
    {
        getDecorated().setAttributeId( attrId );
    }


    /**
     * {@inheritDoc}
     */
    public Value<?> getAssertionValue()
    {
        return getDecorated().getAssertionValue();
    }
}
