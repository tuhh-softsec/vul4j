package org.codehaus.plexus.util;

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

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Convenience class to handle throwable stacktraces
 *
 * <p>Created on 18/06/2003</p>
 *
 * @author <a href="mailto:bert@tuaworks.co.nz">Bert van Brakel</a>
 * @version $Revision$
 */
public class Tracer
{

    /**
     * Constructor
     *
     *
     */
    private Tracer()
    {
        super();
    }

    /**
     * Return the throwable stack trace as a string
     * */
    public static String traceToString( Throwable t )
    {
        if ( t == null )
        {
            return null;
        }
        StringWriter sw = new StringWriter();
        t.printStackTrace( new PrintWriter( sw ) );
        return sw.toString();
    }

}
