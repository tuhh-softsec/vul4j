package org.codehaus.plexus.archiver;

import java.util.List;

/**
 * An archive finalizer is just before archive creation (after user has added all files),
 * allowing the finalizer to do stuff like create manifests etc.
 */
public interface ArchiveFinalizer
{
    
    void finalizeArchiveCreation( Archiver archiver )
        throws ArchiverException;
    
    void finalizeArchiveExtraction( UnArchiver unarchiver )
        throws ArchiverException;
    
    List getVirtualFiles();

}
