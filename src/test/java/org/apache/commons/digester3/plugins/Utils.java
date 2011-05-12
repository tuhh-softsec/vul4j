/* $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.digester3.plugins;

import java.io.IOException;
import java.io.InputStream;

/**
 * Entry point for all plugins package tests.
 * 
 * @author Simon Kitching
 */
public class Utils
{

    /**
     * Return an appropriate InputStream for the specified test file (which must be inside our current package.
     * 
     * @param caller is always "this" for the calling object.
     * @param name is the test file we want
     * @exception IOException if an input/output error occurs
     */
    public static InputStream getInputStream( Object caller, String name )
        throws IOException
    {
        return ( caller.getClass().getResourceAsStream( "/org/apache/commons/digester3/plugins/" + name ) );
    }
}
