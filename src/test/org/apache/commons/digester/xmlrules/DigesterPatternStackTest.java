/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 

package org.apache.commons.digester.xmlrules;


import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * This test case tests the behavior of
 * DigesterRuleParser.PatternStack, a specialized stack whose
 * toString() method returns a /-separated representation of the
 * stack's elements. The tests ensure that
 * DigesterRuleParser.PatternStack.toString() returns the properly
 * formatted string.
 */
public class DigesterPatternStackTest extends TestCase {

    public DigesterPatternStackTest(String testName) {
        super(testName);
    }

    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite(DigesterPatternStackTest.class);

        return suite;
    }

    private DigesterRuleParser.PatternStack stack;

    public void setUp() {
        DigesterRuleParser parser = new DigesterRuleParser();
        stack = parser.patternStack;
    }

    public void test1() throws Exception {
        assertEquals("", stack.toString());
    }

    public void test2() throws Exception {
        stack.push("A");
        assertEquals("A", stack.toString());
        stack.pop();
        assertEquals("", stack.toString());
    }

    public void test3() throws Exception {
        stack.push("A");
        stack.push("B");
        assertEquals("A/B", stack.toString());

        stack.pop();
        assertEquals("A", stack.toString());
    }

    public void test4() throws Exception {
        stack.push("");
        assertEquals("", stack.toString());

        stack.push("");
        assertEquals("", stack.toString());
    }

    public void test5() throws Exception {
        stack.push("A");
        assertEquals("A", stack.toString());

        stack.push("");
        stack.push("");
        assertEquals("A", stack.toString());

    }

    public void test6() throws Exception {
        stack.push("A");
        stack.push("B");
        stack.clear();
        assertEquals("", stack.toString());
    }

    public void test7() throws Exception {
        stack.push("///");
        assertEquals("///", stack.toString());

        stack.push("/");
        assertEquals("/////", stack.toString());

        stack.pop();
        assertEquals("///", stack.toString());

        stack.pop();
        assertEquals("", stack.toString());
    }

}
