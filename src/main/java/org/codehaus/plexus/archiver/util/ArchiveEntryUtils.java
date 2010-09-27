package org.codehaus.plexus.archiver.util;

import java.io.File;
import java.lang.reflect.Method;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.cli.Commandline;

public final class ArchiveEntryUtils
{
    
    public static boolean jvmFilePermAvailable = false;
    
    static
    {
        try
        {
            jvmFilePermAvailable = File.class.getMethod( "setReadable", new Class[] { Boolean.TYPE } ) != null;
        }
        catch ( Exception e )
        {
            // ignore exception log this ?
        }
    }

    private ArchiveEntryUtils()
    {
        // no op
    }
    
    /**
     * @since 1.1
     * @param file
     * @param mode
     * @param logger
     * @param useJvmChmod will use jvm file permissions <b>not available for group level</b>
     * @throws ArchiverException
     */
    public static void chmod( File file, int mode, Logger logger, boolean useJvmChmod )
        throws ArchiverException
    {
        if ( !Os.isFamily( Os.FAMILY_UNIX ) )
        {
            return;
        }

        String m = Integer.toOctalString( mode & 0xfff );

        if ( useJvmChmod && !jvmFilePermAvailable )
        {
            logger.info( "you want to use jvmChmod but it's not possible where your current jvm" );
            useJvmChmod = false;
        }
        
        if ( useJvmChmod && jvmFilePermAvailable )
        {
            logger.info( "useJvmChmod" );
            applyPermissionsWithJvm( file, m, logger );
            return;
        }
        
        try
        {
            Commandline commandline = new Commandline();

            commandline.setWorkingDirectory( file.getParentFile().getAbsolutePath() );

            logger.info( "mode " + mode + ", chmod " + m );
            
            commandline.setExecutable( "chmod" );

            commandline.createArg().setValue( m );

            String path = file.getAbsolutePath();

            commandline.createArg().setValue( path );

            // commenting this debug statement, since it can produce VERY verbose output...
            // this method is called often during archive creation.
//            logger.debug( "Executing:\n\n" + commandline.toString() + "\n\n" );

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

    /**
     * <b>jvm chmod will be used only if System property <code>useJvmChmod</code> set to true</b>
     * @param file
     * @param mode
     * @param logger
     * @throws ArchiverException
     */
    private static void chmod( File file, int mode, Logger logger )
        throws ArchiverException
    {
        chmod( file, mode, logger, Boolean.getBoolean( "useJvmChmod" ) && jvmFilePermAvailable );
    }
    
       
    private static void applyPermissionsWithJvm( File file, String mode, Logger logger )
    {
        FilePermission filePermission = FilePermissionUtils.getFilePermissionFromMode( mode, logger );

        Method method;
        try
        {
            method = File.class.getMethod( "setReadable", new Class[] { Boolean.TYPE, Boolean.TYPE } );

            method.invoke( file,
                           new Object[] {
                               Boolean.valueOf( filePermission.isReadable() ),
                               Boolean.valueOf( filePermission.isOwnerOnlyReadable() ) } );

            method = File.class.getMethod( "setExecutable", new Class[] { Boolean.TYPE, Boolean.TYPE } );
            method.invoke( file,
                           new Object[] {
                               Boolean.valueOf( filePermission.isExecutable() ),
                               Boolean.valueOf( filePermission.isOwnerOnlyExecutable() ) } );

            method = File.class.getMethod( "setWritable", new Class[] { Boolean.TYPE, Boolean.TYPE } );
            method.invoke( file,
                           new Object[] {
                               Boolean.valueOf( filePermission.isWritable() ),
                               Boolean.valueOf( filePermission.isOwnerOnlyWritable() ) } );
        }
        catch ( Exception e )
        {
            logger.error( "error calling dynamically file permissons with jvm " + e.getMessage(), e );
            throw new RuntimeException( "error calling dynamically file permissons with jvm " + e.getMessage(), e );
        }
    }

}
