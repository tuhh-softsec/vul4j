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
package org.apache.directory.shared.ldap.codec;

import org.apache.directory.shared.asn1.codec.EncoderException;

/**
 * Create an exception containing the messageId
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$, $Date$
 */
public class MessageEncoderException extends EncoderException
{
    /** The message ID */
    private int messageId;

    /**
     * Creates a new instance of MessageEncoderException.
     *
     * @param messageId The message ID
     * @param message The exception message
     */
    public MessageEncoderException( int messageId, String message )
    {
        super( message );
        this.messageId = messageId;
    }


    /**
     * @return the messageId
     */
    public int getMessageId()
    {
        return messageId;
    }
}
