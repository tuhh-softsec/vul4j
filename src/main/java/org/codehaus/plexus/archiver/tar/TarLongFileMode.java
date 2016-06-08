/*
 * Copyright  2000-2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.codehaus.plexus.archiver.tar;

/**
 * Set of options for long file handling in the task.
 */
public enum TarLongFileMode
{

    warn,
    fail,
    truncate,
    gnu,
    omit,
    posix,
    posix_warn;

    /**
     * @return true if value is "truncate".
     */
    public boolean isTruncateMode()
    {
        return truncate.equals( this );
    }

    /**
     * @return true if value is "warn".
     */
    public boolean isWarnMode()
    {
        return warn.equals( this );
    }

    /**
     * @return true if value is "gnu".
     */
    public boolean isGnuMode()
    {
        return gnu.equals( this );
    }

    /**
     * @return true if value is "fail".
     */
    public boolean isFailMode()
    {
        return fail.equals( this );
    }

    /**
     * @return true if value is "omit".
     */
    public boolean isOmitMode()
    {
        return omit.equals( this );
    }

    /**
     * @return true if value is "posix".
     */
    public boolean isPosixMode()
    {
        return posix.equals( this );
    }

    /**
     * @return true if value is "posix_warn".
     */
    public boolean isPosixWarnMode()
    {
        return posix_warn.equals( this );
    }

}
