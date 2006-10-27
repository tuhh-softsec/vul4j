package org.codehaus.plexus.archiver.util;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

import java.io.File;

public final class ArchiveEntryUtils
{
    
    private ArchiveEntryUtils()
    {
    }

    public static void chmod( File file, int mode, Logger logger )
        throws ArchiverException
    {
        if ( !Os.isFamily( "unix" ) )
        {
            return;
        }

        String m = Integer.toOctalString( mode & 0xfff );

        try
        {
            Commandline commandline = new Commandline();

            commandline.setWorkingDirectory( file.getParentFile().getAbsolutePath() );

            commandline.setExecutable( "chmod" );

            commandline.createArgument().setValue( m );

            commandline.createArgument().setValue( file.getAbsolutePath() );

            CommandLineUtils.StringStreamConsumer stderr = new CommandLineUtils.StringStreamConsumer();

            CommandLineUtils.StringStreamConsumer stdout = new CommandLineUtils.StringStreamConsumer();

            int exitCode = CommandLineUtils.executeCommandLine( commandline, stderr, stdout );

            if ( exitCode != 0 )
            {
                logger.warn( "-------------------------------" );
                logger.warn( "Standard error:" );
                logger.warn( "-------------------------------" );
                logger.warn( stderr.getOutput() );
                logger.warn( "-------------------------------" );
                logger.warn( "Standard output:" );
                logger.warn( "-------------------------------" );
                logger.warn( stdout.getOutput() );
                logger.warn( "-------------------------------" );

                throw new ArchiverException( "chmod exit code was: " + exitCode );
            }
        }
        catch ( CommandLineException e )
        {
            throw new ArchiverException( "Error while executing chmod.", e );
        }
    }

}
