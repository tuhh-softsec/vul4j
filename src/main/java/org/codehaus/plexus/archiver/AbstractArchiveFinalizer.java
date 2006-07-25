package org.codehaus.plexus.archiver;

public abstract class AbstractArchiveFinalizer
    implements ArchiveFinalizer
{
    
    protected AbstractArchiveFinalizer()
    {
    }

    public void finalizeArchiveCreation( Archiver archiver )
        throws ArchiverException
    {
    }

    public void finalizeArchiveExtraction( UnArchiver unarchiver )
        throws ArchiverException
    {
    }

}
