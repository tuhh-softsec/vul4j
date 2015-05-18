package org.codehaus.plexus.archiver.ear;

/*
 * Copyright  2001-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

import org.apache.commons.compress.archivers.zip.ParallelScatterZipCreator;
import org.codehaus.plexus.archiver.ArchiveEntry;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.jar.JarArchiver;
import org.codehaus.plexus.archiver.util.ResourceUtils;
import org.codehaus.plexus.archiver.zip.ConcurrentJarCreator;

import java.io.File;
import java.io.IOException;

/**
 * Creates a EAR archive. Based on WAR task
 */
public class EarArchiver
    extends JarArchiver
{

    private File deploymentDescriptor;

    private boolean descriptorAdded;

    /**
     * Create an Ear.
     */
    public EarArchiver()
    {
        super();
        archiveType = "ear";
    }

    /**
     * File to incorporate as application.xml.
     */
    public void setAppxml( File descr )
        throws ArchiverException
    {
        deploymentDescriptor = descr;
        if ( !deploymentDescriptor.exists() )
        {
            throw new ArchiverException( "Deployment descriptor: " + deploymentDescriptor + " does not exist." );
        }

        addFile( descr, "META-INF/application.xml" );
    }

    /**
     * Adds archive.
     */
    public void addArchive( File fileName )
        throws ArchiverException
    {
        addDirectory( fileName.getParentFile(), "/", new String[]{fileName.getName()}, null );
    }

    /**
     * Adds archives.
     */
    public void addArchives( File directoryName, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addDirectory( directoryName, "/", includes, excludes );
    }

    protected void initZipOutputStream( ConcurrentJarCreator zOut )
        throws ArchiverException, IOException
    {
        // If no webxml file is specified, it's an error.
        if ( deploymentDescriptor == null && !isInUpdateMode() )
        {
            throw new ArchiverException( "appxml attribute is required" );
        }

        super.initZipOutputStream( zOut );
    }

    /**
     * Overridden from ZipArchiver class to deal with application.xml
     */
    protected void zipFile( ArchiveEntry entry, ConcurrentJarCreator zOut, String vPath, int mode )
        throws IOException, ArchiverException
    {
        // If the file being added is META-INF/application.xml, we
        // warn if it's not the one specified in the "appxml"
        // attribute - or if it's being added twice, meaning the same
        // file is specified by the "appxml" attribute and in a
        // <fileset> element.
        if ( vPath.equalsIgnoreCase( "META-INF/application.xml" ) )
        {
            if ( deploymentDescriptor == null
                 || !ResourceUtils.isCanonicalizedSame( entry.getResource(), deploymentDescriptor )
                 || descriptorAdded )
            {
                getLogger().warn( "Warning: selected " + archiveType
                                      + " files include a META-INF/application.xml which will be ignored "
                                      + "(please use appxml attribute to " + archiveType + " task)" );
            }
            else
            {
                super.zipFile( entry, zOut, vPath );
                descriptorAdded = true;
            }
        }
        else
        {
            super.zipFile( entry, zOut, vPath );
        }
    }

    /**
     * Make sure we don't think we already have a application.xml next
     * time this task gets executed.
     */
    protected void cleanUp()
        throws IOException
    {
        descriptorAdded = false;
        super.cleanUp();
    }
}
