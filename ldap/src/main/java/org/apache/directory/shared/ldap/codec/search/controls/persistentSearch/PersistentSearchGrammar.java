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
package org.apache.directory.shared.ldap.codec.search.controls.persistentSearch;


import org.apache.directory.shared.asn1.DecoderException; 
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.*;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.model.message.controls.PersistentSearch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the PSearchControl. All the actions are declared in
 * this class. As it is a singleton, these declaration are only done once.
 * 
 * The decoded grammar is the following :
 * 
 * PersistenceSearch ::= SEQUENCE {
 *     changeTypes  INTEGER,  -- an OR combinaison of 0, 1, 2 and 4 --
 *     changeOnly   BOOLEAN,
 *     returnECs    BOOLEAN
 * }
 * 
 * The changeTypes field is the logical OR of one or more of these values:
 * add    (1), 
 * delete (2), 
 * modify (4), 
 * modDN  (8).
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class PersistentSearchGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( PersistentSearchGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. PSearchControlGrammar is a singleton */
    private static Grammar instance = new PersistentSearchGrammar();


    /**
     * Creates a new PSearchControlGrammar object.
     */
    private PersistentSearchGrammar()
    {
        setName( PersistentSearchGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[ PersistentSearchStates.LAST_PSEARCH_STATE.ordinal()][256];

        /** 
         * Transition from initial state to Psearch sequence
         * PSearch ::= SEQUENCE OF {
         *     ...
         *     
         * Initialize the persistence search object
         */
        super.transitions[ PersistentSearchStates.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] =
            new GrammarTransition( PersistentSearchStates.START_STATE,
                                    PersistentSearchStates.PSEARCH_SEQUENCE_STATE,
                                    UniversalTag.SEQUENCE.getValue(), null );


        /** 
         * Transition from Psearch sequence to Change types
         * PSearch ::= SEQUENCE OF {
         *     changeTypes  INTEGER,  -- an OR combinaison of 0, 1, 2 and 4 --
         *     ...
         *     
         * Stores the change types value
         */
        super.transitions[ PersistentSearchStates.PSEARCH_SEQUENCE_STATE.ordinal()][UniversalTag.INTEGER.getValue()] =
            new GrammarTransition( PersistentSearchStates.PSEARCH_SEQUENCE_STATE,
                PersistentSearchStates.CHANGE_TYPES_STATE,
                UniversalTag.INTEGER.getValue(),
                new GrammarAction<PersistentSearchContainer>( "Set PSearchControl changeTypes" )
            {
                public void action( PersistentSearchContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        // Check that the value is into the allowed interval
                        int changeTypes = IntegerDecoder.parse( value, 
                            PersistentSearch.CHANGE_TYPES_MIN,
                            PersistentSearch.CHANGE_TYPES_MAX );
                        
                        if ( IS_DEBUG )
                        {
                            LOG.debug( "changeTypes = " + changeTypes );
                        }

                        container.getPersistentSearchDecorator().setChangeTypes( changeTypes );
                    }
                    catch ( IntegerDecoderException e )
                    {
                        String msg = I18n.err( I18n.ERR_04051 );
                        LOG.error( msg, e );
                        throw new DecoderException( msg );
                    }
                }
            } );

        /** 
         * Transition from Change types to Changes only
         * PSearch ::= SEQUENCE OF {
         *     ...
         *     changeOnly   BOOLEAN,
         *     ...
         *     
         * Stores the change only flag
         */
        super.transitions[ PersistentSearchStates.CHANGE_TYPES_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] =
            new GrammarTransition( PersistentSearchStates.CHANGE_TYPES_STATE,
                                    PersistentSearchStates.CHANGES_ONLY_STATE, UniversalTag.BOOLEAN.getValue(),
                new GrammarAction<PersistentSearchContainer>( "Set PSearchControl changesOnly" )
            {
                public void action( PersistentSearchContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean changesOnly = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "changesOnly = " + changesOnly );
                        }

                        container.getPersistentSearchDecorator().setChangesOnly( changesOnly );
                    }
                    catch ( BooleanDecoderException e )
                    {
                        String msg = I18n.err( I18n.ERR_04052 );
                        LOG.error( msg, e );
                        throw new DecoderException( msg );
                    }
                }
            } );

        /** 
         * Transition from Change types to Changes only
         * PSearch ::= SEQUENCE OF {
         *     ...
         *     returnECs    BOOLEAN 
         * }
         *     
         * Stores the return ECs flag 
         */
        super.transitions[ PersistentSearchStates.CHANGES_ONLY_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] =
            new GrammarTransition( PersistentSearchStates.CHANGES_ONLY_STATE,
                                    PersistentSearchStates.RETURN_ECS_STATE, UniversalTag.BOOLEAN.getValue(),
                new GrammarAction<PersistentSearchContainer>( "Set PSearchControl returnECs" )
            {
                public void action( PersistentSearchContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean returnECs = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "returnECs = " + returnECs );
                        }

                        container.getPersistentSearchDecorator().setReturnECs( returnECs );

                        // We can have an END transition
                        container.setGrammarEndAllowed( true );
                    }
                    catch ( BooleanDecoderException e )
                    {
                        String msg = I18n.err( I18n.ERR_04053 );
                        LOG.error( msg, e );
                        throw new DecoderException( msg );
                    }
                }
            } );
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
