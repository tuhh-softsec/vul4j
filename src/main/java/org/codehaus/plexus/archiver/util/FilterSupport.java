package org.codehaus.plexus.archiver.util;

import java.io.InputStream;
import java.util.List;

import org.codehaus.plexus.archiver.ArchiveFileFilter;
import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.components.io.fileselectors.FileSelector;
import org.codehaus.plexus.logging.Logger;


/**
 * @deprecated Use {@link FileSelector} and {@link Archiver#addFileSet}.
 */
public class FilterSupport
{

    private final List<ArchiveFileFilter> filters;

    private final Logger logger;

    public FilterSupport( List<ArchiveFileFilter> filters, Logger logger )
    {
        this.filters = filters;
        this.logger = logger;
    }

    public boolean include( InputStream dataStream, String entryName )
    {
        boolean included = true;

        if ( filters != null && !filters.isEmpty() )
        {
			for (ArchiveFileFilter filter : filters) {
				included = filter.include(dataStream, entryName);

				if (!included) {
					if (logger.isDebugEnabled()) {
						logger.debug("Entry: \'" + entryName + "\' excluded by filter: " + filter.getClass().getName());
					}

					break;
				}
			}
        }

        return included;
    }

}
