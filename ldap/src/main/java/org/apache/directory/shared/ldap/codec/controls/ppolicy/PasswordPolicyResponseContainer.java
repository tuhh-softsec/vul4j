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

package org.apache.directory.shared.ldap.codec.controls.ppolicy;


import org.apache.directory.shared.asn1.ber.AbstractContainer;


/**
 * container for PasswordPolicyResponseControl.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyResponseContainer extends AbstractContainer
{
    private PasswordPolicyResponseDecorator control;


    public PasswordPolicyResponseContainer()
    {
        super();
        control = new PasswordPolicyResponseDecorator( new PasswordPolicyResponseDecorator() );
        stateStack = new int[1];
        grammar = PasswordPolicyResponseGrammar.getInstance();
        setTransition( PasswordPolicyResponseStates.START_STATE );
    }


    public PasswordPolicyResponseContainer( IPasswordPolicyResponse ppolicyResponse )
    {
        super();
        control = new PasswordPolicyResponseDecorator( ppolicyResponse );
        stateStack = new int[1];
        grammar = PasswordPolicyResponseGrammar.getInstance();
        setTransition( PasswordPolicyResponseStates.START_STATE );
    }


    public PasswordPolicyResponseDecorator getPasswordPolicyResponseControl()
    {
        return control;
    }


    public void setPasswordPolicyResponseControl( PasswordPolicyResponseDecorator control )
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
