package org.codehaus.plexus.archiver.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.codehaus.plexus.components.io.functions.FileSupplier;
import org.codehaus.plexus.components.io.resources.PlexusIoResource;
import org.codehaus.plexus.util.IOUtil;

/**
 * Utility class for work with {@link PlexusIoResource} instances.
 */
public class ResourceUtils
{

    /**
     * Private constructor, to prevent accidental implementation.
     */
    private ResourceUtils()
    {
        // Does nothing
    }

    /**
     * Queries, whether the given source is up-to-date relative to
     * the given destination.
     */
    public static boolean isUptodate( PlexusIoResource source, File destination )
    {
        return isUptodate( source, destination.lastModified() );
    }

    /**
     * Queries, whether the given source is up-to-date relative to
     * the given modification date.
     */
    public static boolean isUptodate( PlexusIoResource source, long destinationDate )
    {
        final long s = source.getLastModified();
        if ( s == PlexusIoResource.UNKNOWN_MODIFICATION_DATE )
        {
            return false;
        }

        if ( destinationDate == 0 )
        {
            return false;
        }

        return destinationDate > s;
    }

    /**
     * Queries, whether the given source is up-to-date relative to
     * the given modification date.
     */
    public static boolean isUptodate( long sourceDate, long destinationDate )
    {
        if ( sourceDate == PlexusIoResource.UNKNOWN_MODIFICATION_DATE )
        {
            return false;
        }

        if ( destinationDate == 0 )
        {
            return false;
        }

        return destinationDate > sourceDate;
    }

    /**
     * Copies the sources contents to the given destination file.
     */
    public static void copyFile( PlexusIoResource in, File outFile )
        throws IOException
    {
        InputStream input = null;
        OutputStream output = null;
        try
        {
            input = in.getContents();
            output = new FileOutputStream( outFile );
            IOUtil.copy( input, output );
            output.close();
            output = null;
            input.close();
            input = null;
        }
        finally
        {
            IOUtil.close( input );
            IOUtil.close( output );
        }
    }

    /**
     * Copies the sources contents to the given destination file.
     */
    public static void copyFile( InputStream input, File outFile )
        throws IOException
    {
        OutputStream output = null;
        try
        {
            output = new FileOutputStream( outFile );
            IOUtil.copy( input, output );
            output.close();
            output = null;
            input.close();
            input = null;
        }
        finally
        {
            IOUtil.close( input );
            IOUtil.close( output );
        }
    }

    /**
     * Checks, whether the resource and the file are identical.
     */
    public static boolean isSame( PlexusIoResource resource, File file )
    {
        if ( resource instanceof FileSupplier )
        {
            File resourceFile = ( (FileSupplier) resource ).getFile();
            return file.equals( resourceFile );
        }
        return false;
    }

    /**
     * Checks, whether the resource and the file are identical.
     * Uses {@link File#getCanonicalFile()} for comparison, which is much
     * slower than comparing the files.
     */
    public static boolean isCanonicalizedSame( PlexusIoResource resource, File file )
        throws IOException
    {
        if ( resource instanceof FileSupplier )
        {
            File resourceFile = ( (FileSupplier) resource ).getFile();
            return file.getCanonicalFile().equals( resourceFile.getCanonicalFile() );
        }
        return false;
    }

}
