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
package org.apache.directory.shared.asn1.ber;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.ber.grammar.Grammar;
import org.apache.directory.shared.asn1.ber.grammar.States;
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.TLVStateEnum;


/**
 * This class is the abstract container used to store the current state of a PDU
 * being decoded. It also stores the grammars used to decode the PDU, and all
 * the informations needed to decode a PDU.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class AbstractContainer implements Asn1Container
{
    /** All the possible grammars */
    protected Grammar<?> grammar;

    /** Store a stack of the current states used when switching grammars */
    protected int[] stateStack;

    /** The current state of the decoding */
    private TLVStateEnum state;

    /** The current transition */
    private Enum<?> transition;

    /** The current TLV */
    private TLV tlv;

    /** The parent TLV */
    private TLV parentTLV;

    /** The grammar end transition flag */
    private boolean grammarEndAllowed;

    /** A counter for the decoded bytes */
    protected int decodeBytes;

    /** The maximum allowed size for a PDU. Default to MAX int value */
    private int maxPDUSize = Integer.MAX_VALUE;

    /** The incremental id used to tag TLVs */
    private int id = 0;

    /** The Stream being decoded */
    private ByteBuffer stream;

    /** A flag telling if the Value should be accumulated before being decoded
     * for constructed types */
    private boolean isGathering = false;

    /**
     * Creates a new instance of AbstractContainer with a starting state.
     *
     */
    protected AbstractContainer()
    {
        state = TLVStateEnum.TAG_STATE_START;
    }


    /**
     * Creates a new instance of AbstractContainer with a starting state.
     *
     * @param stream the buffer containing the data to decode
     */
    protected AbstractContainer( ByteBuffer stream )
    {
        state = TLVStateEnum.TAG_STATE_START;
        this.stream = stream;
    }


    /**
     * Get the current grammar
     *
     * @return Returns the grammar used to decode a LdapMessage.
     */
    public Grammar<?> getGrammar()
    {
        return grammar;
    }


    /**
     * Get the current grammar state
     *
     * @return Returns the current grammar state
     */
    public TLVStateEnum getState()
    {
        return state;
    }


    /**
     * Set the new current state
     *
     * @param state The new state
     */
    public void setState( TLVStateEnum state )
    {
        this.state = state;
    }


    /**
     * Check that we can have a end state after this transition
     *
     * @return true if this can be the last transition
     */
    public boolean isGrammarEndAllowed()
    {
        return grammarEndAllowed;
    }


    /**
     * Set the flag to allow a end transition
     *
     * @param grammarEndAllowed true or false, depending on the next transition
     * being an end or not.
     */
    public void setGrammarEndAllowed( boolean grammarEndAllowed )
    {
        this.grammarEndAllowed = grammarEndAllowed;
    }


    /**
     * Get the transition
     *
     * @return Returns the transition from the previous state to the new state
     */
    public Enum<?> getTransition()
    {
        return transition;
    }


    /**
     * Update the transition from a state to another
     *
     * @param transition The transition to set
     */
    public void setTransition( Enum<?> transition )
    {
        this.transition = transition;
    }


    /**
     * Set the current TLV
     *
     * @param currentTLV The current TLV
     */
    public void setCurrentTLV( TLV currentTLV )
    {
        this.tlv = currentTLV;
    }


    /**
     * Get the current TLV
     *
     * @return Returns the current TLV being decoded
     */
    public TLV getCurrentTLV()
    {
        return this.tlv;
    }


    /**
     * Get the parent TLV;
     *
     * @return Returns the parent TLV, if any.
     */
    public TLV getParentTLV()
    {
        return parentTLV;
    }


    /**
     * Set the parent TLV.
     *
     * @param parentTLV The parent TLV to set.
     */
    public void setParentTLV( TLV parentTLV )
    {
        this.parentTLV = parentTLV;
    }


    /**
     * Clean the container for the next usage.
     */
    public void clean()
    {
        tlv = null;
        parentTLV = null;
        transition = ( ( States ) transition ).getStartState();
        state = TLVStateEnum.TAG_STATE_START;
    }


    /**
     * Return a new ID and increment the counter
     * @return A new TLV id.
     */
    public int getNewTlvId()
    {
        return id++;
    }


    /**
     * @return The TLV Id
     */
    public int getTlvId()
    {
        return tlv.getId();
    }


    /**
     * @return The number of decoded bytes for this message. This is used
     * to control the PDU size and avoid PDU exceeding the maximum allowed
     * size to break the server.
     */
    public int getDecodeBytes()
    {
        return decodeBytes;
    }


    /**
     * Increment the decodedBytes by the latest received buffer's size.
     * @param nb The buffer size.
     */
    public void incrementDecodeBytes( int nb )
    {
        decodeBytes += nb;
    }


    /**
     * @return The maximum PDU size.
     */
    public int getMaxPDUSize()
    {
        return maxPDUSize;
    }


    /**
     * Set the maximum PDU size.
     * @param maxPDUSize The maximum PDU size (if negative or null, will be
     * replaced by the max integer value)
     */
    public void setMaxPDUSize( int maxPDUSize )
    {
        if ( maxPDUSize > 0 )
        {
            this.maxPDUSize = maxPDUSize;
        }
        else
        {
            this.maxPDUSize = Integer.MAX_VALUE;
        }
    }


    /**
     * {@inheritDoc}
     */
    public ByteBuffer getStream()
    {
        return stream;
    }


    /**
     * {@inheritDoc}
     */
    public void setStream( ByteBuffer stream )
    {
        this.stream = stream;
    }


    /**
     * {@inheritDoc}
     */
    public void rewind()
    {

        int start = stream.position() - 1 - tlv.getLengthNbBytes();
        stream.position( start );
    }


    /**
     * {@inheritDoc}
     */
    public void updateParent()
    {
        TLV parentTlv = tlv.getParent();

        while ( ( parentTlv != null ) && ( parentTlv.getExpectedLength() == 0 ) )
        {
            parentTlv = parentTlv.getParent();
        }

        this.parentTLV = parentTlv;
    }


    /**
     * {@inheritDoc}
     */
    public boolean isGathering()
    {
        return isGathering;
    }


    /**
     * {@inheritDoc}
     */
    public void setGathering( boolean isGathering )
    {
        this.isGathering = isGathering;
    }

}
