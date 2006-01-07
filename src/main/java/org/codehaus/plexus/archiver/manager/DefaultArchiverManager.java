package org.codehaus.plexus.archiver.manager;

/*
 * Copyright  2001,2004 The Apache Software Foundation
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


import org.codehaus.plexus.PlexusConstants;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Contextualizable;

/**
 * @author dantran
 * @version $Revision:
 */

public class DefaultArchiverManager
    implements ArchiverManager, Contextualizable
{
    private PlexusContainer container;

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void contextualize( Context context )
        throws ContextException
    {
        container = (PlexusContainer) context.get( PlexusConstants.PLEXUS_KEY );
    }

    public Archiver getArchiver( String archiverName )
        throws NoSuchArchiverException
    {
        Archiver archiver;
        try
        {
            archiver = (Archiver) container.lookup( Archiver.ROLE, archiverName );
        }
        catch ( ComponentLookupException e )
        {
            throw new NoSuchArchiverException( archiverName );
        }

        return archiver;
    }

    public UnArchiver getUnArchiver( String unArchiverName )
        throws NoSuchArchiverException
    {
        UnArchiver archiver;
        try
        {
            archiver = (UnArchiver) container.lookup( UnArchiver.ROLE, unArchiverName );
        }
        catch ( ComponentLookupException e )
        {
            throw new NoSuchArchiverException( unArchiverName );
        }

        return archiver;
    }
}