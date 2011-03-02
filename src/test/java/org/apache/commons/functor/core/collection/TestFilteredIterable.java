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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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
 */
@SuppressWarnings("unchecked")
public class TestFilteredIterable extends BaseFunctorTest {

    // Attributes
    // ------------------------------------------------------------------------
    private List<Integer> list = null;
    private List<Integer> evens = null;

    private UnaryPredicate<Integer> isEven = new UnaryPredicate<Integer>() {
        public boolean test(Integer obj) {
            return obj != null && obj % 2 == 0;
        }
    };

    // Conventional
    // ------------------------------------------------------------------------

    public TestFilteredIterable(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestFilteredIterable.class);
    }

    public Object makeFunctor() {
        List<String> list = new ArrayList<String>();
        list.add("xyzzy");
        return FilteredIterable.of(list);
    }

    // Lifecycle
    // ------------------------------------------------------------------------

    public void setUp() throws Exception {
        super.setUp();
        list = new ArrayList<Integer>();
        evens = new ArrayList<Integer>();
        for (int i = 0; i < 10; i++) {
            list.add(i);
            if (i % 2 == 0) {
                evens.add(i);
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
        Iterator<Integer> expected = evens.iterator();

        for (Integer i : FilteredIterable.of(list).retain(isEven)) {
            assertTrue(expected.hasNext());
            assertEquals(expected.next(), i);
        }
        assertFalse(expected.hasNext());
    }

    public void testAllPass() {
        Iterator<Integer> expected = evens.iterator();

        for (Integer i : FilteredIterable.of(evens)) {
            assertTrue(expected.hasNext());
            assertEquals(expected.next(), i);
        }
        assertFalse(expected.hasNext());
    }

    public void testAllPass2() {
        Iterator<Integer> expected = list.iterator();
        for (Integer i : FilteredIterable.of(list)) {
            assertTrue(expected.hasNext());
            assertEquals(expected.next(), i);
        }
        assertFalse(expected.hasNext());
    }

    public void testEmptyFilteredIterable() {
        assertFalse(FilteredIterable.empty().iterator().hasNext());
    }

    public void testEmptyList() {
        assertFalse(FilteredIterable.of(Collections.EMPTY_LIST).iterator().hasNext());
    }

    public void testNonePass() {
        assertFalse(FilteredIterable.of(list).retain(Constant.falsePredicate()).iterator().hasNext());
    }

    public void testNextWithoutHasNext() {
        Iterator<Integer> testing = FilteredIterable.of(list).retain(isEven).iterator();
        Iterator<Integer> expected = evens.iterator();
        while (expected.hasNext()) {
            assertEquals(expected.next(), testing.next());
        }
        assertFalse(testing.hasNext());
    }

    public void testNextAfterEndOfList() {
        Iterator<Integer> testing = FilteredIterable.of(list).retain(isEven).iterator();
        Iterator<Integer> expected = evens.iterator();
        while (expected.hasNext()) {
            assertEquals(expected.next(), testing.next());
        }
        try {
            testing.next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void testNextOnEmptyList() {
        try {
            FilteredIterable.empty().iterator().next();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void testRemoveBeforeNext() {
        Iterator<Integer> testing = FilteredIterable.of(list).retain(isEven).iterator();
        try {
            testing.remove();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testRemoveAfterNext() {
        Iterator<Integer> testing = FilteredIterable.of(list).retain(isEven).iterator();
        testing.next();
        testing.remove();
        try {
            testing.remove();
            fail("Expected IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    public void testRemoveSome() {
        Iterator<Integer> testing = FilteredIterable.of(list).retain(isEven).iterator();
        while (testing.hasNext()) {
            testing.next();
            testing.remove();
        }
        assertTrue(Collections.disjoint(list, evens));
    }

    public void testRemoveAll() {
        Iterator<Integer> testing = FilteredIterable.of(list).iterator();
        while (testing.hasNext()) {
            testing.next();
            testing.remove();
        }
        assertTrue(list.isEmpty());
    }

    public void testRemoveWithoutHasNext() {
        Iterator<Integer> testing = FilteredIterable.of(list).iterator();
        for (int i = 0, m = list.size(); i < m; i++) {
            testing.next();
            testing.remove();
        }
        assertTrue(list.isEmpty());
    }

    public void testFilterWithNullIteratorReturnsNull() {
        assertNull(FilteredIterable.of(null));
    }

    public void testRetainOneType() {
        Iterable<Object> objects = Arrays.asList((Object) "foo", "bar", "baz", 2L, BigInteger.ZERO);
        Iterable<String> strings = FilteredIterable.of(objects).retain(String.class);
        for (String s : strings) {
            assertTrue(s instanceof String);
        }
        Iterator<Number> iterator = FilteredIterable.of(objects).retain(Number.class).iterator();
        assertEquals(2L, iterator.next());
        assertEquals(BigInteger.ZERO, iterator.next());
    }

    public void testRetainMultipleTypes() {
        Iterable<Object> objects = Arrays.asList((Object) "foo", "bar", "baz", 2L, BigInteger.ZERO);
        Iterator<Object> iterator = FilteredIterable.of(objects).retain(Long.class, BigInteger.class).iterator();
        assertEquals(2L, iterator.next());
        assertEquals(BigInteger.ZERO, iterator.next());
    }

    public void testRetainNullType() {
        try {
            FilteredIterable.of(Collections.singleton("foo")).retain((Class<?>) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // okay
        }
    }

    public void testRetainNullTypes() {
        try {
            FilteredIterable.of(Collections.singleton("foo")).retain((Class<?>[]) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // okay
        }
    }

    public void testRetainNullPredicate() {
        try {
            FilteredIterable.of(Collections.singleton("foo")).retain((UnaryPredicate<String>) null);
            fail("Expected NullPointerException");
        } catch (NullPointerException e) {
            // okay
        }
    }
}
