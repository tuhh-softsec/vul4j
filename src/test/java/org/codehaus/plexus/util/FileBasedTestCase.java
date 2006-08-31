package org.codehaus.plexus.util;

/*
 * The MIT License
 *
 * Copyright (c) 2004, The Codehaus
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.codehaus.plexus.util.FileUtils;

/**
 * Base class for testcases doing tests with files.
 *
 * @author Jeremias Maerki
 */
public abstract class FileBasedTestCase
    extends TestCase
{
    private static File testDir;

    public static File getTestDirectory()
    {
        if ( testDir == null )
        {
            testDir = ( new File( "target/test/io/" ) ).getAbsoluteFile();
        }
        return testDir;
    }

    protected byte[] createFile( final File file, final long size )
        throws IOException
    {
        if ( !file.getParentFile().exists() )
        {
            throw new IOException( "Cannot create file " + file + " as the parent directory does not exist" );
        }

        byte[] data = generateTestData( size );

        final BufferedOutputStream output = new BufferedOutputStream( new java.io.FileOutputStream( file ) );

        try
        {
            output.write( data );

            return data;
        }
        finally
        {
            output.close();
        }
    }

    protected byte[] generateTestData( final long size )
    {
        try
        {
            ByteArrayOutputStream baout = new ByteArrayOutputStream();
            generateTestData( baout, size );
            return baout.toByteArray();
        }
        catch ( IOException ioe )
        {
            throw new RuntimeException( "This should never happen: " + ioe.getMessage() );
        }
    }

    protected void generateTestData( final OutputStream out, final long size )
        throws IOException
    {
        for ( int i = 0; i < size; i++ )
        {
            //output.write((byte)'X');

            // nice varied byte pattern compatible with Readers and Writers
            out.write( (byte) ( ( i % 127 ) + 1 ) );
        }
    }

    protected File newFile( String filename ) throws IOException
    {
        final File destination = new File( getTestDirectory(), filename );
        /*
        assertTrue( filename + "Test output data file shouldn't previously exist",
                    !destination.exists() );
        */
        if ( destination.exists() )
        {
            FileUtils.forceDelete( destination );
        }
        return destination;
    }

    protected void checkFile( final File file, final File referenceFile )
        throws Exception
    {
        assertTrue( "Check existence of output file", file.exists() );
        assertEqualContent( referenceFile, file );
    }

    protected void checkWrite( final OutputStream output ) throws Exception
    {
        try
        {
            new java.io.PrintStream( output ).write( 0 );
        }
        catch ( final Throwable t )
        {
            throw new AssertionFailedError(
                "The copy() method closed the stream "
                + "when it shouldn't have. "
                + t.getMessage() );
        }
    }

    protected void checkWrite( final Writer output ) throws Exception
    {
        try
        {
            new java.io.PrintWriter( output ).write( 'a' );
        }
        catch ( final Throwable t )
        {
            throw new AssertionFailedError(
                "The copy() method closed the stream "
                + "when it shouldn't have. "
                + t.getMessage() );
        }
    }

    protected void deleteFile( final File file )
        throws Exception
    {
        if ( file.exists() )
        {
            assertTrue( "Couldn't delete file: " + file, file.delete() );
        }
    }

    // ----------------------------------------------------------------------
    // Assertions
    // ----------------------------------------------------------------------

    /** Assert that the content of two files is the same. */
    private void assertEqualContent( final File f0, final File f1 )
        throws IOException
    {
        /* This doesn't work because the filesize isn't updated until the file
         * is closed.
        assertTrue( "The files " + f0 + " and " + f1 +
                    " have differing file sizes (" + f0.length() +
                    " vs " + f1.length() + ")", ( f0.length() == f1.length() ) );
        */
        final InputStream is0 = new java.io.FileInputStream( f0 );
        try
        {
            final InputStream is1 = new java.io.FileInputStream( f1 );
            try
            {
                final byte[] buf0 = new byte[1024];
                final byte[] buf1 = new byte[1024];
                int n0 = 0;
                int n1 = 0;

                while ( -1 != n0 )
                {
                    n0 = is0.read( buf0 );
                    n1 = is1.read( buf1 );
                    assertTrue( "The files " + f0 + " and " + f1 +
                                " have differing number of bytes available (" + n0 +
                                " vs " + n1 + ")", ( n0 == n1 ) );

                    assertTrue( "The files " + f0 + " and " + f1 +
                                " have different content", Arrays.equals( buf0, buf1 ) );
                }
            }
            finally
            {
                is1.close();
            }
        }
        finally
        {
            is0.close();
        }
    }

    /** Assert that the content of a file is equal to that in a byte[]. */
    protected void assertEqualContent( final byte[] b0, final File file )
        throws IOException
    {
        final InputStream is = new java.io.FileInputStream( file );
        try
        {
            byte[] b1 = new byte[b0.length];
            int numRead = is.read( b1 );
            assertTrue( "Different number of bytes", numRead == b0.length && is.available() == 0 );
            for ( int i = 0;
                  i < numRead;
                  assertTrue( "Byte " + i + " differs (" + b0[i] + " != " + b1[i] + ")",
                              b0[i] == b1[i] ), i++
                )
                ;
        }
        finally
        {
            is.close();
        }
    }

    protected void assertIsDirectory( File file )
    {
        assertTrue( "The File doesn't exists: " + file.getAbsolutePath(), file.exists() );

        assertTrue( "The File isn't a directory: " + file.getAbsolutePath(), file.isDirectory() );
    }

    protected void assertIsFile( File file )
    {
        assertTrue( "The File doesn't exists: " + file.getAbsolutePath(), file.exists() );

        assertTrue( "The File isn't a file: " + file.getAbsolutePath(), file.isFile() );
    }
}
