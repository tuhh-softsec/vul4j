/*
 *   Copyright 2006 The Apache Software Foundation
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */
package org.apache.directory.daemon.installers.rpm;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.directory.daemon.installers.MojoCommand;
import org.apache.directory.daemon.installers.MojoHelperUtils;
import org.apache.directory.daemon.installers.ServiceInstallersMojo;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Touch;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.Os;


/**
 * The IzPack installer command.
 * 
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 * @version $Rev$
 */
public class RpmInstallerCommand implements MojoCommand
{
    private final Properties filterProperties = new Properties( System.getProperties() );
    private final ServiceInstallersMojo mymojo;
    private final RpmTarget target;
    private final File rpmConfigurationFile;
    private final Log log;
    
    private File rpmBuilder;
    
    
    public RpmInstallerCommand( ServiceInstallersMojo mymojo, RpmTarget target )
    {
        this.mymojo = mymojo;
        this.target = target;
        this.log = mymojo.getLog();
        File imagesDir = target.getLayout().getBaseDirectory().getParentFile();
        rpmConfigurationFile = new File( imagesDir, target.getId() + ".spec" );
    }
    
    
    /**
     * Performs the following:
     * <ol>
     *   <li>Bail if target is not for linux or current machine is not linux (no rpm builder)</li>
     *   <li>Filter and copy project supplied spec file into place if it has been specified and exists</li>
     *   <li>If no spec file exists filter and deposite into place bundled spec template</li>
     *   <li>Bail if we cannot find the rpm builder executable</li>
     *   <li>Execute rpm build on the filtered spec file</li>
     * </ol> 
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        initializeFiltering();

        // -------------------------------------------------------------------
        // Step 1 & 4: do some error checking first for builder and OS
        // -------------------------------------------------------------------
        
        if ( ! target.getOsFamily().equals( "unix" ) || ! target.getOsName().equalsIgnoreCase( "Linux" ) )
        {
            log.warn( "RPM target " + target.getId() + " cannot be built for an non-linux based machine!" );
            log.warn( "The target will not be built." );
            log.warn( "The rest of the build will not fail because of this acceptable situation." );
            return;
        }
        
        if ( ! Os.isName( "linux" ) ) 
        {
            log.warn( "os name = " + System.getProperty( "os.name" ) );
            log.warn( "RPM target " + target.getId() + " cannot be built on a non-linux based machine!" );
            log.warn( "The target will not be built." );
            log.warn( "The rest of the build will not fail because of this acceptable situation." );
            return;
        }
        
        if ( ! System.getProperties().getProperty( "user.name" ).equals( "root" ) && ! target.isDoSudo() )
        {
            log.warn( "RPM target " + target.getId() + " can only be built by a super user or regular user " +
                    "with sudo capabilities that bypass the password!" );
            log.warn( "The target will not be built." );
            log.warn( "The rest of the build will not fail because of this acceptable situation." );
            return;
        }
        
        // @todo this should really be a parameter taken from the user's settings
        // because the compiler may be installed in different places and is specific
        if ( ! target.getRpmBuilder().exists() )
        {
            throw new MojoFailureException( "Cannot find rpmbuild: " + target.getRpmBuilder() );
        }
        else
        {
        	this.rpmBuilder = target.getRpmBuilder();
        }
        
        // -------------------------------------------------------------------
        // Step 2 & 3: copy rpm spec file and filter 
        // -------------------------------------------------------------------
        
        /** HACK!
         * @todo clean me up
         * @see http://issues.apache.org/jira/browse/DIREVE-333 
         */
        File toolsSource = new File( this.mymojo.getSourceDirectory(), "apacheds-tools.sh" );
        File toolsTarget = new File( target.getLayout().getBinDirectory(), "apacheds-tools.sh" );
        try
        {
            MojoHelperUtils.copyAsciiFile( mymojo, filterProperties, 
                toolsSource, toolsTarget, true );
        }
        catch ( IOException e )
        {
            mymojo.getLog().error( "Failed to copy apacheds-tools.sh file "  
                + toolsSource
                + " into position " + toolsTarget, e );
        }
        
        try
        {
            MojoHelperUtils.copyAsciiFile( mymojo, filterProperties, 
                getClass().getResourceAsStream( "../template.init" ), target.getLayout().getInitScript(), true );
        }
        catch ( IOException e )
        {
            mymojo.getLog().error( "Failed to copy init script "  
                + getClass().getResource( "../template.init" )
                + " into position " + target.getLayout().getInitScript(), e );
        }

        // check first to see if the default spec file is present in src/main/installers
        File projectRpmFile = new File( mymojo.getSourceDirectory(), "spec.template" );
        if ( target.getRpmSpecificationFile() != null && target.getRpmSpecificationFile().exists() )
        {
            try
            {
                MojoHelperUtils.copyAsciiFile( mymojo, filterProperties, target.getRpmSpecificationFile(), 
                    rpmConfigurationFile, true );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to filter and copy project provided " 
                    + target.getRpmSpecificationFile() + " to " + rpmConfigurationFile );
            }
        }
        else if ( projectRpmFile.exists() )
        {
            try
            {
                MojoHelperUtils.copyAsciiFile( mymojo, filterProperties, projectRpmFile, 
                    rpmConfigurationFile, true );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to filter and copy project provided " 
                    + projectRpmFile + " to " + rpmConfigurationFile );
            }
        }
        else
        {
            InputStream in = getClass().getResourceAsStream( "spec.template" );
            URL resource = getClass().getResource( "spec.template" );
            try
            {
                MojoHelperUtils.copyAsciiFile( mymojo, filterProperties, in, rpmConfigurationFile, true );
            }
            catch ( IOException e )
            {
                throw new MojoFailureException( "Failed to filter and copy bundled " + resource
                    + " to " + rpmConfigurationFile );
            }
        }

        buildSourceTarball();
        String[] cmd = new String[] {
            rpmBuilder.getAbsolutePath(), "-ba", rpmConfigurationFile.getAbsolutePath()
        };
        MojoHelperUtils.exec( cmd, target.getLayout().getBaseDirectory().getParentFile(), target.isDoSudo() );
    }


    private void initializeFiltering() throws MojoFailureException 
    {
        filterProperties.putAll( mymojo.getProject().getProperties() );
        filterProperties.put( "app" , target.getApplication().getName() );
        filterProperties.put( "app.caps" , target.getApplication().getName().toUpperCase() );
        filterProperties.put( "app.server.class", mymojo.getApplicationClass() );
        
        char firstChar = target.getApplication().getName().charAt( 0 );
        firstChar = Character.toUpperCase( firstChar );
        filterProperties.put( "app.display.name", firstChar + target.getApplication().getName().substring( 1 ) );
        filterProperties.put( "app.release", "0" );
        filterProperties.put( "app.license.type", target.getApplication().getLicenseType() );

        if ( target.getApplication().getVersion() != null )
        {
            String version = target.getApplication().getVersion().replace( '-', '_' );
            filterProperties.put( "app.version", version );
        }
        else
        {
            filterProperties.put( "app.version", "1.0" );
        }

        // -------------------------------------------------------------------
        // WARNING: hard code values just to for testing
        // -------------------------------------------------------------------

        // @todo use the list of committers and add multiple authors to inno
        if ( target.getApplication().getAuthors().isEmpty() )
        {
            filterProperties.put( "app.author" , "Apache Software Foundation" );
        }
        else
        {
            filterProperties.put( "app.author", target.getApplication().getAuthors().get( 0 ) );
        }
        
        if ( target.getFinalName() != null )
        {
            filterProperties.put( "app.final.name" , target.getFinalName() );
        }
        else
        {
            String finalName = target.getApplication().getName() 
                + "-" + target.getApplication().getVersion() + "-linux-i386.rpm"; 
            filterProperties.put( "app.final.name" , finalName );
        }
        
        filterProperties.put( "app.email" , target.getApplication().getEmail() );
        filterProperties.put( "app.url" , target.getApplication().getUrl() );
        filterProperties.put( "app.java.version" , target.getApplication().getMinimumJavaVersion() );
        filterProperties.put( "app.license" , target.getLayout().getLicenseFile().getPath() );
        filterProperties.put( "app.license.name" , target.getLayout().getLicenseFile().getName() );
        filterProperties.put( "app.company.name" , target.getCompanyName() );
        filterProperties.put( "app.description" , target.getApplication().getDescription() ); 
        filterProperties.put( "app.copyright.year", target.getCopyrightYear() );

        if ( ! target.getLayout().getReadmeFile().exists() )
        {
            touchFile( target.getLayout().getReadmeFile() );
        }
        filterProperties.put( "app.readme" , target.getLayout().getReadmeFile().getPath() );
        filterProperties.put( "app.readme.name" , target.getLayout().getReadmeFile().getName() );
        filterProperties.put( "app.icon" , target.getLayout().getLogoIconFile().getName() );
        filterProperties.put( "app.icon.name" , target.getLayout().getLogoIconFile().getName() );
        filterProperties.put( "image.basedir", target.getLayout().getBaseDirectory().getPath() );
        filterProperties.put( "install.append.libs", getInstallLibraryJars() );
        filterProperties.put( "verify.append.libs", getVerifyLibraryJars() );
        filterProperties.put( "installer.output.directory", target.getLayout().getBaseDirectory().getParent() );
        filterProperties.put( "server.init", target.getLayout().getInitScript().getName() );
    }
    
    
    private Object getVerifyLibraryJars()
    {
        StringBuffer buf = new StringBuffer();
        List artifacts = target.getLibArtifacts();
        for ( int ii = 0; ii < artifacts.size(); ii++ )
        {
            File artifact = ( ( Artifact ) artifacts.get( ii ) ).getFile();
            buf.append( "/usr/local/" );
            buf.append( target.getApplication().getName() );
            buf.append( "-%{version}/lib/" );
            buf.append( artifact.getName() );
            buf.append( "\n" );
        }
        
        return buf.toString();
    }


    private String getInstallLibraryJars() throws MojoFailureException
    {
        StringBuffer buf = new StringBuffer();
        List artifacts = target.getLibArtifacts();
        for ( int ii = 0; ii < artifacts.size(); ii++ )
        {
            buf.append( "install -m 644 " );
            File artifact = ( ( Artifact ) artifacts.get( ii ) ).getFile();
            buf.append( artifact.getAbsoluteFile() );
            buf.append( " $RPM_BUILD_ROOT/usr/local/" );
            buf.append( target.getApplication().getName() );
            buf.append( "-%{version}/lib/" );
            buf.append( artifact.getName() );
            buf.append( "\n" );
        }
        
        return buf.toString();
    }

    
    static void touchFile( File file )
    {
        Touch touch = new Touch();
        touch.setProject( new Project() );
        touch.setFile( file );
        touch.execute();
    }


    private void buildSourceTarball() throws MojoFailureException
    {
        String version = target.getApplication().getVersion().replace( '-', '_' );
        String dirname = target.getApplication().getName() + "-" + version;
        File sourcesDir = new File( target.getLayout().getBaseDirectory().getParentFile(), dirname );
        try
        {
            FileUtils.copyDirectoryStructure( target.getLayout().getBaseDirectory(), sourcesDir );
        }
        catch ( IOException e1 )
        {
            throw new MojoFailureException( "failed to copy directory structure at " + 
                target.getLayout() + " to " + sourcesDir );
        }
        
        String[] cmd = new String[] {
            "tar", "-zcvf", 
            "/usr/src/redhat/SOURCES/" + target.getApplication().getName() 
            + "-" + version + ".tar.gz",
            sourcesDir.getAbsolutePath()
        };
        
        MojoHelperUtils.exec( cmd, target.getLayout().getBaseDirectory().getParentFile(), target.isDoSudo() );
    }
}
