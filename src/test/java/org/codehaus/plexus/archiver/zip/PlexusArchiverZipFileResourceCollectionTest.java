package org.codehaus.plexus.archiver.zip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.components.io.functions.ResourceAttributeSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

public class PlexusArchiverZipFileResourceCollectionTest
    extends PlexusTestCase
{

    public void testFilesWithIllegalHtmlChars()
        throws Exception
    {
        File testZip = new File( getBasedir(), "src/test/resources/archiveWithIllegalHtmlFileName.zip" );
        Set<String> seen = new HashSet<String>();
        seen.add( "AFileThatNeedsHtmlEsc%3F&gt" );
        seen.add( "Afile&lt;Yo&gt;.txt" );
        seen.add( "File With Space.txt" );
        seen.add( "FileWith%.txt" );
        PlexusArchiverZipFileResourceCollection prc = new PlexusArchiverZipFileResourceCollection();
        prc.setFile( testZip );
        final Iterator<PlexusIoResource> entries = prc.getEntries();
        while ( entries.hasNext() )
        {
            final PlexusIoResource next = entries.next();
            assertTrue( next.getName() + "was not present", seen.remove( next.getName() ) );
            final InputStream contents = next.getContents();
            contents.close();
        }
    }

    public void testFileModes()
        throws IOException
    {
        File testZip = new File( getBasedir(), "src/test/resources/zeroFileMode/mixed-file-mode.zip" );
        Map<String, Integer> originalUnixModes = new HashMap<String, Integer>();
        originalUnixModes.put( "platform-fat", -1 );
        originalUnixModes.put( "zero-unix-mode", 0 );
        // ---xrw-r-- the crazy permissions are on purpose so we don't hit some default value
        originalUnixModes.put( "non-zero-unix-mode", 0164 );
        PlexusArchiverZipFileResourceCollection prc = new PlexusArchiverZipFileResourceCollection();
        prc.setFile( testZip );
        Iterator<PlexusIoResource> entries = prc.getEntries();
        while ( entries.hasNext() )
        {
            PlexusIoResource entry = entries.next();
            int entryUnixMode = ( (ResourceAttributeSupplier) entry ).getAttributes().getOctalMode();
            int originalUnixMode = (int) originalUnixModes.get( entry.getName() );
            assertEquals( originalUnixMode, entryUnixMode );
        }
    }

}
