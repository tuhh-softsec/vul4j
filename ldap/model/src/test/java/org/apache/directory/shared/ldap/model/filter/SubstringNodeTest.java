/*
 *   Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing,
 *   software distributed under the License is distributed on an
 *   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *   KIND, either express or implied.  See the License for the
 *   specific language governing permissions and limitations
 *   under the License.
 *
 */
package org.apache.directory.shared.ldap.model.filter;


import com.mycila.junit.concurrent.Concurrency;
import com.mycila.junit.concurrent.ConcurrentJunitRunner;
import org.apache.directory.shared.ldap.model.filter.SubstringNode;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Pattern;

import static org.junit.Assert.*;


/**
 * Unit tests class SubstringNode.
 *
 * @author <a href="mailto:dev@directory.apache.org">Apache Directory Project</a>
 */
@RunWith( ConcurrentJunitRunner.class )
@Concurrency()
public class SubstringNodeTest
{
    @Test
    public void testGetRegexpEmpty() throws Exception
    {
        Pattern pattern = SubstringNode.getRegex("", new String[]
                {""}, "");

        boolean b1 = pattern.matcher( "" ).matches();

        assertTrue( b1 );
    }


    @Test
    public void testGetRegexpInitial() throws Exception
    {
        Pattern pattern = SubstringNode.getRegex("Test", new String[]
                {""}, "");

        boolean b1 = pattern.matcher( "Test just a test" ).matches();

        assertTrue( b1 );

        boolean b3 = pattern.matcher( "test just a test" ).matches();

        assertFalse( b3 );
    }


    @Test
    public void testGetRegexpFinal() throws Exception
    {
        Pattern pattern = SubstringNode.getRegex("", new String[]
                {""}, "Test");

        boolean b1 = pattern.matcher( "test just a Test" ).matches();

        assertTrue( b1 );

        boolean b3 = pattern.matcher( "test just a test" ).matches();

        assertFalse( b3 );
    }


    @Test
    public void testGetRegexpAny() throws Exception
    {
        Pattern pattern = SubstringNode.getRegex("", new String[]
                {"just", "a"}, "");

        boolean b1 = pattern.matcher( "test just a Test" ).matches();

        assertTrue( b1 );

        boolean b3 = pattern.matcher( "test just A test" ).matches();

        assertFalse( b3 );
    }


    @Test
    public void testGetRegexpFull() throws Exception
    {
        Pattern pattern = SubstringNode.getRegex("Test", new String[]
                {"just", "a"}, "test");

        boolean b1 = pattern.matcher( "Test (this is) just (truly !) a (little) test" ).matches();

        assertTrue( b1 );

        boolean b3 = pattern.matcher( "Test (this is) just (truly !) A (little) test" ).matches();

        assertFalse( b3 );
    }


    /**
     * Tests StringTools.getRegex() with some LDAP filter special characters.
     */
    @Test
    public void testGetRegexpWithLdapFilterSpecialChars() throws Exception
    {
        Pattern[] patterns = new Pattern[]
            { SubstringNode.getRegex(null, new String[]
                    {"("}, null), SubstringNode.getRegex(null, new String[]
                    {")"}, null), SubstringNode.getRegex(null, new String[]
                    {"*"}, null), SubstringNode.getRegex(null, new String[]
                    {"\\"}, null), };

        for ( Pattern pattern : patterns )
        {
            boolean b1 = pattern.matcher( "a(b*c\\d)e" ).matches();
            assertTrue( b1 );

            boolean b3 = pattern.matcher( "Test test" ).matches();
            assertFalse( b3 );
        }
    }
}
