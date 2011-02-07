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

package org.apache.directory.shared.ldap.extras.extended.ads_impl;


import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.States;


/**
 * Constants for StoredProcedureGrammar.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum StoredProcedureStatesEnum implements States
{
    //~ Static fields/initializers -----------------------------------------------------------------

    /** The END_STATE */
    END_STATE,

    //=========================================================================
    // StoredProcedure
    //=========================================================================
    /** starting state */
    START_STATE,

    /** StoredProcedure */
    STORED_PROCEDURE_STATE,

    // Language ---------------------------------------------------------------
    /** Language */
    LANGUAGE_STATE,

    // Procedure --------------------------------------------------------------
    /** Procedure */
    PROCEDURE_STATE,

    // Parameters -------------------------------------------------------------
    /** Parameters */
    PARAMETERS_STATE,

    // Parameter --------------------------------------------------------------
    /** Parameter */
    PARAMETER_STATE,

    // Parameter type ---------------------------------------------------------
    /** Parameter type */
    PARAMETER_TYPE_STATE,

    // Parameters value -------------------------------------------------------
    /** Parameter value */
    PARAMETER_VALUE_STATE,

    /** Last Stored Procedure */
    LAST_STORED_PROCEDURE_STATE;


    /**
     * Get the grammar name
     * @param grammar The grammar code
     * @return The grammar name
     */
    public String getGrammarName( int grammar )
    {
        return "STORED_PROCEDURE_GRAMMAR";
    }


    /**
     * Get the grammar name
     * @param grammar The grammar class
     * @return The grammar name
     */
    public String getGrammarName( Grammar grammar )
    {
        if ( grammar instanceof StoredProcedureGrammar )
        {
            return "STORED_PROCEDURE_GRAMMAR";
        }
        else
        {
            return "UNKNOWN GRAMMAR";
        }
    }


    /**
     * Get the string representing the state
     * 
     * @param state The state number
     * @return The String representing the state
     */
    public String getState( int state )
    {
        return ( ( state == END_STATE.ordinal() ) ? "STORED_PROCEDURE_END_STATE" : name() );
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
    public StoredProcedureStatesEnum getStartState()
    {
        return START_STATE;
    }
}
