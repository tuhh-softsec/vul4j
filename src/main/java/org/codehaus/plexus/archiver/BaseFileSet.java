package org.codehaus.plexus.archiver;

import javax.annotation.CheckForNull;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;

/**
 * A file set is a set of files, which may be added to an
 * archive.
 *
 * @since 1.0-alpha-9
 */
public interface BaseFileSet
{

    /**
     * Returns the prefix, which the file sets contents shall
     * have.
     */
    @CheckForNull
    String getPrefix();

    /**
     * Returns a string of patterns, which included files
     * should match.
     */
    @CheckForNull
    String[] getIncludes();

    /**
     * Returns a string of patterns, which excluded files
     * should match.
     */
    @CheckForNull
    String[] getExcludes();

    /**
     * Returns, whether the include/exclude patterns are
     * case sensitive.
     */
    boolean isCaseSensitive();

    /**
     * Returns, whether the default excludes are being
     * applied.
     */
    boolean isUsingDefaultExcludes();

    /**
     * Returns, whether empty directories are being included.
     */
    boolean isIncludingEmptyDirectories();

    /**
     * Returns a set of file selectors, which should be used
     * to select the included files.
     */
    @CheckForNull
    FileSelector[] getFileSelectors();

    /**
     * Returns the InputStreamTransformers that can be applied to this fileset
     *
     * @return The transformers.
     */
    InputStreamTransformer getStreamTransformer();

}
