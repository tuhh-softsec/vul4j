package org.codehaus.plexus.archiver.util;

import java.io.File;

import org.codehaus.plexus.archiver.ArchivedFileSet;


/**
 * Default implementation of {@link ArchivedFileSet}.
 * @since 1.0-alpha-9
 */
public class DefaultArchivedFileSet extends AbstractFileSet implements ArchivedFileSet
{
    private File archive;

    /**
     * Sets the file sets archive.
     */
    public void setArchive( File archive )
    {
        this.archive = archive;
    }

    public File getArchive()
    {
        return archive;
    }
}
