package org.codehaus.plexus.archiver.util;

import org.codehaus.plexus.archiver.BaseFileSet;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;


/**
 * Default implementation of {@link BaseFileSet}.
 * @since 1.0-alpha-9
 */
public abstract class AbstractFileSet implements BaseFileSet
{
    private String prefix;

    private String[] includes;

    private String[] excludes;

    private FileSelector[] fileSelectors;

    private boolean caseSensitive = true;

    private boolean usingDefaultExcludes = true;

    private boolean includingEmptyDirectories = true;

    /**
     * Sets a string of patterns, which excluded files
     * should match.
     */
    public void setExcludes( String[] excludes )
    {
        this.excludes = excludes;
    }

    public String[] getExcludes()
    {
        return excludes;
    }

    /**
     * Sets a set of file selectors, which should be used
     * to select the included files.
     */
    public void setFileSelectors( FileSelector[] fileSelectors )
    {
        this.fileSelectors = fileSelectors;
    }

    public FileSelector[] getFileSelectors()
    {
        return fileSelectors;
    }

    /**
     * Sets a string of patterns, which included files
     * should match.
     */
    public void setIncludes( String[] includes )
    {
        this.includes = includes;
    }

    public String[] getIncludes()
    {
        return includes;
    }

    /**
     * Sets the prefix, which the file sets contents shall
     * have.
     */
    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }

    public String getPrefix()
    {
        return prefix;
    }

    /**
     * Sets, whether the include/exclude patterns are
     * case sensitive. Defaults to true.
     */
    public void setCaseSensitive( boolean caseSensitive )
    {
        this.caseSensitive = caseSensitive;
    }

    public boolean isCaseSensitive()
    {
        return caseSensitive;
    }

    /**
     * Sets, whether the default excludes are being
     * applied. Defaults to true.
     */
    public void setUsingDefaultExcludes( boolean usingDefaultExcludes )
    {
        this.usingDefaultExcludes = usingDefaultExcludes;
    }

    public boolean isUsingDefaultExcludes()
    {
        return usingDefaultExcludes;
    }

    /**
     * Sets, whether empty directories are being included. Defaults
     * to true.
     */
    public void setIncludingEmptyDirectories( boolean includingEmptyDirectories )
    {
        this.includingEmptyDirectories = includingEmptyDirectories;
    }

    public boolean isIncludingEmptyDirectories()
    {
        return includingEmptyDirectories;
    }
}
