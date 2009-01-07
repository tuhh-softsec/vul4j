package org.codehaus.plexus.util;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class WalkCollector
    implements DirectoryWalkListener
{
    public List steps;

    public File startingDir;

    public int startCount;

    public int finishCount;

    public int percentageLow;

    public int percentageHigh;

    public WalkCollector()
    {
        steps = new ArrayList();
        startCount = 0;
        finishCount = 0;
        percentageLow = 0;
        percentageHigh = 0;
    }

    public void directoryWalkStarting( File basedir )
    {
        debug( "Walk Starting: " + basedir );
        startCount++;
        startingDir = basedir;
    }

    public void directoryWalkStep( int percentage, File file )
    {
        percentageLow = Math.min( percentageLow, percentage );
        percentageHigh = Math.max( percentageHigh, percentage );
        debug( "Walk Step: [" + percentage + "%] " + file );
        steps.add( file );
    }

    public void directoryWalkFinished()
    {
        debug( "Walk Finished." );
        finishCount++;
    }

    public void debug( String message )
    {
        System.out.println( message );
    }
}