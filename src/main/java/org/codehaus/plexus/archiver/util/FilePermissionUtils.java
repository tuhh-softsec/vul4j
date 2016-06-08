/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.codehaus.plexus.archiver.util;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

/**
 * @author Olivier Lamy
 * @since 1.0.1
 */
public class FilePermissionUtils
{

    private FilePermissionUtils()
    {
        // no op
    }

    /**
     * @param mode file mode "a la" unix ie 664, 440, etc
     *
     * @return FilePermission associated to the mode (group permission are ignored here)
     */
    public static FilePermission getFilePermissionFromMode( String mode, Logger logger )
    {
        if ( StringUtils.isBlank( mode ) )
        {
            throw new IllegalArgumentException( " file mode cannot be empty" );
        }
        // 4 characters works on some unix (ie solaris)
        if ( mode.length() != 3 && mode.length() != 4 )
        {
            throw new IllegalArgumentException( " file mode must be 3 or 4 characters" );
        }

        List<String> modes = new ArrayList<String>( mode.length() );
        for ( int i = 0, size = mode.length(); i < size; i++ )
        {
            modes.add( String.valueOf( mode.charAt( i ) ) );
        }

        boolean executable = false, ownerOnlyExecutable = true, ownerOnlyReadable = true, readable = false,
            ownerOnlyWritable = true, writable = false;

        // handle user perm
        try
        {
            int userMode = Integer.valueOf( modes.get( mode.length() == 4 ? 1 : 0 ) );
            switch ( userMode )
            {
                case 0:
                    break;
                case 1:
                    executable = true;
                    break;
                case 2:
                    writable = true;
                    break;
                case 3:
                    writable = true;
                    executable = true;
                    break;
                case 4:
                    readable = true;
                    break;
                case 5:
                    readable = true;
                    executable = true;
                    break;
                case 6:
                    readable = true;
                    writable = true;
                    break;
                case 7:
                    writable = true;
                    readable = true;
                    executable = true;
                    break;
                default:
                    logger.warn( "ignore file mode " + userMode );
            }
        }
        catch ( NumberFormatException e )
        {
            throw new IllegalArgumentException( " file mode must contains only number " + mode );
        }

        // handle all perm
        try
        {
            int allMode = Integer.valueOf( modes.get( mode.length() == 4 ? 3 : 2 ) );
            switch ( allMode )
            {
                case 0:
                    break;
                case 1:
                    executable = true;
                    ownerOnlyExecutable = false;
                    break;
                case 2:
                    writable = true;
                    ownerOnlyWritable = false;
                    break;
                case 3:
                    writable = true;
                    executable = true;
                    ownerOnlyExecutable = false;
                    ownerOnlyWritable = false;
                    break;
                case 4:
                    readable = true;
                    ownerOnlyReadable = false;
                    break;
                case 5:
                    readable = true;
                    executable = true;
                    ownerOnlyReadable = false;
                    ownerOnlyExecutable = false;
                    break;
                case 6:
                    readable = true;
                    ownerOnlyReadable = false;
                    writable = true;
                    ownerOnlyWritable = false;
                    break;
                case 7:
                    writable = true;
                    readable = true;
                    executable = true;
                    ownerOnlyReadable = false;
                    ownerOnlyExecutable = false;
                    ownerOnlyWritable = false;
                    break;
                default:
                    logger.warn( "ignore file mode " + allMode );
            }
        }
        catch ( NumberFormatException e )
        {
            throw new IllegalArgumentException( " file mode must contains only number " + mode );
        }

        return new FilePermission( executable, ownerOnlyExecutable, ownerOnlyReadable, readable, ownerOnlyWritable,
                                   writable );

    }

}
