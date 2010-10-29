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
package org.apache.directory.shared.ldap.codec;


import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.States;


/**
 * This class store the Ldap grammar's constants. It is also used for debugging
 * purpose
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum LdapStatesEnum implements States
{
    // ~ Static fields/initializers
    // -----------------------------------------------------------------
    /** The initial state of every grammar */
    INIT_GRAMMAR_STATE(0),

    /** The ending state for every grammars */
    GRAMMAR_END(-1),

    /** The END_STATE */
    END_STATE(-1),

    START_STATE(                      0),
    LDAP_MESSAGE_STATE(               1),
    MESSAGE_ID_STATE(                 2),
    BIND_REQUEST_STATE(               3),
    BIND_RESPONSE_STATE(              4),
    UNBIND_REQUEST_STATE(             5),
    SEARCH_REQUEST_STATE(             6),
    SEARCH_RESULT_ENTRY_STATE(        7),
    SEARCH_RESULT_DONE_STATE(         8),
    SEARCH_RESULT_REFERENCE_STATE(    9),
    MODIFY_REQUEST_STATE(             10),
    MODIFY_RESPONSE_STATE(            11),
    ADD_REQUEST_STATE(                12),
    ADD_RESPONSE_STATE(               13),
    DEL_REQUEST_STATE(                14),
    DEL_RESPONSE_STATE(               15),
    MODIFY_DN_REQUEST_STATE(          16),
    MODIFY_DN_RESPONSE_STATE(         17),
    COMPARE_REQUEST_STATE(            18),
    COMPARE_RESPONSE_STATE(           19),
    ABANDON_REQUEST_STATE(            20),
    EXTENDED_REQUEST_STATE(           21),
    EXTENDED_RESPONSE_STATE(          22),
    VERSION_STATE(                    23),
    NAME_STATE(                       24),
    SIMPLE_STATE(                     25),
    SASL_STATE(                       26),
    MECHANISM_STATE(                  27),
    CREDENTIALS_STATE(                28),
    RESULT_CODE_BR_STATE(             29),
    MATCHED_DN_BR_STATE(              30),
    ERROR_MESSAGE_BR_STATE(           31),
    REFERRALS_BR_STATE(               32),
    REFERRAL_BR_STATE(                33),
    SERVER_SASL_CREDENTIALS_STATE(    34),
    RESULT_CODE_STATE(                35),
    MATCHED_DN_STATE(                 36),
    ERROR_MESSAGE_STATE(              37),
    REFERRALS_STATE(                  38),
    REFERRAL_STATE(                   39),
    REQUEST_NAME_STATE(               40),
    REQUEST_VALUE_STATE(              41),
    RESPONSE_NAME_STATE(              42),
    RESPONSE_STATE(                   43),
    RESULT_CODE_ER_STATE(             44),
    MATCHED_DN_ER_STATE(              45),
    ERROR_MESSAGE_ER_STATE(           46),
    REFERRALS_ER_STATE(               47),
    REFERRAL_ER_STATE(                48),
    ENTRY_STATE(                      49),
    ATTRIBUTES_STATE(                 50),
    ATTRIBUTE_STATE(                  51),
    TYPE_STATE(                       52),
    VALUES_STATE(                     53),
    VALUE_STATE(                      54),
    OBJECT_STATE(                     55),
    MODIFICATIONS_STATE(              56),
    MODIFICATIONS_SEQ_STATE(          57),
    OPERATION_STATE(                  58),
    MODIFICATION_STATE(               59),
    TYPE_MOD_STATE(                   60),
    VALS_STATE(                       61),
    ATTRIBUTE_VALUE_STATE(            62),
    ENTRY_MOD_DN_STATE(               63),
    NEW_RDN_STATE(                    64),
    DELETE_OLD_RDN_STATE(             65),
    NEW_SUPERIOR_STATE(               66),
    ENTRY_COMP_STATE(                 67),
    AVA_STATE(                        68),
    ATTRIBUTE_DESC_STATE(             69),
    ASSERTION_VALUE_STATE(            70),
    BASE_OBJECT_STATE(                71),
    SCOPE_STATE(                      72),
    DEREF_ALIAS_STATE(                73),
    SIZE_LIMIT_STATE(                 74),
    TIME_LIMIT_STATE(                 75),
    TYPES_ONLY_STATE(                 76),
    AND_STATE(                        77),
    OR_STATE(                         78),
    NOT_STATE(                        79),
    EQUALITY_MATCH_STATE(             80),
    SUBSTRING_FILTER_STATE(           81),
    GREATER_OR_EQUAL_STATE(           82),
    LESS_OR_EQUAL_STATE(              83),
    PRESENT_STATE(                    84),
    APPROX_MATCH_STATE(               85),
    EXTENSIBLE_MATCH_STATE(           86),
    ATTRIBUTE_DESC_FILTER_STATE(      87),
    ASSERTION_VALUE_FILTER_STATE(     88),
    ATTRIBUTE_DESCRIPTION_LIST_STATE( 89),
    ATTRIBUTE_DESCRIPTION_STATE(      90),
    TYPE_SUBSTRING_STATE(             91),
    SUBSTRINGS_STATE(                 92),
    INITIAL_STATE(                    93),
    ANY_STATE(                        94),
    FINAL_STATE(                      95),
    MATCHING_RULE_STATE(              96),
    TYPE_MATCHING_RULE_STATE(         97),
    MATCH_VALUE_STATE(                98),
    DN_ATTRIBUTES_STATE(              99),
    OBJECT_NAME_STATE(                100),
    ATTRIBUTES_SR_STATE(              101),
    PARTIAL_ATTRIBUTES_LIST_STATE(    102),
    TYPE_SR_STATE(                    103),
    VALS_SR_STATE(                    104),
    ATTRIBUTE_VALUE_SR_STATE(         105),
    REFERENCE_STATE(                  106),
    CONTROLS_STATE(                   107),
    CONTROL_STATE(                    108),
    CONTROL_TYPE_STATE(               109),
    CRITICALITY_STATE(                110),
    CONTROL_VALUE_STATE(              111),
    INTERMEDIATE_RESPONSE_STATE(      112),
    INTERMEDIATE_RESPONSE_NAME_STATE( 113),
    INTERMEDIATE_RESPONSE_VALUE_STATE(114),
    
    
    LAST_LDAP_STATE(115);

    
    private int state;
    
    /**
     * 
     * Creates a new instance of LdapStatesEnum.
     *
     * @param state
     */
    LdapStatesEnum(int state)
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
     * @param grammar
     *            The grammar code
     * @return The grammar name
     */
    public String getGrammarName( int grammar )
    {
        return "LDAP_MESSAGE_GRAMMAR";
    }


    /**
     * Get the grammar name
     * 
     * @param grammar
     *            The grammar class
     * @return The grammar name
     */
    public String getGrammarName( Grammar grammar )
    {
        if ( grammar instanceof LdapMessageGrammar )
        {
            return "LDAP_MESSAGE_GRAMMAR";
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
        return ( ( state == GRAMMAR_END.getState() ) ? "LDAP_MESSAGE_END_STATE" : name() );
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
    public LdapStatesEnum getStartState()
    {
        return START_STATE;
    }
}
