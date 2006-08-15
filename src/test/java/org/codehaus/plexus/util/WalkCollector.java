package org.codehaus.plexus.util;

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