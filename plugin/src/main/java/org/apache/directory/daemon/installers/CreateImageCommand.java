/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *  
 *    http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License. 
 *  
 */
package org.apache.directory.daemon.installers;


import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.directory.daemon.InstallationLayout;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.codehaus.plexus.util.FileUtils;


/**
 * Command to create installation image (footprint) before installers are triggered.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class CreateImageCommand extends MojoCommand
{
    private final Properties filterProperties = new Properties( System.getProperties() );
    private final Target target;
    private InstallationLayout layout;


    public CreateImageCommand( ServiceInstallersMojo mojo, Target target )
    {
        super( mojo );
        this.target = target;
        initializeFiltering();
    }


    public Properties getFilterProperties()
    {
        return filterProperties;
    }


    private void initializeFiltering()
    {
        filterProperties.putAll( mymojo.getProject().getProperties() );
        filterProperties.put( "app", target.getApplication().getName() );
        filterProperties.put( "app.caps", target.getApplication().getName().toUpperCase() );
        filterProperties.put( "app.server.class", mymojo.getApplicationClass() );

        if ( target.getApplication().getVersion() != null )
        {
            filterProperties.put( "app.version", target.getApplication().getVersion() );
        }

        if ( target.getApplication().getDescription() != null )
        {
            filterProperties.put( "app.init.message", target.getApplication().getDescription() );
        }
    }


    public void execute() throws MojoExecutionException, MojoFailureException
    {
        // make the layout directories
        log.info( "Creating image ... " );
        File dir = new File( mymojo.getOutputDirectory(), target.getId() );
        layout = new InstallationLayout( dir );
        target.setLayout( layout );
        layout.mkdirs();

        // copy over the read me file if present otherwise use the bundled copy
        if ( target.getApplication().getReadme() != null && target.getApplication().getReadme().exists() )
        {
            File readmeTarget = layout.getReadmeFile( target.getApplication().getReadme().getName() );
            try
            {
                FileUtils.copyFile( target.getApplication().getReadme(), readmeTarget );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to copy read me file " + target.getApplication().getReadme()
                    + " into position " + readmeTarget );
            }
        }

        // copy over the license file if present otherwise use the bundled copy
        File licenseTarget = layout.getLicenseFile( target.getApplication().getLicense().getName() );
        if ( target.getApplication().getLicense().exists() )
        {
            try
            {
                FileUtils.copyFile( target.getApplication().getLicense(), licenseTarget );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to copy license file " + target.getApplication().getLicense()
                    + " into position " + licenseTarget );
            }
        }
        else
        {
            try
            {
                MojoHelperUtils.copyAsciiFile( mymojo, filterProperties, getClass().getResourceAsStream( "LICENSE" ),
                    licenseTarget, false );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to bundled ASL license file "
                    + getClass().getResource( "LICENSE" ) + " into position " + licenseTarget );
            }
        }

        // copy over the icon if present otherwise use the bundled copy
        File iconTarget = layout.getLogoIconFile( target.getApplication().getIcon().getName() );
        if ( target.getApplication().getIcon().exists() )
        {
            try
            {
                FileUtils.copyFile( target.getApplication().getIcon(), iconTarget );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to copy icon file " + target.getApplication().getIcon()
                    + " into position " + iconTarget );
            }
        }
        else
        {
            try
            {
                MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream( "logo.ico" ), iconTarget );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to copy icon file " + getClass().getResource( "logo.ico" )
                    + " into position " + iconTarget );
            }
        }

        // copy over the REQUIRED bootstrapper.jar file 
        try
        {
            FileUtils.copyFile( mymojo.getBootstrapper().getFile(), layout.getBootstrapper() );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( "Failed to copy bootstrapper.jar " + mymojo.getBootstrapper().getFile()
                + " into position " + layout.getBootstrapper() );
        }

        // copy over the REQUIRED logger artifact
        /*
                try
                {
                    FileUtils.copyFile( mymojo.getLogger().getFile(), layout.getLogger() );
                }
                catch ( IOException e )
                {
                    throw new MojoFailureException( "Failed to copy logger.jar " + mymojo.getLogger().getFile()
                        + " into position " + layout.getLogger() );
                }
        */

        // copy over the REQUIRED daemon.jar file 
        try
        {
            FileUtils.copyFile( mymojo.getDaemon().getFile(), new File( layout.getLibDirectory(), "wrapper.jar" ) );
        }
        catch ( IOException e )
        {
            throw new MojoFailureException( "Failed to copy daemon.jar " + mymojo.getDaemon().getFile()
                + " into position " + layout.getDaemon() );
        }

        // copy over the optional bootstrapper configuration file
        if ( target.getBootstrapperConfigurationFile() != null )
        {
            try
            {
                FileUtils.copyFile( target.getBootstrapperConfigurationFile(), layout
                    .getBootstrapperConfigurationFile() );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to copy project bootstrapper configuration file "
                    + target.getBootstrapperConfigurationFile() + " into position "
                    + layout.getBootstrapperConfigurationFile() );
            }
        }

        // copy over the optional logging configuration file
        if ( target.getLoggerConfigurationFile().exists() )
        {
            try
            {
                FileUtils.copyFile( target.getLoggerConfigurationFile(), layout.getLoggerConfigurationFile() );
            }
            catch ( IOException e )
            {
                log.error( "Failed to copy logger configuration file " + target.getLoggerConfigurationFile()
                    + " into position " + layout.getLoggerConfigurationFile(), e );
            }
        }

        // copy over the optional server configuration file
        if ( target.getServerConfigurationFile().exists() )
        {
            try
            {
                FileUtils.copyFile( target.getServerConfigurationFile(), layout.getConfigurationFile() );
            }
            catch ( IOException e )
            {
                log.error( "Failed to copy server configuration file " + target.getServerConfigurationFile()
                    + " into position " + layout.getConfigurationFile(), e );
            }
        }

        // -------------------------------------------------------------------
        // Copy Wrapper Files
        // -------------------------------------------------------------------

        // TODO I really wonder if wrapper bin, lib for macosx and solaris don't need to be
        // copied in case if os?
        if ( target.getDaemonFramework().equalsIgnoreCase( "tanuki" ) )
        {
            if ( target.getOsName().equalsIgnoreCase( "linux" ) )
            {
                if ( target.getOsArch().equals( "i386" ) )
                {
                    try
                    {
                        MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream(
                            "wrapper/bin/wrapper-linux-x86-32" ), new File( layout.getBinDirectory(), target
                            .getApplication().getName() ) );
                        MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream(
                            "wrapper/lib/libwrapper-linux-x86-32.so" ), new File( layout.getLibDirectory(),
                            "libwrapper.so" ) );
                    }
                    catch ( IOException e )
                    {
                        throw new MojoFailureException( "Failed to copy Tanuki binary files to lib and bin directories" );
                    }
                }
                else
                {
                    if ( target.getOsArch().equals( "x86_64" ) )
                    {
                        try
                        {
                            MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream(
                                "wrapper/bin/wrapper-linux-x86-64" ), new File( layout.getBinDirectory(), target
                                .getApplication().getName() ) );
                            MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream(
                                "wrapper/lib/libwrapper-linux-x86-64.so" ), new File( layout.getLibDirectory(),
                                "libwrapper.so" ) );
                        }
                        catch ( IOException e )
                        {
                            throw new MojoFailureException(
                                "Failed to copy Tanuki binary files to lib and bin directories" );
                        }
                    }
                    else
                    {
                        throw new MojoFailureException( "OsName='linux' supports only OsArc='[i386|x86_64]'" );
                    }
                }
            }
            else
            {
                if ( target.getOsFamily().equalsIgnoreCase( "windows" ) )
                {
                    try
                    {
                        MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream(
                            "wrapper/bin/wrapper-windows-x86-32.exe" ), new File( layout.getBinDirectory(), target
                            .getApplication().getName() ) );
                        MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream(
                            "wrapper/lib/wrapper-windows-x86-32.dll" ), new File( layout.getLibDirectory(),
                            "libwrapper.so" ) );
                    }
                    catch ( IOException e )
                    {
                        throw new MojoFailureException( "Failed to copy Tanuki binary files to lib and bin directories" );
                    }
                }
                else
                {
                    throw new MojoFailureException(
                        "Not supported for configured daemon framework. OsName=" + target.getOsName() + " OsFamily=" + target.getOsFamily());
                }
            }
        }
        else
        {
            // now copy over the jsvc executable renaming it to the mymojo.getApplicationName() 
            if ( target.getOsName().equalsIgnoreCase( "sunos" ) )
            {
                if ( target.getOsArch().equalsIgnoreCase( "sparc" ) )
                {
                    File executable = new File( layout.getBinDirectory(), target.getApplication().getName() );
                    try
                    {
                        MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream( "jsvc_solaris_sparc" ),
                            executable );
                    }
                    catch ( IOException e )
                    {
                        throw new MojoFailureException( "Failed to copy jsvc executable file "
                            + getClass().getResource( "jsvc_solaris_sparc" ) + " into position "
                            + executable.getAbsolutePath() );
                    }
                }
                else
                {
                    if ( target.getOsArch().equals( "i386" ) )
                    {
                        File executable = new File( layout.getBinDirectory(), target.getApplication().getName() );
                        try
                        {
                            MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream( "jsvc_solaris_i386" ),
                                executable );
                        }
                        catch ( IOException e )
                        {
                            throw new MojoFailureException( "Failed to copy jsvc executable file "
                                + getClass().getResource( "jsvc_solaris_i386" ) + " into position "
                                + executable.getAbsolutePath() );
                        }
                    }
                    else
                    {
                        throw new MojoFailureException( "OsName='sunos' supports only OsArc='[sparc|i386]'" );
                    }
                }
            }
            else
            {
                // now copy over the jsvc executable renaming it to the mymojo.getApplicationName()
                if ( target.getOsName().equalsIgnoreCase( "macosx" ) )
                {
                    if ( target.getOsArch().equalsIgnoreCase( "ppc" ) )
                    {
                        File executable = new File( layout.getBinDirectory(), target.getApplication().getName() );
                        try
                        {
                            MojoHelperUtils.copyBinaryFile( getClass().getResourceAsStream( "jsvc_macosx_ppc" ),
                                executable );
                        }
                        catch ( IOException e )
                        {
                            throw new MojoFailureException( "Failed to copy jsvc executable file "
                                + getClass().getResource( "jsvc_macosx_ppc" ) + " into position "
                                + executable.getAbsolutePath() );
                        }
                    }
                    else
                    {
                        throw new MojoFailureException( "OsName='macosx' supports only OsArch='[ppc]'" );
                    }
                }
                else
                {
                    throw new MojoFailureException( "OsName='" + target.getOsName()
                        + "' is not supported for build process." );
                }
            }
        }

        target.setLibArtifacts( MojoHelperUtils.copyDependencies( mymojo, layout ) );

        // -- copy sources if set --

        if ( target.getSourcesDirectory() != null )
        {
            File sourcesDirectory = new File( layout.getBaseDirectory(), target.getSourcesTargetPath() );
            try
            {
                FileUtils.copyDirectoryStructure( target.getSourcesDirectory(), sourcesDirectory );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to copy sources exported from " + target.getSourcesDirectory()
                    + " to " + sourcesDirectory );
            }
        }

        // -- copy doco if set --

        if ( target.getDocsDirectory() != null )
        {
            File docsDirectory = new File( layout.getBaseDirectory(), target.getDocsTargetPath() );
            try
            {
                FileUtils.copyDirectoryStructure( target.getDocsDirectory(), docsDirectory );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to copy generated docs from " + target.getDocsDirectory()
                    + " to " + docsDirectory );
            }
        }

        // -- if present copy the NOTICE.txt file --

        File noticeFileTarget = new File( layout.getBaseDirectory(), "NOTICE.txt" );
        File noticeFile = new File( "NOTICE.txt" );
        if ( noticeFile.exists() )
        {
            try
            {
                FileUtils.copyFile( noticeFile, noticeFileTarget );
            }
            catch ( IOException e )
            {
                log.error( "Failed to notice file " + noticeFile.getAbsolutePath() + " into position "
                    + noticeFileTarget.getAbsolutePath(), e );
            }
        }

        processPackagedFiles( target, mymojo.getPackagedFiles() );
    }
}
