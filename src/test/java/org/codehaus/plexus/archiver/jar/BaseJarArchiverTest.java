/**
 *
 * Copyright 2018 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.jar;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.util.IOUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class BaseJarArchiverTest
{

    /*
     * Verify that the JarArchiver implementation
     * could create basic JAR file
     */
    @Test
    public void testCreateJar()
        throws IOException, ArchiverException
    {
        File jarFile = new File( "target/output/testJar.jar" );
        jarFile.delete();

        JarArchiver archiver = getJarArchiver();
        archiver.setDestFile( jarFile );
        archiver.addDirectory( new File( "src/test/resources/java-classes" ) );

        archiver.createArchive();

        // verify that the JAR file is created and contains the expected files
        try ( ZipFile resultingArchive = new ZipFile( jarFile ) )
        {
            // verify that the JAR file contains manifest file
            assertNotNull( resultingArchive.getEntry( "META-INF/MANIFEST.MF" )  );

            // verify the JAR contains the class and it is not corrupted
            ZipEntry classFileEntry = resultingArchive.getEntry( "com/example/app/Main.class" );
            InputStream resultingClassFile = resultingArchive.getInputStream( classFileEntry );
            InputStream originalClassFile =
                new FileInputStream( "src/test/resources/java-classes/com/example/app/Main.class" );

            assertTrue( IOUtil.contentEquals( originalClassFile, resultingClassFile ) );
        }
    }

    protected abstract JarArchiver getJarArchiver();

}
