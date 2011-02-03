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
package org.apache.directory.shared.ldap.extras.controls.syncrepl_impl;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoderException;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.ber.tlv.BooleanDecoder;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * Implementation of SyncDoneValueControl. All the actions are declared in
 * this class. As it is a singleton, these declaration are only done once.
 *
 *  The decoded grammar is as follows :
 *  
 *  syncDoneValue ::= SEQUENCE 
 *  {
 *       cookie          syncCookie OPTIONAL,
 *       refreshDeletes  BOOLEAN DEFAULT FALSE
 *  }
 *  
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class SyncDoneValueGrammar extends AbstractGrammar
{

    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger( SyncDoneValueGrammar.class );

    /** speedup for logger */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** SyncDoneValueControlGrammar singleton instance */
    private static final SyncDoneValueGrammar INSTANCE = new SyncDoneValueGrammar();


    /**
     * 
     * Creates a new instance of SyncDoneValueControlGrammar.
     *
     */
    private SyncDoneValueGrammar()
    {
        setName( SyncDoneValueGrammar.class.getName() );

        super.transitions = new GrammarTransition[SyncDoneValueStatesEnum.LAST_SYNC_DONE_VALUE_STATE.ordinal()][256];

        /** 
         * Transition from initial state to SyncDoneValue sequence
         * SyncDoneValue ::= SEQUENCE {
         *     ...
         *     
         * Initialize the syncDoneValue object
         */
        super.transitions[SyncDoneValueStatesEnum.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] = new GrammarTransition(
            SyncDoneValueStatesEnum.START_STATE, SyncDoneValueStatesEnum.SYNC_DONE_VALUE_SEQUENCE_STATE, UniversalTag.SEQUENCE.getValue(),
            new GrammarAction<SyncDoneValueContainer>( "Initialization" )
            {
                public void action( SyncDoneValueContainer container ) throws DecoderException
                {
                    // As all the values are optional or defaulted, we can end here
                    container.setGrammarEndAllowed( true );
                }
            }  );

        /**
         * transition from start to cookie
         * {
         *    cookie          syncCookie OPTIONAL
         *    ....
         * }
         */
        super.transitions[SyncDoneValueStatesEnum.SYNC_DONE_VALUE_SEQUENCE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] = new GrammarTransition(
            SyncDoneValueStatesEnum.SYNC_DONE_VALUE_SEQUENCE_STATE, SyncDoneValueStatesEnum.COOKIE_STATE,
            UniversalTag.OCTET_STRING.getValue(), 
            new GrammarAction<SyncDoneValueContainer>( "Set SyncDoneValueControl cookie" )
            {
                public void action( SyncDoneValueContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    byte[] cookie = value.getData();

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "cookie = {}", Strings.dumpBytes(cookie) );
                    }

                    container.getSyncDoneValueControl().setCookie( cookie );

                    container.setGrammarEndAllowed( true );
                }
            } );

        GrammarAction<SyncDoneValueContainer> refreshDeletesTagAction = 
            new GrammarAction<SyncDoneValueContainer>( "set SyncDoneValueControl refreshDeletes flag" )
        {
            public void action( SyncDoneValueContainer container ) throws DecoderException
            {
                Value value = container.getCurrentTLV().getValue();

                try
                {
                    boolean refreshDeletes = BooleanDecoder.parse( value );

                    if ( IS_DEBUG )
                    {
                        LOG.debug( "refreshDeletes = {}", refreshDeletes );
                    }

                    container.getSyncDoneValueControl().setRefreshDeletes( refreshDeletes );

                    // the END transition for grammar
                    container.setGrammarEndAllowed( true );
                }
                catch ( BooleanDecoderException be )
                {
                    String msg = I18n.err( I18n.ERR_04024 );
                    LOG.error( msg, be );
                    throw new DecoderException( msg );
                }

            }
        }; 
        /**
         * transition from cookie to refreshDeletes
         * {
         *    ....
         *    refreshDeletes BOOLEAN DEFAULT FALSE
         * }
         */
        super.transitions[SyncDoneValueStatesEnum.COOKIE_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = new GrammarTransition(
            SyncDoneValueStatesEnum.COOKIE_STATE, SyncDoneValueStatesEnum.REFRESH_DELETES_STATE,
            UniversalTag.BOOLEAN.getValue(), refreshDeletesTagAction );
        
        /**
         * transition from SEQUENCE to refreshDeletes
         * {
         *    ....
         *    refreshDeletes BOOLEAN DEFAULT FALSE
         * }
         */
        super.transitions[SyncDoneValueStatesEnum.SYNC_DONE_VALUE_SEQUENCE_STATE.ordinal()][UniversalTag.BOOLEAN.getValue()] = new GrammarTransition(
            SyncDoneValueStatesEnum.SYNC_DONE_VALUE_SEQUENCE_STATE, SyncDoneValueStatesEnum.REFRESH_DELETES_STATE,
            UniversalTag.BOOLEAN.getValue(), refreshDeletesTagAction );

    }


    /**
     * @return the singleton instance of the SyncDoneValueControlGrammar
     */
    public static Grammar getInstance()
    {
        return INSTANCE;
    }
}
