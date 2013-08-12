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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Trimms the start and the end of a stream
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class TrimmerOutputStream extends FilterOutputStream {

    private byte[] buffer;
    private int bufferedCount;

    private int preTrimmed = 0;
    private int startTrimLength;
    private int endTrimLength;

    public TrimmerOutputStream(OutputStream out, int bufferSize, int startTrimLength, int endTrimLength) {
        super(out);
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize <= 0");
        }
        if (bufferSize < endTrimLength) {
            throw new IllegalArgumentException("bufferSize < endTrimLength");
        }
        this.buffer = new byte[bufferSize];
        this.startTrimLength = startTrimLength;
        this.endTrimLength = endTrimLength;
    }

    private void flushBuffer() throws IOException {
        if (bufferedCount >= endTrimLength) {
            //write all but the possible end (endTrimLength)
            out.write(buffer, 0, bufferedCount - endTrimLength);
            System.arraycopy(buffer, bufferedCount - endTrimLength, buffer, 0, endTrimLength);
            bufferedCount = endTrimLength;
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (preTrimmed < startTrimLength) {
            //discard byte
            preTrimmed++;
            return;
        }
        if (bufferedCount >= buffer.length) {
            flushBuffer();
        }
        buffer[bufferedCount++] = (byte) b;
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (preTrimmed < startTrimLength) {
            int missingBytes = startTrimLength - preTrimmed;
            if (missingBytes >= len) {
                //discard bytes
                preTrimmed += len;
                return;
            }
            len -= missingBytes;
            off += missingBytes;
            preTrimmed += missingBytes;
        }

        if (len >= (buffer.length - bufferedCount)) {
            out.write(buffer, 0, bufferedCount);
            out.write(b, off, len - endTrimLength);
            System.arraycopy(b, off + len - endTrimLength, buffer, 0, endTrimLength);
            bufferedCount = endTrimLength;
            return;
        }

        System.arraycopy(b, off, buffer, bufferedCount, len);
        bufferedCount += len;
    }

    @Override
    public void flush() throws IOException {
        flushBuffer();
        out.flush();
    }
}
