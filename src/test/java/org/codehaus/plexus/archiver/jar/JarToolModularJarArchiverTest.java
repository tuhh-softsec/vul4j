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

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.codehaus.plexus.archiver.ArchiverException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

public class JarToolModularJarArchiverTest
    extends BaseJarArchiverTest
{

    private ModularJarArchiver archiver;

    /*
     * Configures the ModularJarArchiver for the test cases.
     */
    @Before
    public void ModularJarArchiver()
        throws Exception
    {
        File jarFile = new File( "target/output/modular.jar" );
        jarFile.delete();

        archiver = getJarArchiver();
        archiver.setDestFile( jarFile );
        archiver.addDirectory( new File( "src/test/resources/java-classes" ) );
    }

    /*
     * Verify that the main class and the version are properly set for a modular JAR file.
     */
    @Test
    public void testModularJarWithMainClassAndVersion()
        throws Exception
    {
        assumeTrue( modulesAreSupported() );

        archiver.addDirectory( new File( "src/test/resources/java-module-descriptor" ) );
        archiver.setModuleVersion( "1.0.0" );
        archiver.setModuleMainClass( "com.example.app.Main" );

        archiver.createArchive();

        // verify that the proper version and main class are set
        assertModularJarFile( archiver.getDestFile(),
            "1.0.0", "com.example.app.Main", "com.example.app", "com.example.resources" );
    }

    /*
     * Verify that a modular JAR file is created even when no additional attributes are set.
     */
    @Test
    public void testModularJar()
        throws Exception
    {
        assumeTrue( modulesAreSupported() );

        archiver.addDirectory( new File( "src/test/resources/java-module-descriptor" ) );
        archiver.createArchive();

        // verify that the proper version and main class are set
        assertModularJarFile( archiver.getDestFile(),
            null, null, "com.example.app", "com.example.resources" );
    }

    /*
     * Verify that exception is thrown when the modular JAR is not valid.
     */
    @Test( expected = ArchiverException.class )
    public void testInvalidModularJar()
        throws Exception
    {
        assumeTrue( modulesAreSupported() );

        archiver.addDirectory( new File( "src/test/resources/java-module-descriptor" ) );
        // Not a valid version
        archiver.setModuleVersion( "notAValidVersion" );

        archiver.createArchive();
    }

    /*
     * Verify that modular JAR files could be created even
     * if the Java version does not support modules.
     */
    @Test
    public void testModularJarPriorJava9()
        throws Exception
    {
        assumeFalse( modulesAreSupported() );

        archiver.addDirectory( new File( "src/test/resources/java-module-descriptor" ) );
        archiver.setModuleVersion( "1.0.0" );
        archiver.setModuleMainClass( "com.example.app.Main" );

        archiver.createArchive();

        // verify that the modular jar is created
        try ( ZipFile resultingArchive = new ZipFile( archiver.getDestFile() ) )
        {
            assertNotNull( resultingArchive.getEntry( "module-info.class" )  );
        }
    }

    /*
     * Verify that the compression flag is respected.
     */
    @Test
    public void testNoCompression()
        throws Exception
    {
        assumeTrue( modulesAreSupported() );

        archiver.addDirectory( new File( "src/test/resources/java-module-descriptor" ) );
        archiver.setCompress( false );

        archiver.createArchive();

        // verify that the entries are not compressed
        try ( ZipFile resultingArchive = new ZipFile( archiver.getDestFile() ) )
        {
            Enumeration<? extends ZipEntry> entries = resultingArchive.entries();

            while ( entries.hasMoreElements() )
            {
                ZipEntry entry = entries.nextElement();

                assertEquals( ZipEntry.STORED, entry.getMethod() );
            }
        }
    }

    /*
     * Verify that the compression set in the "plain" JAR file
     * is kept after it is updated to modular JAR file.
     */
    @Test
    public void testCompression()
        throws Exception
    {
        assumeTrue( modulesAreSupported() );

        archiver.addDirectory( new File( "src/test/resources/java-module-descriptor" ) );
        archiver.addFile( new File( "src/test/jars/test.jar" ), "META-INF/lib/test.jar" );
        archiver.setRecompressAddedZips( false );

        archiver.createArchive();

        // verify that the compression is kept
        try ( ZipFile resultingArchive = new ZipFile( archiver.getDestFile() ) )
        {
            Enumeration<? extends ZipEntry> entries = resultingArchive.entries();

            while ( entries.hasMoreElements() )
            {
                ZipEntry entry = entries.nextElement();

                int expectedMethod = entry.isDirectory() || entry.getName().endsWith( ".jar" )
                                     ? ZipEntry.STORED
                                     : ZipEntry.DEFLATED;
                assertEquals( expectedMethod, entry.getMethod() );
            }
        }
    }

    /*
     * Verify that a module descriptor in the versioned area is handled correctly.
     */
    @Test
    public void testModularMultiReleaseJar()
        throws Exception
    {
        assumeTrue( modulesAreSupported() );

        archiver.addFile( new File( "src/test/resources/java-module-descriptor/module-info.class" ),
            "META-INF/versions/9/module-info.class" );
        Manifest manifest = new Manifest();
        manifest.addConfiguredAttribute( new Manifest.Attribute( "Multi-Release", "true" ) );
        archiver.addConfiguredManifest( manifest );
        archiver.setModuleVersion( "1.0.0" );
        archiver.setModuleMainClass( "com.example.app.Main" );

        archiver.createArchive();

        // verify that the resulting modular jar has the proper version and main class set
        try ( ZipFile resultingArchive = new ZipFile( archiver.getDestFile() ) )
        {
            ZipEntry moduleDescriptorEntry =
                resultingArchive.getEntry( "META-INF/versions/9/module-info.class" );
            InputStream resultingModuleDescriptor = resultingArchive.getInputStream( moduleDescriptorEntry );

            assertModuleDescriptor( resultingModuleDescriptor,
                    "1.0.0", "com.example.app.Main", "com.example.app", "com.example.resources" );
        }
    }

    @Override
    protected JarToolModularJarArchiver getJarArchiver()
    {
        return new JarToolModularJarArchiver();
    }

    private void assertModularJarFile( File jarFile ,
                                       String expectedVersion, String expectedMainClass,
                                       String... expectedPackages )
        throws Exception
    {
        try ( ZipFile resultingArchive = new ZipFile( jarFile ) )
        {
            ZipEntry moduleDescriptorEntry = resultingArchive.getEntry( "module-info.class" );
            InputStream resultingModuleDescriptor = resultingArchive.getInputStream( moduleDescriptorEntry );

            assertModuleDescriptor( resultingModuleDescriptor,
                expectedVersion, expectedMainClass, expectedPackages );
        }
    }

    private void assertModuleDescriptor( InputStream moduleDescriptorInputStream,
                                         String expectedVersion, String expectedMainClass,
                                         String... expectedPackages )
        throws Exception
    {
        // ModuleDescriptor methods are available from Java 9 so let's get by reflection
        Class<?> moduleDescriptorClass = Class.forName( "java.lang.module.ModuleDescriptor" );
        Class<?> optionalClass = Class.forName( "java.util.Optional" );
        Method readMethod = moduleDescriptorClass.getMethod( "read", InputStream.class );
        Method mainClassMethod = moduleDescriptorClass.getMethod( "mainClass" );
        Method rawVersionMethod = moduleDescriptorClass.getMethod( "rawVersion" );
        Method packagesMethod = moduleDescriptorClass.getMethod( "packages" );
        Method isPresentMethod = optionalClass.getMethod( "isPresent" );
        Method getMethod = optionalClass.getMethod( "get" );

        // Read the module from the input stream
        Object moduleDescriptor = readMethod.invoke( null, moduleDescriptorInputStream );

        // Get the module main class
        Object mainClassOptional = mainClassMethod.invoke( moduleDescriptor );
        String actualMainClass = null;
        if ( (boolean) isPresentMethod.invoke( mainClassOptional ) )
        {
            actualMainClass = (String) getMethod.invoke( mainClassOptional );
        }

        // Get the module version
        Object versionOptional = rawVersionMethod.invoke( moduleDescriptor );
        String actualVersion = null;
        if ( (boolean) isPresentMethod.invoke( versionOptional ) )
        {
            actualVersion = (String) getMethod.invoke( versionOptional );
        }

        // Get the module packages
        Set<String> actualPackagesSet = (Set<String>) packagesMethod.invoke( moduleDescriptor );
        Set<String> expectedPackagesSet = new HashSet<>( Arrays.asList( expectedPackages ) );

        assertEquals( expectedMainClass, actualMainClass );
        assertEquals( expectedVersion, actualVersion );
        assertEquals( expectedPackagesSet, actualPackagesSet );
    }

    /*
     * Returns true if the current version of Java does support modules.
     */
    private boolean modulesAreSupported()
    {
        try
        {
            Class.forName( "java.lang.module.ModuleDescriptor" );
        }
        catch ( ClassNotFoundException e )
        {
            return false;
        }

        return true;
    }

}
