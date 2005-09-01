package org.codehaus.plexus.archiver.jar;

/*
 * Copyright  2001,2004 The Apache Software Foundation
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

import org.codehaus.plexus.PlexusTestCase;

import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Emmanuel Venisse
 * @version $Id$
 */
public class ManifestTest
    extends PlexusTestCase
{
    public void testManifestReader1()
        throws Exception
    {
        Manifest manifest = getManifest( "src/test/resources/manifests/manifest1.mf" );
        String version = manifest.getManifestVersion();
        assertEquals( "Manifest was not created with correct version - ", "1.0", version );
    }

    public void testManifestReader2()
        throws Exception
    {
        try
        {
            getManifest( "src/test/resources/manifests/manifest2.mf" );
            fail( "Manifest isn't well formed. It must be generate an exception." );
        }
        catch ( ManifestException me )
        {
            if ( me.getMessage().indexOf(
                "is not valid as it does not contain a name and a value separated by ': '" ) == -1 )
            {
                fail( "Manifest isn't well formed. It must generate an exception." );
            }
        }
    }

    public void testManifestReader3()
        throws Exception
    {
        try
        {
            getManifest( "src/test/resources/manifests/manifest3.mf" );
            fail( "Manifest isn't well formed. It must be generate an exception." );
        }
        catch ( ManifestException me )
        {
            if ( me.getMessage().indexOf(
                "is not valid as it does not contain a name and a value separated by ': '" ) == -1 )
            {
                fail( "Manifest isn't well formed. It must generate an exception." );
            }
        }
    }

    public void testManifestReader4()
        throws Exception
    {
        Manifest manifest = getManifest( "src/test/resources/manifests/manifest4.mf" );
        Enumeration warnings = manifest.getWarnings();
        assertTrue( warnings.hasMoreElements() );
        String warn = (String) warnings.nextElement();
        assertFalse( warnings.hasMoreElements() );
        boolean hasWarning = warn.indexOf( "\"Name\" attributes should not occur in the main section" ) != -1;
        assertEquals( "Expected warning about Name in main section", true, hasWarning );
    }

    public void testManifestReader5()
        throws Exception
    {
        try
        {
            getManifest( "src/test/resources/manifests/manifest5.mf" );
            fail();
        }
        catch ( ManifestException me )
        {
            boolean hasWarning = me.getMessage().indexOf(
                "Manifest sections should start with a \"Name\" attribute" ) != -1;
            assertEquals( "Expected warning about section not starting with Name: attribute", true, hasWarning );
        }
    }

    public void testManifestReader6()
        throws Exception
    {
        Manifest manifest = getManifest( "src/test/resources/manifests/manifest6.mf" );
        Enumeration warnings = manifest.getWarnings();
        assertTrue( warnings.hasMoreElements() );
        String warn = (String) warnings.nextElement();
        assertFalse( warnings.hasMoreElements() );
        boolean hasWarning = warn.indexOf( "Manifest attributes should not start with \"From\"" ) != -1;
        assertEquals( "Expected warning about From: attribute", true, hasWarning );
    }

    public void testGetDefaultManifest()
        throws Exception
    {
        Manifest mf = Manifest.getDefaultManifest();
        assertNotNull( mf );
    }

    /**
     * Reads a Manifest file.
     */
    private Manifest getManifest( String filename )
        throws IOException, ManifestException
    {
        FileReader r = new FileReader( getTestFile( filename ) );

        try
        {
            return new Manifest( r );
        }
        finally
        {
            r.close();
        }
    }
}