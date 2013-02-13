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

public class PosixTarEntryTest
    extends TestCase
{
    /**
     * demonstrates bug 18105 on OSes with os.name shorter than 7.
     */
    public void testFileConstructor()
    {
        new PosixTarEntry( new java.io.File( "/foo" ) );
    }
    
    public void testPathSplittig()
    {
    	PosixTarEntry pte = new PosixTarEntry( "/a/very/long/path/to/file/but/not/too/long/so/it/does/not/exceed/TarConstants.NAMELEN/plus/TarConstants.POSIX_PREFIXLEN" );
    	assertEquals("/a/very/long/path/to", pte.prefix.toString());
    	assertEquals("file/but/not/too/long/so/it/does/not/exceed/TarConstants.NAMELEN/plus/TarConstants.POSIX_PREFIXLEN", pte.name.toString());
    	
    	pte = new PosixTarEntry( "this/path/has/exactly/100/characters/one/two/three/four/five/six/seven/eight/nine/ten/eleven/twelve/" );
    	assertEquals("this", pte.prefix.toString());
    	assertEquals("path/has/exactly/100/characters/one/two/three/four/five/six/seven/eight/nine/ten/eleven/twelve/", pte.name.toString());

    	pte = new PosixTarEntry( "this/path/has/exactly/99/characters/one/two/three/four/five/six/seven/eight/nine/ten/eleven/twelve/" );
    	assertEquals("", pte.prefix.toString());
    	assertEquals("this/path/has/exactly/99/characters/one/two/three/four/five/six/seven/eight/nine/ten/eleven/twelve/", pte.name.toString());
    }
}
