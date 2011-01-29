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
package org.apache.directory.shared.ldap.codec.controls.replication.syncRequestValue;


import org.apache.directory.shared.asn1.ber.AbstractContainer;


/**
 * A container for the SyncRequestValue control
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncRequestValueContainer extends AbstractContainer
{
    /** SyncRequestValueControl */
    private SyncRequestValueDecorator control;


    /**
     * Creates a new SyncRequestValueControlContainer object. We will store one grammar,
     * it's enough ...
     */
    public SyncRequestValueContainer()
    {
        super();
        stateStack = new int[1];
        grammar = SyncRequestValueGrammar.getInstance();
        setTransition( SyncRequestValueStatesEnum.START_STATE );
    }


    /**
     * Creates a new SyncRequestValueControlContainer object. We will store one grammar,
     * it's enough ...
     */
    public SyncRequestValueContainer( SyncRequestValueDecorator control )
    {
        super();
        this.control = control;
        stateStack = new int[1];
        grammar = SyncRequestValueGrammar.getInstance();
        setTransition( SyncRequestValueStatesEnum.START_STATE );
    }


    /**
     * @return Returns the syncRequestValue control.
     */
    public SyncRequestValueDecorator getSyncRequestValueControl()
    {
        return control;
    }


    /**
     * Set a SyncRequestValueControl Object into the container. It will be completed by
     * the ldapDecoder.
     * 
     * @param control the SyncRequestValueControl to set.
     */
    public void setSyncRequestValueControl( SyncRequestValueDecorator control )
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
