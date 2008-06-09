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
package org.apache.commons.functor.generator.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;

/**
 * @version $Revision$ $Date$
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class TestLongRange extends BaseFunctorTest {
    // Conventional
    // ------------------------------------------------------------------------

    public TestLongRange(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(TestLongRange.class);
    }

    protected Object makeFunctor() throws Exception {
        return new LongRange(10, 20);
    }

    // Tests
    // ------------------------------------------------------------------------

    public void testGenerateListExample() {
        // generates a collection of Integers from 0 (inclusive) to 10 (exclusive)
        {
            List list = (List)(new LongRange(0,10).to(new ArrayList()));
            for (int i=0;i<10;i++) {
                assertEquals(new Long(i),list.get(i));
            }
        }

        // generates a collection of Integers from 10 (inclusive) to 0 (exclusive)
        {
            List list = (List)(new LongRange(10,0).to(new ArrayList()));
            for (int i=10;i>0;i--) {
                assertEquals(new Long(i),list.get(10-i));
            }
        }
    }

    public void testStepChecking() {
        {
            new LongRange(2, 2, 0); // step of 0 is ok when range is empty
        }
        {
            new LongRange(2, 2, 1); // positive step is ok when range is empty
        }
        {
            new LongRange(2, 2, -1); // negative step is ok when range is empty
        }
        {
            new LongRange(0, 1, 10); // big steps are ok
        }
        {
            new LongRange(1, 0, -10); // big steps are ok
        }
        try {
            new LongRange(0, 1, 0);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            new LongRange(0, 1, -1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
        try {
            new LongRange(0, -1, 1);
            fail("Expected IllegalArgumentException");
        } catch(IllegalArgumentException e) {
            // expected
        }
    }

    public void testObjectConstructor() {
        LongRange range = new LongRange(new Long(0), new Long(5));
        assertEquals("[0, 1, 2, 3, 4]", range.toCollection().toString());
        range = new LongRange(new Integer(0), new Long(5), new Long(1));
        assertEquals("[0, 1, 2, 3, 4]", range.toCollection().toString());
    }


    public void testReverseStep() {
        LongRange range = new LongRange(10, 0, -2);
        assertEquals("[10, 8, 6, 4, 2]", range.toCollection().toString());
        assertEquals("[10, 8, 6, 4, 2]", range.toCollection().toString());
    }

    public void testStep() {
        LongRange range = new LongRange(0, 10, 2);
        assertEquals("[0, 2, 4, 6, 8]", range.toCollection().toString());
        assertEquals("[0, 2, 4, 6, 8]", range.toCollection().toString());
    }

    public void testForwardRange() {
        LongRange range = new LongRange(0, 5);
        assertEquals("[0, 1, 2, 3, 4]", range.toCollection().toString());
        assertEquals("[0, 1, 2, 3, 4]", range.toCollection().toString());
    }

    public void testReverseRange() {
        LongRange range = new LongRange(5, 0);
        assertEquals("[5, 4, 3, 2, 1]", range.toCollection().toString());
        assertEquals("[5, 4, 3, 2, 1]", range.toCollection().toString());
    }

    public void testEdgeCase() {
        LongRange range = new LongRange(Long.MAX_VALUE - 3L, Long.MAX_VALUE);
        assertEquals("[9223372036854775804, 9223372036854775805, 9223372036854775806]", range.toCollection().toString());
        assertEquals("[9223372036854775804, 9223372036854775805, 9223372036854775806]", range.toCollection().toString());
    }

    public void testEquals() {
        LongRange range = new LongRange(1, 5);
        assertObjectsAreEqual(range, range);
        assertObjectsAreEqual(range, new LongRange(1, 5));
        assertObjectsAreEqual(range, new LongRange(1, 5, 1));
        assertObjectsAreEqual(range, new LongRange(new Integer(1), new Long(5)));
        assertObjectsAreEqual(range, new LongRange(new Long(1), new Short((short)5), new Long(1)));
    }

}