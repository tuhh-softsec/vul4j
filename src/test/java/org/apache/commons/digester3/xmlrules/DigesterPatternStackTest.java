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

package org.apache.commons.digester3.xmlrules;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * This test case tests the behavior of DigesterRuleParser.PatternStack, a specialized stack whose toString() method
 * returns a /-separated representation of the stack's elements. The tests ensure that
 * DigesterRuleParser.PatternStack.toString() returns the properly formatted string.
 */
public class DigesterPatternStackTest
{

    private PatternStack patternStack = new PatternStack();

    @Before
    public void setUp()
    {
        patternStack.clear();
    }

    @Test
    public void test1()
        throws Exception
    {
        assertEquals( "", patternStack.toString() );
    }

    @Test
    public void test2()
        throws Exception
    {
        patternStack.push( "A" );
        assertEquals( "A", patternStack.toString() );
        patternStack.pop();
        assertEquals( "", patternStack.toString() );
    }

    @Test
    public void test3()
        throws Exception
    {
        patternStack.push( "A" );
        patternStack.push( "B" );
        assertEquals( "A/B", patternStack.toString() );

        patternStack.pop();
        assertEquals( "A", patternStack.toString() );
    }

    @Test
    public void test4()
        throws Exception
    {
        patternStack.push( "" );
        assertEquals( "", patternStack.toString() );

        patternStack.push( "" );
        assertEquals( "", patternStack.toString() );
    }

    @Test
    public void test5()
        throws Exception
    {
        patternStack.push( "A" );
        assertEquals( "A", patternStack.toString() );

        patternStack.push( "" );
        patternStack.push( "" );
        assertEquals( "A", patternStack.toString() );

    }

    @Test
    public void test6()
        throws Exception
    {
        patternStack.push( "A" );
        patternStack.push( "B" );
        patternStack.clear();
        assertEquals( "", patternStack.toString() );
    }

    @Test
    public void test7()
        throws Exception
    {
        patternStack.push( "///" );
        assertEquals( "///", patternStack.toString() );

        patternStack.push( "/" );
        assertEquals( "/////", patternStack.toString() );

        patternStack.pop();
        assertEquals( "///", patternStack.toString() );

        patternStack.pop();
        assertEquals( "", patternStack.toString() );
    }

}
