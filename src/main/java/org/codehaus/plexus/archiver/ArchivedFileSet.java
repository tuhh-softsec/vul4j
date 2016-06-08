package org.codehaus.plexus.archiver;

import java.io.File;
import javax.annotation.CheckForNull;

/**
 * A file set, which consists of the files and directories in
 * an archive.
 *
 * @since 1.0-alpha-9
 */
public interface ArchivedFileSet
    extends BaseFileSet
{

    /**
     * Returns the archive file.
     */
    @CheckForNull
    File getArchive();

}
