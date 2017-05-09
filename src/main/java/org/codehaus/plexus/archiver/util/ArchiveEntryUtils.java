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
package org.codehaus.plexus.archiver.util;

import java.io.File;
import java.io.IOException;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.components.io.attributes.AttributeUtils;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.Os;

@SuppressWarnings( "JavaDoc" )
public final class ArchiveEntryUtils
{

    private ArchiveEntryUtils()
    {
        // no op
    }

    /**
     * This method is now deprecated.
     *
     * The {@code useJvmChmod} flag is ignored as the JVM is always used.
     * The {@code logger} provided is no longer used.
     *
     * @deprecated Use {@link #chmod(File, int)}
     */
    @Deprecated
    public static void chmod( final File file, final int mode, final Logger logger, boolean useJvmChmod )
        throws ArchiverException
    {
        chmod( file, mode );
    }

    /**
     * This method is now deprecated.
     *
     * The {@code logger} provided is no longer used.
     *
     * @deprecated Use {@link #chmod(File, int)}
     */
    @Deprecated
    public static void chmod( final File file, final int mode, final Logger logger )
        throws ArchiverException
    {
        chmod( file, mode );
    }

    public static void chmod( final File file, final int mode )
        throws ArchiverException
    {
        if ( !Os.isFamily( Os.FAMILY_UNIX ) )
        {
            return;
        }

        try
        {
            AttributeUtils.chmod( file, mode );
        }
        catch ( IOException e )
        {
            throw new ArchiverException( "Failed setting file attributes", e );
        }
    }

}
