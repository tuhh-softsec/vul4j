package org.codehaus.plexus.archiver.tar;

import java.io.IOException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

/**
 * A {@link TarResource} that represents symbolic link.
 */
public class TarSymlinkResource
    extends TarResource
    implements SymlinkDestinationSupplier
{
    private final String symlinkDestination;

    public TarSymlinkResource(TarFile tarFile, TarArchiveEntry entry) {
        super(tarFile, entry);
        symlinkDestination = entry.getLinkName();
    }

    @Override
    public String getSymlinkDestination() throws IOException {
        return symlinkDestination;
    }

    @Override
    public boolean isSymbolicLink() {
        return true;
    }

}
