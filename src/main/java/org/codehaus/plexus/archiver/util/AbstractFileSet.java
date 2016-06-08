/*
 * Copyright 2014 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.util;

import javax.annotation.Nonnull;
import org.codehaus.plexus.archiver.BaseFileSet;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;

/**
 * Default implementation of {@link BaseFileSet}.
 *
 * @since 1.0-alpha-9
 */
public abstract class AbstractFileSet<T extends AbstractFileSet>
    implements BaseFileSet
{

    private String prefix;

    private String[] includes;

    private String[] excludes;

    private FileSelector[] fileSelectors;

    private boolean caseSensitive = true;

    private boolean usingDefaultExcludes = true;

    private boolean includingEmptyDirectories = true;

    private InputStreamTransformer streamTransformer = null;

    /**
     * Sets a string of patterns, which excluded files
     * should match.
     */
    public void setExcludes( String[] excludes )
    {
        this.excludes = excludes;
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public boolean isIncludingEmptyDirectories()
    {
        return includingEmptyDirectories;
    }

    public T prefixed( String prefix )
    {
        setPrefix( prefix );
        return (T) this;
    }

    public T include( String[] includes )
    {
        setIncludes( includes );
        return (T) this;
    }

    public T exclude( String[] excludes )
    {
        setExcludes( excludes );
        return (T) this;
    }

    public T includeExclude( String[] includes, String[] excludes )
    {
        return (T) include( includes ).exclude( excludes );
    }

    public T includeEmptyDirs( boolean includeEmptyDirectories )
    {
        setIncludingEmptyDirectories( includeEmptyDirectories );
        return (T) this;
    }

    public void setStreamTransformer( @Nonnull InputStreamTransformer streamTransformer )
    {
        this.streamTransformer = streamTransformer;
    }

    @Override
    public InputStreamTransformer getStreamTransformer()
    {
        return streamTransformer;
    }

}
