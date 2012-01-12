package org.codehaus.plexus.archiver.war;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.BasePlexusArchiverTest;
import org.codehaus.plexus.archiver.ResourceIterator;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.jar.Manifest;
import org.codehaus.plexus.archiver.jar.ManifestException;
import org.codehaus.plexus.util.FileUtils;

import junit.framework.TestCase;

/**
 * @author Kristian Rosenvold
 */
public class WarArchiverTest
    extends PlexusTestCase
{

    private int expected = 8;

    public void testReAddingPlatformSpecificEncoding()
        throws Exception
    {
        WarArchiver archiver = (WarArchiver) lookup( Archiver.ROLE, "war" );
        archiver.setDestFile( new File( getTargetRarFolder(), "test.war" ) );

        File dummyContent = getTestFile( "src/test/resources/folders" );
        archiver.addDirectory(  dummyContent );

        assertEquals( expected, count( archiver.getResources() ) ); // I wonder what the first entry is
        
        File file = getTestFile( "src/test/resources/folders/WEB-INF/web.xml" );
        archiver.setWebxml(  file);

        assertEquals( expected, count( archiver.getResources() ) ); // I wonder what the first entry is

        archiver.createArchive();
        assertTrue( new File( getTargetRarFolder(), "test.war" ).exists() );
    }

    public File getTargetRarFolder()
    {
        return new File( getBasedir(), "/target/wartest/" );
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        // clean output directory and re create it
        if ( getTargetRarFolder().exists() )
        {
            FileUtils.deleteDirectory( getTargetRarFolder() );
        }
    }
    
    private int count(ResourceIterator resourceIterator){
        int i = 0;
        while (resourceIterator.hasNext()){
            i++;
            ArchiveEntry next = resourceIterator.next();
            System.out.print( next.getMode() );
            System.out.println( next.getName() );
        }
        return i;
    }


}
