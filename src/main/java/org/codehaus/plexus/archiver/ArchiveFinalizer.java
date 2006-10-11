package org.codehaus.plexus.archiver;

import java.util.List;

public interface ArchiveFinalizer
{
    
    void finalizeArchiveCreation( Archiver archiver )
        throws ArchiverException;
    
    void finalizeArchiveExtraction( UnArchiver unarchiver )
        throws ArchiverException;
    
    List getVirtualFiles();

}
