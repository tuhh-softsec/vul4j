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


import org.apache.directory.shared.asn1.ber.grammar.IGrammar;
import org.apache.directory.shared.asn1.ber.grammar.IStates;


/**
 * various states used in {@link PasswordPolicyResponseControlGrammar}.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyResponseControlStates implements IStates
{

    public static final int START_STATE = 0;

    public static final int PPOLICY_TIME_BEFORE_EXPIRATION_STATE = 1;

    public static final int PPOLICY_GRACE_AUTHNS_REMAINING_STATE = 2;

    public static final int PPOLICY_ERROR_STATE = 3;

    public static final int END_STATE = 4;

    private static PasswordPolicyResponseControlStates instance = new PasswordPolicyResponseControlStates();

    public static final String[] ppolicyStateString = new String[]
        { 
          "START_STATE",
          "PPOLICY_TIME_BEFORE_EXPIRATION_STATE",
          "PPOLICY_GRACE_AUTHNS_REMAINING_STATE",
          "PPOLICY_ERROR_STATE"
        };


    private PasswordPolicyResponseControlStates()
    {
    }


    /**
     * Get an instance of this class
     * 
     * @return An instance on this class
     */
    public static IStates getInstance()
    {
        return instance;
    }


    /**
     * {@inheritDoc}
     */
    public String getGrammarName( IGrammar grammar )
    {
        if( grammar instanceof PasswordPolicyResponseControlGrammar )
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
        return ( ( state == GRAMMAR_END ) ? "PASSWORD_POLICY_RESPONSE_CONTROL_GRAMMAR" : ppolicyStateString[state] );
    }

}
