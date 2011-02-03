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
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.ldap.extras.controls.SyncModifyDnType;
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
public final class SyncModifyDnGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( SyncModifyDnGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. SyncStateValueControlGrammar is a singleton */
    private static Grammar instance = new SyncModifyDnGrammar();


    /**
     * Creates a new SyncModifyDnControlGrammar object.
     */
    private SyncModifyDnGrammar()
    {
        setName( SyncModifyDnGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[SyncModifyDnStatesEnum.LAST_SYNC_MODDN_VALUE_STATE.ordinal()][256];

        /** 
         * Transition from initial state to SyncModifyDnControl sequence
         * SyncModifyDnControl ::= SEQUENCE OF {
         *     ...
         *     
         * Initialize the SyncModifyDnControl object
         */
        super.transitions[SyncModifyDnStatesEnum.START_SYNC_MODDN.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.START_SYNC_MODDN, SyncModifyDnStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE,
            UniversalTag.SEQUENCE.getValue(), null );

        /** 
         * Transition from SyncModifyDnControl sequence to entryDn
         * move-name ::= SEQUENCE OF {
         *     Dn        entryDN
         *     ...
         *     
         * Stores the entryDn value
         */
        super.transitions[SyncModifyDnStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE,
            SyncModifyDnStatesEnum.ENTRY_DN_STATE, UniversalTag.OCTET_STRING.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "Set SyncModifyDnControl entryDn value" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    // Check that the value is into the allowed interval
                    String entryDn = Strings.utf8ToString(value.getData());
                    
                    if ( IS_DEBUG )
                    {
                        LOG.debug( "ModDN entryDn = {}", entryDn );
                    }
                    
                    container.getSyncModifyDnControl().setEntryDn( entryDn );
                    
                    // move on to the next transition
                    container.setGrammarEndAllowed( false );
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

        super.transitions[SyncModifyDnStatesEnum.ENTRY_DN_STATE.ordinal()][SyncModifyDnTags.MOVE_TAG.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.ENTRY_DN_STATE, SyncModifyDnStatesEnum.MOVE_STATE,
            SyncModifyDnTags.MOVE_TAG.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "Set SyncModifyDnControl newSuperiorDn" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    container.getSyncModifyDnControl().setModDnType( SyncModifyDnType.MOVE );

                    // We need to read the move operation's superiorDN
                    Value value = container.getCurrentTLV().getValue();

                    // Check that the value is into the allowed interval
                    String newSuperiorDn = Strings.utf8ToString(value.getData());
                    
                    if ( IS_DEBUG )
                    {
                        LOG.debug( "ModDN newSuperiorDn = {}", newSuperiorDn );
                    }
                    
                    container.getSyncModifyDnControl().setNewSuperiorDn( newSuperiorDn );
                    
                    // move on to the next transition
                    container.setGrammarEndAllowed( true );
                }
            } );

        
        /**
         * read the newSuperiorDn
         * move-name       [0] LDAPDN
         */
        super.transitions[SyncModifyDnStatesEnum.ENTRY_DN_STATE.ordinal()][SyncModifyDnTags.RENAME_TAG.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.ENTRY_DN_STATE, SyncModifyDnStatesEnum.RENAME_STATE,
            SyncModifyDnTags.RENAME_TAG.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "enter SyncModifyDnControl rename choice" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    container.getSyncModifyDnControl().setModDnType( SyncModifyDnType.RENAME );
                    container.setGrammarEndAllowed( false );
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
        super.transitions[SyncModifyDnStatesEnum.RENAME_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.RENAME_STATE, SyncModifyDnStatesEnum.RENAME_NEW_RDN_STATE,
            UniversalTag.OCTET_STRING.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "Set SyncModifyDnControl newRdn value" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    String newRdn = Strings.utf8ToString(value.getData());

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newRdn = {}", newRdn );
                    }

                    container.getSyncModifyDnControl().setNewRdn( newRdn );

                    // terminal state
                    container.setGrammarEndAllowed( false );
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
        super.transitions[SyncModifyDnStatesEnum.RENAME_NEW_RDN_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.RENAME_NEW_RDN_STATE, SyncModifyDnStatesEnum.RENAME_DEL_OLD_RDN_STATE,
            UniversalTag.BOOLEAN.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "Set SyncModifyDnControl deleteOldRdn value" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    byte deleteOldRdn = value.getData()[0];

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "deleteOldRdn = {}", deleteOldRdn );
                    }

                    if( deleteOldRdn != 0 )
                    {
                        container.getSyncModifyDnControl().setDeleteOldRdn( true );
                    }

                    // terminal state
                    container.setGrammarEndAllowed( true );
                }
            } );
        

        /** 
         * Transition from entryDN to moveAndRename SEQUENCE
         *  MoveAndRename SEQUENCE {
         *     
         * Stores the deleteOldRdn flag
         */
        super.transitions[SyncModifyDnStatesEnum.ENTRY_DN_STATE.ordinal()][SyncModifyDnTags.MOVEANDRENAME_TAG.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.ENTRY_DN_STATE, SyncModifyDnStatesEnum.MOVE_AND_RENAME_STATE,
            SyncModifyDnTags.MOVEANDRENAME_TAG.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "enter SyncModifyDnControl moveAndRename choice" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    container.getSyncModifyDnControl().setModDnType( SyncModifyDnType.MOVEANDRENAME );
                    container.setGrammarEndAllowed( false );
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
        super.transitions[SyncModifyDnStatesEnum.MOVE_AND_RENAME_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.MOVE_AND_RENAME_STATE, SyncModifyDnStatesEnum.MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE,
            UniversalTag.OCTET_STRING.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "Set SyncModifyDnControl moveAndRename state's newSuperirorDN value" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    String newSuperirorDn = Strings.utf8ToString(value.getData());

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newSuperirorDn = {}", newSuperirorDn );
                    }

                    container.getSyncModifyDnControl().setNewSuperiorDn( newSuperirorDn );

                    // terminal state
                    container.setGrammarEndAllowed( false );
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
        super.transitions[SyncModifyDnStatesEnum.MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.MOVE_AND_RENAME_NEW_SUPERIOR_DN_STATE, SyncModifyDnStatesEnum.MOVE_AND_RENAME_NEW_RDN_STATE,
            UniversalTag.OCTET_STRING.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "Set SyncModifyDnControl moveAndRename state's newRdn value" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    String newRdn = Strings.utf8ToString(value.getData());

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newRdn = {}", newRdn );
                    }

                    container.getSyncModifyDnControl().setNewRdn( newRdn );

                    // terminal state
                    container.setGrammarEndAllowed( false );
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
        super.transitions[SyncModifyDnStatesEnum.MOVE_AND_RENAME_NEW_RDN_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = new GrammarTransition(
            SyncModifyDnStatesEnum.MOVE_AND_RENAME_NEW_RDN_STATE, SyncModifyDnStatesEnum.MOVE_AND_RENAME_DEL_OLD_RDN_STATE,
            UniversalTag.BOOLEAN.getValue(), 
            new GrammarAction<SyncModifyDnContainer>( "Set SyncModifyDnControl deleteOldRdn value" )
            {
                public void action( SyncModifyDnContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    byte deleteOldRdn = value.getData()[0];

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "deleteOldRdn = {}", deleteOldRdn );
                    }

                    if( deleteOldRdn != 0 )
                    {
                        container.getSyncModifyDnControl().setDeleteOldRdn( true );
                    }

                    // terminal state
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
