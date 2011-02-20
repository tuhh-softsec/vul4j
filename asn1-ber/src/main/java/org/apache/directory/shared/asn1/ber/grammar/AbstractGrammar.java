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
package org.apache.directory.shared.asn1.ber.grammar;


import org.apache.directory.shared.asn1.DecoderException;
import org.apache.directory.shared.asn1.ber.Asn1Container;
import org.apache.directory.shared.asn1.util.Asn1StringUtils;
import org.apache.directory.shared.i18n.I18n;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The abstract Grammar which is the Mother of all the grammars. It contains
 * the transitions table.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractGrammar<E extends Asn1Container> implements Grammar<E>
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( AbstractGrammar.class );

    /** Speedup for logs */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /**
     * Table of transitions. It's a two dimension array, the first dimension
     * indice the states, the second dimension indices the Tag value, so it is
     * 256 wide.
     */
    protected GrammarTransition<E>[][] transitions;

    /** The grammar name */
    private String name;

    /** Default constructor */
    public AbstractGrammar()
    {
    }


    /**
     * Return the grammar's name
     *
     * @return The grammar name
     */
    public String getName()
    {
        return name;
    }


    /**
     * Set the grammar's name
     *
     * @param name The new grammar name
     */
    public void setName( String name )
    {
        this.name = name;
    }


    /**
     * Get the transition associated with the state and tag
     *
     * @param state The current state
     * @param tag The current tag
     * @return A valid transition if any, or null.
     */
    public GrammarTransition<E> getTransition( Enum<?> state, int tag )
    {
        return transitions[state.ordinal()][tag & 0x00FF];
    }


    /**
     * The main function. This is where an action is executed. If the action is
     * null, nothing is done.
     *
     * @param container The Asn1Container
     * @throws DecoderException Thrown if anything went wrong
     */
    public void executeAction( E container ) throws DecoderException
    {

        Enum<?> currentState = container.getTransition();
        // We have to deal with the special case of a GRAMMAR_END state
        if ( ((States)currentState).isEndState() )
        {
            return;
        }

        byte tagByte = container.getCurrentTLV().getTag();

        // We will loop until no more actions are to be executed
        GrammarTransition<E> transition = ( ( AbstractGrammar<E> ) container.getGrammar() ).getTransition( currentState,
            tagByte );

        if ( transition == null )
        {
            String errorMessage = I18n.err( I18n.ERR_00001_BAD_TRANSITION_FROM_STATE, currentState, Asn1StringUtils.dumpByte( tagByte ) );

            LOG.error( errorMessage );

            // If we have no more grammar on the stack, then this is an
            // error
            throw new DecoderException( errorMessage );
        }

        if ( IS_DEBUG )
        {
            LOG.debug( transition.toString() );
        }

        if ( transition.hasAction() )
        {
            Action<E> action = transition.getAction();
            action.action( container );
        }

        container.setTransition( transition.getCurrentState() );
    }
}
