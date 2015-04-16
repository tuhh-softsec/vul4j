package org.codehaus.plexus.archiver.diags;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.archiver.ArchivedFileSet;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.FileSet;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;
import org.codehaus.plexus.logging.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * A dry run archiver that does nothing. Some methods fall through to the underlying
 * archiver, but no actions are executed.
 * <li>dry-running (where the delegate archiver is never actually called)</li>
 * </ul>
 */
public class DryRunArchiver
    extends DelgatingArchiver
{

    private final Logger logger;


    public DryRunArchiver( final Archiver target, final Logger logger )
    {
        super( target );
        this.logger = logger;
    }

    /**
     * {@inheritDoc}
     */
    public void addArchivedFileSet( final @Nonnull File archiveFile, final String prefix, final String[] includes,
                                    final String[] excludes )
    {

        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    private void debug( final String message )
    {
        if ( ( logger != null ) && logger.isDebugEnabled() )
        {
            logger.debug( message );
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addArchivedFileSet( final @Nonnull File archiveFile, final String prefix )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addArchivedFileSet( final File archiveFile, final String[] includes, final String[] excludes )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addArchivedFileSet( final @Nonnull File archiveFile )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addDirectory( final @Nonnull File directory, final String prefix, final String[] includes,
                              final String[] excludes )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );

    }

    /**
     * {@inheritDoc}
     */
    public void addSymlink( String symlinkName, String symlinkDestination )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addSymlink( String symlinkName, int permissions, String symlinkDestination )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addDirectory( final @Nonnull File directory, final String prefix )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addDirectory( final @Nonnull File directory, final String[] includes, final String[] excludes )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addDirectory( final @Nonnull File directory )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addFile( final @Nonnull File inputFile, final @Nonnull String destFileName, final int permissions )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addFile( final @Nonnull File inputFile, final @Nonnull String destFileName )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void createArchive()
        throws ArchiverException, IOException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void setDotFileDirectory( final File dotFileDirectory )
    {
        throw new UnsupportedOperationException(
            "Undocumented feature of plexus-archiver; this is not yet supported." );
    }

    /**
     * {@inheritDoc}
     */
    public void addArchivedFileSet( final ArchivedFileSet fileSet )
        throws ArchiverException
    {

        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    @Override
    public void addArchivedFileSet( ArchivedFileSet fileSet, Charset charset )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    /**
     * {@inheritDoc}
     */
    public void addFileSet( final @Nonnull FileSet fileSet )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }


    @Override
    public void addResource( PlexusIoResource resource, String destFileName, int permissions )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }

    @Override
    public void addResources( PlexusIoResourceCollection resources )
        throws ArchiverException
    {
        debug( "DRY RUN: Skipping delegated call to: " + getMethodName() );
    }


    private String getMethodName()
    {
        final NullPointerException npe = new NullPointerException();
        final StackTraceElement[] trace = npe.getStackTrace();

        final StackTraceElement methodElement = trace[1];

        return methodElement.getMethodName() + " (archiver line: " + methodElement.getLineNumber() + ")";
    }


}
