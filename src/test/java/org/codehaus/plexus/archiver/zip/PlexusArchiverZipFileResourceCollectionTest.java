package org.codehaus.plexus.archiver.zip;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PlexusArchiverZipFileResourceCollectionTest
    extends PlexusTestCase
{

    public void testFilesWithIllegalHtmlChars()
        throws Exception
    {
        File testZip = new File( getBasedir(), "src/test/resources/archiveWithIllegalHtmlFileName.zip" );
        Set<String> seen = new HashSet<String>(  );
        seen.add( "AFileThatNeedsHtmlEsc%3F&gt" );
        seen.add( "Afile&lt;Yo&gt;.txt" );
        seen.add( "File With Space.txt" );
        seen.add( "FileWith%.txt" );
        PlexusArchiverZipFileResourceCollection prc = new PlexusArchiverZipFileResourceCollection();
        prc.setFile( testZip );
        final Iterator<PlexusIoResource> entries = prc.getEntries();
        while (entries.hasNext()){
            final PlexusIoResource next = entries.next();
            assertTrue( next.getName() + "was not present", seen.remove( next.getName() ) );
            final InputStream contents = next.getContents();
            contents.close();
        }
    }

}