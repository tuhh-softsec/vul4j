package org.codehaus.plexus.archiver.tar;

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

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.util.EnumeratedAttribute;

/**
 * Set of options for long file handling in the task.
 */
public class TarLongFileMode
    extends EnumeratedAttribute
{

    /**
     * permissible values for longfile attribute
     */
    public static final String
        WARN = "warn",
        FAIL = "fail",
        TRUNCATE = "truncate",
        GNU = "gnu",
        OMIT = "omit",
        POSIX = "posix",
        POSIX_WARN = "posix_warn";

    private final String[] validModes = {WARN, FAIL, TRUNCATE, GNU, OMIT, POSIX, POSIX_WARN};

    /**
     * Constructor, defaults to "warn"
     */
    public TarLongFileMode()
    {
        super();
        try
        {
            setValue( WARN );
        }
        catch ( ArchiverException ae )
        {
            //Do nothing
        }
    }

    /**
     * @return the possible values for this enumerated type.
     */
    public String[] getValues()
    {
        return validModes;
    }

    /**
     * @return true if value is "truncate".
     */
    public boolean isTruncateMode()
    {
        return TRUNCATE.equalsIgnoreCase( getValue() );
    }

    /**
     * @return true if value is "warn".
     */
    public boolean isWarnMode()
    {
        return WARN.equalsIgnoreCase( getValue() );
    }

    /**
     * @return true if value is "gnu".
     */
    public boolean isGnuMode()
    {
        return GNU.equalsIgnoreCase( getValue() );
    }

    /**
     * @return true if value is "fail".
     */
    public boolean isFailMode()
    {
        return FAIL.equalsIgnoreCase( getValue() );
    }

    /**
     * @return true if value is "omit".
     */
    public boolean isOmitMode()
    {
        return OMIT.equalsIgnoreCase( getValue() );
    }

    /**
     * @return true if value is "posix".
     */
    public boolean isPosixMode()
    {
        return POSIX.equalsIgnoreCase( getValue() );
    }

    /**
     * @return true if value is "posix_warn".
     */
    public boolean isPosixWarnMode()
    {
        return POSIX_WARN.equalsIgnoreCase( getValue() );
    }
}
