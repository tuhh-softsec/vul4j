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
package org.apache.directory.shared.ldap.codec.decorators;


import org.apache.directory.shared.ldap.model.message.CompareRequest;


/**
 * Doc me!
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class CompareRequestDecorator extends MessageDecorator
{
    /** The bytes of the attribute id used in the comparison */
    private byte[] attrIdBytes;

    /** The bytes of the attribute value used in the comparison */
    private byte[] attrValBytes;

    /** The compare request length */
    private int compareRequestLength;

    /** The attribute value assertion length */
    private int avaLength;


    /**
     * Makes a CompareRequest encodable.
     *
     * @param decoratedMessage the decorated CompareRequest
     */
    public CompareRequestDecorator( CompareRequest decoratedMessage )
    {
        super( decoratedMessage );
    }


    public CompareRequest getCompareRequest()
    {
        return ( CompareRequest ) getMessage();
    }


    /**
     * Stores the encoded length for the CompareRequest
     * @param compareRequestLength The encoded length
     */
    public void setCompareRequestLength( int compareRequestLength )
    {
        this.compareRequestLength = compareRequestLength;
    }


    /**
     * @return The encoded CompareRequest length
     */
    public int getCompareRequestLength()
    {
        return compareRequestLength;
    }


    /**
     * Stores the encoded length for the ava
     * @param avaLength The encoded length
     */
    public void setAvaLength( int avaLength )
    {
        this.avaLength = avaLength;
    }


    /**
     * @return The encoded ava length
     */
    public int getAvaLength()
    {
        return avaLength;
    }


    /**
     * Gets the attribute id bytes use in making the comparison.
     *
     * @return the attribute id bytes used in comparison.
     */
    public byte[] getAttrIdBytes()
    {
        return attrIdBytes;
    }


    /**
     * Sets the attribute id bytes used in the comparison.
     *
     * @param attrIdBytes the attribute id bytes used in comparison.
     */
    public void setAttrIdBytes( byte[] attrIdBytes )
    {
        this.attrIdBytes = attrIdBytes;
    }


    /**
     * Gets the attribute value bytes use in making the comparison.
     *
     * @return the attribute value bytes used in comparison.
     */
    public byte[] getAttrValBytes()
    {
        return attrValBytes;
    }


    /**
     * Sets the attribute value bytes used in the comparison.
     *
     * @param attrValBytes the attribute value bytes used in comparison.
     */
    public void setAttrValBytes( byte[] attrValBytes )
    {
        this.attrValBytes = attrValBytes;
    }
}
