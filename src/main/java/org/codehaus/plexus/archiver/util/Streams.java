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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.WillClose;
import javax.annotation.WillNotClose;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.IOUtil;

public class Streams
{

    public static BufferedInputStream bufferedInputStream( InputStream is )
    {
        return is instanceof BufferedInputStream
                   ? (BufferedInputStream) is
                   : new BufferedInputStream( is, 65536 );

    }

    public static BufferedOutputStream bufferedOutputStream( OutputStream os )
    {
        return os instanceof BufferedOutputStream
                   ? (BufferedOutputStream) os
                   : new BufferedOutputStream( os, 65536 );

    }

    public static byte[] cacheBuffer()
    {
        return new byte[ 8 * 1024 ];
    }

    public static FileInputStream fileInputStream( File file )
        throws FileNotFoundException
    {
        return new FileInputStream( file );
    }

    public static FileInputStream fileInputStream( File file, String operation )
        throws ArchiverException
    {
        try
        {
            return new FileInputStream( file );
        }
        catch ( IOException e )
        {
            throw new ArchiverException(
                "Problem reading input file for " + operation + " " + file.getParent() + ", " + e.getMessage() );

        }
    }

    public static FileOutputStream fileOutputStream( File file )
        throws FileNotFoundException
    {
        return new FileOutputStream( file );
    }

    public static FileOutputStream fileOutputStream( File file, String operation )
        throws ArchiverException
    {
        try
        {
            return new FileOutputStream( file );
        }
        catch ( IOException e )
        {
            throw new ArchiverException(
                "Problem creating output file for " + operation + " " + file.getParent() + ", " + e.getMessage() );

        }
    }

    public static void copyFully( @WillClose InputStream zIn, @WillClose OutputStream out, String gzip )
        throws ArchiverException
    {
        // There must be 1 million libs out there that do this
        try
        {
            copyFullyDontCloseOutput( zIn, out, gzip );
            out.close();
            out = null;
        }
        catch ( final IOException e )
        {
            throw new ArchiverException( "Failure copying.", e );
        }
        finally
        {
            IOUtil.close( out );
        }
    }

    public static void copyFullyDontCloseOutput( @WillClose InputStream zIn, @WillNotClose OutputStream out,
                                                 String gzip )
        throws ArchiverException
    {
        // There must be 1 million libs out there that do this
        try
        {
            byte[] buffer = cacheBuffer();
            int count = 0;
            do
            {
                try
                {
                    out.write( buffer, 0, count );
                }
                catch ( IOException e )
                {
                    throw new ArchiverException(
                        "Problem writing to output in " + gzip + " operation " + e.getMessage() );

                }
                count = zIn.read( buffer, 0, buffer.length );
            }
            while ( count != -1 );
            zIn.close();
            zIn = null;
        }
        catch ( IOException e )
        {
            throw new ArchiverException(
                "Problem reading from source file in " + gzip + " operation " + e.getMessage() );

        }
        finally
        {
            IOUtil.close( zIn );
        }
    }

}
