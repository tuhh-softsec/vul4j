package org.codehaus.plexus.util;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;

/**
 * DirectoryWalkListener
 */
public interface DirectoryWalkListener
{
    /**
     * The directory walking has begun.
     * 
     * @param basedir the basedir that walk started in.
     */
    void directoryWalkStarting( File basedir );

    /**
     * The included entry that was encountered.
     * 
     * @param percentage rough percentage of the walk completed. (inaccurate)
     * @param file the file that was included.
     */
    void directoryWalkStep( int percentage, File file );

    /**
     * The directory walking has finished.
     */
    void directoryWalkFinished();

    void debug( String message );
}
