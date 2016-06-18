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
package org.codehaus.plexus.archiver.rar;

import java.io.File;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.BasePlexusArchiverTest;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.util.FileUtils;

/**
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 13 mars 07
 */
public class RarArchiverTest
    extends BasePlexusArchiverTest
{

    public File getTargetRarFolder()
    {
        return new File( getBasedir(), "/target/rartest/" );
    }

    @Override
    protected void setUp()
        throws Exception
    {
        super.setUp();
        // clean output directory and re create it
        if ( getTargetRarFolder().exists() )
        {
            FileUtils.deleteDirectory( getTargetRarFolder() );
        }
    }

    public void testArchive()
        throws Exception
    {
        Archiver archiver = (Archiver) lookup( Archiver.ROLE, "rar" );
        archiver.setDestFile( new File( getTargetRarFolder(), "test.rar" ) );
        //archiver.addDirectory( , "manifests" );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "manifests/manifest1.mf" );

        archiver.createArchive();
        assertTrue( new File( getTargetRarFolder(), "test.rar" ).exists() );

        UnArchiver unArchiver = (UnArchiver) lookup( UnArchiver.ROLE, "rar" );
        unArchiver.setSourceFile( new File( getTargetRarFolder(), "test.rar" ) );
        unArchiver.setDestDirectory( getTargetRarFolder() );
        unArchiver.extract();
        File manifestsDir = new File( getTargetRarFolder(), "/manifests" );
        assertTrue( manifestsDir.exists() );

        File manifestsFile = new File( getTargetRarFolder(), "/manifests/manifest1.mf" );
        assertTrue( manifestsFile.exists() );
    }

    public void testUnarchive()
        throws Exception
    {

        UnArchiver unArchiver = (UnArchiver) lookup( UnArchiver.ROLE, "rar" );
        File rarFile = new File( getBasedir() + "/src/test/jars/test.rar" );
        assertTrue( rarFile.exists() );
        unArchiver.setSourceFile( rarFile );
        unArchiver.setDestDirectory( getTargetRarFolder() );
        getTargetRarFolder().mkdir();
        unArchiver.extract();

        File dirExtract = new File( getTargetRarFolder(), "META-INF" );
        assertTrue( dirExtract.exists() );
        assertTrue( dirExtract.isDirectory() );

    }

    /**
     * Tests the .rar archiver is forced set to true, and after that
     * tests the behavior when the forced is set to false.
     *
     * @throws Exception
     */
    public void testRarIsForcedBehaviour() throws Exception
    {
        Archiver rarArvhiver = createArchiver( "rar" );

        assertTrue( rarArvhiver.isSupportingForced() );

        rarArvhiver.createArchive();

        final long creationTime = rarArvhiver.getDestFile().lastModified();

        rarArvhiver = createArchiver( "rar" );

        assertTrue( rarArvhiver.isSupportingForced() );
        //Default should be true
        rarArvhiver.setForced( true );

        waitUntilNewTimestamp( rarArvhiver.getDestFile(), creationTime );
        rarArvhiver.createArchive();

        final long firstRunTime = rarArvhiver.getDestFile().lastModified();
        assertFalse( creationTime == firstRunTime );

        //waitUntilNewTimestamp( rarArvhiver.getDestFile(), firstRunTime );
        rarArvhiver = createArchiver( "rar" );

        rarArvhiver.setForced( false );
        rarArvhiver.createArchive();

        final long secondRunTime = rarArvhiver.getDestFile().lastModified();

        assertEquals( secondRunTime, firstRunTime );
    }

}
