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

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;

/**
 * IV splitting from the first few bytes in the stream.
 * When the iv is completely received the cipher will be initialized
 * and this output stream will be removed from chain of output streams
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class IVSplittingOutputStream extends FilterOutputStream {

    private ReplaceableOuputStream replaceableOuputStream;

    private final byte[] iv;
    private final int ivLength;
    private int pos = 0;

    private final Cipher cipher;
    private final Key secretKey;

    public IVSplittingOutputStream(OutputStream out, Cipher cipher, Key secretKey, int ivLength) {
        super(out);
        this.ivLength = ivLength;
        iv = new byte[ivLength];
        this.cipher = cipher;
        this.secretKey = secretKey;
    }

    public byte[] getIv() {
        return iv;
    }

    public boolean isIVComplete() {
        return pos == iv.length;
    }

    private void initializeCipher() throws IOException {
        IvParameterSpec iv = new IvParameterSpec(this.getIv());
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        } catch (InvalidKeyException e) {
            throw new IOException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(int b) throws IOException {
        if (pos >= ivLength) {
            initializeCipher();
            out.write(b);
            replaceableOuputStream.setNewOutputStream(out);
            return;
        }
        iv[pos++] = (byte) b;
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int missingBytes = ivLength - pos;
        if (missingBytes > len) {
            System.arraycopy(b, off, iv, pos, len);
            pos += len;
        } else {
            System.arraycopy(b, off, iv, pos, missingBytes);
            pos += missingBytes;
            initializeCipher();
            out.write(b, off + missingBytes, len - missingBytes);
            replaceableOuputStream.setNewOutputStream(out);
        }
    }

    public void setParentOutputStream(ReplaceableOuputStream replaceableOuputStream) {
        this.replaceableOuputStream = replaceableOuputStream;
    }
}
