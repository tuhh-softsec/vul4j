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
package org.apache.directory.shared.ldap.codec.controls.replication.syncmodifydn;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.ldap.message.control.replication.SyncModifyDnType;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the SyncModifyDnControl. All the actions are declared in
 * this class. As it is a singleton, these declaration are only done once.
 * 
 * The decoded grammar is the following :
 * 
 *      syncmodifyDnControl ::= SEQUENCE {
 *           entry-name LDAPDN,
 *           Operation
 *      }
 * 
 *      Operation ::= CHOICE {
 *           move-name       [0] LDAPDN,
 *           rename          [1] Rename,
 *           move-and-rename [2] MoveAndRename
 *      }
 *     
 *      Rename SEQUENCE {
 *          new-rdn Rdn,
 *          delete-old-rdn BOOLEAN
 *      }
 * 
 *      MoveAndRename SEQUENCE {
 *          superior-name   LDAPDN
 *          new-rdn Rdn,
 *          delete-old-rdn BOOLEAN
 *      }
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class SyncModifyDnControlGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( SyncModifyDnControlGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. SyncStateValueControlGrammar is a singleton */
    private static Grammar instance = new SyncModifyDnControlGrammar();


    /**
     * Creates a new SyncModifyDnControlGrammar object.
     */
    private SyncModifyDnControlGrammar()
    {
        setName( SyncModifyDnControlGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[SyncModifyDnControlStatesEnum.LAST_SYNC_MODDN_VALUE_STATE.ordinal()][256];

        /** 
         * Transition from initial state to SyncModifyDnControl sequence
         * SyncModifyDnControl ::= SEQUENCE OF {
         *     ...
         *     
         * Initialize the SyncModifyDnControl object
         */
        super.transitions[SyncModifyDnControlStatesEnum.START_SYNC_MODDN.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.START_SYNC_MODDN, SyncModifyDnControlStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE,
            UniversalTag.SEQUENCE.getValue(), null );

        /** 
         * Transition from SyncModifyDnControl sequence to entryDn
         * move-name ::= SEQUENCE OF {
         *     Dn        entryDN
         *     ...
         *     
         * Stores the entryDn value
         */
        super.transitions[SyncModifyDnControlStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE,
            SyncModifyDnControlStatesEnum.ENTRY_DN_STATE, UniversalTag.OCTET_STRING.getValue(), new GrammarAction(
                "Set SyncModifyDnControl entryDn value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    // Check that the value is into the allowed interval
                    String entryDn = Strings.utf8ToString(value.getData());
                    
                    if ( IS_DEBUG )
                    {
                        LOG.debug( "ModDN entryDn = {}", entryDn );
                    }
                    
                    syncModifyDnControlContainer.getSyncModifyDnControl().setEntryDn( entryDn );
                    
                    // move on to the next transistion
                    syncModifyDnControlContainer.setGrammarEndAllowed( false );
                }
            } );

        /** 
         * Transition to move choice
         * Operation ::= CHOICE {
         *     move-name       [0] LDAPDN
         *  }
         *     
         * Stores the newSuperiorDn value
         */

        super.transitions[SyncModifyDnControlStatesEnum.ENTRY_DN_STATE.ordinal()][SyncModifyDnControlTags.MOVE_TAG.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.ENTRY_DN_STATE, SyncModifyDnControlStatesEnum.MOVE_STATE,
            SyncModifyDnControlTags.MOVE_TAG.getValue(), new GrammarAction( "Set SyncModifyDnControl newSuperiorDn" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    syncModifyDnControlContainer.getSyncModifyDnControl().setModDnType( SyncModifyDnType.MOVE );

                    // We need to read the move operation's superiorDN
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    // Check that the value is into the allowed interval
                    String newSuperiorDn = Strings.utf8ToString(value.getData());
                    
                    if ( IS_DEBUG )
                    {
                        LOG.debug( "ModDN newSuperiorDn = {}", newSuperiorDn );
                    }
                    
                    syncModifyDnControlContainer.getSyncModifyDnControl().setNewSuperiorDn( newSuperiorDn );
                    
                    // move on to the next transistion
                    syncModifyDnControlContainer.setGrammarEndAllowed( true );
                }
            } );

        
        /**
         * read the newSuperiorDn
         * move-name       [0] LDAPDN
         */
        super.transitions[SyncModifyDnControlStatesEnum.ENTRY_DN_STATE.ordinal()][SyncModifyDnControlTags.RENAME_TAG.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.ENTRY_DN_STATE, SyncModifyDnControlStatesEnum.RENAME_STATE,
            SyncModifyDnControlTags.RENAME_TAG.getValue(), new GrammarAction( "enter SyncModifyDnControl rename choice" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    syncModifyDnControlContainer.getSyncModifyDnControl().setModDnType( SyncModifyDnType.RENAME );

                    syncModifyDnControlContainer.setGrammarEndAllowed( false );
                }
            } );

        /** 
         * Transition from rename's RENAME state to newRdn
         * 
         * Rename SEQUENCE {
         *     new-rdn Rdn,
         * }
         *            
         * Stores the newRdn value
         */
        super.transitions[SyncModifyDnControlStatesEnum.RENAME_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.RENAME_STATE, SyncModifyDnControlStatesEnum.RENAME_NEW_RDN_STATE,
            UniversalTag.OCTET_STRING.getValue(), new GrammarAction( "Set SyncModifyDnControl newRdn value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    String newRdn = Strings.utf8ToString(value.getData());

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newRdn = {}", newRdn );
                    }

                    syncModifyDnControlContainer.getSyncModifyDnControl().setNewRdn( newRdn );

                    // terminal state
                    syncModifyDnControlContainer.setGrammarEndAllowed( false );
                }
            } );
       
        
        /** 
         * Transition from rename's RENAME newRdn to deleteOldRdn
         * 
         * Rename SEQUENCE {
         *   ....
         *   deleteOldRdn 
         * }
         *            
         * Stores the deleteOldRdn value
         */
        super.transitions[SyncModifyDnControlStatesEnum.RENAME_NEW_RDN_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.RENAME_NEW_RDN_STATE, SyncModifyDnControlStatesEnum.RENAME_DEL_OLD_RDN_STATE,
            UniversalTag.BOOLEAN.getValue(), new GrammarAction( "Set SyncModifyDnControl deleteOldRdn value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    byte deleteOldRdn = value.getData()[0];

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "deleteOldRdn = {}", deleteOldRdn );
                    }

                    if( deleteOldRdn != 0 )
                    {
                        syncModifyDnControlContainer.getSyncModifyDnControl().setDeleteOldRdn( true );
                    }

                    // terminal state
                    syncModifyDnControlContainer.setGrammarEndAllowed( true );
                }
            } );
        

        /** 
         * Transition from entryDN to moveAndRename SEQUENCE
         *  MoveAndRename SEQUENCE {
         *     
         * Stores the deleteOldRdn flag
         */
        super.transitions[SyncModifyDnControlStatesEnum.ENTRY_DN_STATE.ordinal()][SyncModifyDnControlTags.MOVEANDRENAME_TAG.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.ENTRY_DN_STATE, SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_STATE,
            SyncModifyDnControlTags.MOVEANDRENAME_TAG.getValue(), new GrammarAction( "enter SyncModifyDnControl moveAndRename choice" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    syncModifyDnControlContainer.getSyncModifyDnControl().setModDnType( SyncModifyDnType.MOVEANDRENAME );

                    syncModifyDnControlContainer.setGrammarEndAllowed( false );
                }
            } );

        /** 
         * Transition from MOVE_AND_RENAME_STATE to newSuperiorDn
         * 
         * MoveAndRename SEQUENCE {
         *      superior-name   LDAPDN
         *      ....
         *            
         * Stores the newRdn value
         */
        super.transitions[SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_STATE, SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE,
            UniversalTag.OCTET_STRING.getValue(), new GrammarAction( "Set SyncModifyDnControl moveAndRename state's newSuperirorDN value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    String newSuperirorDn = Strings.utf8ToString(value.getData());

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newSuperirorDn = {}", newSuperirorDn );
                    }

                    syncModifyDnControlContainer.getSyncModifyDnControl().setNewSuperiorDn( newSuperirorDn );

                    // terminal state
                    syncModifyDnControlContainer.setGrammarEndAllowed( false );
                }
            } );

        /** 
         * Transition from moveAndRename's newSuperiorDn to newRdn
         * 
         * MoveAndRename SEQUENCE {
         *      superior-name   LDAPDN
         *      ....
         *            
         * Stores the newRdn value
         */
        super.transitions[SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE, SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_NEW_RDN_STATE,
            UniversalTag.OCTET_STRING.getValue(), new GrammarAction( "Set SyncModifyDnControl moveAndRename state's newRdn value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    String newRdn = Strings.utf8ToString(value.getData());

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newRdn = {}", newRdn );
                    }

                    syncModifyDnControlContainer.getSyncModifyDnControl().setNewRdn( newRdn );

                    // terminal state
                    syncModifyDnControlContainer.setGrammarEndAllowed( false );
                }
            } );
        

        /** 
         * Transition from moveAndRename's newRdn to deleteOldRdn
         *  MoveAndRename SEQUENCE {
         *      ....
         *      delete-old-rdn BOOLEAN
         *     
         * Stores the deleteOldRdn flag
         */
        super.transitions[SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_NEW_RDN_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_NEW_RDN_STATE, SyncModifyDnControlStatesEnum.MOVE_AND_RENAME_DEL_OLD_RDN_STATE,
            UniversalTag.BOOLEAN.getValue(), new GrammarAction( "Set SyncModifyDnControl deleteOldRdn value" )
            {
                public void action( Asn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    byte deleteOldRdn = value.getData()[0];

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "deleteOldRdn = {}", deleteOldRdn );
                    }

                    if( deleteOldRdn != 0 )
                    {
                        syncModifyDnControlContainer.getSyncModifyDnControl().setDeleteOldRdn( true );
                    }

                    // terminal state
                    syncModifyDnControlContainer.setGrammarEndAllowed( true );
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
