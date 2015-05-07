package org.codehaus.plexus.archiver.war;

/*
 * Copyright  2000-2004 The Apache Software Foundation
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

import java.io.File;
import java.io.IOException;


/**
 * An extension of &lt;jar&gt; to create a WAR archive.
 * Contains special treatment for files that should end up in the
 * <code>WEB-INF/lib</code>, <code>WEB-INF/classes</code> or
 * <code>WEB-INF</code> directories of the Web Application Archive.</p>
 * <p>(The War task is a shortcut for specifying the particular layout of a WAR file.
 * The same thing can be accomplished by using the <i>prefix</i> and <i>fullpath</i>
 * attributes of zipfilesets in a Zip or Jar task.)</p>
 * <p>The extended zipfileset element from the zip task
 * (with attributes <i>prefix</i>, <i>fullpath</i>, and <i>src</i>)
 * is available in the War task.</p>
 *
 * @see JarArchiver
 */
public class WarArchiver
    extends JarArchiver
{

    /**
     * our web.xml deployment descriptor
     */
    private File deploymentDescriptor;

    /**
     * flag set if finding the webxml is to be expected.
     */
    private boolean expectWebXml = true;

    /**
     * flag set if the descriptor is added
     */
    private boolean descriptorAdded;

    /*
     * @deprecated Use setExpectWebXml instead !
     * @param excpectWebXml true if web xml is *expected* from the client
     */
    @Deprecated
    public void setIgnoreWebxml( boolean excpectWebXml )
    {
        expectWebXml = excpectWebXml;
    }

    /*.
     * Indicates if the client is required to supply web.xml
     * @param excpectWebXml true if web xml is *expected* from the client
     */
    public void setExpectWebXml( boolean expectWebXml )
    {
        this.expectWebXml = expectWebXml;
    }

    public WarArchiver()
    {
        super();
        archiveType = "war";
    }

    /**
     * set the deployment descriptor to use (WEB-INF/web.xml);
     * required unless <tt>update=true</tt>
     */
    public void setWebxml( File descr )
        throws ArchiverException
    {
        deploymentDescriptor = descr;
        if ( !deploymentDescriptor.exists() )
        {
            throw new ArchiverException( "Deployment descriptor: " + deploymentDescriptor + " does not exist." );
        }

        addFile( descr, "WEB-INF" + File.separatorChar + "web.xml" );
    }

    /**
     * add a file under WEB-INF/lib/
     */

    public void addLib( File fileName )
        throws ArchiverException
    {
        addDirectory( fileName.getParentFile(), "WEB-INF/lib/", new String[]{fileName.getName()}, null );
    }

    /**
     * add files under WEB-INF/lib/
     */

    public void addLibs( File directoryName, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addDirectory( directoryName, "WEB-INF/lib/", includes, excludes );
    }

    /**
     * add a file under WEB-INF/lib/
     */

    public void addClass( File fileName )
        throws ArchiverException
    {
        addDirectory( fileName.getParentFile(), "WEB-INF/classes/", new String[]{fileName.getName()}, null );
    }

    /**
     * add files under WEB-INF/classes
     */
    public void addClasses( File directoryName, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addDirectory( directoryName, "WEB-INF/classes/", includes, excludes );
    }

    /**
     * files to add under WEB-INF;
     */
    public void addWebinf( File directoryName, String[] includes, String[] excludes )
        throws ArchiverException
    {
        addDirectory( directoryName, "WEB-INF/", includes, excludes );
    }

    /**
     * override of  parent; validates configuration
     * before initializing the output stream.
     * @param zOut
     */
    protected void initZipOutputStream( ParallelScatterZipCreator zOut )
        throws ArchiverException, IOException
    {
        // If no webxml file is specified, it's an error.
        if ( expectWebXml && deploymentDescriptor == null && !isInUpdateMode() )
        {
            throw new ArchiverException( "webxml attribute is required (or pre-existing WEB-INF/web.xml if executing in update mode)" );
        }
        super.initZipOutputStream( zOut );
    }

    /**
     * Overridden from ZipArchiver class to deal with web.xml
     */
    protected void zipFile( ArchiveEntry entry, ParallelScatterZipCreator zOut, String vPath )
        throws IOException, ArchiverException
    {
        // If the file being added is WEB-INF/web.xml, we warn if it's
        // not the one specified in the "webxml" attribute - or if
        // it's being added twice, meaning the same file is specified
        // by the "webxml" attribute and in a <fileset> element.
        if ( vPath.equalsIgnoreCase( "WEB-INF/web.xml" ) )
        {
            if ( descriptorAdded || ( expectWebXml
                 && ( deploymentDescriptor == null
                     || !ResourceUtils.isCanonicalizedSame( entry.getResource(), deploymentDescriptor ) ) ) )
            {
                getLogger().warn( "Warning: selected " + archiveType
                                  + " files include a WEB-INF/web.xml which will be ignored "
                                  + "\n(webxml attribute is missing from "
                                  + archiveType + " task, or ignoreWebxml attribute is specified as 'true')" );
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
     * Make sure we don't think we already have a web.xml next time this task
     * gets executed.
     */
    protected void cleanUp()
        throws IOException
    {
        descriptorAdded = false;
        expectWebXml = true;
        super.cleanUp();
    }
}
