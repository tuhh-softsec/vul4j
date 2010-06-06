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


import org.apache.directory.shared.asn1.ber.grammar.IGrammar;
import org.apache.directory.shared.asn1.ber.grammar.IStates;


/**
 * This class store the SyncModifyDnControl's grammar constants. It is also used for
 * debugging purposes.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class SyncModifyDnControlStatesEnum implements IStates
{
    // ~ Static fields/initializers
    // -----------------------------------------------------------------

    // =========================================================================
    // SyncModifyDnControl's control grammar states
    // =========================================================================
    /** Initial state */
    public static final int START_SYNC_MODDN = 0;

    /** Sequence Value */
    public static final int SYNC_MODDN_VALUE_SEQUENCE_STATE = 1;

    /** modDn control's entryDN */
    public static final int ENTRY_DN_STATE = 2;
    
    /** modDn control's move operation state */
    public static final int MOVE_STATE = 3;
    
    /** modDn rename sequence */
    public static final int RENAME_STATE = 4;

    /** modDn rename sequence */
    public static final int MOVE_AND_RENAME_STATE = 5;

    /** modDn control's rename newRDN */
    public static final int RENAME_NEW_RDN_STATE = 6;

    /** modDn control's rename deleteOldRdn flag */
    public static final int RENAME_DEL_OLD_RDN_STATE = 7;
    
    /** modDn control's move and rename newSuperiorDN */
    public static final int MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE = 8;

    /** modDn control's move and rename newRDN */
    public static final int MOVE_AND_RENAME_NEW_RDN_STATE = 9;

    /** modDn control's move and rename deleteOldRdn flag */
    public static final int MOVE_AND_RENAME_DEL_OLD_RDN_STATE = 10;

    /** terminal state */
    public static final int LAST_SYNC_MODDN_VALUE_STATE = 11;
    
    // =========================================================================
    // States debug strings
    // =========================================================================
    /** A string representation of all the states */
    private static String[] syncModifyDnString = new String[]
        { 
        "START_SYNC_MODDN", 
        "SYNC_MODDN_VALUE_SEQUENCE_STATE", 
        "ENTRY_DN_STATE",
        "MOVE_STATE", 
        "RENAME_STATE",
        "MOVE_AND_RENAME_STATE",
        "RENAME_NEW_RDN_STATE",
        "RENAME_DEL_OLD_RDN_STATE",
        "MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE",
        "MOVE_AND_RENAME_NEW_RDN_STATE",
        "MOVE_AND_RENAME_DEL_OLD_RDN_STATE",
        };

    /** The instance */
    private static SyncModifyDnControlStatesEnum instance = new SyncModifyDnControlStatesEnum();


    // ~ Constructors
    // -------------------------------------------------------------------------------

    /**
     * This is a private constructor. This class is a singleton
     */
    private SyncModifyDnControlStatesEnum()
    {
    }


    // ~ Methods
    // ------------------------------------------------------------------------------------

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
    public String getGrammarName( IGrammar grammar )
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
        return ( ( state == GRAMMAR_END ) ? "SYNC_MODDN_VALUE_END_STATE" : syncModifyDnString[state] );
    }
}
