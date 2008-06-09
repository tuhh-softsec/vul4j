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
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.core.NoOp;

/**
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
public class TestUnarySequence extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestUnarySequence(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestUnarySequence.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new UnarySequence(new NoOp(),new NoOp());
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
        UnarySequence seq = new UnarySequence();
        seq.run(null);
        seq.run("xyzzy");
    }

    public void testRunOne() throws Exception {
        RunCounter counter = new RunCounter();
        UnarySequence seq = new UnarySequence(counter);
        assertEquals(0,counter.count);
        seq.run(null);
        assertEquals(1,counter.count);
        seq.run("xyzzy");
        assertEquals(2,counter.count);
    }

    public void testRunTwo() throws Exception {
        RunCounter[] counter = { new RunCounter(), new RunCounter() };
        UnarySequence seq = new UnarySequence(counter[0],counter[1]);
        assertEquals(0,counter[0].count);
        assertEquals(0,counter[1].count);
        seq.run(null);
        assertEquals(1,counter[0].count);
        assertEquals(1,counter[1].count);
        seq.run("xyzzy");
        assertEquals(2,counter[0].count);
        assertEquals(2,counter[1].count);
    }

    public void testThen() throws Exception {
        List list = new ArrayList();
        UnarySequence seq = new UnarySequence();
        seq.run(null);
        for (int i=0;i<10;i++) {
            RunCounter counter = new RunCounter();
            seq.then(counter);
            list.add(counter);
            seq.run("xyzzy");
            for (int j=0;j<list.size();j++) {
                assertEquals(list.size()-j,(((RunCounter)(list.get(j))).count));
            }
        }
    }

    public void testEquals() throws Exception {
        UnarySequence p = new UnarySequence();
        assertEquals(p,p);
        UnarySequence q = new UnarySequence();
        assertObjectsAreEqual(p,q);

        for (int i=0;i<3;i++) {
            p.then(new NoOp());
            assertObjectsAreNotEqual(p,q);
            q.then(new NoOp());
            assertObjectsAreEqual(p,q);
            p.then(new UnarySequence(new NoOp(),new NoOp()));
            assertObjectsAreNotEqual(p,q);
            q.then(new UnarySequence(new NoOp(),new NoOp()));
            assertObjectsAreEqual(p,q);
        }

        assertObjectsAreNotEqual(p,new NoOp());
    }

    // Classes
    // ------------------------------------------------------------------------

    static class RunCounter implements UnaryProcedure {
        public void run(Object that) {
            count++;
        }
        public int count = 0;
    }
}
