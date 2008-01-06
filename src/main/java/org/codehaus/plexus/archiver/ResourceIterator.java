/**
 * 
 */
package org.codehaus.plexus.archiver;

public interface ResourceIterator
{
    boolean hasNext() throws ArchiverException;

    ArchiveEntry next() throws ArchiverException;
}