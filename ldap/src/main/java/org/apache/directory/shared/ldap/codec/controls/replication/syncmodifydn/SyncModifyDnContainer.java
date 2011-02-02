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
package org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn;


import org.apache.directory.shared.asn1.ber.AbstractContainer;
import org.apache.directory.shared.ldap.codec.api.ILdapCodecService;


/**
 * A container for the SyncModifyDnControl control
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncModifyDnContainer extends AbstractContainer
{
    /** SyncModifyDnControl */
    private SyncModifyDnDecorator control;


    /**
     * Creates a new SyncStateValueControlContainer object. We will store one grammar,
     * it's enough ...
     */
    public SyncModifyDnContainer( ILdapCodecService codec )
    {
        super();
        this.control = new SyncModifyDnDecorator( codec );
        stateStack = new int[1];
        grammar = SyncModifyDnGrammar.getInstance();
        setTransition( SyncModifyDnStatesEnum.START_SYNC_MODDN );
    }


    /**
     * Creates a new SyncStateValueControlContainer object. We will store one grammar,
     * it's enough ...
     */
    public SyncModifyDnContainer( SyncModifyDnDecorator control )
    {
        super();
        this.control = control;
        stateStack = new int[1];
        grammar = SyncModifyDnGrammar.getInstance();
        setTransition( SyncModifyDnStatesEnum.START_SYNC_MODDN );
    }


    /**
     * @return Returns the SyncModifyDnControl control.
     */
    public SyncModifyDnDecorator getSyncModifyDnControl()
    {
        return control;
    }


    /**
     * Set a SyncModifyDnControl Object into the container. It will be completed by
     * the ldapDecoder.
     * 
     * @param control the SyncStateValueControl to set.
     */
    public void setSyncModifyDnDecorator( SyncModifyDnDecorator control )
    {
        this.control = control;
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
