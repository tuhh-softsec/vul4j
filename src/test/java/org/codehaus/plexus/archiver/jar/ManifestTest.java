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

import org.codehaus.plexus.archiver.ArchiverException;

import java.io.IOException;
import java.io.FileReader;
import java.util.Enumeration;

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 */
public class ManifestTest extends TestCase
{
    public ManifestTest( String testName )
    {
        super( testName );
    }

    public void testManifestReader1()
    {
        try
        {
            Manifest manifest = getManifest( "src/test/resources/manifests/manifest1.mf" );
            String version = manifest.getManifestVersion();
            assertEquals("Manifest was not created with correct version - ", "1.0", version);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
        catch (ManifestException me)
        {
            me.printStackTrace();
            fail( me.getMessage() );
        }
    }

    public void testManifestReader2()
    {
        try
        {
            Manifest manifest = getManifest( "src/test/resources/manifests/manifest2.mf" );
            fail( "Manifest isn't well formed. It must be generate an exception." );
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
        catch (ManifestException me)
        {
            if ( me.getMessage().indexOf("is not valid as it does not contain a name and a value separated by ': '") == -1)
            {
                fail( "Manifest isn't well formed. It must generate an exception." );
            }
            return;
        }
    }

    public void testManifestReader3()
    {
        try
        {
            Manifest manifest = getManifest( "src/test/resources/manifests/manifest3.mf" );
            fail( "Manifest isn't well formed. It must be generate an exception." );
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
        catch (ManifestException me)
        {
            if ( me.getMessage().indexOf("is not valid as it does not contain a name and a value separated by ': '") == -1)
            {
                fail( "Manifest isn't well formed. It must generate an exception." );
            }
            return;
        }
    }

    public void testManifestReader4()
    {
        try
        {
            Manifest manifest = getManifest( "src/test/resources/manifests/manifest4.mf" );
            Enumeration warnings = manifest.getWarnings();
            assertTrue( warnings.hasMoreElements() );
            String warn = (String)warnings.nextElement();
            assertFalse( warnings.hasMoreElements() );
            boolean hasWarning = warn.indexOf("\"Name\" attributes should not occur in the main section") != -1;
            assertEquals("Expected warning about Name in main section", true, hasWarning);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
        catch (ManifestException me)
        {
            me.printStackTrace();
            fail( me.getMessage() );
        }
    }

    public void testManifestReader5()
    {
        try
        {
            Manifest manifest = getManifest( "src/test/resources/manifests/manifest5.mf" );
            fail();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
        catch (ManifestException me)
        {
            boolean hasWarning = me.getMessage().indexOf("Manifest sections should start with a \"Name\" attribute") != -1;
            assertEquals("Expected warning about section not starting with Name: attribute", true, hasWarning);
            return;
        }
    }

    public void testManifestReader6()
    {
        try
        {
            Manifest manifest = getManifest( "src/test/resources/manifests/manifest6.mf" );
            Enumeration warnings = manifest.getWarnings();
            assertTrue( warnings.hasMoreElements() );
            String warn = (String)warnings.nextElement();
            assertFalse( warnings.hasMoreElements() );
            boolean hasWarning = warn.indexOf("Manifest attributes should not start with \"From\"") != -1;
            assertEquals("Expected warning about From: attribute", true, hasWarning);
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            fail( ioe.getMessage() );
        }
        catch (ManifestException me)
        {
            me.printStackTrace();
            fail( me.getMessage() );
        }
    }

    public void testGetDefaultManifest()
    {
        try
        {
            Manifest mf = Manifest.getDefaultManifest();
            assertNotNull(mf);
        }
        catch(ArchiverException ae)
        {
            ae.printStackTrace();
            fail( ae.getMessage() );
        }
    }

    /**
     * Reads a Manifest file.
     */
    private Manifest getManifest(String filename) throws IOException, ManifestException {
        FileReader r = new FileReader(filename);
        try {
            return new Manifest(r);
        } finally {
            r.close();
        }
    }
}