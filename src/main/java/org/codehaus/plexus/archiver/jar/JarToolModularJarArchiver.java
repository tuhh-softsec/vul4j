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

import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.ArchiveEntryUtils;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.archiver.zip.ConcurrentJarCreator;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A {@link ModularJarArchiver} implementation that uses
 * the {@code jar} tool provided by
 * {@code java.util.spi.ToolProvider} to create
 * modular JAR files.
 *
 * <p>
 * The basic JAR archive is created by {@link JarArchiver}
 * and the {@code jar} tool is used to upgrade it to modular JAR.
 *
 * <p>
 * If the JAR file does not contain module descriptor
 * or the JDK does not provide the {@code jar} tool
 * (for example JDK prior to Java 9), then the
 * archive created by {@link JarArchiver}
 * is left unchanged.
 */
public class JarToolModularJarArchiver
    extends ModularJarArchiver
{
    private static final String MODULE_DESCRIPTOR_FILE_NAME
        = "module-info.class";

    private static final Pattern MRJAR_VERSION_AREA
        = Pattern.compile( "META-INF/versions/\\d+/" );

    private Object jarTool;

    private boolean moduleDescriptorFound;

    private Path tempDir;

    public JarToolModularJarArchiver()
    {
        try
        {
            Class<?> toolProviderClass =
                Class.forName( "java.util.spi.ToolProvider" );
            Object jarToolOptional = toolProviderClass
                .getMethod( "findFirst", String.class )
                .invoke( null, "jar" );

            jarTool = jarToolOptional.getClass().getMethod( "get" )
                .invoke( jarToolOptional );
        }
        catch ( ReflectiveOperationException | SecurityException e )
        {
            // Ignore. It is expected that the jar tool
            // may not be available.
        }
    }

    @Override
    protected void zipFile( InputStreamSupplier is, ConcurrentJarCreator zOut,
                            String vPath, long lastModified, File fromArchive,
                            int mode, String symlinkDestination,
                            boolean addInParallel )
        throws IOException, ArchiverException
    {
        // We store the module descriptors in temporary location
        // and then add it to the JAR file using the JDK jar tool.
        // It may look strange at first, but to update a JAR file
        // you need to add new files[1] and the only files
        //  we're sure that exists in modular JAR file
        // are the module descriptors.
        //
        // [1] There are some exceptions but we need at least one file to
        // ensure it will work in all cases.
        if ( jarTool != null && isModuleDescriptor( vPath ) )
        {
            getLogger().debug( "Module descriptor found: " + vPath );

            moduleDescriptorFound = true;

            // Copy the module descriptor to temporary directory
            // so later then can be added to the JAR archive
            // by the jar tool.

            if ( tempDir == null )
            {
                tempDir = Files
                    .createTempDirectory( "plexus-archiver-modular_jar-" );
                tempDir.toFile().deleteOnExit();
            }

            File destFile = tempDir.resolve( vPath ).toFile();
            destFile.getParentFile().mkdirs();
            destFile.deleteOnExit();

            ResourceUtils.copyFile( is.get(), destFile );
            ArchiveEntryUtils.chmod( destFile,  mode );
            destFile.setLastModified( lastModified == PlexusIoResource.UNKNOWN_MODIFICATION_DATE
                                                      ? System.currentTimeMillis()
                                                      : lastModified );
        }
        else
        {
            super.zipFile( is, zOut, vPath, lastModified,
                fromArchive, mode, symlinkDestination, addInParallel );
        }
    }

    @Override
    protected void postCreateArchive()
        throws ArchiverException
    {
        if ( !moduleDescriptorFound )
        {
            // no need to update the JAR archive
            return;
        }

        try
        {
            getLogger().debug( "Using the jar tool to " +
                "update the archive to modular JAR." );

			Integer result = (Integer) jarTool.getClass()
                .getMethod( "run",
                    PrintStream.class, PrintStream.class, String[].class )
                .invoke( jarTool,
                    System.out, System.err,
                    getJarToolArguments() );

            if ( result != null && result != 0 )
            {
                throw new ArchiverException( "Could not create modular JAR file. " +
                    "The JDK jar tool exited with " + result );
            }
        }
        catch ( ReflectiveOperationException | SecurityException e )
        {
            throw new ArchiverException( "Exception occurred " +
                "while creating modular JAR file", e );
        }
        finally
        {
            clearTempDirectory();
        }
    }

    /**
     * Returns {@code true} if {@code path}
     * is a module descriptor.
     */
    private boolean isModuleDescriptor( String path )
    {
        if ( path.endsWith( MODULE_DESCRIPTOR_FILE_NAME ) )
        {
            String prefix = path.substring( 0,
                path.lastIndexOf( MODULE_DESCRIPTOR_FILE_NAME ) );

            // the path is a module descriptor if it located
            // into the root of the archive or into the
            // version are of a multi-release JAR file
            return prefix.isEmpty() ||
                MRJAR_VERSION_AREA.matcher( prefix ).matches();
        }
        else
        {
            return false;
        }
    }

    /**
     * Prepares the arguments for the jar tool.
     * It takes into account the module version,
     * main class, etc.
     */
    private String[] getJarToolArguments()
    {
        List<String> args = new ArrayList<>();

        args.add( "--update" );
        args.add( "--file" );
        args.add( getDestFile().getAbsolutePath() );

        if ( getModuleMainClass() != null )
        {
            args.add( "--main-class" );
            args.add( getModuleMainClass() );
        }

        if ( getModuleVersion() != null )
        {
            args.add( "--module-version" );
            args.add( getModuleVersion() );
        }

        if ( !isCompress() )
        {
            args.add( "--no-compress" );
        }

        args.add( "-C" );
        args.add( tempDir.toFile().getAbsolutePath() );
        args.add( "." );

        return args.toArray( new String[]{} );
    }

    /**
     * Makes best effort the clean up
     * the temporary directory used.
     */
    private void clearTempDirectory()
    {
        try
        {
            if ( tempDir != null )
            {
                FileUtils.deleteDirectory( tempDir.toFile() );
            }
        }
        catch ( IOException e )
        {
            // Ignore. It is just best effort.
        }
    }

}
