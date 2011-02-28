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
package org.apache.directory.shared.ldap.codec.search;


import java.nio.ByteBuffer;

import org.apache.directory.shared.asn1.EncoderException;


/**
 * An abstract Asn1Object used to store the filter. A filter is seen as a tree
 * with a root. This class does nothing, it's just the root of all the different
 * filters.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public abstract class Filter
{
    /** The identifier of the associated TLV */
    int tlvId;

    /** The parent TLV id */
    protected int parentTlvId;

    /** The parent Filter */
    protected Filter parent;


    /**
     * The constructor.
     */
    public Filter( int tlvId )
    {
        this.tlvId = tlvId;
    }


    /**
     * The constructor.
     */
    public Filter()
    {
    }


    /**
     * Get the parent
     * 
     * @return Returns the parent.
     */
    public Filter getParent()
    {
        return parent;
    }


    /**
     * Get the parent
     * 
     * @return Returns the parent.
     */
    public int getParentTlvId()
    {
        return parentTlvId;
    }


    /**
     * Set the parent
     * 
     * @param parent The parent to set.
     */
    public void setParent( Filter parent, int parentTlvId )
    {
        this.parent = parent;
        this.parentTlvId = parentTlvId;
    }


    public int getTlvId()
    {
        return tlvId;
    }


    /**
     * Compute the Filter length 
     */
    public abstract int computeLength();


    /**
     * Encode the Filter message to a PDU. 
     * @param buffer The buffer where to put the PDU
     * @return The PDU.
     */
    public abstract ByteBuffer encode( ByteBuffer buffer ) throws EncoderException;
}
