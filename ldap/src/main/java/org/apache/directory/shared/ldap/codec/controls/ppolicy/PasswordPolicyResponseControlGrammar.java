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

package org.apache.directory.shared.ldap.codec.controls.ppolicy;


import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.actions.CheckNotNullLength;
import org.apache.directory.shared.ldap.codec.controls.ppolicy.actions.PPolicyInit;
import org.apache.directory.shared.ldap.codec.controls.ppolicy.actions.StoreError;
import org.apache.directory.shared.ldap.codec.controls.ppolicy.actions.StoreGraceAuthsRemaining;
import org.apache.directory.shared.ldap.codec.controls.ppolicy.actions.StoreTimeBeforeExpiration;


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
public class PasswordPolicyResponseControlGrammar extends AbstractGrammar
{
    /** PasswordPolicyResponseControlGrammar singleton instance */
    private static final PasswordPolicyResponseControlGrammar INSTANCE = new PasswordPolicyResponseControlGrammar();


    private PasswordPolicyResponseControlGrammar()
    {
        setName( PasswordPolicyResponseControlGrammar.class.getName() );

        super.transitions = new GrammarTransition[PasswordPolicyResponseControlStates.END_STATE.ordinal()][256];


        // PasswordPolicyResponseValue ::= SEQUENCE {
        // ...
        super.transitions[PasswordPolicyResponseControlStates.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.START_STATE, PasswordPolicyResponseControlStates.PPOLICY_SEQ_STATE, UniversalTag.SEQUENCE.getValue(),
            new PPolicyInit());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_SEQ_STATE.ordinal()][PasswordPolicyResponseControlTags.PPOLICY_WARNING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_SEQ_STATE, PasswordPolicyResponseControlStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyResponseControlTags.PPOLICY_WARNING_TAG.getValue(),
            new CheckNotNullLength());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_SEQ_STATE.ordinal()][PasswordPolicyResponseControlTags.PPOLICY_ERROR_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_SEQ_STATE, PasswordPolicyResponseControlStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyResponseControlTags.PPOLICY_ERROR_TAG.getValue(),
            new StoreError());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //                      timeBeforeExpiration [0] INTEGER (0 .. maxInt),
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_WARNING_TAG_STATE.ordinal()][PasswordPolicyResponseControlTags.TIME_BEFORE_EXPIRATION_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE, PasswordPolicyResponseControlTags.TIME_BEFORE_EXPIRATION_TAG.getValue(),
            new StoreTimeBeforeExpiration());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //                      ...
        //                      graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_WARNING_TAG_STATE.ordinal()][PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE, PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG.getValue(),
            new StoreGraceAuthsRemaining());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE.ordinal()][PasswordPolicyResponseControlTags.PPOLICY_ERROR_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE, PasswordPolicyResponseControlStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyResponseControlTags.PPOLICY_ERROR_TAG.getValue(),
            new StoreError());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE.ordinal()][PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE, PasswordPolicyResponseControlStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG.getValue(),
            new StoreError());
    }


    public static Grammar getInstance()
    {
        return INSTANCE;
    }
}
