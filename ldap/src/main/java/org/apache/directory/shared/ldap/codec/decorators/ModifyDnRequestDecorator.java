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
package org.apache.directory.shared.ldap.codec.decorators;


import org.apache.directory.shared.ldap.model.message.ModifyDnRequest;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.ldap.model.name.Rdn;


/**
 * A decorator for the ModifyDnRequest message
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class ModifyDnRequestDecorator extends SingleReplyRequestDecorator implements ModifyDnRequest
{
    /** The modify Dn request length */
    private int modifyDnRequestLength;


    /**
     * Makes a ModifyDnRequest encodable.
     *
     * @param decoratedMessage the decorated ModifyDnRequest
     */
    public ModifyDnRequestDecorator( ModifyDnRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    /**
     * @return The decorated ModifyDnRequest
     */
    public ModifyDnRequest getModifyDnRequest()
    {
        return ( ModifyDnRequest ) getDecoratedMessage();
    }


    /**
     * @param modifyDnRequestLength The encoded ModifyDnRequest's length
     */
    public void setModifyDnRequestLength( int modifyDnRequestLength )
    {
        this.modifyDnRequestLength = modifyDnRequestLength;
    }


    /**
     * Stores the encoded length for the ModifyDnRequest
     * @return the encoded length
     */
    public int getModifyDnResponseLength()
    {
        return modifyDnRequestLength;
    }


    //-------------------------------------------------------------------------
    // The ModifyDnResponse methods
    //-------------------------------------------------------------------------
    
    
    /**
     * {@inheritDoc}
     */
    public Dn getName()
    {
        return getModifyDnRequest().getName();
    }


    /**
     * {@inheritDoc}
     */
    public void setName( Dn name )
    {
        getModifyDnRequest().setName( name );
    }


    /**
     * {@inheritDoc}
     */
    public Rdn getNewRdn()
    {
        return getModifyDnRequest().getNewRdn();
    }


    /**
     * {@inheritDoc}
     */
    public void setNewRdn( Rdn newRdn )
    {
        getModifyDnRequest().setNewRdn( newRdn );
    }


    /**
     * {@inheritDoc}
     */
    public boolean getDeleteOldRdn()
    {
        return getModifyDnRequest().getDeleteOldRdn();
    }


    /**
     * {@inheritDoc}
     */
    public void setDeleteOldRdn( boolean deleteOldRdn )
    {
        getModifyDnRequest().setDeleteOldRdn( deleteOldRdn );
    }


    /**
     * {@inheritDoc}
     */
    public Dn getNewSuperior()
    {
        return getModifyDnRequest().getNewSuperior();
    }


    /**
     * {@inheritDoc}
     */
    public void setNewSuperior( Dn newSuperior )
    {
        getModifyDnRequest().setNewSuperior( newSuperior );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isMove()
    {
        return getModifyDnRequest().isMove();
    }
}
