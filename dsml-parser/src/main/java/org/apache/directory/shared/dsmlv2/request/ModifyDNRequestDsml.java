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
import org.apache.directory.shared.ldap.model.message.MessageTypeEnum;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.message.ModifyDnRequestImpl;
import org.apache.directory.shared.ldap.model.message.ModifyDnResponse;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;
import org.dom4j.Element;


/**
 * DSML Decorator for ModifyDNRequest
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyDNRequestDsml 
    extends AbstractResultResponseRequestDsml<ModifyDnRequest, ModifyDnResponse>
    implements ModifyDnRequest
{
    /**
     * Creates a new getDecoratedMessage() of ModifyDNRequestDsml.
     */
    public ModifyDNRequestDsml( LdapCodecService codec )
    {
        super( codec, new ModifyDnRequestImpl() );
    }


    /**
     * Creates a new getDecoratedMessage() of ModifyDNRequestDsml.
     *
     * @param ldapMessage
     *      the message to decorate
     */
    public ModifyDNRequestDsml( LdapCodecService codec, ModifyDnRequest ldapMessage )
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

        ModifyDnRequest request = ( ModifyDnRequest ) getDecorated();

        // Dn
        if ( request.getName() != null )
        {
            element.addAttribute( "dn", request.getName().getName() );
        }

        // NewRDN
        if ( request.getNewRdn() != null )
        {
            element.addAttribute( "newrdn", request.getNewRdn().getName() );
        }

        // DeleteOldRDN
        element.addAttribute( "deleteoldrdn", ( request.getDeleteOldRdn() ? "true" : "false" ) );

        // NewSuperior
        if ( request.getNewRdn() != null )
        {
            element.addAttribute( "newSuperior", request.getNewSuperior().getName() );
        }

        return element;
    }


    /**
     * Get the modification's Dn
     * 
     * @return Returns the name.
     */
    public Dn getName()
    {
        return getDecorated().getName();
    }


    /**
     * Set the modification Dn.
     * 
     * @param name The name to set.
     */
    public void setEntry( Dn name )
    {
        getDecorated().setName( name );
    }


    /**
     * Tells if the old Rdn is to be deleted
     * 
     * @return Returns the deleteOldRDN.
     */
    public boolean isDeleteOldRDN()
    {
        return getDecorated().getDeleteOldRdn();
    }


    /**
     * Set the flag to delete the old Rdn
     * 
     * @param deleteOldRDN The deleteOldRDN to set.
     */
    public void setDeleteOldRDN( boolean deleteOldRDN )
    {
        getDecorated().setDeleteOldRdn( deleteOldRDN );
    }


    /**
     * Get the new Rdn
     * 
     * @return Returns the newRDN.
     */
    public Rdn getNewRDN()
    {
        return getDecorated().getNewRdn();
    }


    /**
     * Set the new Rdn
     * 
     * @param newRdn The newRdn to set.
     */
    public void setNewRDN( Rdn newRdn)
    {
        getDecorated().setNewRdn( newRdn );
    }


    /**
     * Get the newSuperior
     * 
     * @return Returns the newSuperior.
     */
    public Dn getNewSuperior()
    {
        return getDecorated().getNewSuperior();
    }


    /**
     * Set the new superior
     * 
     * @param newSuperior The newSuperior to set.
     */
    public void setNewSuperior( Dn newSuperior )
    {
        getDecorated().setNewSuperior( newSuperior );
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
    public void setName( Dn name )
    {
        getDecorated().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public Rdn getNewRdn()
    {
        return getDecorated().getNewRdn();
    }


    /**
     * {@inheritDoc}
     */
    public void setNewRdn( Rdn newRdn )
    {
        getDecorated().setNewRdn( newRdn );
    }


    /**
     * {@inheritDoc}
     */
    public boolean getDeleteOldRdn()
    {
        return getDecorated().getDeleteOldRdn();
    }


    /**
     * {@inheritDoc}
     */
    public void setDeleteOldRdn( boolean deleteOldRdn )
    {
        getDecorated().setDeleteOldRdn( deleteOldRdn );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isMove()
    {
        return getDecorated().isMove();
    }
}
