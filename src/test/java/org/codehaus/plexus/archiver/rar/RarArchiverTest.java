package org.codehaus.plexus.archiver.rar;

import java.io.File;

import org.codehaus.plexus.PlexusTestCase;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.util.FileUtils;

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
/**
 * @author <a href="mailto:olamy@codehaus.org">olamy</a>
 * @since 13 mars 07
 * @version $Id$
 */
public class RarArchiverTest
    extends PlexusTestCase
{

    public File getTargetRarFolfer()
    {
        return new File( getBasedir(), "/target/rartest/" );
    }

    protected void setUp()
        throws Exception
    {
        super.setUp();
        // clean output directory and re create it
        if ( getTargetRarFolfer().exists() )
        {
            FileUtils.deleteDirectory( getTargetRarFolfer() );
        }
    }

    public void testArchive()
        throws Exception
    {
        Archiver archiver = (Archiver) lookup( Archiver.ROLE, "rar" );
        archiver.setDestFile( new File( getTargetRarFolfer(), "test.rar" ) );
        //archiver.addDirectory( , "manifests" );
        archiver.addFile( getTestFile( "src/test/resources/manifests/manifest1.mf" ), "manifests/manifest1.mf" );

        archiver.createArchive();
        assertTrue( new File( getTargetRarFolfer(), "test.rar" ).exists() );

        UnArchiver unArchiver = (UnArchiver) lookup( UnArchiver.ROLE, "rar" );
        unArchiver.setSourceFile( new File( getTargetRarFolfer(), "test.rar" ) );
        unArchiver.setDestDirectory( getTargetRarFolfer() );
        unArchiver.extract();
        File manifestsDir = new File( getTargetRarFolfer(), "/manifests" );
        assertTrue( manifestsDir.exists() );

        File manifestsFile = new File( getTargetRarFolfer(), "/manifests/manifest1.mf" );
        assertTrue( manifestsFile.exists() );
    }

    public void atestUnarchive()
        throws Exception
    {

        UnArchiver unArchiver = (UnArchiver) lookup( UnArchiver.ROLE, "rar" );
        File rarFile = new File( getBasedir() + "/src/test/jars/test.rar" );
        assertTrue( rarFile.exists() );
        unArchiver.setSourceFile( rarFile );
        unArchiver.setDestDirectory( getTargetRarFolfer() );
        getTargetRarFolfer().mkdir();
        unArchiver.extract();

        File dirExtract = new File( getTargetRarFolfer(), "META-INF" );
        assertTrue( dirExtract.exists() );
        assertTrue( dirExtract.isDirectory() );

    }
}
