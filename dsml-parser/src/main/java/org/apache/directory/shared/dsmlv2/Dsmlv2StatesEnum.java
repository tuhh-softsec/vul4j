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
package org.apache.directory.shared.dsmlv2;


/**
 * This class store the Dsml grammar's constants. It is also used for debugging
 * purpose.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum Dsmlv2StatesEnum
{
    /** The initial state of every grammar */
    INIT_GRAMMAR_STATE,

    /** The ending state for every grammars */
    GRAMMAR_END,

    //====================================================
    //  <batchRequest> ... </batchRequest>
    //====================================================
    /** The &lt;batchRequest&gt; tag */
    BATCHREQUEST_START_TAG,

    BATCHREQUEST_LOOP,

    /** The &lt;/batchRequest&gt; tag */
    BATCHREQUEST_END_TAG,

    //====================================================
    //  <abandonRequest> ... </abandonRequest>
    //====================================================
    /** The &lt;abandonRequest&gt; tag */
    ABANDON_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    ABANDON_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    ABANDON_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    ABANDON_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    ABANDON_REQUEST_CONTROLVALUE_END_TAG,

    //====================================================
    //  <addRequest> ... </addRequest>
    //====================================================
    /** The &lt;addRequest&gt; tag */
    ADD_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    ADD_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    ADD_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    ADD_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    ADD_REQUEST_CONTROLVALUE_END_TAG,

    /** The &lt;attr&gt; tag */
    ADD_REQUEST_ATTR_START_TAG,

    /** The &lt;/attr&gt; tag */
    ADD_REQUEST_ATTR_END_TAG,

    /** The &lt;value&gt; tag */
    ADD_REQUEST_VALUE_START_TAG,

    /** The &lt;/value&gt; tag */
    ADD_REQUEST_VALUE_END_TAG,

    //====================================================
    //  <authRequest> ... </authRequest>
    //====================================================
    /** The &lt;authRequest&gt; tag */
    AUTH_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    AUTH_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    AUTH_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    AUTH_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    AUTH_REQUEST_CONTROLVALUE_END_TAG,

    //====================================================
    //  <compareRequest> ... </compareRequest>
    //====================================================
    /** The &lt;compareRequest&gt; tag */
    COMPARE_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    COMPARE_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    COMPARE_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    COMPARE_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    COMPARE_REQUEST_CONTROLVALUE_END_TAG,

    /** The &lt;assertion&gt; tag */
    COMPARE_REQUEST_ASSERTION_START_TAG,

    /** The &lt;/assertion&gt; tag */
    COMPARE_REQUEST_ASSERTION_END_TAG,

    /** The &lt;value&gt; tag */
    COMPARE_REQUEST_VALUE_START_TAG,

    /** The &lt;/value&gt; tag */
    COMPARE_REQUEST_VALUE_END_TAG,

    //====================================================
    //  <delRequest> ... </delRequest>
    //====================================================
    /** The &lt;delRequest&gt; tag */
    DEL_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    DEL_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    DEL_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    DEL_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    DEL_REQUEST_CONTROLVALUE_END_TAG,

    //====================================================
    //  <extendedRequest> ... </extendedRequest>
    //====================================================
    /** The &lt;extendedRequest&gt; tag */
    EXTENDED_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    EXTENDED_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    EXTENDED_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    EXTENDED_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    EXTENDED_REQUEST_CONTROLVALUE_END_TAG,

    /** The &lt;requestName&gt; tag */
    EXTENDED_REQUEST_REQUESTNAME_START_TAG,

    /** The &lt;/requestName&gt; tag */
    EXTENDED_REQUEST_REQUESTNAME_END_TAG,

    /** The &lt;requestValue&gt; tag */
    EXTENDED_REQUEST_REQUESTVALUE_START_TAG,

    /** The &lt;/requestValue&gt; tag */
    EXTENDED_REQUEST_REQUESTVALUE_END_TAG,

    //====================================================
    //  <modDNRequest> ... </modDNRequest>
    //====================================================
    /** The &lt;modDNRequest&gt; tag */
    MODIFY_DN_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    MODIFY_DN_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    MODIFY_DN_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    MODIFY_DN_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    MODIFY_DN_REQUEST_CONTROLVALUE_END_TAG,

    //====================================================
    //  <modifyRequest> ... </modifyRequest>
    //====================================================
    /** The &lt;modifyRequest&gt; tag */
    MODIFY_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    MODIFY_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    MODIFY_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    MODIFY_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    MODIFY_REQUEST_CONTROLVALUE_END_TAG,

    /** The &lt;modification&gt; tag */
    MODIFY_REQUEST_MODIFICATION_START_TAG,

    /** The &lt;/modification&gt; tag */
    MODIFY_REQUEST_MODIFICATION_END_TAG,

    /** The &lt;value&gt; tag */
    MODIFY_REQUEST_VALUE_START_TAG,

    /** The &lt;/value&gt; tag */
    MODIFY_REQUEST_VALUE_END_TAG,

    //====================================================
    //  <searchRequest> ... </searchRequest>
    //====================================================
    /** The &lt;searchRequest&gt; tag */
    SEARCH_REQUEST_START_TAG,

    /** The &lt;control&gt; tag */
    SEARCH_REQUEST_CONTROL_START_TAG,

    /** The &lt;/control&gt; tag */
    SEARCH_REQUEST_CONTROL_END_TAG,

    /** The &lt;controlValue&gt; tag */
    SEARCH_REQUEST_CONTROLVALUE_START_TAG,

    /** The &lt;/controlValue&gt; tag */
    SEARCH_REQUEST_CONTROLVALUE_END_TAG,

    /** The &lt;filter&gt; tag */
    SEARCH_REQUEST_FILTER_START_TAG,

    /** The &lt;/filter&gt; tag */
    SEARCH_REQUEST_FILTER_END_TAG,

    /** The &lt;attributes&gt; tag */
    SEARCH_REQUEST_ATTRIBUTES_START_TAG,

    /** The &lt;/attributes&gt; tag */
    SEARCH_REQUEST_ATTRIBUTES_END_TAG,

    /** The &lt;attribute&gt; tag */
    SEARCH_REQUEST_ATTRIBUTE_START_TAG,

    /** The &lt;/attribute&gt; tag */
    SEARCH_REQUEST_ATTRIBUTE_END_TAG,

    /** The &lt;equalityMatch&gt; tag */
    SEARCH_REQUEST_EQUALITYMATCH_START_TAG,

    /** The &lt;subStrings&gt; tag */
    SEARCH_REQUEST_SUBSTRINGS_START_TAG,

    /** The &lt;/subStrings&gt; tag */
    SEARCH_REQUEST_SUBSTRINGS_END_TAG,

    /** The &lt;greaterOrEqual&gt; tag */
    SEARCH_REQUEST_GREATEROREQUAL_START_TAG,

    /** The &lt;lessOrEqual&gt; tag */
    SEARCH_REQUEST_LESSOREQUAL_START_TAG,

    /** The &lt;present&gt; tag */
    SEARCH_REQUEST_PRESENT_START_TAG,

    /** The &lt;approxMatch&gt; tag */
    SEARCH_REQUEST_APPROXMATCH_START_TAG,

    /** The &lt;extensibleMatch&gt; tag */
    SEARCH_REQUEST_EXTENSIBLEMATCH_START_TAG,

    /** The &lt;value&gt; tag */
    SEARCH_REQUEST_EXTENSIBLEMATCH_VALUE_START_TAG,

    /** The &lt;/value&gt; tag */
    SEARCH_REQUEST_EXTENSIBLEMATCH_VALUE_END_TAG,

    /** The &lt;initial&gt; tag */
    SEARCH_REQUEST_INITIAL_START_TAG,

    /** The &lt;/initial&gt; tag */
    SEARCH_REQUEST_INITIAL_END_TAG,

    /** The &lt;any&gt; tag */
    SEARCH_REQUEST_ANY_START_TAG,

    /** The &lt;/any&gt; tag */
    SEARCH_REQUEST_ANY_END_TAG,

    /** The &lt;final&gt; tag */
    SEARCH_REQUEST_FINAL_START_TAG,

    /** The &lt;/final&gt; tag */
    SEARCH_REQUEST_FINAL_END_TAG,

    /** The &lt;value&gt; tag */
    SEARCH_REQUEST_VALUE_START_TAG,

    /** The &lt;/value&gt; tag */
    SEARCH_REQUEST_VALUE_END_TAG,

    /** The Filter Loop state */
    SEARCH_REQUEST_FILTER_LOOP,

    //****************
    // DSML Response 
    //****************

    /** The Batch Response Loop state */
    BATCH_RESPONSE_LOOP,

    /** The Error Response Loop state */
    ERROR_RESPONSE,

    /** The Message Start state */
    MESSAGE_START,

    /** The Message End state */
    MESSAGE_END,

    /** The Detail Start state */
    DETAIL_START,

    /** The Detail End state */
    DETAIL_END,

    /** The Extended Response state */
    EXTENDED_RESPONSE,

    /** The Extended Response Control Start state */
    EXTENDED_RESPONSE_CONTROL_START,

    /** The Extended Response Control End state */
    EXTENDED_RESPONSE_CONTROL_END,

    /** The Extended Response Control Value Start state */
    EXTENDED_RESPONSE_CONTROL_VALUE_START,

    /** The Extended Response Control Value End state */
    EXTENDED_RESPONSE_CONTROL_VALUE_END,

    /** The Extended Response Result Code Start state */
    EXTENDED_RESPONSE_RESULT_CODE_START,

    /** The Extended Response Result Code End state */
    EXTENDED_RESPONSE_RESULT_CODE_END,

    /** The Extended Response Error Message Start state */
    EXTENDED_RESPONSE_ERROR_MESSAGE_START,

    /** The Extended Response Error Message End state */
    EXTENDED_RESPONSE_ERROR_MESSAGE_END,

    /** The Extended Response Referral Start state */
    EXTENDED_RESPONSE_REFERRAL_START,

    /** The Extended Response Referral End state */
    EXTENDED_RESPONSE_REFERRAL_END,

    /** The Response Name Start state */
    RESPONSE_NAME_START,

    /** The Response Name End state */
    RESPONSE_NAME_END,

    /** The Response Start state */
    RESPONSE_START,

    /** The Response End state */
    RESPONSE_END,

    /** The LDAP Result state */
    LDAP_RESULT,

    /** The LDAP Result Control Start state */
    LDAP_RESULT_CONTROL_START,

    /** The LDAP Result Control End state */
    LDAP_RESULT_CONTROL_END,

    /** The LDAP Result Control Value Start state */
    LDAP_RESULT_CONTROL_VALUE_START,

    /** The LDAP Result Control Value End state */
    LDAP_RESULT_CONTROL_VALUE_END,

    /** The LDAP Result Result Code Start state */
    LDAP_RESULT_RESULT_CODE_START,

    /** The LDAP Result Result Code End state */
    LDAP_RESULT_RESULT_CODE_END,

    /** The LDAP Result Error Message Start state */
    LDAP_RESULT_ERROR_MESSAGE_START,

    /** The LDAP Result Error Message End state */
    LDAP_RESULT_ERROR_MESSAGE_END,

    /** The LDAP Result Referral Start state */
    LDAP_RESULT_REFERRAL_START,

    /** The LDAP Result Referral End state */
    LDAP_RESULT_REFERRAL_END,

    /** The LDAP Result End state */
    LDAP_RESULT_END,

    /** The Search Response state */
    SEARCH_RESPONSE,

    /** The Search Result Entry state */
    SEARCH_RESULT_ENTRY,

    /** The Search Result Entry Control Start state */
    SEARCH_RESULT_ENTRY_CONTROL_START,

    /** The Search Result Entry Control End state */
    SEARCH_RESULT_ENTRY_CONTROL_END,

    /** The Search Result Entry Control Value Start state */
    SEARCH_RESULT_ENTRY_CONTROL_VALUE_START,

    /** The Search Result Entry Control Value End state */
    SEARCH_RESULT_ENTRY_CONTROL_VALUE_END,

    /** The Search Result Entry Attr Start state */
    SEARCH_RESULT_ENTRY_ATTR_START,

    /** The Search Result Entry Attr End state */
    SEARCH_RESULT_ENTRY_ATTR_END,

    /** The Search Result Entry Value Start state */
    SEARCH_RESULT_ENTRY_VALUE_START,

    /** The Search Result Entry Value End state */
    SEARCH_RESULT_ENTRY_VALUE_END,

    /** The Search Result Entry Loop state */
    SEARCH_RESULT_ENTRY_LOOP,

    /** The Search Result Reference state */
    SEARCH_RESULT_REFERENCE,

    /** The Search Result Reference Control Start state */
    SEARCH_RESULT_REFERENCE_CONTROL_START,

    /** The Search Result Reference Control End state */
    SEARCH_RESULT_REFERENCE_CONTROL_END,

    /** The Search Result Reference Control Value Start state */
    SEARCH_RESULT_REFERENCE_CONTROL_VALUE_START,

    /** The Search Result Reference Control Value End state */
    SEARCH_RESULT_REFERENCE_CONTROL_VALUE_END,

    /** The Search Result Reference Ref Start state */
    SEARCH_RESULT_REFERENCE_REF_START,

    /** The Search Result Reference Ref End state */
    SEARCH_RESULT_REFERENCE_REF_END,

    /** The Search Result Reference Loop state */
    SEARCH_RESULT_REFERENCE_LOOP,

    /** The Search Result Done End state */
    SEARCH_RESULT_DONE_END
}
