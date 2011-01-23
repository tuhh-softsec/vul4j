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
package org.apache.directory.shared.asn1.ber.tlv;


/**
 * Stores the different states of a PDU parsing.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public enum TLVStateEnum
{
    /** Start means that the deconding hasn't read the first byte */
    TAG_STATE_START,

    /** Pending means that the Type Tag is contained in more that one byte */
    TAG_STATE_PENDING,

    /** End means that the Type is totally read */
    TAG_STATE_END,

    /**
     * Overflow could have two meaning : either there are more than 5 bytes to
     * encode the value (5 bytes = 5bits + 4*7 bits = 33 bits) or the value that
     * is represented by those bytes is over MAX_INTEGER
     */
    TAG_STATE_OVERFLOW,

    /** Start means that the decoding hasn't read the first byte */
    LENGTH_STATE_START,

    /** Pending means that the Type length is contained in more that one byte */
    LENGTH_STATE_PENDING,

    /** End means that the Length is totally read */
    LENGTH_STATE_END,

    /** Start means that the decoding hasn't read the first byte */
    VALUE_STATE_START,

    /** Pending means that the Type Value is contained in more that one byte */
    VALUE_STATE_PENDING,

    /** End means that the Value is totally read */
    VALUE_STATE_END,

    /** The decoding of a TLV is done */
    TLV_STATE_DONE,

    /** The decoding of a PDU is done */
    PDU_DECODED,

    /** The ending state */
    GRAMMAR_END
}
