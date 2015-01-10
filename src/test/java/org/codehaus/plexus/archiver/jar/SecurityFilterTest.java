package org.codehaus.plexus.archiver.jar;

/*
 * Copyright 2007 The Codehaus Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.filters.JarSecurityFileFilter;

/**
 * @author Mike Cumings
 * @version $Id$
 * @since 14 Jun 07
 */
public class SecurityFilterTest
    extends PlexusTestCase
{

    public void testSecurityFilters()
        throws Exception
    {
        File dummyContent = getTestFile( "src/test/resources/jar-security/dummy.txt" );

        String[] unFilteredFiles = new String[] { "META-INF/BOB.txt", "META-INF/harry.xml", };

        String[] filteredFiles = new String[] {
            "META-INF/FOO.DSA",
            "META-INF/BAR.dsa",
            "META-INF/BAZ.RSA",
            "META-INF/BOO.rsa",
            "META-INF/SIG.SF",
            "META-INF/FILE.sf" };

        // Load up our filter ist
        List filters = new ArrayList();
        filters.add( new JarSecurityFileFilter() );
        //filters.add( new JarSecurityFileSelector() );
        // Create our test jar with fake security files
        JarArchiver archiver = (JarArchiver) lookup( Archiver.ROLE, "jar" );
        archiver.setArchiveFilters(filters);
        for (String filteredFile1 : filteredFiles) {
            archiver.addFile(dummyContent, filteredFile1);
        }
        for (String unFilteredFile1 : unFilteredFiles) {
            archiver.addFile(dummyContent, unFilteredFile1);
        }
        archiver.setDestFile( getTestFile( "target/jar-security/test-archive.jar" ) );
        archiver.createArchive();

        // Verify that the fake files were filtered out of the created jar and that
        // the legitimate files were not
        org.apache.commons.compress.archivers.zip.ZipFile zf = new org.apache.commons.compress.archivers.zip.ZipFile( archiver.getDestFile() );
        for (String filteredFile : filteredFiles) {
            ZipArchiveEntry entry = zf.getEntry(filteredFile);
            assertNull("Entry was not filtered out: " + filteredFile, entry);
        }
        for (String unFilteredFile : unFilteredFiles) {
            ZipArchiveEntry entry = zf.getEntry(unFilteredFile);
            assertNotNull("Entry was filtered out: " + unFilteredFile, entry);
        }
    }

}
