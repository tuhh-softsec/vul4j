package org.codehaus.plexus.archiver;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.jar.JarArchiver;

import java.io.File;
import java.util.jar.JarFile;

/** @author Jason van Zyl */
public class DotDirectiveArchiveFinalizerTest
    extends PlexusTestCase
{
    public void testDotDirectiveArchiveFinalizer()
        throws Exception
    {
        DotDirectiveArchiveFinalizer ddaf =
            new DotDirectiveArchiveFinalizer( new File( getBasedir(), "src/test/dotfiles" ) );

        JarArchiver archiver = new JarArchiver();

        File jarFile = new File( getBasedir(), "target/dotfiles.jar" );

        archiver.setDestFile( jarFile );

        archiver.addArchiveFinalizer( ddaf );

        archiver.createArchive();

        JarFile jar = new JarFile( jarFile );

        assertNotNull( jar.getEntry( "LICENSE.txt" ) );

        assertNotNull( jar.getEntry( "NOTICE.txt" ) );

        assertNotNull( jar.getEntry( "META-INF/maven/NOTICE.txt" ) );

        assertNotNull( jar.getEntry( "META-INF/maven/NOTICE.txt" ) );
    }
}
