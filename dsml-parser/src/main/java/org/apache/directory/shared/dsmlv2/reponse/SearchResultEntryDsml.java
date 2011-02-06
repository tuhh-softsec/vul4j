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


import org.apache.directory.shared.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.codec.decorators.SearchResultEntryDecorator;
import org.apache.directory.shared.ldap.model.entry.Entry;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.exception.LdapException;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.SearchResultEntry;
import org.apache.directory.shared.ldap.model.message.SearchResultEntryImpl;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for SearchResultEntry
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SearchResultEntryDsml 
    extends AbstractResponseDsml<SearchResultEntry>
    implements SearchResultEntry
{
    /**
     * Creates a new getDecoratedMessage() of SearchResultEntryDsml.
     */
    public SearchResultEntryDsml( LdapCodecService codec )
    {
        super( codec, new SearchResultEntryImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of SearchResultEntryDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public SearchResultEntryDsml( LdapCodecService codec, SearchResultEntry ldapMessage )
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
        Element element = root.addElement( "searchResultEntry" );
        SearchResultEntry searchResultEntry = ( SearchResultEntry ) getDecorated();
        element.addAttribute( "dn", searchResultEntry.getObjectName().getName() );

        Entry entry = searchResultEntry.getEntry();
        for ( EntryAttribute attribute : entry )
        {

            Element attributeElement = element.addElement( "attr" );
            attributeElement.addAttribute( "name", attribute.getUpId() );

            for ( Value<?> value : attribute )
            {
                if ( ParserUtils.needsBase64Encoding( value.get() ) )
                {
                    Namespace xsdNamespace = new Namespace( ParserUtils.XSD, ParserUtils.XML_SCHEMA_URI );
                    Namespace xsiNamespace = new Namespace( ParserUtils.XSI, ParserUtils.XML_SCHEMA_INSTANCE_URI );
                    attributeElement.getDocument().getRootElement().add( xsdNamespace );
                    attributeElement.getDocument().getRootElement().add( xsiNamespace );

                    Element valueElement = attributeElement.addElement( "value" ).addText(
                        ParserUtils.base64Encode( value.get() ) );
                    valueElement.addAttribute( new QName( "type", xsiNamespace ), ParserUtils.XSD + ":"
                        + ParserUtils.BASE64BINARY );
                }
                else
                {
                    attributeElement.addElement( "value" ).addText( value.getString() );
                }
            }
        }

        return element;
    }


    /**
     * Get the entry Dn
     * 
     * @return Returns the objectName.
     */
    public Dn getObjectName()
    {
        return getDecorated().getObjectName();
    }


    /**
     * Set the entry Dn
     * 
     * @param objectName The objectName to set.
     */
    public void setObjectName( Dn objectName )
    {
        getDecorated().setObjectName( objectName );
    }


    /**
     * Get the entry.
     * 
     * @return Returns the entry.
     */
    public Entry getEntry()
    {
        return getDecorated().getEntry();
    }


    /**
     * Initialize the entry.
     * 
     * @param entry the entry
     */
    public void setEntry( Entry entry )
    {
        getDecorated().setEntry( entry );
    }


    /**
     * Create a new attribute.
     * 
     * @param type The attribute's name
     * @throws LdapException if the type doesn't exist
     */
    public void addAttributeType( String type ) throws LdapException
    {
        ( ( SearchResultEntryDecorator ) getDecorated() ).addAttribute( type );
    }


    /**
     * Add a new value to the current attribute.
     * 
     * @param value the added value
     */
    public void addAttributeValue( Object value )
    {
        ( ( SearchResultEntryDecorator ) getDecorated() ).addAttributeValue( value );
    }
}
