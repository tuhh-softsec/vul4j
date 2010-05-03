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


import org.apache.directory.shared.asn1.ber.IAsn1Container;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.grammar.IGrammar;
import org.apache.directory.shared.asn1.ber.grammar.IStates;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.codec.DecoderException;
import org.apache.directory.shared.ldap.message.control.replication.SyncModifyDnType;
import org.apache.directory.shared.ldap.util.StringTools;
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
 *          new-rdn RDN,
 *          delete-old-rdn BOOLEAN
 *      }
 * 
 *      MoveAndRename SEQUENCE {
 *          superior-name   LDAPDN
 *          new-rd RDN,
 *          delete-old-rdn BOOLEAN
 *      }
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev: 741888 $, $Date: 2009-02-07 13:57:03 +0100 (Sat, 07 Feb 2009) $, 
 */
public class SyncModifyDnControlGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( SyncModifyDnControlGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. SyncStateValueControlGrammar is a singleton */
    private static IGrammar instance = new SyncModifyDnControlGrammar();


    /**
     * Creates a new SyncModifyDnControlGrammar object.
     */
    private SyncModifyDnControlGrammar()
    {
        name = SyncModifyDnControlGrammar.class.getName();
        statesEnum = SyncModifyDnControlStatesEnum.getInstance();

        // Create the transitions table
        super.transitions = new GrammarTransition[SyncModifyDnControlStatesEnum.LAST_SYNC_MODDN_VALUE_STATE][256];

        /** 
         * Transition from initial state to SyncModifyDnControl sequence
         * SyncModifyDnControl ::= SEQUENCE OF {
         *     ...
         *     
         * Initialize the SyncModifyDnControl object
         */
        super.transitions[IStates.INIT_GRAMMAR_STATE][UniversalTag.SEQUENCE_TAG] = new GrammarTransition(
            IStates.INIT_GRAMMAR_STATE, SyncModifyDnControlStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE,
            UniversalTag.SEQUENCE_TAG, null );

        /** 
         * Transition from SyncModifyDnControl sequence to entryDn
         * move-name ::= SEQUENCE OF {
         *     DN        entryDN
         *     ...
         *     
         * Stores the entryDn value
         */
        super.transitions[SyncModifyDnControlStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
            IStates.INIT_GRAMMAR_STATE,
            SyncModifyDnControlStatesEnum.SYNC_MODDN_VALUE_SEQUENCE_STATE, UniversalTag.OCTET_STRING_TAG, new GrammarAction(
                "Set SyncModifyDnControl entryDn value" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    // Check that the value is into the allowed interval
                    String entryDn = StringTools.utf8ToString( value.getData() );
                    
                    if ( IS_DEBUG )
                    {
                        LOG.debug( "ModDN entryDn = {}", entryDn );
                    }
                    
                    syncModifyDnControlContainer.getSyncModifyDnControl().setEntryDn( entryDn );
                    
                    // move on to the next transistion
                    syncModifyDnControlContainer.grammarEndAllowed( false );
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

        super.transitions[IStates.INIT_GRAMMAR_STATE][SyncModifyDnControlTags.MOVE_TAG.getValue()] = new GrammarTransition(
            IStates.INIT_GRAMMAR_STATE, SyncModifyDnControlStatesEnum.MOVE_DN_STATE,
            SyncModifyDnControlTags.MOVE_TAG.getValue(), new GrammarAction( "Set SyncModifyDnControl newSuperiorDn" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    syncModifyDnControlContainer.getSyncModifyDnControl().setModDnType( SyncModifyDnType.MOVE );
                    // We need to read the move operation's superiorDN
                    syncModifyDnControlContainer.grammarEndAllowed( false );
                }
            } );

        /**
         * read the newSuperiorDn
         * move-name       [0] LDAPDN
         */
        super.transitions[SyncModifyDnControlStatesEnum.MOVE_DN_STATE][UniversalTag.OCTET_STRING_TAG] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.MOVE_DN_STATE, SyncModifyDnControlStatesEnum.NEW_SUPERIOR_DN_STATE,
            UniversalTag.OCTET_STRING_TAG, new GrammarAction( "Set SyncModifyDnControl newSuperiorDn" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    String newSuperiorDn = StringTools.utf8ToString( value.getData() );

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newSuperiorDn = {}", newSuperiorDn );
                    }

                    syncModifyDnControlContainer.getSyncModifyDnControl().setNewSuperiorDn( newSuperiorDn );

                    // We can have an END transition
                    syncModifyDnControlContainer.grammarEndAllowed( true );
                }
            } );

        // elecharny, the below transitions are wrong, but if the above code for reading the 'move' CHOICE's superiorDN is solved
        // will take the cue from there and try fixing the below for 'rename' and 'moveAndRename' CHOICE
        /** 
         * Transition from entryDN to newRdn
         * 
         * Rename SEQUENCE {
         *     new-rdn RDN,
         *     delete-old-rdn BOOLEAN
         * }
         *            
         * Stores the newRdn value
         */
        super.transitions[SyncModifyDnControlStatesEnum.NEW_SUPERIOR_DN_STATE][SyncModifyDnControlTags.RENAME_TAG.getValue()] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.NEW_SUPERIOR_DN_STATE, SyncModifyDnControlStatesEnum.NEW_RDN_STATE,
            UniversalTag.OCTET_STRING_TAG, new GrammarAction( "Set SyncModifyDnControl newRdn value" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    String newRdn = StringTools.utf8ToString( value.getData() );

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "newRdn = {}", newRdn );
                    }

                    syncModifyDnControlContainer.getSyncModifyDnControl().setNewRdn( newRdn );

                    // terminal state
                    syncModifyDnControlContainer.grammarEndAllowed( true );
                }
            } );
        
        /** 
         * Transition from entryDN to deleteOldRdn
         *  MoveAndRename SEQUENCE {
         *      superior-name   LDAPDN
         *      new-rd RDN,
         *      delete-old-rdn BOOLEAN
         *  }
         *     
         * Stores the deleteOldRdn flag
         */
        super.transitions[SyncModifyDnControlStatesEnum.NEW_RDN_STATE][UniversalTag.BOOLEAN_TAG] = new GrammarTransition(
            SyncModifyDnControlStatesEnum.NEW_RDN_STATE, SyncModifyDnControlStatesEnum.DEL_OLD_RDN_STATE,
            UniversalTag.BOOLEAN_TAG, new GrammarAction( "Set SyncModifyDnControl deleteOldRdn value" )
            {
                public void action( IAsn1Container container ) throws DecoderException
                {
                    SyncModifyDnControlContainer syncModifyDnControlContainer = ( SyncModifyDnControlContainer ) container;
                    Value value = syncModifyDnControlContainer.getCurrentTLV().getValue();

                    byte deleteOldRdn = value.getData()[0];

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "deleteOldRdn = {}", deleteOldRdn );
                    }

                    if( deleteOldRdn == 1 )
                    {
                        syncModifyDnControlContainer.getSyncModifyDnControl().setDeleteOldRdn( true );
                    }

                    // terminal state
                    syncModifyDnControlContainer.grammarEndAllowed( true );
                }
            } );
    }


    /**
     * This class is a singleton.
     * 
     * @return An instance on this grammar
     */
    public static IGrammar getInstance()
    {
        return instance;
    }
}
