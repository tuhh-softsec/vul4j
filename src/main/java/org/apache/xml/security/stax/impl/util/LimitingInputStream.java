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

import org.apache.xml.security.utils.I18n;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class LimitingInputStream extends FilterInputStream {

    private long limit;
    private long count;

    public LimitingInputStream(InputStream in, long limit) {
        super(in);
        this.limit = limit;
    }

    @Override
    public int read() throws IOException {
        int r = super.read();
        if (r >= 0) {
            incrementCountAndTestLimit(r);
        }
        return r;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int r = super.read(b, off, len);
        if (r >= 0) {
            incrementCountAndTestLimit(r);
        }
        return r;
    }

    private void incrementCountAndTestLimit(long read) throws IOException {
        this.count += read;
        if (this.count > this.limit) {
            throw new IOException(I18n.getExceptionMessage("secureProcessing.inputStreamLimitReached", new Object[]{this.limit}));
        }
    }
}
