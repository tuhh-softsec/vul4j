package org.codehaus.plexus.archiver.util;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.codehaus.plexus.archiver.ArchiveFileFilter;
import org.codehaus.plexus.archiver.ArchiveFilterException;
import org.codehaus.plexus.logging.Logger;

public class FilterSupport
{
    
    private final List filters;
    private final Logger logger;
    
    public FilterSupport( List filters, Logger logger )
    {
        this.filters = filters;
        this.logger = logger;
    }
    
    public boolean include( InputStream dataStream, String entryName )
        throws ArchiveFilterException
    {
        boolean included = true;
        
        if ( filters != null && !filters.isEmpty() )
        {
            for ( Iterator it = filters.iterator(); it.hasNext(); )
            {
                ArchiveFileFilter filter = (ArchiveFileFilter) it.next();
                
                included = filter.include( dataStream, entryName );
                
                if ( !included )
                {
                    if ( logger.isDebugEnabled() )
                    {
                        logger.debug( "Entry: \'" + entryName + "\' excluded by filter: " + filter.getClass().getName() );
                    }
                    
                    break;
                }
            }
        }
        
        return included;
    }

}
