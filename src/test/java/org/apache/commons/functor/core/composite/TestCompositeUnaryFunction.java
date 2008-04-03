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
package org.apache.commons.functor.core.composite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.Identity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestCompositeUnaryFunction extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestCompositeUnaryFunction(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestCompositeUnaryFunction.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new CompositeUnaryFunction(new Identity(),new Constant(new Integer(3)));
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
        // empty composite acts like identity function
        assertEquals("xyzzy",(new CompositeUnaryFunction()).evaluate("xyzzy"));
        assertNull(null,(new CompositeUnaryFunction()).evaluate(null));

        assertEquals(new Integer(4),(new CompositeUnaryFunction(new Constant(new Integer(4)))).evaluate(null));

        assertEquals(new Integer(4),(new CompositeUnaryFunction(new Constant(new Integer(4)),new Constant(new Integer(3)))).evaluate("xyzzy"));
        assertEquals(new Integer(3),(new CompositeUnaryFunction(new Constant(new Integer(3)),new Constant(new Integer(4)))).evaluate("xyzzy"));
    }

    public void testOf() throws Exception {
        CompositeUnaryFunction f = new CompositeUnaryFunction();
        assertNull(f.evaluate(null));
        for (int i=0;i<10;i++) {
            f.of(new UnaryFunction() {
                    public Object evaluate(Object obj) {
                        if (obj instanceof Integer) {
                            return new Integer((((Integer) obj).intValue())+1);
                        } else {
                            return new Integer(1);
                        }
                    }
                });
            assertEquals(new Integer(i+1),f.evaluate(null));
        }
    }

    public void testEquals() throws Exception {
        CompositeUnaryFunction f = new CompositeUnaryFunction();
        assertEquals(f,f);
        CompositeUnaryFunction g = new CompositeUnaryFunction();
        assertObjectsAreEqual(f,g);

        for (int i=0;i<3;i++) {
            f.of(new Constant("x"));
            assertObjectsAreNotEqual(f,g);
            g.of(new Constant("x"));
            assertObjectsAreEqual(f,g);
            f.of(new CompositeUnaryFunction(new Constant("y"),new Constant("z")));
            assertObjectsAreNotEqual(f,g);
            g.of(new CompositeUnaryFunction(new Constant("y"),new Constant("z")));
            assertObjectsAreEqual(f,g);
        }

        assertObjectsAreNotEqual(f,new Constant("y"));
    }

}
