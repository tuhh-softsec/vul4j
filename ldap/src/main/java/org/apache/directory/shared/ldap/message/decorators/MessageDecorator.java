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
package org.apache.directory.shared.ldap.message.decorators;


import org.apache.directory.shared.asn1.EncoderException;
import org.apache.directory.shared.ldap.model.message.*;


/**
 * Doc me man!
 *
 * @TODO make this class abstract, after finishing switch and all types and make default blow an EncoderException
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class MessageDecorator
{
    // ~ Instance fields
    // ----------------------------------------------------------------------------

    /** The decorated Control */
    private final Message decoratedMessage;

    /** The encoded Message length */
    protected int messageLength;

    /** The length of the controls */
    private int controlsLength;


    public static MessageDecorator getDecorator( Message decoratedMessage ) throws EncoderException
    {
        switch ( decoratedMessage.getType() )
        {
            case ABANDON_REQUEST:
                return new MessageDecorator( decoratedMessage );
            case ADD_REQUEST:
                return new AddRequestDecorator( ( AddRequest ) decoratedMessage );
            case ADD_RESPONSE:
                return new AddResponseDecorator( ( AddResponse ) decoratedMessage );
            case BIND_REQUEST:
                return new BindRequestDecorator( ( BindRequest ) decoratedMessage );
            case BIND_RESPONSE:
                return new BindResponseDecorator( ( BindResponse ) decoratedMessage );
            default:
                return new MessageDecorator( decoratedMessage );
        }
    }


    /**
     * Makes a Message an Encodeable object.
     *
     * @TODO make me protected after making this class abstract
     */
    public MessageDecorator( Message decoratedMessage )
    {
        this.decoratedMessage = decoratedMessage;
    }


    public Message getMessage()
    {
        return decoratedMessage;
    }


    public void setControlsLength( int controlsLength )
    {
        this.controlsLength = controlsLength;
    }


    public int getControlsLength()
    {
        return controlsLength;
    }


    public void setMessageLength( int messageLength )
    {
        this.messageLength = messageLength;
    }


    public int getMessageLength()
    {
        return messageLength;
    }
}
