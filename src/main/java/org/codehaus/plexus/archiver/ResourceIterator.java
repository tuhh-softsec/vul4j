package org.codehaus.plexus.archiver;

import java.util.Iterator;

public interface ResourceIterator extends Iterator<ArchiveEntry>
{

    @Override
    boolean hasNext();

    @Override
    ArchiveEntry next();

}
