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
package org.apache.directory.shared.ldap.codec.search.controls.entryChange;


import org.apache.directory.shared.asn1.ber.AbstractContainer;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EntryChangeContainer extends AbstractContainer
{
    /** EntryChangeControl */
    private EntryChangeDecorator control;


    /**
     * Creates a new EntryChangeContainer object. We will store one
     * grammar, it's enough ...
     */
    public EntryChangeContainer()
    {
        super();
        stateStack = new int[1];
        grammar = EntryChangeGrammar.getInstance();
        setTransition( EntryChangeStates.START_STATE );
    }


    /**
     * Creates a container with decorator, optionally decorating the supplied
     * Control if it is not a decorator implementation.
     *
     * @param control The EntryChange ControlDecorator, or a Control to be
     * wrapped by a new decorator.
     */
    public EntryChangeContainer( EntryChange control )
    {
        this();
        decorate( control );
    }


    /**
     * @return Returns the EntryChangeControl.
     */
    public EntryChangeDecorator getEntryChangeDecorator()
    {
        return control;
    }


    /**
     * Checks to see if the supplied EntryChange implementation is a decorator
     * and if so just sets the EntryChangeDecorator to it. Otherwise the supplied
     * control is decorated by creating a new EntryChangeDecorator to wrap the
     * object.
     *
     * @param control The EntryChange Control to wrap, if it is not a decorator.
     */
    public void decorate( EntryChange control )
    {
        if ( control instanceof EntryChangeDecorator )
        {
            this.control = ( EntryChangeDecorator ) control;
        }
        else
        {
            this.control = new EntryChangeDecorator( control );
        }
    }


    /**
     * Set a EntryChangeControl Object into the container. It will be completed
     * by the ldapDecoder.
     * 
     * @param control the EntryChangeControl to set.
     */
    public void setEntryChangeDecorator( EntryChangeDecorator control )
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
