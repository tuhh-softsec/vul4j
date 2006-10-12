package org.codehaus.plexus.archiver.zip;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.codehaus.plexus.archiver.AbstractUnArchiver;
import org.codehaus.plexus.archiver.ArchiveFilterException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;

/**
 * @author <a href="mailto:evenisse@codehaus.org">Emmanuel Venisse</a>
 * @version $Id$
 */
public abstract class AbstractZipUnArchiver
    extends AbstractUnArchiver
{
    private static final String NATIVE_ENCODING = "native-encoding";

    private String encoding = "UTF8";

    public AbstractZipUnArchiver()
    {
    }

    public AbstractZipUnArchiver( File sourceFile )
    {
        super( sourceFile );
    }

    /**
     * Sets the encoding to assume for file names and comments.
     * <p/>
     * <p>Set to <code>native-encoding</code> if you want your
     * platform's native encoding, defaults to UTF8.</p>
     */
    public void setEncoding( String encoding )
    {
        if ( NATIVE_ENCODING.equals( encoding ) )
        {
            encoding = null;
        }
        this.encoding = encoding;
    }

    protected void execute()
        throws ArchiverException
    {
        getLogger().info( "Expanding: " + getSourceFile() + " into " + getDestDirectory() );
        ZipFile zf = null;
        try
        {
            zf = new ZipFile( getSourceFile(), encoding );
            Enumeration e = zf.getEntries();
            while ( e.hasMoreElements() )
            {
                ZipEntry ze = (ZipEntry) e.nextElement();
                extractFileIfIncluded( getSourceFile(), getDestDirectory(), zf.getInputStream( ze ), ze.getName(),
                                       new Date( ze.getTime() ), ze.isDirectory() );
            }

            getLogger().debug( "expand complete" );
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Error while expanding " + getSourceFile().getAbsolutePath(), ioe );
        }
        finally
        {
            if ( zf != null )
            {
                try
                {
                    zf.close();
                }
                catch ( IOException e )
                {
                    //ignore
                }
            }
        }
    }

    private void extractFileIfIncluded( File sourceFile,
                                        File destDirectory,
                                        InputStream inputStream,
                                        String name,
                                        Date time,
                                        boolean isDirectory )
        throws IOException, ArchiverException
    {
        try
        {
            if ( include( inputStream, name ) )
            {
                extractFile( sourceFile, destDirectory, inputStream, name, time, isDirectory );
            }
        }
        catch ( ArchiveFilterException e )
        {
            throw new ArchiverException( "Error verifying \'" + name + "\' for inclusion: " + e.getMessage(), e );
        }
    }

    protected void extractFile( File srcF,
                                File dir,
                                InputStream compressedInputStream,
                                String entryName,
                                Date entryDate,
                                boolean isDirectory )
        throws IOException
    {
        File f = FileUtils.resolveFile( dir, entryName );

        try
        {
            if ( !isOverwrite() && f.exists() && f.lastModified() >= entryDate.getTime() )
            {
                return;
            }

            // create intermediary directories - sometimes zip don't add them
            File dirF = f.getParentFile();
            if ( dirF != null )
            {
                dirF.mkdirs();
            }

            if ( isDirectory )
            {
                f.mkdirs();
            }
            else
            {
                byte[] buffer = new byte[1024];
                int length;
                FileOutputStream fos = null;
                try
                {
                    fos = new FileOutputStream( f );

                    while ( ( length = compressedInputStream.read( buffer ) ) >= 0 )
                    {
                        fos.write( buffer, 0, length );
                    }

                    fos.close();
                    fos = null;
                }
                finally
                {
                    if ( fos != null )
                    {
                        try
                        {
                            fos.close();
                        }
                        catch ( IOException e )
                        {
                            // ignore
                        }
                    }
                }
            }

            f.setLastModified( entryDate.getTime() );
        }
        catch ( FileNotFoundException ex )
        {
            getLogger().warn( "Unable to expand to file " + f.getPath() );
        }
    }

    protected void execute( String path,
                         File outputDirectory )
        throws ArchiverException
    {
        ZipFile zipFile = null;

        try
        {
            zipFile = new ZipFile( getSourceFile(), encoding );

            Enumeration e = zipFile.getEntries();

            while ( e.hasMoreElements() )
            {
                ZipEntry ze = (ZipEntry) e.nextElement();

                if ( ze.getName().startsWith( path ) )
                {
                    extractFileIfIncluded( getSourceFile(), outputDirectory, zipFile.getInputStream( ze ), ze.getName(),
                                           new Date( ze.getTime() ), ze.isDirectory() );
                }
            }
        }
        catch ( IOException ioe )
        {
            throw new ArchiverException( "Error while expanding " + getSourceFile().getAbsolutePath(), ioe );
        }
        finally
        {
            if ( zipFile != null )
            {
                try
                {
                    zipFile.close();
                }
                catch ( IOException e )
                {
                    //ignore
                }
            }
        }
    }
}
