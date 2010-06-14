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


import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.grammar.IGrammar;
import org.apache.directory.shared.asn1.ber.grammar.IStates;
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
        name = PasswordPolicyResponseControlGrammar.class.getName();
        statesEnum = PasswordPolicyResponseControlStates.getInstance();

        super.transitions = new GrammarTransition[PasswordPolicyResponseControlStates.END_STATE][256];

        super.transitions[IStates.INIT_GRAMMAR_STATE][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
            IStates.INIT_GRAMMAR_STATE, PasswordPolicyResponseControlStates.START_STATE, UniversalTag.SEQUENCE_TAG,
            new GrammarAction( "Initialization" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;

                    // As all the values are optional or defaulted, we can end here
                    ppolicyRespContainer.grammarEndAllowed( true );
                }
            } );

        super.transitions[PasswordPolicyResponseControlStates.START_STATE][PasswordPolicyResponseControlTags.TIME_BEFORE_EXPIRATION_TAG
            .getValue()] = new GrammarTransition( PasswordPolicyResponseControlStates.START_STATE,
            PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE,
            PasswordPolicyResponseControlTags.TIME_BEFORE_EXPIRATION_TAG.getValue(), new GrammarAction(
                "set time before expiration value" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;
                    Value value = ppolicyRespContainer.getCurrentTLV().getValue();
                    try
                    {
                        int timeBeforeExp = IntegerDecoder.parse( value, 0, Integer.MAX_VALUE );
                        
                        if( IS_DEBUG )
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

                    ppolicyRespContainer.grammarEndAllowed( true );
                }
            } );

        super.transitions[PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE][UniversalTag.ENUMERATED_TAG] = new GrammarTransition(
            PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE, PasswordPolicyResponseControlStates.END_STATE,
            UniversalTag.ENUMERATED_TAG, new GrammarAction( "set ppolicy error value" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;

                    setPasswordPolicyError( ppolicyRespContainer );
                    
                    ppolicyRespContainer.grammarEndAllowed( true );
                }
            } );

        super.transitions[PasswordPolicyResponseControlStates.START_STATE][PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG
            .getValue()] = new GrammarTransition( PasswordPolicyResponseControlStates.START_STATE,
            PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE,
            PasswordPolicyResponseControlTags.GRACE_AUTHNS_REMAINING_TAG.getValue(), new GrammarAction(
                "set number of grace authentications remaining" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;
                    Value value = ppolicyRespContainer.getCurrentTLV().getValue();
                    try
                    {
                        int graceAuthNum = IntegerDecoder.parse( value, 0, Integer.MAX_VALUE );
                        
                        if( IS_DEBUG )
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

                    ppolicyRespContainer.grammarEndAllowed( true );
                }
            } );

        super.transitions[PasswordPolicyResponseControlStates.START_STATE][UniversalTag.ENUMERATED_TAG] = new GrammarTransition(
            PasswordPolicyResponseControlStates.START_STATE, PasswordPolicyResponseControlStates.PPOLICY_ERROR_STATE,
            UniversalTag.ENUMERATED_TAG, new GrammarAction( "set ppolicy error value" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    PasswordPolicyResponseControlContainer ppolicyRespContainer = ( PasswordPolicyResponseControlContainer ) container;
                    
                    setPasswordPolicyError( ppolicyRespContainer );

                    ppolicyRespContainer.grammarEndAllowed( true );
                }
            } );

    }


    /**
     * read and set the Value of password policy error
     *
     * @param ppolicyRespContainer the container holding PasswordPolicyResponceControl
     * @throws DecoderException
     */
    private void setPasswordPolicyError( PasswordPolicyResponseControlContainer ppolicyRespContainer ) throws DecoderException
    {
        Value value = ppolicyRespContainer.getCurrentTLV().getValue();
        try
        {
            int errorNum = IntegerDecoder.parse( value,
                PasswordPolicyErrorEnum.PASSWORD_EXPIRED.getValue(),
                PasswordPolicyErrorEnum.PASSWORD_IN_HISTORY.getValue() );
            
            if( IS_DEBUG )
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

    public static IGrammar getInstance()
    {
        return INSTANCE;
    }
}
