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


import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.States;


/**
 * This class store the SyncModifyDnControl's grammar constants. It is also used for
 * debugging purposes.
 * 
 * TODO: should this be an enum?
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum SyncModifyDnControlStatesEnum implements States
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
    // SyncModifyDnControl's control grammar states
    // =========================================================================
    /** Initial state */
    START_SYNC_MODDN(0),

    /** Sequence Value */
    SYNC_MODDN_VALUE_SEQUENCE_STATE(1),

    /** modDn control's entryDN */
    ENTRY_DN_STATE(2),
    
    /** modDn control's move operation state */
    MOVE_STATE(3),
    
    /** modDn rename sequence */
    RENAME_STATE(4),

    /** modDn rename sequence */
    MOVE_AND_RENAME_STATE(5),

    /** modDn control's rename newRDN */
    RENAME_NEW_RDN_STATE(6),

    /** modDn control's rename deleteOldRdn flag */
    RENAME_DEL_OLD_RDN_STATE(7),
    
    /** modDn control's move and rename newSuperiorDN */
    MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE(8),

    /** modDn control's move and rename newRDN */
    MOVE_AND_RENAME_NEW_RDN_STATE(9),

    /** modDn control's move and rename deleteOldRdn flag */
    MOVE_AND_RENAME_DEL_OLD_RDN_STATE(10),

    /** terminal state */
    LAST_SYNC_MODDN_VALUE_STATE(11);
    
    private int state;
    
    /**
     * 
     * Creates a new instance of SyncModifyDnControlStatesEnum.
     *
     * @param state
     */
    SyncModifyDnControlStatesEnum(int state)
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
        return "SYNC_MODIFYDN_GRAMMAR";
    }


    /**
     * Get the grammar name
     * 
     * @param grammar The grammar class
     * @return The grammar name
     */
    public String getGrammarName( Grammar grammar )
    {
        if ( grammar instanceof SyncModifyDnControlGrammar )
        {
            return "SYNC_MODIFYDN_GRAMMAR";
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
        return ( ( state == GRAMMAR_END.getState() ) ? "SYNC_MODDN_VALUE_END_STATE" : this.name() );
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
    public SyncModifyDnControlStatesEnum getStartState()
    {
        return START_SYNC_MODDN;
    }
}
