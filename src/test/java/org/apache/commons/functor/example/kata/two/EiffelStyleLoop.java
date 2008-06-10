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
package org.apache.commons.functor.example.kata.two;

import org.apache.commons.functor.Function;
import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.Procedure;
import org.apache.commons.functor.core.Constant;
import org.apache.commons.functor.core.NoOp;

/**
 * Supports an Eiffel style loop construct.
 * <pre>
 * new EiffelStyleLoop()
 *   .from(new Procedure() { public void run() {} }) // init code
 *   .invariant(new Predicate() { public boolean test() {} }) // invariants
 *   .variant(new Procedure() { public Object evaluate() {} }) // diminishing comparable value
 *   // or
 *   // .variant(new Predicate() { public boolean test() {} }) // more invariants
 *   .until(new Predicate() { public boolean test() {} }) // terminating condition
 *   .loop(new Procedure() { public void run() {} }) // the acutal loop
 *   .run();
 * </pre>
 *
 * Note that <tt>new EiffelStyleLoop().run()</tt> executes just fine.
 * You only need to set the parts of the loop you want to use.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public class EiffelStyleLoop implements Procedure {
    public EiffelStyleLoop from(Procedure procedure) {
        from = procedure;
        return this;
    }

    public EiffelStyleLoop invariant(Predicate predicate) {
        invariant = predicate;
        return this;
    }

    public EiffelStyleLoop variant(Predicate predicate) {
        variant = predicate;
        return this;
    }

    @SuppressWarnings("unchecked")
    public EiffelStyleLoop variant(final Function function) {
        return variant(new Predicate() {
            public boolean test() {
                boolean result = true;
                Comparable next = (Comparable)(function.evaluate());
                if (null != last) {
                    result = last.compareTo(next) > 0;
                }
                last = next;
                return result;
            }
            private Comparable last = null;
        });
    }

    public EiffelStyleLoop until(Predicate predicate) {
        until = predicate;
        return this;
    }

    public EiffelStyleLoop loop(Procedure procedure) {
        loop = procedure;
        return this;
    }

    public void run() {
        from.run();
        assertTrue(invariant.test());
        while(! until.test() ) {
            loop.run();
            assertTrue(variant.test());
            assertTrue(invariant.test());
        }

        // Note that:
        //   assertTrue(until.test());
        // holds here, but isn't necessary since that's
        // the only way we could get out of the loop

        // Also note that:
        //   assertTrue(invariant.test());
        // holds here, but was the last thing called
        // before until.test()
    }

    private void assertTrue(boolean value) {
        if (!value) {
            throw new IllegalStateException("Assertion failed");
        }
    }

    private Procedure from = NoOp.instance();
    private Predicate invariant = Constant.truePredicate();
    private Predicate variant = Constant.truePredicate();
    private Predicate until = Constant.falsePredicate();
    private Procedure loop = NoOp.instance();

}