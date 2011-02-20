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
import org.apache.directory.shared.asn1.ber.tlv.TLV;
import org.apache.directory.shared.asn1.ber.tlv.TLVStateEnum;


/**
 * Every ASN1 container must implement this interface.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public interface Asn1Container
{
    /**
     * Gets the current stream containing the bytes to decode
     *
     * @return The current stream
     */
    ByteBuffer getStream();


    /**
     * Stores the Stream being decoded
     *
     * @param stream The stream being decoded
     */
    void setStream( ByteBuffer stream );


    /**
     * Get the current grammar state
     *
     * @return Returns the current grammar state
     */
    TLVStateEnum getState();


    /**
     * Set the new current state
     *
     * @param state The new state
     */
    void setState( TLVStateEnum state );


    /**
     * Get the currentTLV
     *
     * @return Returns the current TLV being decoded
     */
    TLV getCurrentTLV();


    /**
     * Set the current TLV
     *
     * @param tlv The current TLV
     */
    void setCurrentTLV( TLV tlv );


    /**
     * Get the grammar
     *
     * @return Returns the grammar used to decode a LdapMessage.
     */
    Grammar getGrammar();


    /**
     * Get the transition
     *
     * @return Returns the transition from the previous state to the new state
     */
    Enum<?> getTransition();


    /**
     * Update the transition from a state to another
     *
     * @param transition The transition to set
     */
    void setTransition( Enum<?> transition );


    /**
     * @return get the parent TLV.
     */
    TLV getParentTLV();


    /**
     * Set the parent TLV
     *
     * @param parentTLV The new parent TLV
     */
    void setParentTLV( TLV parentTLV );


    /**
     * Check that we can have a end state after this transition
     *
     * @return true if this can be the last transition
     */
    boolean isGrammarEndAllowed();


    /**
     * Set the flag to allow a end transition
     *
     * @param grammarEndAllowed true or false, depending on the next transition
     * being an end or not.
     */
    void setGrammarEndAllowed( boolean grammarEndAllowed );


    /**
     * Get a new TLV id
     * @return a unique value representing the current TLV id
     */
    int getNewTlvId();


    /**
     * Get the current TLV id
     * @return a unique value representing the current TLV id
     */
    int getTlvId();


    /**
     * @return The number of decoded bytes for this message. This is used
     * to control the PDU size and avoid PDU exceeding the maximum allowed
     * size to break the server.
     */
    int getDecodeBytes();


    /**
     * Increment the decodedBytes by the latest received buffer's size.
     * @param nb The buffer size.
     */
    void incrementDecodeBytes( int nb );


    /**
     * @return The maximum PDU size.
     */
    int getMaxPDUSize();


    /**
     * Set the maximum PDU size.
     * @param maxPDUSize The maximum PDU size (if negative or null, will be
     * replaced by the max integer value)
     */
    void setMaxPDUSize( int maxPDUSize );


    /**
     * Move backward in the stream to the first byte for a given TLV. This is useful when we have
     * read some Tag and Length in order to define the next transition, and if this transition
     * do a grammar switch.
     * @param tlv The TLV to rollback
     */
    void rewind();


    /**
     * Look for the closest parent which has an expected length above 0
     */
    void updateParent();


    /**
     * @return true if the container should gather the value into itself, false
     * if the decoding of the Value part should be done immediately for
     * constructed types.
     */
    boolean isGathering();


    /**
     * Set the isGathering flag
     * @param isGathering true to ask the Asn1Decoder to gather the data
     * into the container. If not set, the default value is 'false'
     */
    void setGathering( boolean isGathering );
}
