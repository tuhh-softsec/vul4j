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
package org.apache.directory.shared.asn1.codec;


import org.apache.directory.shared.asn1.codec.stateful.DecoderCallback;
import org.apache.directory.shared.asn1.codec.stateful.StatefulDecoder;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderAdapter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


/**
 * Adapts {@link StatefulDecoder} to MINA <tt>ProtocolDecoder</tt>.
 *
 * @author The Apache Directory Project (mina-dev@directory.apache.org)
 */
public class Asn1CodecDecoder extends ProtocolDecoderAdapter
{
    /** The stateful decoder */
    private final StatefulDecoder decoder;

    /** The Output queue */
    private ProtocolDecoderOutput decOut;


    /**
     * Creates a new instance of Asn1CodecDecoder.
     * 
     * @param decoder The associated decoder
     */
    public Asn1CodecDecoder( StatefulDecoder decoder )
    {
        this.decoder = decoder;
        this.decoder.setCallback( new DecoderCallback()
        {
            public void decodeOccurred( StatefulDecoder decoder, Object decoded )
            {
                decOut.write( decoded );
            }
        } );
    }


    /**
     * {@inheritDoc}
     */
    public void decode( IoSession session, IoBuffer in, ProtocolDecoderOutput out ) throws DecoderException
    {
        decOut = out;
        decoder.decode( in.buf() );
    }
}
