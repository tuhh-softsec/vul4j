package org.codehaus.plexus.archiver.tar;

/*
 * Copyright  2003-2004 The Apache Software Foundation
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

import junit.framework.TestCase;

/**
 * @version $Revision$ $Date$
 *          from org.apache.ant.tools.tar.TarEntryTest v1.4
 * @since Ant 1.6
 */
public class TarEntryTest
    extends TestCase
{
    /**
     * demonstrates bug 18105 on OSes with os.name shorter than 7.
     */
    public void testFileConstructor()
    {
        new TarEntry( new java.io.File( "/foo" ) );
    }
}
