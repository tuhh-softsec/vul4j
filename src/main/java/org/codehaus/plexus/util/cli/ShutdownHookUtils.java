package org.codehaus.plexus.util.cli;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.security.AccessControlException;

/**
 * A shutdown hook that does not throw any exceptions upon container startup/shutdown or security manager
 * restrictions.
 *
 * Incorrect usage of the hook itself may still throw an exception.
 *
 * @author Kristian Rosenvold
 */
class ShutdownHookUtils
{

    public static void addShutDownHook( Thread hook )
    {
        try
        {
            Runtime.getRuntime().addShutdownHook( hook );
        }
        catch ( IllegalStateException ignore )
        {
        }
        catch ( AccessControlException ignore )
        {
        }


    }

    public static void removeShutdownHook( Thread hook )
    {
        try
        {
            Runtime.getRuntime().removeShutdownHook( hook );
        }
        catch ( IllegalStateException ignore )
        {
        }
        catch ( AccessControlException ignore )
        {
        }
    }

}
