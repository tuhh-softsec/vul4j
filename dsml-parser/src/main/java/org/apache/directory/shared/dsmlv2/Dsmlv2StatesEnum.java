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
    INIT_GRAMMAR_STATE(0),

    /** The ending state for every grammars */
    GRAMMAR_END(-1),

    /** The END_STATE */
    END_STATE(-1),

    //====================================================
    //  <batchRequest> ... </batchRequest>
    //====================================================
    /** The &lt;batchRequest&gt; tag */
    BATCHREQUEST_START_TAG(104),

    BATCHREQUEST_LOOP(105),

    /** The &lt;/batchRequest&gt; tag */
    BATCHREQUEST_END_TAG(1),

    //====================================================
    //  <abandonRequest> ... </abandonRequest>
    //====================================================
    /** The &lt;abandonRequest&gt; tag */
    ABANDON_REQUEST_START_TAG(2),

    /** The &lt;control&gt; tag */
    ABANDON_REQUEST_CONTROL_START_TAG(4),

    /** The &lt;/control&gt; tag */
    ABANDON_REQUEST_CONTROL_END_TAG(5),

    /** The &lt;controlValue&gt; tag */
    ABANDON_REQUEST_CONTROLVALUE_START_TAG(6),

    /** The &lt;/controlValue&gt; tag */
    ABANDON_REQUEST_CONTROLVALUE_END_TAG(7),

    //====================================================
    //  <addRequest> ... </addRequest>
    //====================================================
    /** The &lt;addRequest&gt; tag */
    ADD_REQUEST_START_TAG(8),

    /** The &lt;control&gt; tag */
    ADD_REQUEST_CONTROL_START_TAG(10),

    /** The &lt;/control&gt; tag */
    ADD_REQUEST_CONTROL_END_TAG(11),

    /** The &lt;controlValue&gt; tag */
    ADD_REQUEST_CONTROLVALUE_START_TAG(12),

    /** The &lt;/controlValue&gt; tag */
    ADD_REQUEST_CONTROLVALUE_END_TAG(13),

    /** The &lt;attr&gt; tag */
    ADD_REQUEST_ATTR_START_TAG(14),

    /** The &lt;/attr&gt; tag */
    ADD_REQUEST_ATTR_END_TAG(15),

    /** The &lt;value&gt; tag */
    ADD_REQUEST_VALUE_START_TAG(16),

    /** The &lt;/value&gt; tag */
    ADD_REQUEST_VALUE_END_TAG(17),

    //====================================================
    //  <authRequest> ... </authRequest>
    //====================================================
    /** The &lt;authRequest&gt; tag */
    AUTH_REQUEST_START_TAG(18),

    /** The &lt;control&gt; tag */
    AUTH_REQUEST_CONTROL_START_TAG(20),

    /** The &lt;/control&gt; tag */
    AUTH_REQUEST_CONTROL_END_TAG(21),

    /** The &lt;controlValue&gt; tag */
    AUTH_REQUEST_CONTROLVALUE_START_TAG(22),

    /** The &lt;/controlValue&gt; tag */
    AUTH_REQUEST_CONTROLVALUE_END_TAG(23),

    //====================================================
    //  <compareRequest> ... </compareRequest>
    //====================================================
    /** The &lt;compareRequest&gt; tag */
    COMPARE_REQUEST_START_TAG(24),

    /** The &lt;control&gt; tag */
    COMPARE_REQUEST_CONTROL_START_TAG(26),

    /** The &lt;/control&gt; tag */
    COMPARE_REQUEST_CONTROL_END_TAG(27),

    /** The &lt;controlValue&gt; tag */
    COMPARE_REQUEST_CONTROLVALUE_START_TAG(28),

    /** The &lt;/controlValue&gt; tag */
    COMPARE_REQUEST_CONTROLVALUE_END_TAG(29),

    /** The &lt;assertion&gt; tag */
    COMPARE_REQUEST_ASSERTION_START_TAG(30),

    /** The &lt;/assertion&gt; tag */
    COMPARE_REQUEST_ASSERTION_END_TAG(31),

    /** The &lt;value&gt; tag */
    COMPARE_REQUEST_VALUE_START_TAG(32),

    /** The &lt;/value&gt; tag */
    COMPARE_REQUEST_VALUE_END_TAG(33),

    //====================================================
    //  <delRequest> ... </delRequest>
    //====================================================
    /** The &lt;delRequest&gt; tag */
    DEL_REQUEST_START_TAG(34),

    /** The &lt;control&gt; tag */
    DEL_REQUEST_CONTROL_START_TAG(36),

    /** The &lt;/control&gt; tag */
    DEL_REQUEST_CONTROL_END_TAG(37),

    /** The &lt;controlValue&gt; tag */
    DEL_REQUEST_CONTROLVALUE_START_TAG(38),

    /** The &lt;/controlValue&gt; tag */
    DEL_REQUEST_CONTROLVALUE_END_TAG(39),

    //====================================================
    //  <extendedRequest> ... </extendedRequest>
    //====================================================
    /** The &lt;extendedRequest&gt; tag */
    EXTENDED_REQUEST_START_TAG(40),

    /** The &lt;control&gt; tag */
    EXTENDED_REQUEST_CONTROL_START_TAG(42),

    /** The &lt;/control&gt; tag */
    EXTENDED_REQUEST_CONTROL_END_TAG(43),

    /** The &lt;controlValue&gt; tag */
    EXTENDED_REQUEST_CONTROLVALUE_START_TAG(44),

    /** The &lt;/controlValue&gt; tag */
    EXTENDED_REQUEST_CONTROLVALUE_END_TAG(45),

    /** The &lt;requestName&gt; tag */
    EXTENDED_REQUEST_REQUESTNAME_START_TAG(46),

    /** The &lt;/requestName&gt; tag */
    EXTENDED_REQUEST_REQUESTNAME_END_TAG(47),

    /** The &lt;requestValue&gt; tag */
    EXTENDED_REQUEST_REQUESTVALUE_START_TAG(48),

    /** The &lt;/requestValue&gt; tag */
    EXTENDED_REQUEST_REQUESTVALUE_END_TAG(49),

    //====================================================
    //  <modDNRequest> ... </modDNRequest>
    //====================================================
    /** The &lt;modDNRequest&gt; tag */
    MODIFY_DN_REQUEST_START_TAG(50),

    /** The &lt;control&gt; tag */
    MODIFY_DN_REQUEST_CONTROL_START_TAG(52),

    /** The &lt;/control&gt; tag */
    MODIFY_DN_REQUEST_CONTROL_END_TAG(53),

    /** The &lt;controlValue&gt; tag */
    MODIFY_DN_REQUEST_CONTROLVALUE_START_TAG(54),

    /** The &lt;/controlValue&gt; tag */
    MODIFY_DN_REQUEST_CONTROLVALUE_END_TAG(55),

    //====================================================
    //  <modifyRequest> ... </modifyRequest>
    //====================================================
    /** The &lt;modifyRequest&gt; tag */
    MODIFY_REQUEST_START_TAG(56),

    /** The &lt;control&gt; tag */
    MODIFY_REQUEST_CONTROL_START_TAG(58),

    /** The &lt;/control&gt; tag */
    MODIFY_REQUEST_CONTROL_END_TAG(59),

    /** The &lt;controlValue&gt; tag */
    MODIFY_REQUEST_CONTROLVALUE_START_TAG(60),

    /** The &lt;/controlValue&gt; tag */
    MODIFY_REQUEST_CONTROLVALUE_END_TAG(61),

    /** The &lt;modification&gt; tag */
    MODIFY_REQUEST_MODIFICATION_START_TAG(62),

    /** The &lt;/modification&gt; tag */
    MODIFY_REQUEST_MODIFICATION_END_TAG(63),

    /** The &lt;value&gt; tag */
    MODIFY_REQUEST_VALUE_START_TAG(64),

    /** The &lt;/value&gt; tag */
    MODIFY_REQUEST_VALUE_END_TAG(65),

    //====================================================
    //  <searchRequest> ... </searchRequest>
    //====================================================
    /** The &lt;searchRequest&gt; tag */
    SEARCH_REQUEST_START_TAG(66),

    /** The &lt;control&gt; tag */
    SEARCH_REQUEST_CONTROL_START_TAG(68),

    /** The &lt;/control&gt; tag */
    SEARCH_REQUEST_CONTROL_END_TAG(69),

    /** The &lt;controlValue&gt; tag */
    SEARCH_REQUEST_CONTROLVALUE_START_TAG(70),

    /** The &lt;/controlValue&gt; tag */
    SEARCH_REQUEST_CONTROLVALUE_END_TAG(71),

    /** The &lt;filter&gt; tag */
    SEARCH_REQUEST_FILTER_START_TAG(72),

    /** The &lt;/filter&gt; tag */
    SEARCH_REQUEST_FILTER_END_TAG(73),

    /** The &lt;attributes&gt; tag */
    SEARCH_REQUEST_ATTRIBUTES_START_TAG(74),

    /** The &lt;/attributes&gt; tag */
    SEARCH_REQUEST_ATTRIBUTES_END_TAG(75),

    /** The &lt;attribute&gt; tag */
    SEARCH_REQUEST_ATTRIBUTE_START_TAG(76),

    /** The &lt;/attribute&gt; tag */
    SEARCH_REQUEST_ATTRIBUTE_END_TAG(77),

    /** The &lt;equalityMatch&gt; tag */
    SEARCH_REQUEST_EQUALITYMATCH_START_TAG(84),

    /** The &lt;subStrings&gt; tag */
    SEARCH_REQUEST_SUBSTRINGS_START_TAG(86),

    /** The &lt;/subStrings&gt; tag */
    SEARCH_REQUEST_SUBSTRINGS_END_TAG(87),

    /** The &lt;greaterOrEqual&gt; tag */
    SEARCH_REQUEST_GREATEROREQUAL_START_TAG(88),

    /** The &lt;lessOrEqual&gt; tag */
    SEARCH_REQUEST_LESSOREQUAL_START_TAG(90),

    /** The &lt;present&gt; tag */
    SEARCH_REQUEST_PRESENT_START_TAG(92),

    /** The &lt;approxMatch&gt; tag */
    SEARCH_REQUEST_APPROXMATCH_START_TAG(94),

    /** The &lt;extensibleMatch&gt; tag */
    SEARCH_REQUEST_EXTENSIBLEMATCH_START_TAG(96),

    /** The &lt;value&gt; tag */
    SEARCH_REQUEST_EXTENSIBLEMATCH_VALUE_START_TAG(109),

    /** The &lt;/value&gt; tag */
    SEARCH_REQUEST_EXTENSIBLEMATCH_VALUE_END_TAG(110),

    /** The &lt;initial&gt; tag */
    SEARCH_REQUEST_INITIAL_START_TAG(98),

    /** The &lt;/initial&gt; tag */
    SEARCH_REQUEST_INITIAL_END_TAG(99),

    /** The &lt;any&gt; tag */
    SEARCH_REQUEST_ANY_START_TAG(100),

    /** The &lt;/any&gt; tag */
    SEARCH_REQUEST_ANY_END_TAG(101),

    /** The &lt;final&gt; tag */
    SEARCH_REQUEST_FINAL_START_TAG(102),

    /** The &lt;/final&gt; tag */
    SEARCH_REQUEST_FINAL_END_TAG(103),

    /** The &lt;value&gt; tag */
    SEARCH_REQUEST_VALUE_START_TAG(107),

    /** The &lt;/value&gt; tag */
    SEARCH_REQUEST_VALUE_END_TAG(108),

    /** The Filter Loop state */
    SEARCH_REQUEST_FILTER_LOOP(106),

    //****************
    // DSML Response 
    //****************

    /** The Batch Response Loop state */
    BATCH_RESPONSE_LOOP(200),

    /** The Error Response Loop state */
    ERROR_RESPONSE(201),

    /** The Message Start state */
    MESSAGE_START(202),

    /** The Message End state */
    MESSAGE_END(203),

    /** The Detail Start state */
    DETAIL_START(204),

    /** The Detail End state */
    DETAIL_END(205),

    /** The Extended Response state */
    EXTENDED_RESPONSE(206),

    /** The Extended Response Control Start state */
    EXTENDED_RESPONSE_CONTROL_START(207),

    /** The Extended Response Control End state */
    EXTENDED_RESPONSE_CONTROL_END(208),

    /** The Extended Response Control Value Start state */
    EXTENDED_RESPONSE_CONTROL_VALUE_START(245),

    /** The Extended Response Control Value End state */
    EXTENDED_RESPONSE_CONTROL_VALUE_END(246),

    /** The Extended Response Result Code Start state */
    EXTENDED_RESPONSE_RESULT_CODE_START(209),

    /** The Extended Response Result Code End state */
    EXTENDED_RESPONSE_RESULT_CODE_END(210),

    /** The Extended Response Error Message Start state */
    EXTENDED_RESPONSE_ERROR_MESSAGE_START(211),

    /** The Extended Response Error Message End state */
    EXTENDED_RESPONSE_ERROR_MESSAGE_END(212),

    /** The Extended Response Referral Start state */
    EXTENDED_RESPONSE_REFERRAL_START(213),

    /** The Extended Response Referral End state */
    EXTENDED_RESPONSE_REFERRAL_END(214),

    /** The Response Name Start state */
    RESPONSE_NAME_START(215),

    /** The Response Name End state */
    RESPONSE_NAME_END(216),

    /** The Response Start state */
    RESPONSE_START(217),

    /** The Response End state */
    RESPONSE_END(218),

    /** The LDAP Result state */
    LDAP_RESULT(219),

    /** The LDAP Result Control Start state */
    LDAP_RESULT_CONTROL_START(220),

    /** The LDAP Result Control End state */
    LDAP_RESULT_CONTROL_END(221),

    /** The LDAP Result Control Value Start state */
    LDAP_RESULT_CONTROL_VALUE_START(247),

    /** The LDAP Result Control Value End state */
    LDAP_RESULT_CONTROL_VALUE_END(248),

    /** The LDAP Result Result Code Start state */
    LDAP_RESULT_RESULT_CODE_START(222),

    /** The LDAP Result Result Code End state */
    LDAP_RESULT_RESULT_CODE_END(223),

    /** The LDAP Result Error Message Start state */
    LDAP_RESULT_ERROR_MESSAGE_START(224),

    /** The LDAP Result Error Message End state */
    LDAP_RESULT_ERROR_MESSAGE_END(225),

    /** The LDAP Result Referral Start state */
    LDAP_RESULT_REFERRAL_START(226),

    /** The LDAP Result Referral End state */
    LDAP_RESULT_REFERRAL_END(227),

    /** The LDAP Result End state */
    LDAP_RESULT_END(228),

    /** The Search Response state */
    SEARCH_RESPONSE(229),

    /** The Search Result Entry state */
    SEARCH_RESULT_ENTRY(230),

    /** The Search Result Entry Control Start state */
    SEARCH_RESULT_ENTRY_CONTROL_START(231),

    /** The Search Result Entry Control End state */
    SEARCH_RESULT_ENTRY_CONTROL_END(232),

    /** The Search Result Entry Control Value Start state */
    SEARCH_RESULT_ENTRY_CONTROL_VALUE_START(249),

    /** The Search Result Entry Control Value End state */
    SEARCH_RESULT_ENTRY_CONTROL_VALUE_END(250),

    /** The Search Result Entry Attr Start state */
    SEARCH_RESULT_ENTRY_ATTR_START(233),

    /** The Search Result Entry Attr End state */
    SEARCH_RESULT_ENTRY_ATTR_END(234),

    /** The Search Result Entry Value Start state */
    SEARCH_RESULT_ENTRY_VALUE_START(235),

    /** The Search Result Entry Value End state */
    SEARCH_RESULT_ENTRY_VALUE_END(236),

    /** The Search Result Entry Loop state */
    SEARCH_RESULT_ENTRY_LOOP(237),

    /** The Search Result Reference state */
    SEARCH_RESULT_REFERENCE(238),

    /** The Search Result Reference Control Start state */
    SEARCH_RESULT_REFERENCE_CONTROL_START(239),

    /** The Search Result Reference Control End state */
    SEARCH_RESULT_REFERENCE_CONTROL_END(240),

    /** The Search Result Reference Control Value Start state */
    SEARCH_RESULT_REFERENCE_CONTROL_VALUE_START(251),

    /** The Search Result Reference Control Value End state */
    SEARCH_RESULT_REFERENCE_CONTROL_VALUE_END(252),

    /** The Search Result Reference Ref Start state */
    SEARCH_RESULT_REFERENCE_REF_START(241),

    /** The Search Result Reference Ref End state */
    SEARCH_RESULT_REFERENCE_REF_END(242),

    /** The Search Result Reference Loop state */
    SEARCH_RESULT_REFERENCE_LOOP(243),

    /** The Search Result Done End state */
    SEARCH_RESULT_DONE_END(244);
    
    private int state;

    /**
     * 
     * Creates a new instance of Dsmlv2StatesEnum.
     *
     * @param state
     */
    Dsmlv2StatesEnum(int state)
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
}
