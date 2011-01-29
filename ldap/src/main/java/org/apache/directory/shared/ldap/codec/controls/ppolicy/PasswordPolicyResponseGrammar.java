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
public class PasswordPolicyResponseGrammar extends AbstractGrammar
{
    /** PasswordPolicyResponseControlGrammar singleton instance */
    private static final PasswordPolicyResponseGrammar INSTANCE = new PasswordPolicyResponseGrammar();


    private PasswordPolicyResponseGrammar()
    {
        setName( PasswordPolicyResponseGrammar.class.getName() );

        super.transitions = new GrammarTransition[PasswordPolicyResponseStates.END_STATE.ordinal()][256];


        // PasswordPolicyResponseValue ::= SEQUENCE {
        // ...
        super.transitions[PasswordPolicyResponseStates.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            PasswordPolicyResponseStates.START_STATE, PasswordPolicyResponseStates.PPOLICY_SEQ_STATE, UniversalTag.SEQUENCE.getValue(),
            new PPolicyInit());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        super.transitions[PasswordPolicyResponseStates.PPOLICY_SEQ_STATE.ordinal()][PasswordPolicyResponseTags.PPOLICY_WARNING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseStates.PPOLICY_SEQ_STATE, PasswordPolicyResponseStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyResponseTags.PPOLICY_WARNING_TAG.getValue(),
            new CheckNotNullLength());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseStates.PPOLICY_SEQ_STATE.ordinal()][PasswordPolicyResponseTags.PPOLICY_ERROR_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseStates.PPOLICY_SEQ_STATE, PasswordPolicyResponseStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyResponseTags.PPOLICY_ERROR_TAG.getValue(),
            new StoreError());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //                      timeBeforeExpiration [0] INTEGER (0 .. maxInt),
        super.transitions[PasswordPolicyResponseStates.PPOLICY_WARNING_TAG_STATE.ordinal()][PasswordPolicyResponseTags.TIME_BEFORE_EXPIRATION_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyResponseStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE, PasswordPolicyResponseTags.TIME_BEFORE_EXPIRATION_TAG.getValue(),
            new StoreTimeBeforeExpiration());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //                      ...
        //                      graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
        super.transitions[PasswordPolicyResponseStates.PPOLICY_WARNING_TAG_STATE.ordinal()][PasswordPolicyResponseTags.GRACE_AUTHNS_REMAINING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseStates.PPOLICY_WARNING_TAG_STATE, PasswordPolicyResponseStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE, PasswordPolicyResponseTags.GRACE_AUTHNS_REMAINING_TAG.getValue(),
            new StoreGraceAuthsRemaining());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE.ordinal()][PasswordPolicyResponseTags.PPOLICY_ERROR_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE, PasswordPolicyResponseStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyResponseTags.PPOLICY_ERROR_TAG.getValue(),
            new StoreError());
        
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              ...
        //              error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE.ordinal()][PasswordPolicyResponseTags.GRACE_AUTHNS_REMAINING_TAG.getValue()] = new GrammarTransition(
            PasswordPolicyResponseStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE, PasswordPolicyResponseStates.PPOLICY_ERROR_TAG_STATE, PasswordPolicyResponseTags.GRACE_AUTHNS_REMAINING_TAG.getValue(),
            new StoreError());
    }


    public static Grammar getInstance()
    {
        return INSTANCE;
    }
}
