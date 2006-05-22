package org.codehaus.plexus.archiver.jar;

/*
 * Copyright  2006 The Apache Software Foundation
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

import java.io.BufferedInputStream;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.zip.ZipEntry;
import org.codehaus.plexus.archiver.zip.ZipFile;

/**
 * @author Richard van der Hoff <richardv@mxtelecom.com>
 * @version $Id$
 */
public class IndexTest extends PlexusTestCase {
    public void testCreateArchiveWithIndexedJars()
    throws Exception
    {
        /* create a dummy jar */
        JarArchiver archiver1 = (JarArchiver) lookup( Archiver.ROLE, "jar" );
        archiver1.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "one.txt" );
        archiver1.setDestFile( getTestFile( "target/output/archive1.jar" ) );
        archiver1.createArchive();

        /* now create another jar, with an index, and whose manifest includes a Class-Path entry for the first jar.
         */
        Manifest m = new Manifest();
        Manifest.Attribute classpathAttr = new Manifest.Attribute( "Class-Path", "archive1.jar" );
        m.addConfiguredAttribute( classpathAttr );

        JarArchiver archiver2 = (JarArchiver) lookup( Archiver.ROLE, "jar" );
        archiver2.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "two.txt" );
        archiver2.setIndex(true);
        archiver2.addConfiguredIndexJars(archiver1.getDestFile());
        archiver2.setDestFile( getTestFile( "target/output/archive2.jar" ) );
        archiver2.addConfiguredManifest(m);
        archiver2.createArchive();

        // read the index file back and check it looks like it ought to
        ZipFile zf = new ZipFile( archiver2.getDestFile() );
        ZipEntry indexEntry = zf.getEntry("META-INF/INDEX.LIST");
        assertNotNull(indexEntry);
        BufferedInputStream bis = new BufferedInputStream(zf.getInputStream(indexEntry));

        byte buf[] = new byte[1024];
        int i = bis.read(buf);
        String res = new String(buf,0,i);
        assertEquals("JarIndex-Version: 1.0\n\narchive2.jar\ntwo.txt\n\narchive1.jar\none.txt\n\n", res.replaceAll("\r\n", "\n"));
    }
}
