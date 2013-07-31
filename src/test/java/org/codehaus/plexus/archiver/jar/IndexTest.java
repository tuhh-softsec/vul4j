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
    
    
    /**
     * this is pretty much a duplicate of testCreateArchiveWithIndexedJars(), but adds some extra
     * tests for files in META-INF
     */
    public void testCreateArchiveWithIndexedJarsAndMetaInf()
    throws Exception
    {
        /* create a dummy jar */
        JarArchiver archiver1 = (JarArchiver) lookup( Archiver.ROLE, "jar" );
        archiver1.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "one.txt" );

        // add a file in the META-INF directory, as this previously didn't make it into the index
        archiver1.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "META-INF/foo" );
        archiver1.setDestFile( getTestFile( "target/output/archive1.jar" ) );
        archiver1.createArchive();

        /* create another dummy jar, with an index but nothing else in META-INF. Also checks non-leaf files. */
        JarArchiver archiver3 = (JarArchiver) lookup( Archiver.ROLE, "jar" );
        archiver3.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "org/apache/maven/one.txt" );
        archiver3.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "META-INF/INDEX.LIST" );
        archiver3.setDestFile( getTestFile( "target/output/archive3.jar" ) );
        archiver3.createArchive();

        /* now create another jar, with an index, and whose manifest includes a Class-Path entry for the first two jars.
         */
        Manifest m = new Manifest();
        Manifest.Attribute classpathAttr = new Manifest.Attribute( "Class-Path", "archive1.jar archive3.jar" );
        m.addConfiguredAttribute( classpathAttr );

        JarArchiver archiver2 = (JarArchiver) lookup( Archiver.ROLE, "jar" );
        archiver2.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), "two.txt" );
        archiver2.setIndex(true);
        archiver2.addConfiguredIndexJars(archiver1.getDestFile());
        archiver2.addConfiguredIndexJars(archiver3.getDestFile());
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
        //System.out.println(res);
        
        StringBuilder expected = new StringBuilder();
        expected.append("JarIndex-Version: 1.0\n\n");
        expected.append("archive2.jar\ntwo.txt\n\n");
        expected.append("archive1.jar\nMETA-INF\none.txt\n\n");
        expected.append("archive3.jar\norg\norg/apache\norg/apache/maven\n\n");
        assertEquals(expected.toString(), res.replaceAll("\r\n", "\n"));
    }
}
