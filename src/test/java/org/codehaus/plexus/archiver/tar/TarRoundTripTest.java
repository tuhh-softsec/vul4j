/*
 * Copyright  2003-2004 The Apache Software Foundation
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
package org.codehaus.plexus.archiver.tar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import junit.framework.TestCase;

/**
 * from org.apache.ant.tools.tar.TarRoundTripTest v1.6
 */
public class TarRoundTripTest
    extends TestCase
{

    private static final String LONG_NAME =
        "this/path/name/contains/more/than/one/hundred/characters/in/order/"
            + "to/test/the/GNU/long/file/name/capability/round/tripped";

    /**
     * test round-tripping long (GNU) entries
     */
    public void testLongRoundTripping()
        throws IOException
    {
        TarArchiveEntry original = new TarArchiveEntry( LONG_NAME );
        assertEquals( "over 100 chars", true, LONG_NAME.length() > 100 );
        assertEquals( "original name", LONG_NAME, original.getName() );

        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        TarArchiveOutputStream tos = new TarArchiveOutputStream( buff );
        tos.setLongFileMode( TarArchiveOutputStream.LONGFILE_GNU );
        tos.putArchiveEntry( original );
        tos.closeArchiveEntry();
        tos.close();

        TarArchiveInputStream tis = new TarArchiveInputStream( new ByteArrayInputStream( buff.toByteArray() ) );
        TarArchiveEntry tripped = tis.getNextTarEntry();
        assertEquals( "round-tripped name", LONG_NAME, tripped.getName() );
        assertNull( "no more entries", tis.getNextEntry() );
        tis.close();
    }

}
