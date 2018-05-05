/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.components.io.attributes.SymlinkUtils;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @todo there should really be constructors which take the source file.
 */
public abstract class AbstractUnArchiver
    extends AbstractLogEnabled
    implements UnArchiver, FinalizerEnabled
{

    private File destDirectory;

    private File destFile;

    private File sourceFile;

    private boolean overwrite = true;

    private List finalizers;

    private FileSelector[] fileSelectors;

    /**
     * since 2.3 is on by default
     *
     * @since 1.1
     */
    private boolean useJvmChmod = true;

    /**
     * @since 1.1
     */
    private boolean ignorePermissions = false;

    public AbstractUnArchiver()
    {
        // no op
    }

    public AbstractUnArchiver( final File sourceFile )
    {
        this.sourceFile = sourceFile;
    }

    @Override
    public File getDestDirectory()
    {
        return destDirectory;
    }

    @Override
    public void setDestDirectory( final File destDirectory )
    {
        this.destDirectory = destDirectory;
    }

    @Override
    public File getDestFile()
    {
        return destFile;
    }

    @Override
    public void setDestFile( final File destFile )
    {
        this.destFile = destFile;
    }

    @Override
    public File getSourceFile()
    {
        return sourceFile;
    }

    @Override
    public void setSourceFile( final File sourceFile )
    {
        this.sourceFile = sourceFile;
    }

    @Override
    public boolean isOverwrite()
    {
        return overwrite;
    }

    @Override
    public void setOverwrite( final boolean b )
    {
        overwrite = b;
    }

    @Override
    public final void extract()
        throws ArchiverException
    {
        validate();
        execute();
        runArchiveFinalizers();
    }

    @Override
    public final void extract( final String path, final File outputDirectory )
        throws ArchiverException
    {
        validate( path, outputDirectory );
        execute( path, outputDirectory );
        runArchiveFinalizers();
    }

    @Override
    public void addArchiveFinalizer( final ArchiveFinalizer finalizer )
    {
        if ( finalizers == null )
        {
            finalizers = new ArrayList();
        }

        finalizers.add( finalizer );
    }

    @Override
    public void setArchiveFinalizers( final List archiveFinalizers )
    {
        finalizers = archiveFinalizers;
    }

    private void runArchiveFinalizers()
        throws ArchiverException
    {
        if ( finalizers != null )
        {
            for ( Object finalizer1 : finalizers )
            {
                final ArchiveFinalizer finalizer = (ArchiveFinalizer) finalizer1;

                finalizer.finalizeArchiveExtraction( this );
            }
        }
    }

    protected void validate( final String path, final File outputDirectory )
    {
    }

    protected void validate()
        throws ArchiverException
    {
        if ( sourceFile == null )
        {
            throw new ArchiverException( "The source file isn't defined." );
        }

        if ( sourceFile.isDirectory() )
        {
            throw new ArchiverException( "The source must not be a directory." );
        }

        if ( !sourceFile.exists() )
        {
            throw new ArchiverException( "The source file " + sourceFile + " doesn't exist." );
        }

        if ( destDirectory == null && destFile == null )
        {
            throw new ArchiverException( "The destination isn't defined." );
        }

        if ( destDirectory != null && destFile != null )
        {
            throw new ArchiverException( "You must choose between a destination directory and a destination file." );
        }

        if ( destDirectory != null && !destDirectory.isDirectory() )
        {
            destFile = destDirectory;
            destDirectory = null;
        }

        if ( destFile != null && destFile.isDirectory() )
        {
            destDirectory = destFile;
            destFile = null;
        }
    }

    @Override
    public void setFileSelectors( final FileSelector[] fileSelectors )
    {
        this.fileSelectors = fileSelectors;
    }

    @Override
    public FileSelector[] getFileSelectors()
    {
        return fileSelectors;
    }

    protected boolean isSelected( final String fileName, final PlexusIoResource fileInfo )
        throws ArchiverException
    {
        if ( fileSelectors != null )
        {
            for ( FileSelector fileSelector : fileSelectors )
            {
                try
                {

                    if ( !fileSelector.isSelected( fileInfo ) )
                    {
                        return false;
                    }
                }
                catch ( final IOException e )
                {
                    throw new ArchiverException(
                        "Failed to check, whether " + fileInfo.getName() + " is selected: " + e.getMessage(), e );
                }
            }
        }
        return true;
    }

    protected abstract void execute()
        throws ArchiverException;

    protected abstract void execute( String path, File outputDirectory )
        throws ArchiverException;

    /**
     * @since 1.1
     */
    @Override
    public boolean isUseJvmChmod()
    {
        return useJvmChmod;
    }

    /**
     * <b>jvm chmod won't set group level permissions !</b>
     *
     * @since 1.1
     */
    @Override
    public void setUseJvmChmod( final boolean useJvmChmod )
    {
        this.useJvmChmod = useJvmChmod;
    }

    /**
     * @since 1.1
     */
    @Override
    public boolean isIgnorePermissions()
    {
        return ignorePermissions;
    }

    /**
     * @since 1.1
     */
    @Override
    public void setIgnorePermissions( final boolean ignorePermissions )
    {
        this.ignorePermissions = ignorePermissions;
    }

    protected void extractFile( final File srcF, final File dir, final InputStream compressedInputStream,
                                final String entryName, final Date entryDate, final boolean isDirectory,
                                final Integer mode, String symlinkDestination )
        throws IOException, ArchiverException
    {
        // Hmm. Symlinks re-evaluate back to the original file here. Unsure if this is a good thing...
        final File f = FileUtils.resolveFile( dir, entryName );

        // Make sure that the resolved path of the extracted file doesn't escape the destination directory
        String canonicalDirPath = dir.getCanonicalPath();
        String canonicalDestPath = f.getCanonicalPath();

        if ( !canonicalDestPath.startsWith( canonicalDirPath ) )
        {
            throw new ArchiverException( "Entry is outside of the target directory (" + entryName + ")" );
        }

        try
        {
            if ( !isOverwrite() && f.exists() && ( f.lastModified() >= entryDate.getTime() ) )
            {
                return;
            }

            // create intermediary directories - sometimes zip don't add them
            final File dirF = f.getParentFile();
            if ( dirF != null )
            {
                dirF.mkdirs();
            }

            if ( !StringUtils.isEmpty( symlinkDestination ) )
            {
                SymlinkUtils.createSymbolicLink( f, new File( symlinkDestination ) );
            }
            else if ( isDirectory )
            {
                f.mkdirs();
            }
            else
            {
                OutputStream out = null;
                try
                {
                    out = new FileOutputStream( f );

                    IOUtil.copy( compressedInputStream, out );
                    out.close();
                    out = null;
                }
                finally
                {
                    IOUtil.close( out );
                }
            }

            f.setLastModified( entryDate.getTime() );

            if ( !isIgnorePermissions() && mode != null && !isDirectory )
            {
                ArchiveEntryUtils.chmod( f, mode );
            }
        }
        catch ( final FileNotFoundException ex )
        {
            getLogger().warn( "Unable to expand to file " + f.getPath() );
        }
    }

}
