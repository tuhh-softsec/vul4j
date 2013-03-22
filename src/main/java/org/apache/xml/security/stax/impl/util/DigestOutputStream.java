/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.xml.security.stax.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.security.MessageDigest;

/**
 * A Streaming based message-digest implementation
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class DigestOutputStream extends OutputStream {

    protected static final transient Logger log = LoggerFactory.getLogger(DigestOutputStream.class);
    protected static final transient boolean isDebugEnabled = log.isDebugEnabled();

    private final MessageDigest messageDigest;
    private StringBuilder stringBuilder;

    public DigestOutputStream(MessageDigest messageDigest) {
        this.messageDigest = messageDigest;
        if (isDebugEnabled) {
            stringBuilder = new StringBuilder();
        }
    }

    @Override
    public void write(byte[] arg0) {
        write(arg0, 0, arg0.length);
    }

    @Override
    public void write(int arg0) {
        messageDigest.update((byte) arg0);
        if (isDebugEnabled) {
            stringBuilder.append(new String(new byte[]{(byte) arg0}));
        }
    }

    @Override
    public void write(byte[] arg0, int arg1, int arg2) {
        messageDigest.update(arg0, arg1, arg2);
        if (isDebugEnabled) {
            stringBuilder.append(new String(arg0, arg1, arg2));
        }
    }

    public byte[] getDigestValue() {
        if (isDebugEnabled) {
            log.debug("Pre Digest: ");
            log.debug(stringBuilder.toString());
            log.debug("End pre Digest ");
            stringBuilder = new StringBuilder();
        }
        return messageDigest.digest();
    }
}
