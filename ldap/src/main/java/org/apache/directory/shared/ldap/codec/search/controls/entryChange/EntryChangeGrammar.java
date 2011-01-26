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
package org.apache.directory.shared.ldap.codec.search.controls.entryChange;


import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.*;
import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.ldap.codec.search.controls.ChangeType;
import org.apache.directory.shared.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.model.name.Dn;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the EntryChangeControl. All the actions are declared in
 * this class. As it is a singleton, these declaration are only done once.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class EntryChangeGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( EntryChangeGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. EntryChangeGrammar is a singleton */
    private static Grammar instance = new EntryChangeGrammar();


    /**
     * Creates a new EntryChangeGrammar object.
     */
    private EntryChangeGrammar()
    {
        setName( EntryChangeGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[ EntryChangeStates.LAST_EC_STATE.ordinal()][256];

        // ============================================================================================
        // Transition from start state to Entry Change sequence
        // ============================================================================================
        // EntryChangeNotification ::= SEQUENCE {
        //     ...
        //
        // Initialization of the structure
        super.transitions[ EntryChangeStates.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] =
            new GrammarTransition( EntryChangeStates.START_STATE,
                                    EntryChangeStates.EC_SEQUENCE_STATE,
                                    UniversalTag.SEQUENCE.getValue(), null );

        // ============================================================================================
        // transition from Entry Change sequence to Change Type
        // ============================================================================================
        // EntryChangeNotification ::= SEQUENCE {
        //     changeType ENUMERATED {
        //     ...
        //
        // Evaluates the changeType
        super.transitions[ EntryChangeStates.EC_SEQUENCE_STATE.ordinal()][UniversalTag.ENUMERATED.getValue()] =
            new GrammarTransition( EntryChangeStates.EC_SEQUENCE_STATE,
                                    EntryChangeStates.CHANGE_TYPE_STATE,
                                    UniversalTag.ENUMERATED.getValue(),
            new GrammarAction( "Set EntryChangeControl changeType" )
        {
            public void action( Asn1Container container ) throws DecoderException
            {
                EntryChangeContainer entryChangeContainer = ( EntryChangeContainer ) container;
                Value value = entryChangeContainer.getCurrentTLV().getValue();

                try
                {
                    int change = IntegerDecoder.parse( value, 1, 8 );
                    
                    switch ( ChangeType.getChangeType( change ) )
                    {
                        case ADD:
                        case DELETE:
                        case MODDN:
                        case MODIFY:
                            ChangeType changeType = ChangeType.getChangeType( change );

                            if ( IS_DEBUG )
                            {
                                LOG.debug( "changeType = " + changeType );
                            }

                            ( ( EntryChange ) entryChangeContainer.getEntryChangeDecorator().getDecorated() )
                                    .setChangeType( changeType );
                            break;

                        default:
                            String msg = I18n.err( I18n.ERR_04044 );
                            LOG.error( msg );
                            throw new DecoderException( msg );
                    }

                    // We can have an END transition
                    entryChangeContainer.setGrammarEndAllowed( true );
                }
                catch ( IntegerDecoderException e )
                {
                    String msg = I18n.err( I18n.ERR_04044 );
                    LOG.error( msg, e );
                    throw new DecoderException( msg );
                }
                catch ( IllegalArgumentException e )
                {
                    throw new DecoderException( e.getLocalizedMessage() );
                }
            }
        } );

        // ============================================================================================
        // Transition from Change Type to Previous Dn
        // ============================================================================================
        // EntryChangeNotification ::= SEQUENCE {
        //     ...
        //     previousDN LDAPDN OPTIONAL,
        //     ...
        //
        // Set the previousDN into the structure. We first check that it's a
        // valid Dn
        super.transitions[ EntryChangeStates.CHANGE_TYPE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] =
            new GrammarTransition( EntryChangeStates.CHANGE_TYPE_STATE,
                                    EntryChangeStates.PREVIOUS_DN_STATE,
                                    UniversalTag.OCTET_STRING.getValue(),
            new GrammarAction( "Set EntryChangeControl previousDN" )
        {
            public void action( Asn1Container container ) throws DecoderException
            {
                EntryChangeContainer entryChangeContainer = ( EntryChangeContainer ) container;

                EntryChange entryChange = ( EntryChange ) entryChangeContainer
                        .getEntryChangeDecorator().getDecorated();
                ChangeType changeType = entryChange.getChangeType();


                if ( changeType != ChangeType.MODDN )
                {
                    LOG.error( I18n.err( I18n.ERR_04045 ) );
                    throw new DecoderException( I18n.err( I18n.ERR_04046 ));
                }
                else
                {
                    Value value = entryChangeContainer.getCurrentTLV().getValue();
                    Dn previousDn;

                    try
                    {
                        previousDn = new Dn( Strings.utf8ToString(value.getData()) );
                    }
                    catch ( LdapInvalidDnException ine )
                    {
                        LOG.error( I18n.err( I18n.ERR_04047, Strings.dumpBytes(value.getData()) ) );
                        throw new DecoderException( I18n.err( I18n.ERR_04048 ) );
                    }

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "previousDN = " + previousDn );
                    }

                    entryChange.setPreviousDn( previousDn );

                    // We can have an END transition
                    entryChangeContainer.setGrammarEndAllowed( true );
                }
            }
        } );

        // Change Number action
        GrammarAction setChangeNumberAction = new GrammarAction( "Set EntryChangeControl changeNumber" )
        {
            public void action( Asn1Container container ) throws DecoderException
            {
                EntryChangeContainer entryChangeContainer = ( EntryChangeContainer ) container;
                Value value = entryChangeContainer.getCurrentTLV().getValue();

                try
                {
                    long changeNumber = LongDecoder.parse( value );

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "changeNumber = " + changeNumber );
                    }

                    EntryChange entryChange = ( EntryChange ) entryChangeContainer
                            .getEntryChangeDecorator().getDecorated();
                    entryChange.setChangeNumber( changeNumber );

                    // We can have an END transition
                    entryChangeContainer.setGrammarEndAllowed( true );
                }
                catch ( LongDecoderException e )
                {
                    String msg = I18n.err( I18n.ERR_04049 );
                    LOG.error( msg, e );
                    throw new DecoderException( msg );
                }
            }
        };

        // ============================================================================================
        // Transition from Previous Dn to Change Number
        // ============================================================================================
        // EntryChangeNotification ::= SEQUENCE {
        //     ...
        //     changeNumber INTEGER OPTIONAL
        // }
        //
        // Set the changeNumber into the structure
        super.transitions[ EntryChangeStates.PREVIOUS_DN_STATE.ordinal()][UniversalTag.INTEGER.getValue()] =
            new GrammarTransition( EntryChangeStates.PREVIOUS_DN_STATE,
                                    EntryChangeStates.CHANGE_NUMBER_STATE,
                                    UniversalTag.INTEGER.getValue(),
                setChangeNumberAction );

        // ============================================================================================
        // Transition from Previous Dn to Change Number
        // ============================================================================================
        // EntryChangeNotification ::= SEQUENCE {
        //     ...
        //     changeNumber INTEGER OPTIONAL
        // }
        //
        // Set the changeNumber into the structure
        super.transitions[ EntryChangeStates.CHANGE_TYPE_STATE.ordinal()][UniversalTag.INTEGER.getValue()] =
            new GrammarTransition( EntryChangeStates.CHANGE_TYPE_STATE,
                                    EntryChangeStates.CHANGE_NUMBER_STATE,
                                    UniversalTag.INTEGER.getValue(),
                setChangeNumberAction );
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
