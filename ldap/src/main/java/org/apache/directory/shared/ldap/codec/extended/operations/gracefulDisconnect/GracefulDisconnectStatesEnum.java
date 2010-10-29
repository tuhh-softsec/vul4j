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
package org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect;


import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.States;


/**
 * This class store the GracefulDisconnect's grammar constants. It is also used
 * for debugging purposes.
 * 
 * TODO: should this be an enum?
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum GracefulDisconnectStatesEnum implements States
{
    // ~ Static fields/initializers
    // -----------------------------------------------------------------

    /** The initial state of every grammar */
    INIT_GRAMMAR_STATE(0),

    /** The ending state for every grammars */
    GRAMMAR_END(-1),

    /** The END_STATE */
    END_STATE(-1),

    // =========================================================================
    // GracefulDisconnect grammar states
    // =========================================================================
    /** Initial state */
    START_STATE(0),

    /** Sequence */
    GRACEFUL_DISCONNECT_SEQUENCE_STATE(1),

    /** Time offline */
    TIME_OFFLINE_STATE(2),

    /** Delay */
    DELAY_STATE(3),

    /** Replicated contexts */
    REPLICATED_CONTEXTS_STATE(4),

    /** Referral */
    REFERRAL_STATE(5),

    /** terminal state */
    LAST_GRACEFUL_DISCONNECT_STATE(6);

    private int state;
    
    /**
     * 
     * Creates a new instance of GracefulDisconnectStatesEnum.
     *
     * @param state
     */
    private GracefulDisconnectStatesEnum(int state)
    {
        this.state = state;
    }

    /**
     * 
     * Get the state.
     *
     * @return State as integer value
     */
    public int getState()
    {
        return state;
    }
    
    /**
     * Get the grammar name
     * 
     * @param grammar The grammar code
     * @return The grammar name
     */
    public String getGrammarName( int grammar )
    {
        return "GRACEFUL_DISCONNECT_GRAMMAR";
    }


    /**
     * Get the grammar name
     * 
     * @param grammar The grammar class
     * @return The grammar name
     */
    public String getGrammarName( Grammar grammar )
    {
        if ( grammar instanceof GracefulDisconnectGrammar )
        {
            return "GRACEFUL_DISCONNECT_GRAMMAR";
        }

        return "UNKNOWN GRAMMAR";
    }


    /**
     * Get the string representing the state
     * 
     * @param state The state number
     * @return The String representing the state
     */
    public String getState( int state )
    {
        return ( ( state == GRAMMAR_END.getState() ) ? "GRACEFUL_DISCONNECT_END_STATE" : name() );
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
    public GracefulDisconnectStatesEnum getStartState()
    {
        return START_STATE;
    }
}
