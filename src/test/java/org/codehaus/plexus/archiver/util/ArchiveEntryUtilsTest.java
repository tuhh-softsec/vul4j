package org.codehaus.plexus.archiver.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import org.codehaus.plexus.components.io.attributes.Java7FileAttributes;
import org.codehaus.plexus.components.io.attributes.Java7Reflector;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.codehaus.plexus.util.Os;
import junit.framework.TestCase;

public class ArchiveEntryUtilsTest extends TestCase
{

    public void testChmodForFileWithDollarPLXCOMP164() throws Exception
    {

        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            return;
        }
        File temp = File.createTempFile( "A$A", "BB$" );
        ArchiveEntryUtils.chmod( temp, 0770, new ConsoleLogger( org.codehaus.plexus.logging.Logger.LEVEL_DEBUG, "foo" ),
                                 false );
        assert0770( temp );
    }

    public void testChmodWithJava7() throws Exception
    {
        if ( !Java7Reflector.isAtLeastJava7() )
        {
            return; // Require at least java7
        }
        if ( Os.isFamily( Os.FAMILY_WINDOWS ) )
        {
            return;
        }

        File temp = File.createTempFile( "D$D", "BB$" );
        ArchiveEntryUtils.
            chmod( temp, 0770, new ConsoleLogger( org.codehaus.plexus.logging.Logger.LEVEL_DEBUG, "foo" ) );
        assert0770( temp );
    }

    private void assert0770( File temp ) throws IOException
    {
        Java7FileAttributes j7 = new Java7FileAttributes( temp, new HashMap<Integer, String>(),
                                                          new HashMap<Integer, String>() );
        assertTrue( j7.isGroupExecutable() );
        assertTrue( j7.isGroupReadable() );
        assertTrue( j7.isGroupWritable() );

        assertFalse( j7.isWorldExecutable() );
        assertFalse( j7.isWorldReadable() );
        assertFalse( j7.isWorldWritable() );
    }

}
