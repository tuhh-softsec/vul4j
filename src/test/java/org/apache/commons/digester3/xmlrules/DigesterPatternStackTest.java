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

import static org.junit.Assert.*;

import org.apache.commons.digester3.xmlrules.DigesterRuleParser;
import org.junit.Before;
import org.junit.Test;

/**
 * This test case tests the behavior of DigesterRuleParser.PatternStack, a specialized stack whose toString() method
 * returns a /-separated representation of the stack's elements. The tests ensure that
 * DigesterRuleParser.PatternStack.toString() returns the properly formatted string.
 */
public class DigesterPatternStackTest
{

    private DigesterRuleParser parser;

    @Before
    public void setUp()
    {
        parser = new DigesterRuleParser();
    }

    @Test
    public void test1()
        throws Exception
    {
        assertEquals( "", parser.patternStack.toString() );
    }

    @Test
    public void test2()
        throws Exception
    {
        parser.patternStack.push( "A" );
        assertEquals( "A", parser.patternStack.toString() );
        parser.patternStack.pop();
        assertEquals( "", parser.patternStack.toString() );
    }

    @Test
    public void test3()
        throws Exception
    {
        parser.patternStack.push( "A" );
        parser.patternStack.push( "B" );
        assertEquals( "A/B", parser.patternStack.toString() );

        parser.patternStack.pop();
        assertEquals( "A", parser.patternStack.toString() );
    }

    @Test
    public void test4()
        throws Exception
    {
        parser.patternStack.push( "" );
        assertEquals( "", parser.patternStack.toString() );

        parser.patternStack.push( "" );
        assertEquals( "", parser.patternStack.toString() );
    }

    @Test
    public void test5()
        throws Exception
    {
        parser.patternStack.push( "A" );
        assertEquals( "A", parser.patternStack.toString() );

        parser.patternStack.push( "" );
        parser.patternStack.push( "" );
        assertEquals( "A", parser.patternStack.toString() );

    }

    @Test
    public void test6()
        throws Exception
    {
        parser.patternStack.push( "A" );
        parser.patternStack.push( "B" );
        parser.patternStack.clear();
        assertEquals( "", parser.patternStack.toString() );
    }

    @Test
    public void test7()
        throws Exception
    {
        parser.patternStack.push( "///" );
        assertEquals( "///", parser.patternStack.toString() );

        parser.patternStack.push( "/" );
        assertEquals( "/////", parser.patternStack.toString() );

        parser.patternStack.pop();
        assertEquals( "///", parser.patternStack.toString() );

        parser.patternStack.pop();
        assertEquals( "", parser.patternStack.toString() );
    }

}
