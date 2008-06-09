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
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.Constant;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class TestFilteredIterator extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestFilteredIterator(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFilteredIterator.class);
    }

    public Object makeFunctor() {
        List list = new ArrayList();
        list.add("xyzzy");
        return FilteredIterator.filter(list.iterator(),Constant.truePredicate());
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
        list = new ArrayList();
        evens = new ArrayList();
        for (int i=0;i<10;i++) {
            list.add(new Integer(i));
            if (i%2 == 0) {
                evens.add(new Integer(i));
            }
        }
    }

    public void tearDown() throws Exception {
        super.tearDown();
        list = null;
        evens = null;
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testSomePass() {
        Iterator expected = evens.iterator();
        Iterator testing = new FilteredIterator(list.iterator(),isEven);
        while(expected.hasNext()) {
            assertTrue(testing.hasNext());
            assertEquals(expected.next(),testing.next());
        }
        assertTrue(!testing.hasNext());
    }

    public void testAllPass() {
        Iterator expected = evens.iterator();
        Iterator testing = new FilteredIterator(evens.iterator(),isEven);
        while(expected.hasNext()) {
            assertTrue(testing.hasNext());
            assertEquals(expected.next(),testing.next());
        }
        assertTrue(!testing.hasNext());
    }

    public void testAllPass2() {
        Iterator expected = list.iterator();
        Iterator testing = new FilteredIterator(list.iterator(),Constant.truePredicate());
        while(expected.hasNext()) {
            assertTrue(testing.hasNext());
            assertEquals(expected.next(),testing.next());
        }
        assertTrue(!testing.hasNext());
    }

    public void testEmptyList() {
        Iterator testing = new FilteredIterator(Collections.EMPTY_LIST.iterator(),isEven);
        assertTrue(!testing.hasNext());
    }

    public void testNonePass() {
        Iterator testing = new FilteredIterator(Collections.EMPTY_LIST.iterator(),Constant.falsePredicate());
        assertTrue(!testing.hasNext());
    }

    public void testNextWithoutHasNext() {
        Iterator testing = new FilteredIterator(list.iterator(),isEven);
        Iterator expected = evens.iterator();
        while(expected.hasNext()) {
            assertEquals(expected.next(),testing.next());
        }
        assertTrue(!(testing.hasNext()));
    }

    public void testNextAfterEndOfList() {
        Iterator testing = new FilteredIterator(list.iterator(),isEven);
        Iterator expected = evens.iterator();
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
        Iterator testing = new FilteredIterator(Collections.EMPTY_LIST.iterator(),isEven);
        try {
            testing.next();
            fail("ExpectedNoSuchElementException");
        } catch(NoSuchElementException e) {
            // expected
        }
    }

    public void testRemoveBeforeNext() {
        Iterator testing = new FilteredIterator(list.iterator(),isEven);
        try {
            testing.remove();
            fail("IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

    public void testRemoveAfterNext() {
        Iterator testing = new FilteredIterator(list.iterator(),isEven);
        testing.next();
        testing.remove();
        try {
            testing.remove();
            fail("IllegalStateException");
        } catch(IllegalStateException e) {
            // expected
        }
    }

    public void testRemoveSome() {
        Iterator testing = new FilteredIterator(list.iterator(),isEven);
        while(testing.hasNext()) {
            testing.next();
            testing.remove();
        }
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            assertTrue(! isEven.test(iter.next()) );
        }
    }

    public void testRemoveAll() {
        Iterator testing = new FilteredIterator(list.iterator(),Constant.truePredicate());
        while(testing.hasNext()) {
            testing.next();
            testing.remove();
        }
        assertTrue(list.isEmpty());
    }

    public void testRemoveWithoutHasNext() {
        Iterator testing = new FilteredIterator(list.iterator(),Constant.truePredicate());
        for (int i=0,m = list.size();i<m;i++) {
            testing.next();
            testing.remove();
        }
        assertTrue(list.isEmpty());
    }

    public void testFilterWithNullIteratorReturnsNull() {
        assertNull(FilteredIterator.filter(null,Constant.truePredicate()));
    }

    public void testFilterWithNullPredicateReturnsIdentity() {
        Iterator iter = list.iterator();
        assertSame(iter,FilteredIterator.filter(iter,null));
    }

    public void testConstructorProhibitsNull() {
        try {
            new FilteredIterator(null,null);
            fail("ExpectedNullPointerException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            new FilteredIterator(null,Constant.truePredicate());
            fail("ExpectedNullPointerException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            new FilteredIterator(list.iterator(),null);
            fail("ExpectedNullPointerException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }


    // Attributes
    // ------------------------------------------------------------------------
    private List list = null;
    private List evens = null;
    private UnaryPredicate isEven = new UnaryPredicate() {
        public boolean test(Object obj) {
            return ((Number) obj).intValue() % 2 == 0;
        }
    };

}
