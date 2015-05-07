package org.codehaus.plexus.archiver.zip;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.components.io.resources.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PlexusIoZipFileResourceCollectionTest
    extends PlexusTestCase
{

    public void testNamelessRootFolder()
        throws Exception
    {
        PlexusIoZipFileResourceCollection resourceCollection = new PlexusIoZipFileResourceCollection();
        resourceCollection.setFile( getTestFile( "src/test/jars/namelessrootfolder.jar" ) );
        Iterator iterator = resourceCollection.getResources();
        PlexusIoURLResource entry = (PlexusIoURLResource) iterator.next();
        assertEquals( "/dummy.txt", entry.getName() );
        final URL url = entry.getURL();
        BufferedReader d = new BufferedReader( new InputStreamReader( entry.getContents() ) );
        assertEquals( "dummy content", d.readLine() );
    }

    public void testDescriptionForError()
        throws Exception
    {
        PlexusIoZipFileResourceCollection resourceCollection = new PlexusIoZipFileResourceCollection();
        resourceCollection.setFile( getTestFile( "src/test/jars/namelessrootfolder.jar" ) );
        Iterator<PlexusIoResource> iterator = resourceCollection.getResources();
        PlexusIoURLResource entry = (PlexusIoURLResource) iterator.next();
        final URL url = entry.getURL();
        String descriptionForError = entry.getDescriptionForError( url );
        assertTrue( descriptionForError.endsWith( "namelessrootfolder.jar!//dummy.txt" ) );
    }

    public void testFilesWithIllegalHtmlChars()
        throws Exception
    {
        File testZip = new File( getBasedir(), "src/test/resources/bogusManifest.zip" );
       PlexusIoZipFileResourceCollection
            prc = new PlexusIoZipFileResourceCollection();
        prc.setFile( testZip );
        final Iterator<PlexusIoResource> entries = prc.getEntries();
        while ( entries.hasNext() )
        {
            final PlexusIoResource next = entries.next();
            if ( next.getName().endsWith( "MANIFEST.MF" ) )
            {
                final InputStream contents1 = next.getContents();
                final String manifest = IOUtils.toString( contents1, "UTF-8" );
                assertTrue( manifest.contains( "bogs=fus" ) );
                contents1.close();
            }

        }
    }

    public void testFilesThatAreNotThere()
        throws Exception
    {
        File testZip = new File( getBasedir(), "src/test/resources/archiveWithIllegalHtmlFileName.zip" );
        Set<String> seen = new HashSet<String>();
        seen.add( "AFileThatNeedsHtmlEsc%3F&gt" );
        seen.add( "Afile&lt;Yo&gt;.txt" );
        seen.add( "File With Space.txt" );
        seen.add( "FileWith%.txt" );
        PlexusIoZipFileResourceCollection
            prc = new PlexusIoZipFileResourceCollection();
        prc.setFile( testZip );
        final Iterator<PlexusIoResource> entries = prc.getEntries();
        while ( entries.hasNext() )
        {
            final PlexusIoResource next = entries.next();
            assertTrue( next.getName() + "was not present", seen.remove( next.getName() ) );
            final URL url = next.getURL();
            final InputStream contents = next.getContents();
            contents.close();
        }
    }

}
