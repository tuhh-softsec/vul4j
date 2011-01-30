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
package org.apache.directory.shared.ldap.codec.controls.replication.syncInfoValue;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoder;
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the SyncInfoValueControl. All the actions are declared in
 * this class. As it is a singleton, these declaration are only done once.
 * 
 * The decoded grammar is the following :
 * 
 * syncInfoValue ::= CHOICE {
 *     newcookie      [0] syncCookie,
 *     refreshDelete  [1] SEQUENCE {
 *         cookie         syncCookie OPTIONAL,
 *         refreshDone    BOOLEAN DEFAULT TRUE
 *     },
 *     refreshPresent [2] SEQUENCE {
 *         cookie         syncCookie OPTIONAL,
 *         refreshDone    BOOLEAN DEFAULT TRUE
 *     },
 *     syncIdSet      [3] SEQUENCE {
 *         cookie         syncCookie OPTIONAL,
 *         refreshDeletes BOOLEAN DEFAULT FALSE,
 *         syncUUIDs      SET OF syncUUID
 *     }
 * }
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class SyncInfoValueGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( SyncInfoValueGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. SyncInfoValueControlGrammar is a singleton */
    private static Grammar instance = new SyncInfoValueGrammar();


    /**
     * Creates a new SyncInfoValueControlGrammar object.
     */
    private SyncInfoValueGrammar()
    {
        setName( SyncInfoValueGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[SyncInfoValueStatesEnum.LAST_SYNC_INFO_VALUE_STATE.ordinal()][256];

        /** 
         * Transition from initial state to SyncInfoValue newCookie choice
         * SyncInfoValue ::= CHOICE {
         *     newCookie [0] syncCookie,
         *     ...
         *     
         * Initialize the syncInfoValue object
         */
        super.transitions[SyncInfoValueStatesEnum.START_STATE.ordinal()][SyncInfoValueTags.NEW_COOKIE_TAG.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.START_STATE, 
                                    SyncInfoValueStatesEnum.NEW_COOKIE_STATE, 
                                    SyncInfoValueTags.NEW_COOKIE_TAG.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "NewCookie choice for SyncInfoValueControl" )
            {
                public void action( SyncInfoValueContainer container )
                {
                    SyncInfoValueDecorator control = 
                        new SyncInfoValueDecorator( container.getCodecService(), SynchronizationInfoEnum.NEW_COOKIE);
                    
                    Value value = container.getCurrentTLV().getValue();

                    byte[] newCookie = value.getData();

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newcookie = " + Strings.dumpBytes(newCookie) );
                    }

                    control.setCookie( newCookie );

                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                    
                    container.setSyncInfoValueControl( control );
                }
            } );


        /** 
         * Transition from initial state to SyncInfoValue refreshDelete choice
         * SyncInfoValue ::= CHOICE {
         *     ...
         *     refreshDelete [1] SEQUENCE {
         *     ...
         *     
         * Initialize the syncInfoValue object
         */
        super.transitions[SyncInfoValueStatesEnum.START_STATE.ordinal()][SyncInfoValueTags.REFRESH_DELETE_TAG.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.START_STATE, 
                                    SyncInfoValueStatesEnum.REFRESH_DELETE_STATE, 
                                    SyncInfoValueTags.REFRESH_DELETE_TAG.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshDelete choice for SyncInfoValueControl" )
            {
                public void action( SyncInfoValueContainer container )
                {
                    SyncInfoValueDecorator control = 
                        new SyncInfoValueDecorator( container.getCodecService(), SynchronizationInfoEnum.REFRESH_DELETE);
                    
                    container.setSyncInfoValueControl( control );

                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );


        /** 
         * Transition from refreshDelete state to cookie
         *     refreshDelete [1] SEQUENCE {
         *         cookie syncCookie OPTIONAL,
         *     ...
         *     
         * Load the cookie object
         */
        super.transitions[SyncInfoValueStatesEnum.REFRESH_DELETE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.REFRESH_DELETE_STATE, 
                                    SyncInfoValueStatesEnum.REFRESH_DELETE_COOKIE_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshDelete cookie" )
            {
                public void action( SyncInfoValueContainer container )
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    byte[] cookie = value.getData();

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "cookie = " + Strings.dumpBytes(cookie) );
                    }

                    container.getSyncInfoValueControl().setCookie( cookie );
                    container.setSyncInfoValueControl( control );

                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );


        /** 
         * Transition from refreshDelete cookie state to refreshDone
         *     refreshDelete [1] SEQUENCE {
         *         ....
         *         refreshDone BOOLEAN DEFAULT TRUE
         *     }
         *     
         * Load the refreshDone flag
         */
        super.transitions[SyncInfoValueStatesEnum.REFRESH_DELETE_COOKIE_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.REFRESH_DELETE_COOKIE_STATE, 
                                    SyncInfoValueStatesEnum.LAST_SYNC_INFO_VALUE_STATE, 
                                    UniversalTag.BOOLEAN.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshDelete refreshDone flag" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean refreshDone = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "refreshDone = {}", refreshDone );
                        }

                        control.setRefreshDone( refreshDone );

                        container.setSyncInfoValueControl( control );

                        // the END transition for grammar
                        container.setGrammarEndAllowed( true );
                    }
                    catch ( BooleanDecoderException be )
                    {
                        String msg = I18n.err( I18n.ERR_04025 );
                        LOG.error( msg, be );
                        throw new DecoderException( msg );
                    }


                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );


        /** 
         * Transition from refreshDelete choice state to refreshDone
         *     refreshDelete [1] SEQUENCE {
         *         ....
         *         refreshDone BOOLEAN DEFAULT TRUE
         *     }
         *     
         * Load the refreshDone flag
         */
        super.transitions[SyncInfoValueStatesEnum.REFRESH_DELETE_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.REFRESH_DELETE_STATE, 
                                    SyncInfoValueStatesEnum.LAST_SYNC_INFO_VALUE_STATE, 
                                    UniversalTag.BOOLEAN.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshDelete refreshDone flag" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean refreshDone = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "refreshDone = {}", refreshDone );
                        }

                        control.setRefreshDone( refreshDone );

                        container.setSyncInfoValueControl( control );

                        // the END transition for grammar
                        container.setGrammarEndAllowed( true );
                    }
                    catch ( BooleanDecoderException be )
                    {
                        String msg = I18n.err( I18n.ERR_04025 );
                        LOG.error( msg, be );
                        throw new DecoderException( msg );
                    }


                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );
        
        
        /** 
         * Transition from initial state to SyncInfoValue refreshPresent choice
         * SyncInfoValue ::= CHOICE {
         *     ...
         *     refreshPresent [2] SEQUENCE {
         *     ...
         *     
         * Initialize the syncInfoValue object
         */
        super.transitions[SyncInfoValueStatesEnum.START_STATE.ordinal()][SyncInfoValueTags.REFRESH_PRESENT_TAG.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.START_STATE, 
                                    SyncInfoValueStatesEnum.REFRESH_PRESENT_STATE, 
                                    SyncInfoValueTags.REFRESH_PRESENT_TAG.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshDelete choice for SyncInfoValueControl" )
            {
                public void action( SyncInfoValueContainer container )
                {
                    SyncInfoValueDecorator control = 
                        new SyncInfoValueDecorator( container.getCodecService(), SynchronizationInfoEnum.REFRESH_PRESENT);
                    
                    container.setSyncInfoValueControl( control );

                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );

    
        /** 
         * Transition from refreshPresent state to cookie
         *     refreshPresent [2] SEQUENCE {
         *         cookie syncCookie OPTIONAL,
         *     ...
         *     
         * Load the cookie object
         */
        super.transitions[SyncInfoValueStatesEnum.REFRESH_PRESENT_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.REFRESH_PRESENT_STATE, 
                                    SyncInfoValueStatesEnum.REFRESH_PRESENT_COOKIE_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshPresent cookie" )
            {
                public void action( SyncInfoValueContainer container )
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    byte[] cookie = value.getData();

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "cookie = " + Strings.dumpBytes(cookie) );
                    }

                    container.getSyncInfoValueControl().setCookie( cookie );
                    container.setSyncInfoValueControl( control );

                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );
        
        


        /** 
         * Transition from refreshPresent cookie state to refreshDone
         *     refreshPresent [2] SEQUENCE {
         *         ....
         *         refreshDone BOOLEAN DEFAULT TRUE
         *     }
         *     
         * Load the refreshDone flag
         */
        super.transitions[SyncInfoValueStatesEnum.REFRESH_PRESENT_COOKIE_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.REFRESH_PRESENT_COOKIE_STATE, 
                                    SyncInfoValueStatesEnum.LAST_SYNC_INFO_VALUE_STATE, 
                                    UniversalTag.BOOLEAN.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshPresent refreshDone flag" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean refreshDone = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "refreshDone = {}", refreshDone );
                        }

                        control.setRefreshDone( refreshDone );

                        container.setSyncInfoValueControl( control );

                        // the END transition for grammar
                        container.setGrammarEndAllowed( true );
                    }
                    catch ( BooleanDecoderException be )
                    {
                        String msg = I18n.err( I18n.ERR_04025 );
                        LOG.error( msg, be );
                        throw new DecoderException( msg );
                    }


                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );


        /** 
         * Transition from refreshPresent choice state to refreshDone
         *     refreshPresent [1] SEQUENCE {
         *         ....
         *         refreshDone BOOLEAN DEFAULT TRUE
         *     }
         *     
         * Load the refreshDone flag
         */
        super.transitions[SyncInfoValueStatesEnum.REFRESH_PRESENT_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.REFRESH_PRESENT_STATE, 
                                    SyncInfoValueStatesEnum.LAST_SYNC_INFO_VALUE_STATE, 
                                    UniversalTag.BOOLEAN.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "RefreshPresent refreshDone flag" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean refreshDone = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "refreshDone = {}", refreshDone );
                        }

                        control.setRefreshDone( refreshDone );

                        container.setSyncInfoValueControl( control );

                        // the END transition for grammar
                        container.setGrammarEndAllowed( true );
                    }
                    catch ( BooleanDecoderException be )
                    {
                        String msg = I18n.err( I18n.ERR_04025 );
                        LOG.error( msg, be );
                        throw new DecoderException( msg );
                    }

                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );
        
        
        /** 
         * Transition from initial state to SyncInfoValue syncIdSet choice
         * SyncInfoValue ::= CHOICE {
         *     ...
         *     syncIdSet [3] SEQUENCE {
         *     ...
         *     
         * Initialize the syncInfoValue object
         */
        super.transitions[SyncInfoValueStatesEnum.START_STATE.ordinal()][SyncInfoValueTags.SYNC_ID_SET_TAG.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.START_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_STATE, 
                                    SyncInfoValueTags.SYNC_ID_SET_TAG.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet choice for SyncInfoValueControl" )
            {
                public void action( SyncInfoValueContainer container )
                {
                    SyncInfoValueDecorator control = 
                        new SyncInfoValueDecorator( container.getCodecService(), SynchronizationInfoEnum.SYNC_ID_SET);
                    
                    container.setSyncInfoValueControl( control );
                }
            } );
        
        
        /** 
         * Transition from syncIdSet state to cookie
         *     syncIdSet [3] SEQUENCE {
         *         cookie syncCookie OPTIONAL,
         *     ...
         *     
         * Load the cookie object
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_COOKIE_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet cookie" )
            {
                public void action( SyncInfoValueContainer container )
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    byte[] cookie = value.getData();

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "cookie = " + Strings.dumpBytes(cookie) );
                    }

                    container.getSyncInfoValueControl().setCookie( cookie );
                    container.setSyncInfoValueControl( control );
                }
            } );
        
        
        /** 
         * Transition from syncIdSet state to refreshDeletes
         *     syncIdSet [3] SEQUENCE {
         *         ...
         *         refreshDeletes BOOLEAN DEFAULT FALSE,
         *     ...
         *     
         * Load the refreshDeletes flag
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_REFRESH_DELETES_STATE, 
                                    UniversalTag.BOOLEAN.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet refreshDeletes" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean refreshDeletes = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "refreshDeletes = {}", refreshDeletes );
                        }

                        control.setRefreshDeletes( refreshDeletes );

                        container.setSyncInfoValueControl( control );
                    }
                    catch ( BooleanDecoderException be )
                    {
                        String msg = I18n.err( I18n.ERR_04026 );
                        LOG.error( msg, be );
                        throw new DecoderException( msg );
                    }
                }
            } );
        
        
        /** 
         * Transition from syncIdSet cookie state to refreshDeletes
         *     syncIdSet [3] SEQUENCE {
         *         ...
         *         refreshDeletes BOOLEAN DEFAULT FALSE,
         *     ...
         *     
         * Load the refreshDeletes flag
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_COOKIE_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_COOKIE_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_REFRESH_DELETES_STATE, 
                                    UniversalTag.BOOLEAN.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet refreshDeletes" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        boolean refreshDeletes = BooleanDecoder.parse( value );

                        if ( IS_DEBUG )
                        {
                            LOG.debug( "refreshDeletes = {}", refreshDeletes );
                        }

                        control.setRefreshDeletes( refreshDeletes );

                        container.setSyncInfoValueControl( control );
                    }
                    catch ( BooleanDecoderException be )
                    {
                        String msg = I18n.err( I18n.ERR_04024 );
                        LOG.error( msg, be );
                        throw new DecoderException( msg );
                    }
                }
            } );
        
        
        /** 
         * Transition from syncIdSet state to syncUUIDs
         *     syncIdSet [3] SEQUENCE {
         *         ...
         *         syncUUIDs      *SET OF* syncUUID
         *     }
         *     
         * Initialize the UUID set : no action associated, except allowing a grammar end
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_STATE.ordinal()][UniversalTag.SET.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_SET_OF_UUIDS_STATE, 
                                    UniversalTag.SET.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet syncUUIDs" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );
        
        
        /** 
         * Transition from syncIdSet cookie state to syncUUIDs
         *     syncIdSet [3] SEQUENCE {
         *         ...
         *         syncUUIDs      *SET OF* syncUUID
         *     }
         *     
         * Initialize the UUID set : no action associated
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_COOKIE_STATE.ordinal()][UniversalTag.SET.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_COOKIE_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_SET_OF_UUIDS_STATE, 
                                    UniversalTag.SET.getValue(),
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet syncUUIDs" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );
          
        
        /** 
         * Transition from syncIdSet refreshDeletes state to syncUUIDs
         *     syncIdSet [3] SEQUENCE {
         *         ...
         *         syncUUIDs      *SET OF* syncUUID
         *     }
         *     
         * Initialize the UUID set : no action associated
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_REFRESH_DELETES_STATE.ordinal()][UniversalTag.SET.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_REFRESH_DELETES_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_SET_OF_UUIDS_STATE, 
                                    UniversalTag.SET.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet syncUUIDs" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );
        
        
        /** 
         * Transition from syncIdSet syncUUIDs to syncUUID
         *     syncIdSet [3] SEQUENCE {
         *         ...
         *         syncUUIDs      SET OF *syncUUID*
         *     }
         *     
         * Add the first UUID in the UUIDs list
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_SET_OF_UUIDS_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_SET_OF_UUIDS_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_UUID_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet first UUID" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    byte[] uuid = value.getData();
                    
                    // UUID must be exactly 16 bytes long
                    if ( ( uuid == null ) || ( uuid.length != 16 ) )
                    {
                        String msg = I18n.err( I18n.ERR_04027 );
                        LOG.error( msg );
                        throw new DecoderException( msg );
                    }

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "UUID = " + Strings.dumpBytes(uuid) );
                    }

                    // Store the UUID in the UUIDs list
                    control.addSyncUUID( uuid );
                    
                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
                }
            } );
        
        
        /** 
         * Transition from syncIdSet syncUUID to syncUUID
         *     syncIdSet [3] SEQUENCE {
         *         ...
         *         syncUUIDs      SET OF *syncUUID*
         *     }
         *     
         * Add a new UUID in the UUIDs list
         */
        super.transitions[SyncInfoValueStatesEnum.SYNC_ID_SET_UUID_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = 
            new GrammarTransition( SyncInfoValueStatesEnum.SYNC_ID_SET_UUID_STATE, 
                                    SyncInfoValueStatesEnum.SYNC_ID_SET_UUID_STATE, 
                                    UniversalTag.OCTET_STRING.getValue(), 
                new GrammarAction<SyncInfoValueContainer>( "SyncIdSet UUID" )
            {
                public void action( SyncInfoValueContainer container ) throws DecoderException
                {
                    SyncInfoValueDecorator control = container.getSyncInfoValueControl();
                    
                    Value value = container.getCurrentTLV().getValue();

                    byte[] uuid = value.getData();
                    
                    // UUID must be exactly 16 bytes long
                    if ( ( uuid == null ) || ( uuid.length != 16 ) )
                    {
                        String msg = I18n.err( I18n.ERR_04027 );
                        LOG.error( msg );
                        throw new DecoderException( msg );
                    }

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "UUID = " + Strings.dumpBytes(uuid) );
                    }

                    // Store the UUID in the UUIDs list
                    control.getSyncUUIDs().add( uuid );
                    
                    // We can have an END transition
                    container.setGrammarEndAllowed( true );
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
