/*
 * Copyright 2016 Codehaus.
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
package org.codehaus.plexus.archiver.tar;

import java.io.File;
import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.xz.XZArchiver;
import org.codehaus.plexus.util.FileUtils;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.codehaus.plexus.PlexusTestCase.getTestFile;

/**
 * @author philip.lourandos
 * @since 3.3
 */
public class TarXzUnArchiverTest extends PlexusTestCase
{

    public void testExtract()
        throws Exception
    {
        TarArchiver tarArchiver = (TarArchiver) lookup( Archiver.ROLE, "tar" );
        tarArchiver.setLongfile( TarLongFileMode.posix );

        String fileName1 = "TarBZip2UnArchiverTest1.txt";
        String fileName2 = "TarBZip2UnArchiverTest2.txt";
        File file1InTar = getTestFile( "target/output/" + fileName1 );
        File file2InTar = getTestFile( "target/output/" + fileName2 );
        file1InTar.delete();
        file2InTar.delete();

        assertFalse( file1InTar.exists() );
        assertFalse( file2InTar.exists() );

        File testXZFile = getTestFile( "target/output/archive.tar.xz" );
        if ( testXZFile.exists() )
        {
            FileUtils.fileDelete( testXZFile.getPath() );
        }
        assertFalse( testXZFile.exists() );

        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), fileName1 );
        tarArchiver.addFile( getTestFile( "src/test/resources/manifests/manifest2.mf" ), fileName2, 0664 );
        tarArchiver.setDestFile( getTestFile( "target/output/archive.tar" ) );
        tarArchiver.createArchive();

        XZArchiver xzArchiver = (XZArchiver) lookup( Archiver.ROLE, "xz" );

        xzArchiver.setDestFile( testXZFile );
        xzArchiver.addFile( getTestFile( "target/output/archive.tar" ), "dontcare" );
        xzArchiver.createArchive();

        assertTrue( testXZFile.exists() );

        TarXZUnArchiver tarXZUnArchiver = (TarXZUnArchiver) lookup( UnArchiver.ROLE, "tar.xz" );

        tarXZUnArchiver.setDestDirectory( getTestFile( "target/output" ) );
        tarXZUnArchiver.setSourceFile( testXZFile );
        tarXZUnArchiver.extract();

        assertTrue( file1InTar.exists() );
        assertTrue( file2InTar.exists() );

        assertEquals( testXZFile, tarXZUnArchiver.getSourceFile() );
    }

    public void testLookup() throws Exception
    {
        assertNotNull( lookup( UnArchiver.ROLE, "tar.xz" ) );
    }

}
