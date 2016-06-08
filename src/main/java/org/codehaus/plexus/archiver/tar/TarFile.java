package org.codehaus.plexus.archiver.tar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.codehaus.plexus.archiver.ArchiveFile;
import static org.codehaus.plexus.archiver.util.Streams.bufferedInputStream;

/**
 * <p>
 * Implementation of {@link ArchiveFile} for tar files.</p>
 * <p>
 * Compared to
 * {@link org.apache.commons.compress.archivers.zip.ZipFile}, this one should be used with some care, due to the
 * nature of a tar file: While a zip file contains a catalog, a tar
 * file does not. In other words, the only way to read a tar file in
 * a performant manner is by iterating over it from the beginning to
 * the end. If you try to open another entry than the "next" entry,
 * then you force to skip entries, until the requested entry is found.
 * This may require to reread the entire file!</p>
 * <p>
 * In other words, the recommended use of this class is to use
 * {@link #getEntries()} and invoke {@link #getInputStream(TarArchiveEntry)}
 * only for the current entry. Basically, this is to handle it like
 * {@link TarArchiveInputStream}.</p>
 * <p>
 * The advantage of this class is that you may write code for the
 * {@link ArchiveFile}, which is valid for both tar files and zip files.</p>
 */
public class TarFile
    implements ArchiveFile
{

    private final java.io.File file;

    private TarArchiveInputStream inputStream;

    private TarArchiveEntry currentEntry;

    /**
     * Creates a new instance with the given file.
     */
    public TarFile( File file )
    {
        this.file = file;
    }

    /**
     * Implementation of {@link ArchiveFile#getEntries()}. Note, that there is
     * an interaction between this method and {@link #getInputStream(TarArchiveEntry)},
     * or {@link #getInputStream(org.apache.commons.compress.archivers.ArchiveEntry)}:
     * If an input stream is opened for any other entry than the enumerations
     * current entry, then entries may be skipped.
     */
    @Override
    public Enumeration<org.apache.commons.compress.archivers.ArchiveEntry> getEntries()
        throws IOException
    {
        if ( inputStream != null )
        {
            close();
        }
        open();
        return new Enumeration<org.apache.commons.compress.archivers.ArchiveEntry>()
        {

            boolean currentEntryValid;

            @Override
            public boolean hasMoreElements()
            {
                if ( !currentEntryValid )
                {
                    try
                    {
                        currentEntry = inputStream.getNextTarEntry();
                    }
                    catch ( IOException e )
                    {
                        throw new UndeclaredThrowableException( e );
                    }
                }
                return currentEntry != null;
            }

            @Override
            public org.apache.commons.compress.archivers.ArchiveEntry nextElement()
            {
                if ( currentEntry == null )
                {
                    throw new NoSuchElementException();
                }
                currentEntryValid = false;
                return currentEntry;
            }

        };
    }

    public void close()
        throws IOException
    {
        if ( inputStream != null )
        {
            inputStream.close();
            inputStream = null;
        }
    }

    @Override
    public InputStream getInputStream( org.apache.commons.compress.archivers.ArchiveEntry entry )
        throws IOException
    {
        return getInputStream( new TarArchiveEntry( entry.getName() ) );
    }

    /**
     * Returns an {@link InputStream} with the given entries
     * contents. This {@link InputStream} may be closed: Nothing
     * happens in that case, because an actual close would invalidate
     * the underlying {@link TarArchiveInputStream}.
     */
    public InputStream getInputStream( TarArchiveEntry entry )
        throws IOException
    {
        if ( entry.equals( (Object) currentEntry ) && inputStream != null )
        {
            return new FilterInputStream( inputStream )
            {

                public void close()
                    throws IOException
                {
                    // Does nothing.
                }

            };
        }
        return getInputStream( entry, currentEntry );
    }

    protected InputStream getInputStream( File file )
        throws IOException
    {
        return new FileInputStream( file );
    }

    private InputStream getInputStream( TarArchiveEntry entry, TarArchiveEntry currentEntry )
        throws IOException
    {
        if ( currentEntry == null || inputStream == null )
        {
            // Search for the entry from the beginning of the file to the end.
            if ( inputStream != null )
            {
                close();
            }
            open();
            if ( !findEntry( entry, null ) )
            {
                throw new IOException( "Unknown entry: " + entry.getName() );
            }
        }
        else
        {
            // Search for the entry from the current position to the end of the file.
            if ( findEntry( entry, null ) )
            {
                return getInputStream( entry );
            }
            close();
            open();
            if ( !findEntry( entry, currentEntry ) )
            {
                throw new IOException( "No such entry: " + entry.getName() );
            }
        }
        return getInputStream( entry );
    }

    private void open()
        throws IOException
    {
        inputStream = new TarArchiveInputStream( bufferedInputStream( getInputStream( file ) ), "UTF8" );
    }

    private boolean findEntry( TarArchiveEntry entry, TarArchiveEntry currentEntry )
        throws IOException
    {
        for ( ;; )
        {
            this.currentEntry = inputStream.getNextTarEntry();
            if ( this.currentEntry == null
                     || ( currentEntry != null && this.currentEntry.equals( currentEntry ) ) )
            {
                return false;
            }
            if ( this.currentEntry.equals( entry ) )
            {
                return true;
            }
        }
    }

}
