package org.codehaus.plexus.archiver;

import java.io.InputStream;

/**
 * @deprecated Use {@link FileSelector}
 */
public interface ArchiveFileFilter
{
    
    boolean include( InputStream dataStream, String entryName )
        throws ArchiveFilterException;

}
