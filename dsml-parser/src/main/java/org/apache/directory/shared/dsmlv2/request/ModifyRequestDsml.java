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


import java.util.Collection;
import java.util.Iterator;

import org.apache.directory.shared.dsmlv2.ParserUtils;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.model.entry.EntryAttribute;
import org.apache.directory.shared.ldap.model.entry.Modification;
import org.apache.directory.shared.ldap.model.entry.ModificationOperation;
import org.apache.directory.shared.ldap.model.entry.Value;
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.ModifyRequest;
import org.apache.directory.shared.ldap.model.message.ModifyRequestImpl;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;


/**
 * DSML Decorator for ModifyRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyRequestDsml 
    extends AbstractResultResponseRequestDsml<ModifyRequest>
    implements ModifyRequest
{
    /**
     * Creates a new getDecoratedMessage() of ModifyRequestDsml.
     */
    public ModifyRequestDsml( LdapCodecService codec )
    {
        super( codec, new ModifyRequestImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of ModifyRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public ModifyRequestDsml( LdapCodecService codec, ModifyRequest ldapMessage )
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

        ModifyRequest request = ( ModifyRequest ) getDecorated();

        // Dn
        if ( request.getName() != null )
        {
            element.addAttribute( "dn", request.getName().getName() );
        }

        // Modifications
        Collection<Modification> modifications = request.getModifications();

        for ( Modification modification : modifications )
        {
            Element modElement = element.addElement( "modification" );

            if ( modification.getAttribute() != null )
            {
                modElement.addAttribute( "name", modification.getAttribute().getId() );

                Iterator<Value<?>> iterator = modification.getAttribute().getAll();

                while ( iterator.hasNext() )
                {
                    Value<?> value = iterator.next();

                    if ( value.get() != null )
                    {
                        if ( ParserUtils.needsBase64Encoding( value.get() ) )
                        {
                            Namespace xsdNamespace = new Namespace( "xsd", ParserUtils.XML_SCHEMA_URI );
                            Namespace xsiNamespace = new Namespace( "xsi", ParserUtils.XML_SCHEMA_INSTANCE_URI );
                            element.getDocument().getRootElement().add( xsdNamespace );
                            element.getDocument().getRootElement().add( xsiNamespace );

                            Element valueElement = modElement.addElement( "value" ).addText(
                                ParserUtils.base64Encode( value.get() ) );
                            valueElement.addAttribute( new QName( "type", xsiNamespace ), "xsd:"
                                + ParserUtils.BASE64BINARY );
                        }
                        else
                        {
                            modElement.addElement( "value" ).setText( value.getString() );
                        }
                    }
                }
            }

            ModificationOperation operation = modification.getOperation();

            if ( operation == ModificationOperation.ADD_ATTRIBUTE )
            {
                modElement.addAttribute( "operation", "add" );
            }
            else if ( operation == ModificationOperation.REPLACE_ATTRIBUTE )
            {
                modElement.addAttribute( "operation", "replace" );
            }
            else if ( operation == ModificationOperation.REMOVE_ATTRIBUTE )
            {
                modElement.addAttribute( "operation", "delete" );
            }
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
    public Collection<Modification> getModifications()
    {
        return getDecorated().getModifications();
    }


    /**
     * {@inheritDoc}
     */
    public void addModification( Modification mod )
    {
        getDecorated().addModification( mod );
    }


    /**
     * {@inheritDoc}
     */
    public void removeModification( Modification mod )
    {
        getDecorated().removeModification( mod );
    }


    /**
     * {@inheritDoc}
     */
    public void remove( String attributeName, String... attributeValue )
    {
        getDecorated().remove( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void remove( String attributeName, byte[]... attributeValue )
    {
        getDecorated().remove( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void remove( EntryAttribute attr )
    {
        getDecorated().remove( attr );
    }


    /**
     * {@inheritDoc}
     */
    public void addModification( EntryAttribute attr, ModificationOperation modOp )
    {
        getDecorated().addModification( attr, modOp );
    }


    /**
     * {@inheritDoc}
     */
    public void add( String attributeName, String... attributeValue )
    {
        getDecorated().add( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void add( String attributeName, byte[]... attributeValue )
    {
        getDecorated().add( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void add( EntryAttribute attr )
    {
        getDecorated().add( attr );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( String attributeName )
    {
        getDecorated().replace( attributeName );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( String attributeName, String... attributeValue )
    {
        getDecorated().replace( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( String attributeName, byte[]... attributeValue )
    {
        getDecorated().replace( attributeName, attributeValue );
    }


    /**
     * {@inheritDoc}
     */
    public void replace( EntryAttribute attr )
    {
        getDecorated().replace( attr );
    }
}
