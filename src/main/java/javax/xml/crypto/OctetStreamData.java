/*
 * Copyright 2005 The Apache Software Foundation.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
/*
 * Copyright 2005 Sun Microsystems, Inc. All rights reserved.
 *
 * @author Sean Mullan
 * @author JSR 105 Expert Group
 */
/*
 * $Id$
 */
package javax.xml.crypto;

import java.io.InputStream;

/**
 * A representation of a <code>Data</code> type containing an octet stream.
 */
public class OctetStreamData implements Data {
  
    private InputStream octetStream;
    private String uri;
    private String mimeType;

    /**
     * Creates a new <code>OctetStreamData</code>.
     *
     * @param octetStream the input stream containing the octets
     * @throws NullPointerException if <code>octetStream</code> is 
     *    <code>null</code>
     */
    public OctetStreamData(InputStream octetStream) {
        if (octetStream == null) {
            throw new NullPointerException("octetStream is null");
        }
        this.octetStream = octetStream;
    }

    /**
     * Creates a new <code>OctetStreamData</code>.
     *
     * @param octetStream the input stream containing the octets
     * @param uri the URI String identifying the data object (may be 
     *    <code>null</code>) 
     * @param mimeType the MIME type associated with the data object (may be 
     *    <code>null</code>) 
     * @throws NullPointerException if <code>octetStream</code> is 
     *    <code>null</code>
     */
    public OctetStreamData(InputStream octetStream, String uri, 
        String mimeType) {
        if (octetStream == null) {
            throw new NullPointerException("octetStream is null");
        }
        this.octetStream = octetStream;
        this.uri = uri;
        this.mimeType = mimeType;
    }

    /**
     * Returns the input stream of this <code>OctetStreamData</code>.
     *
     * @return the input stream of this <code>OctetStreamData</code>.
     */
    public InputStream getOctetStream() {
        return octetStream;
    }

    /**
     * Returns the URI String identifying the data object represented by this
     * <code>OctetStreamData</code>.
     *
     * @return the URI String or <code>null</code> if not applicable
     */
    public String getURI() {
        return uri;
    }

    /**
     * Returns the MIME type associated with the data object represented by this
     * <code>OctetStreamData</code>.
     *
     * @return the MIME type or <code>null</code> if not applicable
     */
    public String getMimeType() {
        return mimeType;
    }
}
