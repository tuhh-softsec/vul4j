package org.codehaus.plexus.archiver;

import java.util.Iterator;

public interface ResourceIterator extends Iterator<ArchiveEntry>
{
    boolean hasNext();

    ArchiveEntry next();

}