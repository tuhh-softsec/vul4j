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


import org.apache.directory.shared.asn1.util.OID;
import org.apache.directory.shared.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.api.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.ExtendedResponse;
import org.apache.directory.shared.ldap.model.message.ExtendedResponseImpl;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.util.Strings;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for ExtendedResponse
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ExtendedResponseDsml extends AbstractResultResponseDsml<ExtendedResponse>
{
    /**
     * Creates a new getDecoratedMessage() of ExtendedResponseDsml.
     */
    public ExtendedResponseDsml( ILdapCodecService codec )
    {
        super( codec, new ExtendedResponseImpl( "" ) );
    }


    /**
     * Creates a new getDecoratedMessage() of ExtendedResponseDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public ExtendedResponseDsml( ILdapCodecService codec, ExtendedResponse ldapMessage )
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
        Element element = root.addElement( "extendedResponse" );
        ExtendedResponse extendedResponse = ( ExtendedResponse ) getDecorated();

        // LDAP Result
        LdapResultDsml ldapResultDsml = new LdapResultDsml( getCodecService(), 
            getDecorated().getLdapResult(), getDecorated() );
        ldapResultDsml.toDsml( element );

        // ResponseName
        String responseName = extendedResponse.getResponseName();
        if ( responseName != null )
        {
            element.addElement( "responseName" ).addText( responseName );
        }

        // Response
        Object response = extendedResponse.getResponseValue();

        if ( response != null )
        {
            if ( ParserUtils.needsBase64Encoding( response ) )
            {
                Namespace xsdNamespace = new Namespace( ParserUtils.XSD, ParserUtils.XML_SCHEMA_URI );
                Namespace xsiNamespace = new Namespace( ParserUtils.XSI, ParserUtils.XML_SCHEMA_INSTANCE_URI );
                element.getDocument().getRootElement().add( xsdNamespace );
                element.getDocument().getRootElement().add( xsiNamespace );

                Element responseElement = element.addElement( "response" )
                    .addText( ParserUtils.base64Encode( response ) );
                responseElement.addAttribute( new QName( "type", xsiNamespace ), ParserUtils.XSD + ":"
                    + ParserUtils.BASE64BINARY );
            }
            else
            {
                element.addElement( "response" ).addText( Strings.utf8ToString((byte[]) response) );
            }
        }

        return element;
    }


    /**
     * Get the extended response name
     * 
     * @return Returns the name.
     */
    public String getResponseName()
    {
        return ( ( ExtendedResponse ) getDecorated() ).getResponseName();
    }


    /**
     * Set the extended response name
     * 
     * @param responseName The name to set.
     */
    public void setResponseName( OID responseName )
    {
        ( ( ExtendedResponse ) getDecorated() ).setResponseName( responseName.toString() );
    }


    /**
     * Get the extended response
     * 
     * @return Returns the response.
     */
    public Object getResponseValue()
    {
        return ( ( ExtendedResponse ) getDecorated() ).getResponseValue();
    }


    /**
     * Set the extended response
     * 
     * @param response The response to set.
     */
    public void setResponseValue( byte[] response )
    {
        ( ( ExtendedResponse ) getDecorated() ).setResponseValue( response );
    }
}
