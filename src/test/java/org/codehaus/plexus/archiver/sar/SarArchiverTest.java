package org.codehaus.plexus.archiver.sar;

import org.codehaus.plexus.archiver.BasePlexusArchiverTest;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.components.io.resources.PlexusIoResourceCollection;

/*
 * Copyright 2007 The Codehaus Foundation.
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

public class SarArchiverTest
    extends BasePlexusArchiverTest
{


    public void testLookup()
        throws Exception
    {
        ArchiverManager dam = (ArchiverManager) lookup( ArchiverManager.ROLE );
        PlexusIoResourceCollection sar = dam.getResourceCollection( "sar" );
        assertNotNull(sar);
        PlexusIoResourceCollection archiver = (PlexusIoResourceCollection) lookup( PlexusIoResourceCollection.ROLE, "sar" );
        assertNotNull( archiver);
    }


}
