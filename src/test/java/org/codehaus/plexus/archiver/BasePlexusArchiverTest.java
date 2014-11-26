package org.codehaus.plexus.archiver;

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

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.util.FileUtils;
/**
 * Base abstract class that all the test-cases for different archivers 
 * extend so that they can use its helpful methids.
 * 
 * @version $Id$
 */
public abstract class BasePlexusArchiverTest extends PlexusTestCase
{
    /**
     * Ensure that when a new file is created at the specified location that the timestamp of
     * that file will be greater than the one specified as a reference.
     * 
     * Warning: Runs in a busy loop creating a file until the output file is newer than the reference timestamp.
     * This should be better than sleeping for a race condition time out value.
     * 
     * @param outputFile the file to be created
     * @param timestampReference the created file will have a newer timestamp than this reference timestamp.
     * @throws Exception failures
     */
    protected void waitUntilNewTimestamp( File outputFile, long timestampReference ) throws Exception
    {
        File tmpFile = File.createTempFile( "ZipArchiverTest.waitUntilNewTimestamp", null );
        // slurp the file into a temp file and then copy the temp back over the top until it is newer.
        FileUtils.copyFile( outputFile, tmpFile );
        
        FileUtils.copyFile( tmpFile, outputFile );       
        while ( timestampReference >= outputFile.lastModified() )
        {
            FileUtils.copyFile( tmpFile, outputFile );
            Thread.yield();
        }
        
        tmpFile.delete();
    }
    
    /**
     * Base method for all the Archivers to create an archiver.
     * 
     * @param format
     * @return
     * @throws Exception
     */
    protected Archiver createArchiver(String format) throws Exception {
        
        final File pomFile = new File("pom.xml");
        final File rarFile = new File( "target/output/pom.xml."+format );
        
        Archiver archiver = (Archiver) lookup( Archiver.ROLE, format );
        archiver.setDestFile( rarFile );
        archiver.addFile( pomFile, "pom.xml" );

        return archiver;
    }
}
