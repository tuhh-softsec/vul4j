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
package org.apache.directory.shared.ldap.codec.search.controls.subentries;


import org.apache.directory.shared.asn1.ber.AbstractContainer;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SubentriesContainer extends AbstractContainer
{
    /** PSearchControl */
    private SubentriesDecorator decorator;


    /**
     * Creates a new SubEntryControlContainer object.
     */
    public SubentriesContainer()
    {
        super();
        stateStack = new int[1];
        grammar = SubentriesGrammar.getInstance();
        setTransition( SubentriesStates.START_STATE );
    }


    /**
     * Creates a new SubEntryControlContainer object, pre-populating it with the
     * supplied Subentries control, and optionally wrapping it with a decorator
     * if it is not a decorator instance.
     *
     * @param control The Subentries Control to decorate and add to this
     * container, or if the Control already is a ControlDecorator it is directly
     * added.
     */
    public SubentriesContainer( SubentriesDecorator control )
    {
        this();
        this.decorator = control;
    }


    /**
     * @return Returns the persistent search control.
     */
    public SubentriesDecorator getSubentriesControl()
    {
        return decorator;
    }


    /**
     * Set a SubEntryControl Object into the container. It will be completed by
     * the ldapDecoder.
     * 
     * @param decorator the SubEntryControl to set.
     */
    public void setSubentriesDecorator( SubentriesDecorator decorator )
    {
        this.decorator = decorator;
    }


    /**
     * Clean the current container
     */
    public void clean()
    {
        super.clean();
        decorator = null;
    }
}
