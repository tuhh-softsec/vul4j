package org.codehaus.plexus.util;

/*
 * Copyright 2007 The Codehaus Foundation.
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Unzip a file.
 *
 * @author costin@dnt.ro
 * @author <a href="mailto:stefan.bodewig@epost.de">Stefan Bodewig</a>
 * @author <a href="mailto:umagesh@codehaus.org">Magesh Umasankar</a>
 * @since Ant 1.1 @ant.task category="packaging" name="unzip" name="unjar"
 *      name="unwar"
 * @version $Id$
 */
public class Expand
{
    private File dest;//req

    private File source;// req

    private boolean overwrite = true;

    /**
     * Do the work.
     *
     * @exception Exception Thrown in unrecoverable error.
     */
    public void execute()
        throws Exception
    {
        expandFile( source, dest );
    }

    /*
     * This method is to be overridden by extending unarchival tasks.
     */
    /**
     * Description of the Method
     */
    protected void expandFile( File srcF, File dir )
        throws Exception
    {
        ZipInputStream zis = null;
        try
        {
            // code from WarExpand
            zis = new ZipInputStream( new FileInputStream( srcF ) );
            ZipEntry ze = null;

            while ( ( ze = zis.getNextEntry() ) != null )
            {
                extractFile( srcF, dir, zis, ze.getName(), new Date( ze.getTime() ), ze.isDirectory() );
            }

            //log("expand complete", Project.MSG_VERBOSE);
        }
        catch ( IOException ioe )
        {
            throw new Exception( "Error while expanding " + srcF.getPath(), ioe );
        }
        finally
        {
            if ( zis != null )
            {
                try
                {
                    zis.close();
                }
                catch ( IOException e )
                {
                }
            }
        }
    }

    /**
     * Description of the Method
     */
    protected void extractFile( File srcF, File dir, InputStream compressedInputStream, String entryName,
                                Date entryDate, boolean isDirectory )
        throws Exception
    {
        File f = FileUtils.resolveFile( dir, entryName );
        try
        {
            if ( !overwrite && f.exists() && f.lastModified() >= entryDate.getTime() )
            {
                return;
            }

            // create intermediary directories - sometimes zip don't add them
            File dirF = f.getParentFile();
            dirF.mkdirs();

            if ( isDirectory )
            {
                f.mkdirs();
            }
            else
            {
                byte[] buffer = new byte[1024];
                int length = 0;
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
                        }
                    }
                }
            }

            f.setLastModified( entryDate.getTime() );
        }
        catch ( FileNotFoundException ex )
        {
            throw new Exception( "Can't extract file " + srcF.getPath(), ex );
        }

    }

    /**
     * Set the destination directory. File will be unzipped into the destination
     * directory.
     *
     * @param d Path to the directory.
     */
    public void setDest( File d )
    {
        this.dest = d;
    }

    /**
     * Set the path to zip-file.
     *
     * @param s Path to zip-file.
     */
    public void setSrc( File s )
    {
        this.source = s;
    }

    /**
     * Should we overwrite files in dest, even if they are newer than the
     * corresponding entries in the archive?
     */
    public void setOverwrite( boolean b )
    {
        overwrite = b;
    }
}
