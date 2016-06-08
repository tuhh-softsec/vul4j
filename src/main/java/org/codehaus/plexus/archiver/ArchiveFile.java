package org.codehaus.plexus.archiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * Interface of a zip, or tar file.
 */
public interface ArchiveFile
{

    /**
     * Returns an enumeration with the archive files entries.
     * Any element returned by the enumeration is an instance
     * of {@link org.apache.commons.compress.archivers.ArchiveEntry}.
     */
    public Enumeration<? extends org.apache.commons.compress.archivers.ArchiveEntry> getEntries()
        throws IOException;

    /**
     * Returns an {@link InputStream} with the given entries contents.
     * org.apache.commons.compress.archivers.ArchiveEntry
     */
    InputStream getInputStream( org.apache.commons.compress.archivers.ArchiveEntry entry )
        throws IOException;

}
