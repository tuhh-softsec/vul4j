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
 * This class store the CertGeneration's grammar constants. It is also used
 * for debugging purposes.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum CertGenerationStatesEnum implements States
{

    /** The END_STATE */
    END_STATE,

    /** start state*/
    START_STATE,

    /** sequence*/
    CERT_GENERATION_REQUEST_SEQUENCE_STATE,

    /** the target Dn*/
    TARGETDN_STATE,

    /** the issuer Dn*/
    ISSUER_STATE,

    /** the subject Dn*/
    SUBJECT_STATE,

    /** the key algorithm*/
    KEY_ALGORITHM_STATE,

    /** terminal state */
    LAST_CERT_GENERATION_STATE;

    /**
     * Get the grammar name
     * 
     * @param grammar The grammar class
     * @return The grammar name
     */
    public String getGrammarName( Grammar grammar )
    {
        if ( grammar instanceof CertGenerationGrammar )
        {
            return "CERT_GENERATION_GRAMMER";
        }
        
        return "UNKNOWN GRAMMAR";
    }

    
    /**
     * Get the grammar name
     * 
     * @param grammar The grammar code
     * @return The grammar name
     */
    public String getGrammarName( int grammar )
    {
        return "CERT_GENERATION_GRAMMER";
    }

    
    /**
     * Get the string representing the state
     * 
     * @param state The state number
     * @return The String representing the state
     */
    public String getState( int state )
    {
        return ( ( state == END_STATE.ordinal() ) ? "CERT_GENERATION_END_STATE" : this.name() );
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
    public CertGenerationStatesEnum getStartState()
    {
        return START_STATE;
    }
}
