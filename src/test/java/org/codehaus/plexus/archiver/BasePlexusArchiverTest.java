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
package org.codehaus.plexus.archiver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;

/**
 * Base abstract class that all the test-cases for different archivers
 * extend so that they can use its helpful methids.
 */
public abstract class BasePlexusArchiverTest extends PlexusTestCase
{

    /**
     * Ensure that the last modified timestamp of a file will be greater
     * than the one specified as a reference.
     *
     * @param outputFile the file
     * @param timestampReference the file will have a newer timestamp
     *        than this reference timestamp.
     *
     * @throws IOException if the timestamp could not be modified
     */
    protected void waitUntilNewTimestamp( File outputFile, long timestampReference )
        throws IOException
    {
        long startTime = System.currentTimeMillis();
        File tmpFile = File.createTempFile(
            "BasePlexusArchiverTest.waitUntilNewTimestamp", null );
        long newTimestamp;

        // We could easily just set the last modified time using
        // Files.setLastModifiedTime and System.currentTimeMillis(),
        // but the problem is that tests are using this method to verify that
        // the force flag is working. To ensure that modified or
        // newly created files will have timestamp newer than
        // `timestampReference`, we need to modify a file ourself.
        // Otherwise the build may fail because when the test overrides
        // `outputFile` it will have timestamp that is equal
        // to `timestampReference`.
        do
        {
            FileUtils.fileWrite( tmpFile, "waitUntilNewTimestamp" );
            newTimestamp = tmpFile.lastModified();
            Thread.yield();
        }
        while ( timestampReference >= newTimestamp
                // A simple guard to ensure that we'll not do this forever.
                // If the last modified timestamp is not changed to
                // a newer value after 10 seconds, probably it never will.
                && System.currentTimeMillis() - startTime < 10_000 );

        tmpFile.delete();

        if ( timestampReference >= newTimestamp )
        {
            throw new IOException("Could not modify the last modified timestamp "
                + "to newer than the refence value." );
        }

        FileTime newTimestampTime = FileTime.fromMillis( newTimestamp );
        Files.setLastModifiedTime( outputFile.toPath(), newTimestampTime );
    }

    /**
     * Base method for all the Archivers to create an archiver.
     *
     * @param format
     *
     * @return
     *
     * @throws Exception
     */
    protected Archiver createArchiver( String format ) throws Exception
    {

        final File pomFile = new File( "pom.xml" );
        final File rarFile = new File( "target/output/pom.xml." + format );

        Archiver archiver = (Archiver) lookup( Archiver.ROLE, format );
        archiver.setDestFile( rarFile );
        archiver.addFile( pomFile, "pom.xml" );

        return archiver;
    }

}
