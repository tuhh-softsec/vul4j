/*
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
package org.apache.commons.functor.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.BinaryPredicate;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestLeftIdentity extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestLeftIdentity(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestLeftIdentity.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return LeftIdentity.FUNCTION;
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testEvaluate() throws Exception {
        BinaryFunction<Object, Object, Object> f = LeftIdentity.FUNCTION;
        assertNull(f.evaluate(null,null));
        assertNull(f.evaluate(null,"xyzzy"));
        assertEquals("xyzzy",f.evaluate("xyzzy","abcdefg"));
        assertEquals("xyzzy",f.evaluate("xyzzy",null));
        assertEquals(new Integer(3),f.evaluate(new Integer(3),null));
        Object obj = new Long(12345L);
        assertSame(obj,f.evaluate(obj,null));
        assertSame(obj,f.evaluate(obj,obj));
    }

    public void testTest() throws Exception {
        BinaryPredicate<Boolean, Object> p = LeftIdentity.PREDICATE;
        assertTrue(p.test(Boolean.TRUE,null));
        assertTrue(!p.test(Boolean.FALSE,null));
        try {
            p.test(null, null);
            fail("Expected NullPointerException");
        } catch (NullPointerException npe) {
        }
    }

    public void testEquals() throws Exception {
        BinaryFunction<Object, Object, Object> f = LeftIdentity.<Object, Object>function();
        assertEquals(f,f);
        assertObjectsAreEqual(f,LeftIdentity.FUNCTION);
        assertObjectsAreEqual(f,LeftIdentity.FUNCTION);
        assertObjectsAreNotEqual(f,RightIdentity.FUNCTION);
        assertObjectsAreNotEqual(f,Constant.of("abcde"));
        assertObjectsAreNotEqual(f,Constant.of(true));
    }

    public void testConstant() throws Exception {
        assertEquals(LeftIdentity.function(),LeftIdentity.function());
    }
}
