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
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.adapter.BoundPredicate;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.NoOp;
import org.apache.commons.functor.core.collection.IsEmpty;

import java.util.LinkedList;
import java.util.List;

/**
 * @version $Revision$ $Date$
 * @author Herve Quiroz
 */
public class TestWhileDoProcedure extends BaseFunctorTest {

    // Conventional
    // ------------------------------------------------------------------------

    public TestWhileDoProcedure(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestWhileDoProcedure.class);
    }

    // Functor Testing Framework
    // ------------------------------------------------------------------------

    protected Object makeFunctor() {
        return new WhileDoProcedure(Constant.FALSE, NoOp.INSTANCE);
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
    public class ListRemoveFirstProcedure implements Procedure {
        protected List<Object> list;


        public ListRemoveFirstProcedure(List<Object> list) {
            this.list=list;
        }


        public void run() {
            list.remove(0);
        }
    }


    private List<Object> getList() {
        List<Object> list=new LinkedList<Object>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.add("d");
        return list;
    }


    public void testLoopWithAction() throws Exception {
        List<Object> list=getList();

        Procedure action=new ListRemoveFirstProcedure(list);
        Predicate condition=new Not(new BoundPredicate(new IsEmpty<List<Object>>(), list));
        Procedure procedure=new WhileDoProcedure(condition, action);

        assertTrue("The condition should be true before running the loop", condition.test());
        assertFalse("The list should not be empty then", list.isEmpty());
        procedure.run();
        assertFalse("The condition should be false after running the loop", condition.test());
        assertTrue("The list should be empty then", list.isEmpty());

        list=getList();
        action=new ListRemoveFirstProcedure(list);
        condition=new Predicate() {
                      private int count=2;

                      public boolean test() {
                          return count-- > 0;
                      }
                  };
        procedure=new WhileDoProcedure(condition, action);
        procedure.run();
        assertFalse("The list should not contain \"a\" anymore", list.contains("a"));
        assertFalse("The list should not contain \"b\" anymore", list.contains("b"));
        assertTrue("The list should still contain \"c\"", list.contains("c"));
        assertTrue("The list should still contain \"d\"", list.contains("d"));
    }

    public void testLoopForNothing() {
        List<Object> list=getList();
        Procedure action=new ListRemoveFirstProcedure(list);
        Procedure procedure=new WhileDoProcedure(Constant.FALSE, action);
        assertTrue("The list should contain 4 elements before runnning the loop", list.size()==4);
        procedure.run();
        assertTrue("The list should contain 4 elements after runnning the loop", list.size()==4);
    }
}

