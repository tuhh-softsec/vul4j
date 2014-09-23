package org.codehaus.plexus.archiver.resources;

import java.io.File;
import java.io.IOException;

import org.codehaus.plexus.components.io.attributes.SymlinkUtils;
import org.codehaus.plexus.components.io.resources.PlexusIoSymlink;

/**
 * A symlink that does not necessarily exist (anywhere).
 */

public class PlexusIoVirtualSymlinkResource extends PlexusIoVirtualFileResource
	implements PlexusIoSymlink {
		private final String symnlinkDestination;

		public PlexusIoVirtualSymlinkResource(File symlinkFile, String symnlinkDestination)
		{
			super( symlinkFile, getName(symlinkFile) );
			this.symnlinkDestination = symnlinkDestination;
		}

		public PlexusIoVirtualSymlinkResource(File symlinkfile, String name, String symnlinkDestination)
		{
			super( symlinkfile, name );
			this.symnlinkDestination = symnlinkDestination;
		}

	public String getSymlinkDestination()
			throws IOException
	{
		return symnlinkDestination == null ? SymlinkUtils.readSymbolicLink(getFile()).toString() : symnlinkDestination;
	}

	@Override public boolean isSymbolicLink() {
		return true;
	}
}
