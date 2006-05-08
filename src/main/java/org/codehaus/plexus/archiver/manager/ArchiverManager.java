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


import java.io.File;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.UnArchiver;

/**
 * @author dantran
 * @version $Revision:
 */
public interface ArchiverManager
{
    String ROLE = ArchiverManager.class.getName();

    Archiver getArchiver( String archiverName )
        throws NoSuchArchiverException;

    Archiver getArchiver( File file )
      throws NoSuchArchiverException;
    
    UnArchiver getUnArchiver( String unArchiverName )
        throws NoSuchArchiverException;

    UnArchiver getUnArchiver( File file )
        throws NoSuchArchiverException;    
}
