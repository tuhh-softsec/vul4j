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

import org.codehaus.plexus.logging.Logger;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import junit.framework.TestCase;

/**
 * @author Olivier Lamy
 */
public class FilePermissionUtilsTest
    extends TestCase
{

    Logger getLogger()
    {
        return new ConsoleLogger( Logger.LEVEL_DEBUG, "foo" );

    }

    public void testOnlyWritableOnlyUser() throws Exception
    {
        FilePermission fp = FilePermissionUtils.getFilePermissionFromMode( "200", getLogger() );
        assertTrue( fp.isWritable() );
        assertTrue( fp.isOwnerOnlyWritable() );
        assertFalse( fp.isExecutable() );
        assertTrue( fp.isOwnerOnlyExecutable() );
        assertFalse( fp.isReadable() );
    }

    public void testExecAndRead()
    {
        FilePermission fp = FilePermissionUtils.getFilePermissionFromMode( "500", getLogger() );
        assertFalse( fp.isWritable() );
        assertTrue( fp.isOwnerOnlyWritable() );
        assertTrue( fp.isExecutable() );
        assertTrue( fp.isOwnerOnlyExecutable() );
        assertTrue( fp.isReadable() );
    }

    public void testAllUser()
    {
        FilePermission fp = FilePermissionUtils.getFilePermissionFromMode( "700", getLogger() );
        assertTrue( fp.isWritable() );
        assertTrue( fp.isOwnerOnlyWritable() );
        assertTrue( fp.isExecutable() );
        assertTrue( fp.isOwnerOnlyExecutable() );
        assertTrue( fp.isReadable() );
    }

    public void testAllAllUser()
    {
        FilePermission fp = FilePermissionUtils.getFilePermissionFromMode( "707", getLogger() );
        assertTrue( fp.isWritable() );
        assertFalse( fp.isOwnerOnlyWritable() );
        assertTrue( fp.isExecutable() );
        assertFalse( fp.isOwnerOnlyExecutable() );
        assertTrue( fp.isReadable() );
        assertFalse( fp.isOwnerOnlyReadable() );
    }

}
