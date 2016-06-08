package org.codehaus.plexus.archiver;

import java.io.File;

/**
 * A file set, which consists of the files and directories in
 * a common base directory.
 *
 * @since 1.0-alpha-9
 */
public interface FileSet
    extends BaseFileSet
{

    /**
     * Returns the file sets base directory.
     */
    File getDirectory();

}
