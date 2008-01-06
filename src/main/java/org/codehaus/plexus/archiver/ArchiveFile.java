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
     * Interfave of a archive file entry. An entry may be a file,
     * or directory.
     */
    public interface Entry
    {
        /**
         * Returns the entries name.
         */
        String getName();

        /**
         * Returns, whether the entry is a directory.
         */
        boolean isDirectory();

        /**
         * Returns the time of the entries last modification.
         * @return Modification time, or -1, if unknown.
         */
        long getLastModificationTime();

        /**
         * Returns the entries size.
         * @return File size; undefined for directories.
         */
        long getSize();
    }

    /**
     * Returns an enumeration with the archive files entries.
     * Any element returned by the enumeration is an instance
     * of {@link Entry}.
     */
    public Enumeration getEntries() throws IOException;

    /**
     * Returns an {@link InputStream} with the given entries contents.
     */
    InputStream getInputStream(Entry entry) throws IOException;
}
