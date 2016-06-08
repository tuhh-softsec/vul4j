package org.codehaus.plexus.archiver.util;

import java.io.File;
import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.FileSet;

/**
 * Default implementation of {@link FileSet}.
 *
 * @since 1.0-alpha-9
 */
public class DefaultFileSet
    extends AbstractFileSet<DefaultFileSet>
    implements FileSet
{

    private File directory;

    public DefaultFileSet( File directory )
    {
        this.directory = directory;
    }

    public DefaultFileSet()
    {
    }

    /**
     * Sets the file sets base directory.
     */
    public void setDirectory( @Nonnull File directory )
    {
        this.directory = directory;
    }

    @Nonnull public File getDirectory()
    {
        return directory;
    }

    public static DefaultFileSet fileSet( File directory )
    {
        final DefaultFileSet defaultFileSet = new DefaultFileSet( directory );
        return defaultFileSet;
    }

}
