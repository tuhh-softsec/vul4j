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

package org.apache.directory.shared.ldap.extras.controls.ppolicy_impl;


import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.actions.CheckNotNullLength;


/**
 * Grammar for decoding PasswordPolicyResponseControl.
 *
 * PasswordPolicyResponseValue ::= SEQUENCE {
 *         warning [0] CHOICE {
 *         timeBeforeExpiration [0] INTEGER (0 .. maxInt),
 *         graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
 *         
 *      error   [1] ENUMERATED {
 *          passwordExpired             (0),
 *          accountLocked               (1),
 *          changeAfterReset            (2),
 *          passwordModNotAllowed       (3),
 *          mustSupplyOldPassword       (4),
 *          insufficientPasswordQuality (5),
 *          passwordTooShort            (6),
 *          passwordTooYoung            (7),
 *          passwordInHistory           (8) } OPTIONAL }
 *          
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class PasswordPolicyGrammar extends AbstractGrammar<PasswordPolicyContainer>
{
    /** PasswordPolicyResponseControlGrammar singleton instance */
    private static final PasswordPolicyGrammar INSTANCE = new PasswordPolicyGrammar();


    @SuppressWarnings({ "unchecked", "rawtypes" })
    private PasswordPolicyGrammar()
    {
        setName( PasswordPolicyGrammar.class.getName() );

        super.transitions = new GrammarTransition[PasswordPolicyStates.END_STATE.ordinal()][256];


        // PasswordPolicyResponseValue ::= SEQUENCE {
        // ...
        super.transitions[PasswordPolicyStates.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            PasswordPolicyStates.START_STATE, PasswordPolicyStates.PPOLICY_SEQ_STATE, UniversalTag.SEQUENCE.getValue(),
            new PPolicyInit());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        super.transitions[PasswordPolicyStates.PPOLICY_SEQ_STATE.ordinal()][PasswordPolicyTags.PPOLICY_WARNING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyStates.PPOLICY_SEQ_STATE, PasswordPolicyStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyTags.PPOLICY_WARNING_TAG.getValue(),
            new CheckNotNullLength());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyStates.PPOLICY_SEQ_STATE.ordinal()][PasswordPolicyTags.PPOLICY_ERROR_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyStates.PPOLICY_SEQ_STATE, PasswordPolicyStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyTags.PPOLICY_ERROR_TAG.getValue(),
            new StoreError());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //                      timeBeforeExpiration [0] INTEGER (0 .. maxInt),
        super.transitions[PasswordPolicyStates.PPOLICY_WARNING_TAG_STATE.ordinal()][PasswordPolicyTags.TIME_BEFORE_EXPIRATION_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE, PasswordPolicyTags.TIME_BEFORE_EXPIRATION_TAG.getValue(),
            new StoreTimeBeforeExpiration());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //                      ...
        //                      graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
        super.transitions[PasswordPolicyStates.PPOLICY_WARNING_TAG_STATE.ordinal()][PasswordPolicyTags.GRACE_AUTHNS_REMAINING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE, PasswordPolicyTags.GRACE_AUTHNS_REMAINING_TAG.getValue(),
            new StoreGraceAuthsRemaining());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE.ordinal()][PasswordPolicyTags.PPOLICY_ERROR_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE, PasswordPolicyStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyTags.PPOLICY_ERROR_TAG.getValue(),
            new StoreError());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE.ordinal()][PasswordPolicyTags.GRACE_AUTHNS_REMAINING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE, PasswordPolicyStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyTags.GRACE_AUTHNS_REMAINING_TAG.getValue(),
            new StoreError());
    }


    public static Grammar<PasswordPolicyContainer> getInstance()
    {
        return INSTANCE;
    }
}
