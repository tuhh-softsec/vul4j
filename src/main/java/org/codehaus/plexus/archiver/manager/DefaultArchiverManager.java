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


import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;

/**
 * @author dantran
 * @version $Revision:
 */

public class DefaultArchiverManager
   implements ArchiverManager, Initializable
{
	private Map archivers;

	private Map unArchivers;

	// ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
    {
        if ( archivers == null )
        {
        	archivers = new HashMap();
        }

        if ( unArchivers == null )
        {
        	unArchivers = new HashMap();
        }
    }

    public Archiver getArchiver( String archiverName )
        throws NoSuchArchiverException
    {
    	Archiver archiver = (Archiver) archivers.get( archiverName );

        if ( archiver == null )
        {
            throw new NoSuchArchiverException( archiverName );
        }

        return archiver;
    }

    public UnArchiver getUnArchiver( String archiverName )
        throws NoSuchArchiverException
    {
	    UnArchiver unarchiver = (UnArchiver) unArchivers.get( archiverName );

        if ( unarchiver == null )
        {
           throw new NoSuchArchiverException( archiverName );
        }

        return unarchiver;
    }
}