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
package org.apache.commons.functor.core.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.core.Identity;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class TestTransformedIterator extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestTransformedIterator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestTransformedIterator.class);
    }

    public Object makeFunctor() {
        List list = new ArrayList();
        list.add("xyzzy");
        return TransformedIterator.transform(list.iterator(),Identity.instance());
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
        list = new ArrayList();
        negatives = new ArrayList();
        for (int i=0;i<10;i++) {
            list.add(new Integer(i));
            negatives.add(new Integer(i*-1));
        }
    }

    public void tearDown() throws Exception {
        super.tearDown();
        list = null;
        negatives = null;
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testBasicTransform() {
        Iterator expected = negatives.iterator();
        Iterator testing = new TransformedIterator(list.iterator(),negate);
        while(expected.hasNext()) {
            assertTrue(testing.hasNext());
            assertEquals(expected.next(),testing.next());
        }
        assertTrue(!testing.hasNext());
    }

    public void testEmptyList() {
        Iterator testing = new TransformedIterator(Collections.EMPTY_LIST.iterator(),negate);
        assertTrue(!testing.hasNext());
    }

    public void testNextWithoutHasNext() {
        Iterator testing = new TransformedIterator(list.iterator(),negate);
        Iterator expected = negatives.iterator();
        while(expected.hasNext()) {
            assertEquals(expected.next(),testing.next());
        }
        assertTrue(!(testing.hasNext()));
    }

    public void testNextAfterEndOfList() {
        Iterator testing = new TransformedIterator(list.iterator(),negate);
        Iterator expected = negatives.iterator();
        while(expected.hasNext()) {
            assertEquals(expected.next(),testing.next());
        }
        try {
            testing.next();
            fail("ExpectedNoSuchElementException");
        } catch(NoSuchElementException e) {
            // expected
        }
    }

    public void testNextOnEmptyList() {
        Iterator testing = new TransformedIterator(Collections.EMPTY_LIST.iterator(),negate);
        try {
            testing.next();
            fail("ExpectedNoSuchElementException");
        } catch(NoSuchElementException e) {
            // expected
        }
    }

    public void testRemoveBeforeNext() {
        Iterator testing = new TransformedIterator(list.iterator(),negate);
        try {
            testing.remove();
            fail("IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

    public void testRemoveAfterNext() {
        Iterator testing = new TransformedIterator(list.iterator(),negate);
        testing.next();
        testing.remove();
        try {
            testing.remove();
            fail("IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

    public void testRemoveAll() {
        Iterator testing = new TransformedIterator(list.iterator(),negate);
        while(testing.hasNext()) {
            testing.next();
            testing.remove();
        }
        assertTrue(list.isEmpty());
    }

    public void testRemoveWithoutHasNext() {
        Iterator testing = new TransformedIterator(list.iterator(),negate);
        for (int i=0,m = list.size();i<m;i++) {
            testing.next();
            testing.remove();
        }
        assertTrue(list.isEmpty());
    }

    public void testTransformWithNullIteratorReturnsNull() {
        assertNull(TransformedIterator.transform(null,negate));
    }

    public void testTransformWithNullPredicateReturnsIdentity() {
        Iterator iter = list.iterator();
        assertSame(iter,TransformedIterator.transform(iter,null));
    }

    public void testConstructorProhibitsNull() {
        try {
            new TransformedIterator(null,null);
            fail("ExpectedNullPointerException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            new TransformedIterator(null,negate);
            fail("ExpectedNullPointerException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            new TransformedIterator(list.iterator(),null);
            fail("ExpectedNullPointerException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }


    // Attributes
    // ------------------------------------------------------------------------
    private List list = null;
    private List negatives = null;
    private UnaryFunction negate = new UnaryFunction() {
        public Object evaluate(Object obj) {
            return new Integer(((Number) obj).intValue() * -1);
        }
    };

}
