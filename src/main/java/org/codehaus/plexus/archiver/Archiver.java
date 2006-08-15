package org.codehaus.plexus.archiver;

/**
 *
 * Copyright 2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * @version $Revision$ $Date$
 */
public interface Archiver
{
    String ROLE = Archiver.class.getName();

    void createArchive()
        throws ArchiverException, IOException;

    void addDirectory( File directory )
        throws ArchiverException;

    void addDirectory( File directory, String prefix )
        throws ArchiverException;

    void addDirectory( File directory, String[] includes, String[] excludes )
        throws ArchiverException;

    void addDirectory( File directory, String prefix, String[] includes, String[] excludes )
        throws ArchiverException;

    void addFile( File inputFile, String destFileName )
        throws ArchiverException;

    void addFile( File inputFile, String destFileName, int permissions )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile, String prefix )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile, String[] includes, String[] excludes )
        throws ArchiverException;

    void addArchivedFileSet( File archiveFile, String prefix, String[] includes, String[] excludes )
        throws ArchiverException;

    File getDestFile();

    void setDestFile( File destFile );

    void setDefaultFileMode( int mode );

    int getDefaultFileMode();

    void setDefaultDirectoryMode( int mode );

    int getDefaultDirectoryMode();

    boolean getIncludeEmptyDirs();

    void setIncludeEmptyDirs( boolean includeEmptyDirs );
    
    Map getFiles();
}
