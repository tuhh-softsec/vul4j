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
package org.apache.directory.shared.asn1;


/**
 * Convenience class to not have to re-implement the two setter methods every time
 * one starts a new decoder.
 * 
 * @author <a href="mailto:dev@directory.apache.org"> Apache Directory Project</a>
 */
public abstract class AbstractStatefulDecoder implements StatefulDecoder
{
    /** this decoder's callback */
    private DecoderCallback cb = null;


    /**
     * Creates a stateful decoder where the callback and monitor must be set.
     */
    public AbstractStatefulDecoder()
    {
    }


    /**
     * Creates a stateful decoder with a callback.
     * 
     * @param cb the callback to use for this decoder
     */
    public AbstractStatefulDecoder( DecoderCallback cb )
    {
        setCallback( cb );
    }


    /**
     * {@inheritDoc}
     */
    public void setCallback( DecoderCallback callback )
    {
        this.cb = callback;
    }


    /**
     * {@inheritDoc}
     */
    public DecoderCallback getCallback()
    {
        return cb;
    }


    /**
     * Notifies via the callback if one has been set that this decoder has
     * decoded a unit of encoded data.
     * 
     * @param decoded the decoded byproduct.
     */
    protected void decodeOccurred( Object decoded )
    {
        if ( cb != null )
        {
            cb.decodeOccurred( this, decoded );
        }
    }
}
