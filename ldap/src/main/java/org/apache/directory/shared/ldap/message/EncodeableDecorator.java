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
package org.apache.directory.shared.ldap.message;


import org.apache.directory.shared.ldap.model.message.Message;


/**
 * Example to show decorator application. The decorator interface is CodecControl which
 * adds the additional functionality. This class would be the concrete decorator for the
 * CodecControl. The decorated component is Control, and an example of a concrete
 * decorated component would be LdifControl.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
public class EncodeableDecorator implements Encodeable
{
    // ~ Instance fields
    // ----------------------------------------------------------------------------

    /** The decorated Control */
    private final Message decoratedMessage;

    /** The encoded Message length */
    protected int messageLength;

    /** The length of the controls */
    private int controlsLength;


    /**
     * Makes a Message an Encodeable object.
     */
    public EncodeableDecorator(Message decoratedMessage)
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
