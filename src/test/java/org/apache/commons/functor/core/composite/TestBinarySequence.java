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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.commons.functor.BaseFunctorTest;
import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class TestBinarySequence extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestBinarySequence(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestBinarySequence.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new BinarySequence(new NoOp(),new NoOp());
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

    public void testRunZero() throws Exception {
        BinarySequence seq = new BinarySequence();
        seq.run(null,null);
        seq.run("xyzzy","xyzzy");
    }

    public void testRunOne() throws Exception {
        RunCounter counter = new RunCounter();
        BinarySequence seq = new BinarySequence(counter);
        assertEquals(0,counter.count);
        seq.run(null,null);
        assertEquals(1,counter.count);
        seq.run("xyzzy","xyzzy");
        assertEquals(2,counter.count);
    }

    public void testRunTwo() throws Exception {
        RunCounter[] counter = { new RunCounter(), new RunCounter() };
        BinarySequence seq = new BinarySequence(counter[0],counter[1]);
        assertEquals(0,counter[0].count);
        assertEquals(0,counter[1].count);
        seq.run(null,null);
        assertEquals(1,counter[0].count);
        assertEquals(1,counter[1].count);
        seq.run("xyzzy","xyzzy");
        assertEquals(2,counter[0].count);
        assertEquals(2,counter[1].count);
    }

    public void testThen() throws Exception {
        List list = new ArrayList();
        BinarySequence seq = new BinarySequence();
        seq.run(null,null);
        for (int i=0;i<10;i++) {
            RunCounter counter = new RunCounter();
            seq.then(counter);
            list.add(counter);
            seq.run("xyzzy","xyzzy");
            for (int j=0;j<list.size();j++) {
                assertEquals(list.size()-j,(((RunCounter)(list.get(j))).count));
            }
        }
    }

    public void testEquals() throws Exception {
        BinarySequence p = new BinarySequence();
        assertEquals(p,p);
        BinarySequence q = new BinarySequence();
        assertObjectsAreEqual(p,q);

        for (int i=0;i<3;i++) {
            p.then(new NoOp());
            assertObjectsAreNotEqual(p,q);
            q.then(new NoOp());
            assertObjectsAreEqual(p,q);
            p.then(new BinarySequence(new NoOp(),new NoOp()));
            assertObjectsAreNotEqual(p,q);
            q.then(new BinarySequence(new NoOp(),new NoOp()));
            assertObjectsAreEqual(p,q);
        }

        assertObjectsAreNotEqual(p,new NoOp());
    }

    // Classes
    // ------------------------------------------------------------------------

    static class RunCounter implements BinaryProcedure {
        public void run(Object a, Object b) {
            count++;
        }
        public int count = 0;
    }
}
