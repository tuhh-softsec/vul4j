package org.codehaus.plexus.archiver;

import java.util.List;

public interface FinalizerEnabled
{

    void addArchiveFinalizer( ArchiveFinalizer finalizer );

    void setArchiveFinalizers( List<ArchiveFinalizer> archiveFinalizers );

}
