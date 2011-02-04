/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */

package org.apache.directory.shared.ldap.extras.controls.ppolicy_impl;


import org.apache.directory.shared.asn1.ber.AbstractContainer;
import org.apache.directory.shared.ldap.codec.LdapCodecService;
import org.apache.directory.shared.ldap.extras.controls.PasswordPolicy;
import org.apache.directory.shared.ldap.extras.controls.PasswordPolicyImpl;


/**
 * container for PasswordPolicyResponseControl.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyContainer extends AbstractContainer
{
    private PasswordPolicyDecorator control;


    public PasswordPolicyContainer( LdapCodecService codec )
    {
        super();
        control = new PasswordPolicyDecorator( codec, new PasswordPolicyImpl() );
        stateStack = new int[1];
        grammar = PasswordPolicyGrammar.getInstance();
        setTransition( PasswordPolicyStates.START_STATE );
    }


    public PasswordPolicyContainer( LdapCodecService codec, PasswordPolicy ppolicyResponse )
    {
        super();
        control = new PasswordPolicyDecorator( codec, ppolicyResponse );
        stateStack = new int[1];
        grammar = PasswordPolicyGrammar.getInstance();
        setTransition( PasswordPolicyStates.START_STATE );
    }


    public PasswordPolicyDecorator getPasswordPolicyResponseControl()
    {
        return control;
    }


    public void setPasswordPolicyResponseControl( PasswordPolicyDecorator control )
    {
        this.control = control;
    }


    /**
     * clean the container
     */
    @Override
    public void clean()
    {
        super.clean();
        control = null;
    }

}
