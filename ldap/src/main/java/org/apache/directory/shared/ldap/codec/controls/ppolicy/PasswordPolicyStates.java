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


import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.States;


/**
 * various states used in {@link PasswordPolicyGrammar}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum PasswordPolicyStates implements States
{

    START_STATE,                            // 0

    PPOLICY_SEQ_STATE,                      // 1
    
    PPOLICY_WARNING_TAG_STATE,              // 2

    PPOLICY_TIME_BEFORE_EXPIRATION_STATE,   // 3

    PPOLICY_GRACE_AUTHNS_REMAINING_STATE,   // 4

    PPOLICY_ERROR_TAG_STATE,                // 5

    END_STATE;                              // 6

    /**
     * {@inheritDoc}
     */
    public String getGrammarName( Grammar grammar )
    {
        if( grammar instanceof PasswordPolicyGrammar )
        {
            return "PASSWORD_POLICY_RESPONSE_CONTROL_GRAMMAR";
        }
        
        return "UNKNOWN_GRAMMAR";
    }

    /**
     * {@inheritDoc}
     */
    public String getGrammarName( int grammar )
    {
        return "PASSWORD_POLICY_RESPONSE_CONTROL_GRAMMAR";
    }


    /**
     * {@inheritDoc}
     */
    public String getState( int state )
    {
        return ( ( state == END_STATE.ordinal() ) ? "PASSWORD_POLICY_RESPONSE_CONTROL_GRAMMAR" : name() );
    }


    /**
     * {@inheritDoc}
     */
    public boolean isEndState()
    {
        return this == END_STATE;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public PasswordPolicyStates getStartState()
    {
        return START_STATE;
    }
}
