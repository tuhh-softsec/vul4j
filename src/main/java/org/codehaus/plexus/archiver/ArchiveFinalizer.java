package org.codehaus.plexus.archiver;

public interface ArchiveFinalizer
{
    
    void finalizeArchiveCreation( Archiver archiver )
        throws ArchiverException;
    
    void finalizeArchiveExtraction( UnArchiver unarchiver )
        throws ArchiverException;

}
