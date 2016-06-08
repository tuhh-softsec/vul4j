/*
 * Copyright 2014 The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.codehaus.plexus.archiver.resources;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.components.io.attributes.SymlinkUtils;
import org.codehaus.plexus.components.io.functions.SymlinkDestinationSupplier;

/**
 * A symlink that does not necessarily exist (anywhere).
 */
public class PlexusIoVirtualSymlinkResource extends PlexusIoVirtualFileResource
    implements SymlinkDestinationSupplier
{

    private final String symnlinkDestination;

    public PlexusIoVirtualSymlinkResource( File symlinkFile, String symnlinkDestination )
    {
        super( symlinkFile, getName( symlinkFile ) );
        this.symnlinkDestination = symnlinkDestination;
    }

    @Override
    public String getSymlinkDestination()
        throws IOException
    {
        return symnlinkDestination == null
                   ? SymlinkUtils.readSymbolicLink( getFile() ).toString()
                   : symnlinkDestination;

    }

    @Override public boolean isSymbolicLink()
    {
        return true;
    }

}
