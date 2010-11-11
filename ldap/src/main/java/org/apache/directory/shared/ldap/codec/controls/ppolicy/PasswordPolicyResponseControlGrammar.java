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


import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.asn1.util.IntegerDecoder;
import org.apache.directory.shared.asn1.util.IntegerDecoderException;
import org.apache.directory.shared.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger( PasswordPolicyResponseControlGrammar.class );

    /** speedup for logger */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** PasswordPolicyResponseControlGrammar singleton instance */
    private static final PasswordPolicyResponseControlGrammar INSTANCE = new PasswordPolicyResponseControlGrammar();


    private PasswordPolicyResponseControlGrammar()
    {
        setName( PasswordPolicyResponseControlGrammar.class.getName() );

        super.transitions = new GrammarTransition[PasswordPolicyResponseControlStates.END_STATE.ordinal()][256];

        // PasswordPolicyResponseValue ::= SEQUENCE {
        // ...
        super.transitions[PasswordPolicyResponseControlStates.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.START_STATE, PasswordPolicyResponseControlStates.SEQ_STATE,
            UniversalTag.SEQUENCE.getValue(),
            new GrammarAction( "Initializating PasswordPolicyResponseControl" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;

                    // As all the values are optional or defaulted, we can end here
                    ppolicyRespContainer.setGrammarEndAllowed( true );
                }
            } );

        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //              timeBeforeExpiration [0]
        super.transitions[PasswordPolicyResponseControlStates.SEQ_STATE.ordinal()][PasswordPolicyResponseControlTags.TIME_BEFORE_EXPIRATION_TAG
             .getValue()] = new GrammarTransition( PasswordPolicyResponseControlStates.SEQ_STATE,
             PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_TAG_STATE,
             PasswordPolicyResponseControlTags.TIME_BEFORE_EXPIRATION_TAG.getValue(), new GrammarAction( "read the ppolicy time before expiration warning tag" )
             {
                 public void action( Asn1Container container ) throws DecoderException
                 {

                 }
             } );

        // timeBeforeExpiration [0] INTEGER (0 .. maxInt)
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()]
            = new GrammarTransition( PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_TAG_STATE,
            PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE,
            UniversalTag.INTEGER.getValue(), new GrammarAction(
                "set time before expiration value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;
                    Value value = ppolicyRespContainer.getCurrentTLV().getValue();
                    try
                    {
                        int timeBeforeExp = IntegerDecoder.parse( value, 0, Integer.MAX_VALUE );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "timeBeforeExpiration {}", timeBeforeExp );
                        }

                        ppolicyRespContainer.getPasswordPolicyResponseControl().setTimeBeforeExpiration( timeBeforeExp );
                    }
                    catch ( IntegerDecoderException e )
                    {
                        String msg = I18n.err( I18n.ERR_04028 );
                        LOG.error( msg, e );
                        throw new DecoderException( msg );
                    }

                    ppolicyRespContainer.setGrammarEndAllowed( true );
                }
            } );

        // PasswordPolicyResponseValue ::= SEQUENCE {
        //              warning [0] CHOICE {
        //              graceAuthNsRemaining [1]
        super.transitions[PasswordPolicyResponseControlStates.SEQ_STATE.ordinal()][PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG
             .getValue()] = new GrammarTransition( PasswordPolicyResponseControlStates.SEQ_STATE,
             PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_TAG_STATE,
             PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG.getValue(), new GrammarAction( "read the ppolicy grace auth warning tag" )
             {
                 public void action( Asn1Container container ) throws DecoderException
                 {

                 }
             } );

        // graceAuthNsRemaining [1] INTEGER (0 .. maxInt)
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_TAG_STATE.ordinal()][UniversalTag.INTEGER.getValue()]
            = new GrammarTransition( PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_TAG_STATE,
            PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE,
            UniversalTag.INTEGER.getValue(), new GrammarAction(
                "set number of grace authentications remaining" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;
                    Value value = ppolicyRespContainer.getCurrentTLV().getValue();
                    try
                    {
                        int graceAuthNum = IntegerDecoder.parse( value, 0, Integer.MAX_VALUE );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "graceAuthNsRemaining {}", graceAuthNum );
                        }

                        ppolicyRespContainer.getPasswordPolicyResponseControl().setGraceAuthNsRemaining( graceAuthNum );
                    }
                    catch ( IntegerDecoderException e )
                    {
                        String msg = I18n.err( I18n.ERR_04028 );
                        LOG.error( msg, e );
                        throw new DecoderException( msg );
                    }

                    ppolicyRespContainer.setGrammarEndAllowed( true );
                }
            } );


        // transition to the ppolic error after the PPOLICY_TIME_BEFORE_EXPIRATION_STATE
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //         warning [0] CHOICE {
        //         timeBeforeExpiration [0] INTEGER (0 .. maxInt),
        //
        //         error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE.ordinal()][UniversalTag.ENUMERATED.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_TIME_BEFORE_EXPIRATION_STATE, PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE,
            UniversalTag.ENUMERATED.getValue(), new GrammarAction( "set ppolicy error value after reading the timeBeforeExpiration value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;

                    setPasswordPolicyError( ppolicyRespContainer );

                    ppolicyRespContainer.setGrammarEndAllowed( true );
                }
            } );

        
        // transition to the ppolic error after the PPOLICY_GRACE_AUTHNS_REMAINING_STATE
        // PasswordPolicyResponseValue ::= SEQUENCE {
        //         warning [0] CHOICE {
        //          ...
        //         graceAuthNsRemaining [1] INTEGER (0 .. maxInt) } OPTIONAL,
        //
        //         error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE.ordinal()][UniversalTag.ENUMERATED.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_GRACE_AUTHNS_REMAINING_STATE, PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE,
            UniversalTag.ENUMERATED.getValue(), new GrammarAction( "set ppolicy error value after reading the graceAuthNsRemaining value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;

                    setPasswordPolicyError( ppolicyRespContainer );

                    ppolicyRespContainer.setGrammarEndAllowed( true );
                }
            } );

        // PasswordPolicyResponseValue ::= SEQUENCE {
        //          error   [1] ENUMERATED {
        super.transitions[PasswordPolicyResponseControlStates.SEQ_STATE.ordinal()][UniversalTag.ENUMERATED.getValue()] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE, PasswordPolicyResponseControlStates.END_STATE,
            UniversalTag.ENUMERATED.getValue(), new GrammarAction( "set ppolicy error value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;

                    setPasswordPolicyError( ppolicyRespContainer );

                    ppolicyRespContainer.setGrammarEndAllowed( true );
                }
            } );
    }


    /**
     * read and set the Value of password policy error
     *
     * @param ppolicyRespContainer the container holding PasswordPolicyResponceControl
     * @throws DecoderException
     */
    private void setPasswordPolicyError( PasswordPolicyResponseControlContainer ppolicyRespContainer )
        throws DecoderException
    {
        Value value = ppolicyRespContainer.getCurrentTLV().getValue();
        try
        {
            int errorNum = IntegerDecoder.parse( value,
                PasswordPolicyErrorEnum.PASSWORD_EXPIRED.getValue(),
                PasswordPolicyErrorEnum.PASSWORD_IN_HISTORY.getValue() );

            if ( IS_DEBUG )
            {
                LOG.debug( "password policy error {}", errorNum );
            }

            ppolicyRespContainer.getPasswordPolicyResponseControl().setPasswordPolicyError(
                PasswordPolicyErrorEnum.get( errorNum ) );
        }
        catch ( IntegerDecoderException e )
        {
            String msg = I18n.err( I18n.ERR_04028 );
            LOG.error( msg, e );
            throw new DecoderException( msg );
        }

    }


    public static Grammar getInstance()
    {
        return INSTANCE;
    }
}
