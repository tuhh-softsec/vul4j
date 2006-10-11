package org.codehaus.plexus.archiver.util;

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

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.logging.AbstractLogEnabled;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @version $Revision$ $Date$
 */
public abstract class Compressor
    extends AbstractLogEnabled
{
    private File destFile;

    private File sourceFile;

    /**
     * the required destination file.
     *
     * @param compressFile
     */
    public void setDestFile( File compressFile )
    {
        this.destFile = compressFile;
    }

    public File getDestFile()
    {
        return destFile;
    }

    /**
     * the file to compress; required.
     *
     * @param srcFile
     */
    public void setSourceFile( File srcFile )
    {
        this.sourceFile = srcFile;
    }

    public File getSourceFile()
    {
        return sourceFile;
    }

    /**
     * validation routine
     *
     * @throws ArchiverException if anything is invalid
     */
    private void validate()
        throws ArchiverException
    {
        if ( destFile == null )
        {
            throw new ArchiverException( "Destination file attribute is required" );
        }

        if ( destFile.isDirectory() )
        {
            throw new ArchiverException( "Destination file attribute must not "
                                         + "represent a directory!" );
        }

        if ( sourceFile == null )
        {
            throw new ArchiverException( "Source file attribute is required" );
        }

        if ( sourceFile.isDirectory() )
        {
            throw new ArchiverException( "Source file attribute must not "
                                         + "represent a directory!" );
        }
    }

    /**
     * validate, then hand off to the subclass
     *
     * @throws BuildException
     */
    public void execute()
        throws ArchiverException
    {
        validate();

        try
        {
            if ( !sourceFile.exists() )
            {
//                getLogger().info( "Nothing to do: " + sourceFile.getAbsolutePath()
//                    + " doesn't exist." );
            }
            else if ( destFile.lastModified() < sourceFile.lastModified() )
            {
//                getLogger().info( "Building: " + destFile.getAbsolutePath() );
                compress();
            }
            else
            {
//                getLogger().info( "Nothing to do: " + destFile.getAbsolutePath()
//                    + " is up to date." );
            }
        }
        finally
        {
            close();
        }
    }

    /**
     * compress a stream to an output stream
     *
     * @param in
     * @param zOut
     * @throws IOException
     */
    private void compressFile( InputStream in, OutputStream zOut )
        throws IOException
    {
        byte[] buffer = new byte[8 * 1024];
        int count = 0;
        do
        {
            zOut.write( buffer, 0, count );
            count = in.read( buffer, 0, buffer.length );
        }
        while ( count != -1 );
    }

    /**
     * compress a file to an output stream
     *
     * @param file
     * @param zOut
     * @throws IOException
     */
    protected void compressFile( File file, OutputStream zOut )
        throws IOException
    {
        FileInputStream fIn = new FileInputStream( file );
        try
        {
            compressFile( fIn, zOut );
        }
        finally
        {
            fIn.close();
        }
    }

    /**
     * subclasses must implement this method to do their compression
     * 
     * this is public so the process of compression and closing can be dealt with separately.
     */
    public abstract void compress()
        throws ArchiverException;
    
    /**
     * subclasses must implement this method to cleanup after compression
     * 
     * this is public so the process of compression and closing can be dealt with separately.
     */
    public abstract void close();
}
