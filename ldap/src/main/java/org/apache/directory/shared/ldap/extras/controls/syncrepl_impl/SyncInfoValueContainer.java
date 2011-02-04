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
package org.apache.directory.shared.ldap.extras.controls.syncrepl_impl;


import org.apache.directory.shared.asn1.ber.AbstractContainer;
import org.apache.directory.shared.ldap.codec.LdapCodecService;


/**
 * A container for the SyncInfoValue control
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncInfoValueContainer extends AbstractContainer
{
    /** SyncInfoValueControl */
    private SyncInfoValueDecorator control;

    private LdapCodecService codec;

    
    /**
     * Creates a new SyncInfoValueControlContainer object. We will store one grammar,
     * it's enough ...
     */
    public SyncInfoValueContainer( LdapCodecService codec )
    {
        super();
        this.codec = codec;
        this.control = new SyncInfoValueDecorator( codec );
        stateStack = new int[1];
        grammar = SyncInfoValueGrammar.getInstance();
        setTransition( SyncInfoValueStatesEnum.START_STATE );
    }


    /**
     * Creates a new SyncInfoValueControlContainer object. We will store one grammar,
     * it's enough ...
     */
    public SyncInfoValueContainer( LdapCodecService codec, SyncInfoValueDecorator control )
    {
        super();
        this.codec = codec;
        this.control = control;
        stateStack = new int[1];
        grammar = SyncInfoValueGrammar.getInstance();
        setTransition( SyncInfoValueStatesEnum.START_STATE );
    }


    /**
     * @return Returns the syncInfoValue control.
     */
    public SyncInfoValueDecorator getSyncInfoValueControl()
    {
        return control;
    }


    /**
     * Set a SyncInfoValueControl Object into the container. It will be completed by
     * the ldapDecoder.
     * 
     * @param control the SyncInfoValueControlCodec to set.
     */
    public void setSyncInfoValueControl( SyncInfoValueDecorator control )
    {
        this.control = control;
    }

    
    public LdapCodecService getCodecService()
    {
        return codec;
    }
    
    
    /**
     * Clean the container
     */
    public void clean()
    {
        super.clean();
        control = null;
    }
}
