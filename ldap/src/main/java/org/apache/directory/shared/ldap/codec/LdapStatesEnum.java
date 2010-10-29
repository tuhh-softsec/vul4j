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

    /** The END_STATE */
    END_STATE,

    START_STATE,
    LDAP_MESSAGE_STATE,
    MESSAGE_ID_STATE,
    BIND_REQUEST_STATE,
    BIND_RESPONSE_STATE,
    UNBIND_REQUEST_STATE,
    SEARCH_REQUEST_STATE,
    SEARCH_RESULT_ENTRY_STATE,
    SEARCH_RESULT_DONE_STATE,
    SEARCH_RESULT_REFERENCE_STATE,
    MODIFY_REQUEST_STATE,
    MODIFY_RESPONSE_STATE,
    ADD_REQUEST_STATE,
    ADD_RESPONSE_STATE,
    DEL_REQUEST_STATE,
    DEL_RESPONSE_STATE,
    MODIFY_DN_REQUEST_STATE,
    MODIFY_DN_RESPONSE_STATE,
    COMPARE_REQUEST_STATE,
    COMPARE_RESPONSE_STATE,
    ABANDON_REQUEST_STATE,
    EXTENDED_REQUEST_STATE,
    EXTENDED_RESPONSE_STATE,
    VERSION_STATE,
    NAME_STATE,
    SIMPLE_STATE,
    SASL_STATE,
    MECHANISM_STATE,
    CREDENTIALS_STATE,
    RESULT_CODE_BR_STATE,
    MATCHED_DN_BR_STATE,
    ERROR_MESSAGE_BR_STATE,
    REFERRALS_BR_STATE,
    REFERRAL_BR_STATE,
    SERVER_SASL_CREDENTIALS_STATE,
    RESULT_CODE_STATE,
    MATCHED_DN_STATE,
    ERROR_MESSAGE_STATE,
    REFERRALS_STATE,
    REFERRAL_STATE,
    REQUEST_NAME_STATE,
    REQUEST_VALUE_STATE,
    RESPONSE_NAME_STATE,
    RESPONSE_STATE,
    RESULT_CODE_ER_STATE,
    MATCHED_DN_ER_STATE,
    ERROR_MESSAGE_ER_STATE,
    REFERRALS_ER_STATE,
    REFERRAL_ER_STATE,
    ENTRY_STATE,
    ATTRIBUTES_STATE,
    ATTRIBUTE_STATE,
    TYPE_STATE,
    VALUES_STATE,
    VALUE_STATE,
    OBJECT_STATE,
    MODIFICATIONS_STATE,
    MODIFICATIONS_SEQ_STATE,
    OPERATION_STATE,
    MODIFICATION_STATE,
    TYPE_MOD_STATE,
    VALS_STATE,
    ATTRIBUTE_VALUE_STATE,
    ENTRY_MOD_DN_STATE,
    NEW_RDN_STATE,
    DELETE_OLD_RDN_STATE,
    NEW_SUPERIOR_STATE,
    ENTRY_COMP_STATE,
    AVA_STATE,
    ATTRIBUTE_DESC_STATE,
    ASSERTION_VALUE_STATE,
    BASE_OBJECT_STATE,
    SCOPE_STATE,
    DEREF_ALIAS_STATE,
    SIZE_LIMIT_STATE,
    TIME_LIMIT_STATE,
    TYPES_ONLY_STATE,
    AND_STATE,
    OR_STATE,
    NOT_STATE,
    EQUALITY_MATCH_STATE,
    SUBSTRING_FILTER_STATE,
    GREATER_OR_EQUAL_STATE,
    LESS_OR_EQUAL_STATE,
    PRESENT_STATE,
    APPROX_MATCH_STATE,
    EXTENSIBLE_MATCH_STATE,
    ATTRIBUTE_DESC_FILTER_STATE,
    ASSERTION_VALUE_FILTER_STATE,
    ATTRIBUTE_DESCRIPTION_LIST_STATE,
    ATTRIBUTE_DESCRIPTION_STATE,
    TYPE_SUBSTRING_STATE,
    SUBSTRINGS_STATE,
    INITIAL_STATE,
    ANY_STATE,
    FINAL_STATE,
    MATCHING_RULE_STATE,
    TYPE_MATCHING_RULE_STATE,
    MATCH_VALUE_STATE,
    DN_ATTRIBUTES_STATE,
    OBJECT_NAME_STATE,
    ATTRIBUTES_SR_STATE,
    PARTIAL_ATTRIBUTES_LIST_STATE,
    TYPE_SR_STATE,
    VALS_SR_STATE,
    ATTRIBUTE_VALUE_SR_STATE,
    REFERENCE_STATE,
    CONTROLS_STATE,
    CONTROL_STATE,
    CONTROL_TYPE_STATE,
    CRITICALITY_STATE,
    CONTROL_VALUE_STATE,
    INTERMEDIATE_RESPONSE_STATE,
    INTERMEDIATE_RESPONSE_NAME_STATE,
    INTERMEDIATE_RESPONSE_VALUE_STATE,
    LAST_LDAP_STATE;

    
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
        return ( ( state == END_STATE.ordinal() ) ? "LDAP_MESSAGE_END_STATE" : name() );
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
