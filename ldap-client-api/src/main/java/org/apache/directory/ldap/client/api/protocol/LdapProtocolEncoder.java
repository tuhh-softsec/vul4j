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
package org.apache.directory.ldap.client.api.protocol;


import java.nio.ByteBuffer;

import org.apache.directory.shared.ldap.message.LdapEncoder;
import org.apache.directory.shared.ldap.message.Message;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * A LDAP message decoder. It is based on shared-ldap decoder.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class LdapProtocolEncoder implements ProtocolEncoder
{
    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger( LdapProtocolEncoder.class );

    /** A speedup for logger */
    private static final boolean IS_DEBUG = LOG.isDebugEnabled();

    /** The stateful encoder */
    private static final LdapEncoder encoder = new LdapEncoder();


    public void encode( IoSession session, Object message, ProtocolEncoderOutput out ) throws Exception
    {
        ByteBuffer buffer = encoder.encodeMessage( ( Message ) message );

        IoBuffer ioBuffer = IoBuffer.wrap( buffer );

        out.write( ioBuffer );
    }


    public void dispose( IoSession session ) throws Exception
    {
        // Nothing to do
    }
}
