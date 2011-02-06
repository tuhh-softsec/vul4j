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
package org.apache.directory.shared.ldap.codec.extended.operations.gracefulDisconnect;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.extended.operations.GracefulActionConstants;
import org.apache.directory.shared.ldap.model.exception.LdapURLEncodingException;
import org.apache.directory.shared.ldap.model.filter.LdapURL;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the Graceful Disconnect. All the actions are declared
 * in this class. As it is a singleton, these declaration are only done once.
 * The grammar is :
 * 
 * <pre>
 *  GracefulDisconnect ::= SEQUENCE {
 *      timeOffline INTEGER (0..720) DEFAULT 0,
 *      delay [0] INTEGER (0..86400) DEFAULT 0,
 *      replicatedContexts Referral OPTIONAL
 * }
 *  
 *  Referral ::= SEQUENCE OF LDAPURL
 *  
 *  LDAPURL ::= LDAPString -- limited to characters permitted in URLs
 *  
 *  LDAPString ::= OCTET STRING
 * </pre>
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class GracefulDisconnectGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( GracefulDisconnectGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. GracefulDisconnectnGrammar is a singleton */
    private static Grammar instance = new GracefulDisconnectGrammar();


    /**
     * The action used to store a Time Offline.
     */
    private GrammarAction storeDelay = new GrammarAction( "Set Graceful Disconnect Delay" )
    {
        public void action( Asn1Container container ) throws DecoderException
        {
            GracefulDisconnectContainer gracefulDisconnectContainer = ( GracefulDisconnectContainer ) container;
            Value value = gracefulDisconnectContainer.getCurrentTLV().getValue();
    
            try
            {
                int delay = IntegerDecoder.parse( value, 0, 86400 );
    
                if ( IS_DEBUG )
                {
                    LOG.debug( "Delay = " + delay );
                }
    
                gracefulDisconnectContainer.getGracefulDisconnect().setDelay( delay );
                gracefulDisconnectContainer.setGrammarEndAllowed( true );
            }
            catch ( IntegerDecoderException e )
            {
                String msg = I18n.err( I18n.ERR_04036, Strings.dumpBytes(value.getData()) );
                LOG.error( msg );
                throw new DecoderException( msg );
            }
        }
    };
    
    /**
     * The action used to store a referral.
     */
    private GrammarAction storeReferral = new GrammarAction( "Stores a referral" )
    {
        public void action( Asn1Container container ) throws DecoderException
        {
            GracefulDisconnectContainer gracefulDisconnectContainer = ( GracefulDisconnectContainer ) container;
            Value value = gracefulDisconnectContainer.getCurrentTLV().getValue();

            try
            {
                LdapURL url = new LdapURL( value.getData() );
                gracefulDisconnectContainer.getGracefulDisconnect().addReplicatedContexts( url );
                gracefulDisconnectContainer.setGrammarEndAllowed( true );
                
                if ( IS_DEBUG )
                {
                    LOG.debug( "Stores a referral : {}", url );
                }
            }
            catch ( LdapURLEncodingException e )
            {
                String msg = "failed to decode the URL '" + Strings.dumpBytes(value.getData()) + "'";
                LOG.error( msg );
                throw new DecoderException( msg );
            }
        }
    };
    
    /**
     * The action used to store a Time Offline.
     */
    private GrammarAction storeTimeOffline = new GrammarAction( "Set Graceful Disconnect time offline" )
    {
        public void action( Asn1Container container ) throws DecoderException
        {
            GracefulDisconnectContainer gracefulDisconnectContainer = ( GracefulDisconnectContainer ) container;
            Value value = gracefulDisconnectContainer.getCurrentTLV().getValue();

            try
            {
                int timeOffline = IntegerDecoder.parse( value, 0, 720 );

                if ( IS_DEBUG )
                {
                    LOG.debug( "Time Offline = " + timeOffline );
                }

                gracefulDisconnectContainer.getGracefulDisconnect().setTimeOffline( timeOffline );
                gracefulDisconnectContainer.setGrammarEndAllowed( true );
            }
            catch ( IntegerDecoderException e )
            {
                String msg = I18n.err( I18n.ERR_04037, Strings.dumpBytes(value.getData()) );
                LOG.error( msg );
                throw new DecoderException( msg );
            }
        }
    };

    /**
     * Creates a new GracefulDisconnectGrammar object.
     */
    private GracefulDisconnectGrammar()
    {
        setName( GracefulDisconnectGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[GracefulDisconnectStatesEnum.LAST_GRACEFUL_DISCONNECT_STATE.ordinal()][256];

        /**
         * Transition from init state to graceful disconnect
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         * 
         * Creates the GracefulDisconnect object
         */
        super.transitions[GracefulDisconnectStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.START_STATE,
                                    GracefulDisconnectStatesEnum.GRACEFUL_DISCONNECT_SEQUENCE_STATE, 
                                    UniversalTag.SEQUENCE.getValue(),
                new GrammarAction(
                "Init Graceful Disconnect" )
            {
                public void action( Asn1Container container )
                {
                    GracefulDisconnectContainer gracefulDisconnectContainer = ( GracefulDisconnectContainer ) container;
                    GracefulDisconnect gracefulDisconnect = new GracefulDisconnect();
                    gracefulDisconnectContainer.setGracefulDisconnect( gracefulDisconnect );
                    gracefulDisconnectContainer.setGrammarEndAllowed( true );
                }
            } );

        /**
         * Transition from graceful disconnect to time offline
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     timeOffline INTEGER (0..720) DEFAULT 0, 
         *     ... 
         *     
         * Set the time offline value into the GracefulDisconnect object.    
         */
        super.transitions[GracefulDisconnectStatesEnum.GRACEFUL_DISCONNECT_SEQUENCE_STATE.ordinal()][UniversalTag.INTEGER.getValue()] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.GRACEFUL_DISCONNECT_SEQUENCE_STATE,
                                    GracefulDisconnectStatesEnum.TIME_OFFLINE_STATE, 
                                    UniversalTag.INTEGER.getValue(), 
                storeTimeOffline );
        
        /**
         * Transition from graceful disconnect to delay
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         *     delay [0] INTEGER (0..86400) DEFAULT 0,
         *     ... 
         *     
         * Set the delay value into the GracefulDisconnect object.    
         */
        super.transitions[GracefulDisconnectStatesEnum.GRACEFUL_DISCONNECT_SEQUENCE_STATE.ordinal()]
                         [GracefulActionConstants.GRACEFUL_ACTION_DELAY_TAG] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.GRACEFUL_DISCONNECT_SEQUENCE_STATE,
                                    GracefulDisconnectStatesEnum.DELAY_STATE, 
                                    GracefulActionConstants.GRACEFUL_ACTION_DELAY_TAG, 
                storeDelay );
        
        /**
         * Transition from graceful disconnect to replicated Contexts
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         *     replicatedContexts Referral OPTIONAL } 
         *     
         * Referral ::= SEQUENCE OF LDAPURL
         *     
         * Get some replicated contexts. Nothing to do    
         */
        super.transitions[GracefulDisconnectStatesEnum.GRACEFUL_DISCONNECT_SEQUENCE_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.GRACEFUL_DISCONNECT_SEQUENCE_STATE,
                                    GracefulDisconnectStatesEnum.REPLICATED_CONTEXTS_STATE,
                                    UniversalTag.SEQUENCE.getValue(), null );
        
        /**
         * Transition from time offline to delay
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         *     delay [0] INTEGER (0..86400) DEFAULT 0,
         *     ... 
         *     
         * Set the delay value into the GracefulDisconnect object.    
         */
        super.transitions[GracefulDisconnectStatesEnum.TIME_OFFLINE_STATE.ordinal()][GracefulActionConstants.GRACEFUL_ACTION_DELAY_TAG] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.TIME_OFFLINE_STATE,
                                    GracefulDisconnectStatesEnum.DELAY_STATE, 
                                    GracefulActionConstants.GRACEFUL_ACTION_DELAY_TAG,
                storeDelay );

        /**
         * Transition from time offline to replicated Contexts
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         *     replicatedContexts Referral OPTIONAL } 
         *     
         * Referral ::= SEQUENCE OF LDAPURL
         *     
         * Get some replicated contexts. Nothing to do    
         */
        super.transitions[GracefulDisconnectStatesEnum.TIME_OFFLINE_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.TIME_OFFLINE_STATE,
                                    GracefulDisconnectStatesEnum.REPLICATED_CONTEXTS_STATE,
                                    UniversalTag.SEQUENCE.getValue(), null );
        
        /**
         * Transition from delay to replicated contexts
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         *     replicatedContexts Referral OPTIONAL } 
         *     
         * Referral ::= SEQUENCE OF LDAPURL
         *     
         * Get some replicated contexts. Nothing to do    
         */
        super.transitions[GracefulDisconnectStatesEnum.DELAY_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.DELAY_STATE,
                                    GracefulDisconnectStatesEnum.REPLICATED_CONTEXTS_STATE, 
                                    UniversalTag.SEQUENCE.getValue(), null );
        
        /**
         * Transition from replicated contexts to referral
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         *     replicatedContexts Referral OPTIONAL } 
         *     
         * Referral ::= SEQUENCE OF LDAPURL
         *     
         * Stores the referral
         */
        super.transitions[GracefulDisconnectStatesEnum.REPLICATED_CONTEXTS_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.REPLICATED_CONTEXTS_STATE,
                                    GracefulDisconnectStatesEnum.REFERRAL_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(),
                storeReferral );

        /**
         * Transition from referral to referral
         * 
         * GracefulDisconnect ::= SEQUENCE { 
         *     ... 
         *     replicatedContexts Referral OPTIONAL } 
         *     
         * Referral ::= SEQUENCE OF LDAPURL
         *     
         * Stores the referral
         */
        super.transitions[GracefulDisconnectStatesEnum.REFERRAL_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( GracefulDisconnectStatesEnum.REFERRAL_STATE,
                                    GracefulDisconnectStatesEnum.REFERRAL_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(),
                storeReferral );

    }


    /**
     * This class is a singleton.
     * 
     * @return An instance on this grammar
     */
    public static Grammar getInstance()
    {
        return instance;
    }
}
