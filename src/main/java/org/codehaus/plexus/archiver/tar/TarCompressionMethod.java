package org.codehaus.plexus.archiver.tar;

/*
 * Copyright  2000-2004 The Apache Software Foundation
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

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.bzip2.CBZip2OutputStream;
import org.codehaus.plexus.archiver.util.EnumeratedAttribute;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Valid Modes for Compression attribute to Tar Task
 *
 */
public final class TarCompressionMethod extends EnumeratedAttribute {

    // permissible values for compression attribute
    /**
     *    No compression
     */
    private static final String NONE = "none";
    /**
     *    GZIP compression
     */
    private static final String GZIP = "gzip";
    /**
     *    BZIP2 compression
     */
    private static final String BZIP2 = "bzip2";


    /**
     * Default constructor
     */
    public TarCompressionMethod()
        throws ArchiverException
    {
        super();
        setValue(NONE);
    }

    /**
     *  Get valid enumeration values.
     *  @return valid enumeration values
     */
    public String[] getValues() {
        return new String[] {NONE, GZIP, BZIP2 };
    }

    /**
     *  This method wraps the output stream with the
     *     corresponding compression method
     *
     *  @param ostream output stream
     *  @return output stream with on-the-fly compression
     *  @exception IOException thrown if file is not writable
     */
    private OutputStream compress(final OutputStream ostream)
        throws IOException {
        final String value = getValue();
        if (GZIP.equals(value)) {
            return new GZIPOutputStream(ostream);
        } else {
            if (BZIP2.equals(value)) {
                ostream.write('B');
                ostream.write('Z');
                return new CBZip2OutputStream(ostream);
            }
        }
        return ostream;
    }
}
