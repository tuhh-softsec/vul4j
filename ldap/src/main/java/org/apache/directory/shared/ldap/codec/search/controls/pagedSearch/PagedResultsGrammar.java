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
package org.apache.directory.shared.ldap.codec.search.controls.pagedSearch;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.grammar.AbstractGrammar;
import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.GrammarAction;
import org.apache.directory.shared.asn1.ber.grammar.GrammarTransition;
import org.apache.directory.shared.asn1.ber.tlv.UniversalTag;
import org.apache.directory.shared.asn1.ber.tlv.Value;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoder;
import org.apache.directory.shared.asn1.ber.tlv.IntegerDecoderException;
import org.apache.directory.shared.i18n.I18n;
import org.apache.directory.shared.util.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class implements the PagedSearchControl. All the actions are declared in
 * this class. As it is a singleton, these declaration are only done once.
 * 
 * The decoded grammar is the following :
 * 
 * realSearchControlValue ::= SEQUENCE {
 *     size   INTEGER,
 *     cookie OCTET STRING,
 * }
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public final class PagedResultsGrammar extends AbstractGrammar
{
    /** The logger */
    static final Logger LOG = LoggerFactory.getLogger( PagedResultsGrammar.class );

    /** Speedup for logs */
    static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The instance of grammar. PagedSearchControlGrammar is a singleton */
    private static Grammar instance = new PagedResultsGrammar();


    /**
     * Creates a new PagedSearchControlGrammar object.
     */
    private PagedResultsGrammar()
    {
        setName( PagedResultsGrammar.class.getName() );

        // Create the transitions table
        super.transitions = new GrammarTransition[ PagedResultsStates.LAST_PAGED_SEARCH_STATE.ordinal()][256];

        /** 
         * Transition from initial state to PagedSearch sequence
         * realSearchControlValue ::= SEQUENCE OF {
         *     ...
         *     
         * Nothing to do
         */
        super.transitions[ PagedResultsStates.START_STATE.ordinal()][UniversalTag.SEQUENCE.getValue()] =
            new GrammarTransition( PagedResultsStates.START_STATE,
                                    PagedResultsStates.PAGED_SEARCH_SEQUENCE_STATE,
                                    UniversalTag.SEQUENCE.getValue(), null );


        /** 
         * Transition from PagedSearch sequence to size
         * 
         * realSearchControlValue ::= SEQUENCE OF {
         *     size  INTEGER,  -- INTEGER (0..maxInt),
         *     ...
         *     
         * Stores the size value
         */
        super.transitions[ PagedResultsStates.PAGED_SEARCH_SEQUENCE_STATE.ordinal()][UniversalTag.INTEGER.getValue()] =
            new GrammarTransition( PagedResultsStates.PAGED_SEARCH_SEQUENCE_STATE,
                PagedResultsStates.SIZE_STATE,
                UniversalTag.INTEGER.getValue(),
                new GrammarAction<PagedResultsContainer>( "Set PagedSearchControl size" )
            {
                public void action( PagedResultsContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    try
                    {
                        // Check that the value is into the allowed interval
                        int size = IntegerDecoder.parse( value, Integer.MIN_VALUE, Integer.MAX_VALUE );
                        
                        // We allow negative value to absorb a bug in some M$ client.
                        // Those negative values will be transformed to Integer.MAX_VALUE.
                        if ( size < 0 )
                        {
                            size = Integer.MAX_VALUE;
                        }
                        
                        if ( IS_DEBUG )
                        {
                            LOG.debug( "size = " + size );
                        }

                        container.getDecorator().setSize( size );
                    }
                    catch ( IntegerDecoderException e )
                    {
                        String msg = I18n.err( I18n.ERR_04050 );
                        LOG.error( msg, e );
                        throw new DecoderException( msg );
                    }
                }
            } );

        /** 
         * Transition from size to cookie
         * realSearchControlValue ::= SEQUENCE OF {
         *     ...
         *     cookie   OCTET STRING
         * }
         *     
         * Stores the cookie flag
         */
        super.transitions[ PagedResultsStates.SIZE_STATE.ordinal()][UniversalTag.OCTET_STRING.getValue()] =
            new GrammarTransition( PagedResultsStates.SIZE_STATE,
                                    PagedResultsStates.COOKIE_STATE, UniversalTag.OCTET_STRING.getValue(),
                new GrammarAction<PagedResultsContainer>( "Set PagedSearchControl cookie" )
            {
                public void action( PagedResultsContainer container ) throws DecoderException
                {
                    Value value = container.getCurrentTLV().getValue();

                    if ( container.getCurrentTLV().getLength() == 0 )
                    {
                        container.getDecorator().setCookie( StringConstants.EMPTY_BYTES );
                    }
                    else
                    {
                        container.getDecorator().setCookie( value.getData() );
                    }

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
