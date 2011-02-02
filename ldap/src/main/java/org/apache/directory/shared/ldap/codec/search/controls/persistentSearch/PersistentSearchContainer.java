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
package org.apache.directory.shared.ldap.codec.search.controls.persistentSearch;


import org.apache.directory.shared.asn1.ber.AbstractContainer;
import org.apache.directory.shared.ldap.codec.ILdapCodecService;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;


/**
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PersistentSearchContainer extends AbstractContainer
{
    /** PSearchControl */
    private PersistentSearchDecorator decorator;
    
    private ILdapCodecService codec;


    /**
     * Creates a new PSearchControlContainer object. We will store one grammar,
     * it's enough ...
     */
    public PersistentSearchContainer( ILdapCodecService codec )
    {
        super();
        this.codec = codec;
        stateStack = new int[1];
        grammar = PersistentSearchGrammar.getInstance();
        setTransition( PersistentSearchStates.START_STATE );
    }


    /**
     * Creates a new PSearchControlContainer object pre-populated with a
     * decorator wrapping the supplied control, or using the supplied control if
     * it already is a decorator.
     *
     * @param control The PersistentSearch Control or a decorating wrapper.
     */
    public PersistentSearchContainer( ILdapCodecService codec, PersistentSearch control )
    {
        this( codec );
        decorate( control );
    }


    /**
     * Conditionally decorates a control if is not a decorator already.
     *
     * @param control The PersistentSearch Control to decorate if it already is not
     * a decorator, if it is then the object is set as this container's decorator.
     */
    public void decorate( PersistentSearch control )
    {
        if ( control instanceof PersistentSearchDecorator )
        {
            this.decorator = ( PersistentSearchDecorator ) control;
        }
        else
        {
            this.decorator = new PersistentSearchDecorator( codec, control );
        }
    }



    /**
     * @return Returns the persistent search decorator.
     */
    public PersistentSearchDecorator getPersistentSearchDecorator()
    {

        return decorator;
    }


    /**
     * Set a PSearchControl Object into the container. It will be completed by
     * the ldapDecoder.
     * 
     * @param decorator the PSearchControl to set.
     */
    public void setPersistentSearchDecorator( PersistentSearchDecorator decorator )
    {
        this.decorator = decorator;
    }


    /**
     * Clean the container
     */
    public void clean()
    {
        super.clean();
        decorator = null;
    }
}
