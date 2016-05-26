package org.codehaus.plexus.archiver.jar;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.util.DefaultArchivedFileSet;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;

public class DirectoryArchiverUnpackJarTest
    extends PlexusTestCase
{

    public static final String[] DEFAULT_INCLUDES_ARRAY = { "**/*" };

    static class IdentityTransformer
        implements InputStreamTransformer
    {
        IdentityTransformer()
        {
        }

        @Nonnull
        public InputStream transform( @Nonnull PlexusIoResource resource, @Nonnull InputStream inputStream )
            throws IOException
        {
            return inputStream;
        }
    }

    public void test_dependency_sets_depSet_unpacked_rdonly()
        throws Exception
    {
        File src = new File( "src/test/resources/unpack_issue.jar" );
        assertTrue( src.exists() );
        DefaultArchivedFileSet afs = DefaultArchivedFileSet.archivedFileSet( src );
        afs.setIncludes( DEFAULT_INCLUDES_ARRAY );
        afs.setExcludes( null );
        afs.setPrefix( "child-1/" );
        afs.setStreamTransformer( new IdentityTransformer() );
        Archiver archiver = (Archiver) lookup( Archiver.ROLE, "dir" );
        archiver.setDefaultDirectoryMode( 0555 );
        archiver.setDirectoryMode( 0555 ); // causes permission denied if bug is not fixed.
        archiver.setDestFile( new File( "target/depset_unpack" ) );
        archiver.addArchivedFileSet( afs, Charset.forName( "UTF-8" ) );
        archiver.createArchive();
        assertTrue( new File( "target/depset_unpack/child-1/META-INF/MANIFEST.MF" ).exists() );

        final Logger logger = new ConsoleLogger( Logger.LEVEL_DEBUG, this.getClass().getName() );

        // make them writeable or mvn clean will fail
        ArchiveEntryUtils.chmod( new File( "target/depset_unpack/child-1/META-INF" ), 0777, logger );
        ArchiveEntryUtils.chmod( new File( "target/depset_unpack/child-1/META-INF/maven" ), 0777, logger );
        ArchiveEntryUtils.chmod( new File( "target/depset_unpack/child-1/META-INF/maven/test" ), 0777, logger );
        ArchiveEntryUtils.chmod( new File( "target/depset_unpack/child-1/META-INF/maven/test/child1" ), 0777, logger );
        ArchiveEntryUtils.chmod( new File( "target/depset_unpack/child-1/assembly-resources" ), 0777, logger );
    }
}
