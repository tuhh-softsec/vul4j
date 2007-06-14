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

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.filters.JarSecurityFileFilter;
import org.codehaus.plexus.archiver.zip.ZipEntry;
import org.codehaus.plexus.archiver.zip.ZipFile;

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
        archiver.setArchiveFilters( filters );
        for ( int i = 0; i < filteredFiles.length; i++ )
        {
            archiver.addFile( dummyContent, filteredFiles[i] );
        }
        for ( int i = 0; i < unFilteredFiles.length; i++ )
        {
            archiver.addFile( dummyContent, unFilteredFiles[i] );
        }
        archiver.setDestFile( getTestFile( "target/jar-security/test-archive.jar" ) );
        archiver.createArchive();

        // Verify that the fake files were filtered out of the created jar and that
        // the legitimate files were not
        ZipFile zf = new ZipFile( archiver.getDestFile() );
        for ( int i = 0; i < filteredFiles.length; i++ )
        {
            ZipEntry entry = zf.getEntry( filteredFiles[i] );
            assertNull( "Entry was not filtered out: " + filteredFiles[i], entry );
        }
        for ( int i = 0; i < unFilteredFiles.length; i++ )
        {
            ZipEntry entry = zf.getEntry( unFilteredFiles[i] );
            assertNotNull( "Entry was filtered out: " + unFilteredFiles[i], entry );
        }
    }

}
