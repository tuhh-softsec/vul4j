package org.codehaus.plexus.archiver;

public abstract class AbstractArchiveFinalizer
    implements ArchiveFinalizer
{

    protected AbstractArchiveFinalizer()
    {
    }

    @Override
    public void finalizeArchiveCreation( Archiver archiver )
        throws ArchiverException
    {
    }

    @Override
    public void finalizeArchiveExtraction( UnArchiver unarchiver )
        throws ArchiverException
    {
    }

}
