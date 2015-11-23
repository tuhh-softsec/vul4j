package org.codehaus.plexus.archiver.zip;

import java.io.IOException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.codehaus.plexus.components.io.functions.InputStreamTransformer;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

/**
 * A {@link ZipResource} that represents symbolic link.
 */
public class ZipSymlinkResource
    extends ZipResource
    implements SymlinkDestinationSupplier
{
    private final String symlinkDestination;

    public ZipSymlinkResource(ZipFile zipFile, ZipArchiveEntry entry, InputStreamTransformer streamTransformer)
    {
        super(zipFile, entry, streamTransformer);
        try {
            symlinkDestination = zipFile.getUnixSymlink(entry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getSymlinkDestination() throws IOException
    {
        return symlinkDestination;
    }

    @Override
    public boolean isSymbolicLink()
    {
        return true;
    }

}
