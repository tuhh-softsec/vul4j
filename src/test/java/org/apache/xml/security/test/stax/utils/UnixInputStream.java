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
package org.apache.xml.security.test.stax.utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Simple and stupid dos/windows to unix line ending converter.
 * It's used to convert testdata to unix style independent on the
 * platform running on. This is just a workaround for "svn:eol-style" set
 * to native
 *
 * It's inefficient and simply suppresses the output of '\r' which
 * is not correct in every case. So do not use it in productive code.
 *
 * @author $Author$
 * @version $Revision$ $Date$
 */
public class UnixInputStream extends InputStream {

    private InputStream inputStream;

    public UnixInputStream(InputStream in) {
        super();
        this.inputStream = in;
    }

    @Override
    public int read() throws IOException {
        int read = inputStream.read();
        if (read == '\r') {
            return inputStream.read();
        }
        return read;
    }
}
