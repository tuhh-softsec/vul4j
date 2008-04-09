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

package org.apache.commons.functor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.apache.commons.functor.core.NoOp;
import org.apache.commons.functor.core.collection.FilteredIterator;
import org.apache.commons.functor.core.collection.TransformedIterator;
import org.apache.commons.functor.core.composite.ConditionalUnaryProcedure;
import org.apache.commons.functor.core.composite.Not;
import org.apache.commons.functor.core.composite.UnaryNot;
import org.apache.commons.functor.generator.BaseGenerator;
import org.apache.commons.functor.generator.Generator;
import org.apache.commons.functor.generator.IteratorToGeneratorAdapter;

/**
 * Utility methods and algorithms for applying functors to {@link Generator}s
 * and {@link Iterator}s.
 * {@link Generator}s also many of these utility methods as instance methods.
 * The {@link #select select} and {@link #reject reject} methods
 * return new Generators. This becomes useful for constructing nested expressions.
 * For example:
 *
 * <pre>
 *      Algorithms.apply(new EachElement(list), func1)
 *          .filter(pred)
 *              .apply(func2)
 *                  .reject(pred2)
 *                      .to();
 * </pre>
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public final class Algorithms {

    /** Public constructor for bean-ish APIs */
    public Algorithms() {
    }

    /**
     * Collect the elements of <code>iter</code> into a Collection.
     * @param iter Iterator to collect from
     * @return Collection
     */
    public static Collection collect(Iterator iter) {
        return collect(iter, new ArrayList());
    }

    /**
     * Collect the elements of <code>iter</code> into a Collection.
     * @param iter Iterator to collect from
     * @param col Collection to fill
     * @return Collection
     */
    public static Collection collect(Iterator iter, Collection col) {
        while (iter.hasNext()) {
            col.add(iter.next());
        }
        return col;
    }

    /**
     * {@link ListIterator#set Set} each element of the
     * given {@link ListIterator ListIterator} to
     * the result of applying the
     * given {@link UnaryFunction UnaryFunction} to
     * its original value.
     * @param iter ListIterator to transform
     * @param func to apply
     */
    public static void transform(ListIterator iter, UnaryFunction func) {
        while (iter.hasNext()) {
            iter.set(func.evaluate(iter.next()));
        }
    }

    /**
     * {@link Iterator#remove Remove} from the
     * given {@link Iterator Iterator} all elements
     * that fail to match the
     * given {@link UnaryPredicate UnaryPredicate}.
     *
     * @see #remove(Iterator,UnaryPredicate)
     * @param iter to process
     * @param pred to apply
     */
    public static void retain(Iterator iter, UnaryPredicate pred) {
        while (iter.hasNext()) {
            if (!(pred.test(iter.next()))) {
                iter.remove();
            }
        }
    }

    /**
     * {@link Iterator#remove Renmove} from the
     * given {@link Iterator Iterator} all elements
     * that match the
     * given {@link UnaryPredicate UnaryPredicate}.
     *
     * @see #retain(Iterator,UnaryPredicate)
     * @param iter to process
     * @param pred to apply
     */
    public static void remove(Iterator iter, UnaryPredicate pred) {
        while (iter.hasNext()) {
            if (pred.test(iter.next())) {
                iter.remove();
            }
        }
    }

    /**
     * Returns an {@link Iterator} that will apply the given {@link UnaryFunction} to each
     * element when accessed.
     * @param iter to wrap
     * @param func to apply
     * @return Iterator
     */
    public static final Iterator apply(Iterator iter, UnaryFunction func) {
        return TransformedIterator.transform(iter, func);
    }

    /**
     * Returns a {@link Generator} that will apply the given {@link UnaryFunction} to each
     * generated element from <code>gen</code>.
     * @param gen to wrap
     * @param func to apply
     * @return Generator
     */
    public static final Generator apply(final Generator gen, final UnaryFunction func) {
        return new BaseGenerator(gen) {
            public void run(final UnaryProcedure proc) {
                gen.run(new UnaryProcedure() {
                    public void run(Object obj) {
                        proc.run(func.evaluate(obj));
                    }
                });
            }
        };
    }

    /**
     * Equivalent to
     * <code>{@link #contains(Generator, UnaryPredicate) contains}(new
     * {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter), pred)
     * </code>.
     * @param iter Iterator to search
     * @param pred to test
     * @return <code>true</code> if matched
     */
    public static final boolean contains(Iterator iter, UnaryPredicate pred) {
        return contains(new IteratorToGeneratorAdapter(iter), pred);
    }

    /**
     * Return <code>true</code> if some element in the given {@link Generator}
     * that matches the given {@link UnaryPredicate UnaryPredicate}.
     *
     * @see #detect(Generator,UnaryPredicate)
     * @param gen Generator to search
     * @param pred to test
     * @return <code>true</code> if matched
     */
    public static final boolean contains(Generator gen, UnaryPredicate pred) {
        FindWithinGenerator finder = new FindWithinGenerator(gen, pred);
        gen.run(finder);
        return finder.wasFound();
    }

    /**
     * Equivalent to <code>{@link #detect(Generator, UnaryPredicate) detect}(new
     * {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter), pred)
     * </code>.
     * @param iter Iterator to search
     * @param pred to test
     * @return first match
     */
    public static final Object detect(Iterator iter, UnaryPredicate pred) {
        return detect(new IteratorToGeneratorAdapter(iter), pred);
    }

    /**
     * Equivalent to <code>{@link #detect(Generator, UnaryPredicate,Object) detect}(new
     * {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter),
     * pred, ifNone)</code>.
     * @param iter Iterator to search
     * @param pred to test
     * @param ifNone default result
     * @return first match, or <code>ifNone</code>
     */
    public static final Object detect(Iterator iter, UnaryPredicate pred, Object ifNone) {
        return detect(new IteratorToGeneratorAdapter(iter), pred, ifNone);
    }

    /**
     * Return the first element within the given {@link Generator} that matches
     * the given {@link UnaryPredicate UnaryPredicate}, or throw {@link
     * java.util.NoSuchElementException NoSuchElementException} if no
     * matching element can be found.
     *
     * @see #detect(Generator,UnaryPredicate,Object)
     * @throws NoSuchElementException If no element could be found.
     * @param gen Generator to search
     * @param pred to test
     * @return first match
     */
    public static final Object detect(final Generator gen, final UnaryPredicate pred) {
        FindWithinGenerator finder = new FindWithinGenerator(gen, pred);
        gen.run(finder);
        if (finder.wasFound()) {
            return finder.getFoundObject();
        } else {
            throw new NoSuchElementException("No element matching " + pred + " was found.");
        }
    }

    /**
     * Return the first element within the given {@link Generator} that matches
     * the given {@link UnaryPredicate UnaryPredicate}, or return the given
     * (possibly <code>null</code>) <code>Object</code> if no matching element
     * can be found.
     *
     * @see #detect(Generator,UnaryPredicate)
     * @param gen Generator to search
     * @param pred to test
     * @param ifNone default result
     * @return first match, or <code>ifNone</code>
     */
    public static final Object detect(final Generator gen, final UnaryPredicate pred, Object ifNone) {
        FindWithinGenerator finder = new FindWithinGenerator(gen, pred);
        gen.run(finder);
        return finder.wasFound() ? finder.getFoundObject() : ifNone;
    }

    /**
     * Equivalent to <code>{@link #foreach(Generator, UnaryProcedure) foreach}(new
     * {@link org.apache.commons.functor.generator.IteratorToGeneratorAdapter IteratorToGeneratorAdapter}(iter), proc)
     * </code>.
     * @param iter Iterator to process
     * @param proc to run for each element
     */
    public static final void foreach(Iterator iter, UnaryProcedure proc) {
        foreach(new IteratorToGeneratorAdapter(iter), proc);
    }

    /**
     * {@link UnaryProcedure#run Apply} the given {@link UnaryProcedure
     * UnaryProcedure} to each element in the given {@link Generator}.
     * @param gen Generator to process
     * @param proc to run for each element
     */
    public static final void foreach(Generator gen, UnaryProcedure proc) {
        gen.run(proc);
    }

    /**
     * {@link BinaryFunction#evaluate Evaluate} the pair <i>( previousResult,
     * element )</i> for each element in the given {@link Iterator} where
     * previousResult is initially <i>seed</i>, and thereafter the result of the
     * evaluation of the previous element in the iterator. Returns the result
     * of the final evaluation.
     *
     * <p>
     * In code:
     * <pre>
     * while (iter.hasNext()) {
     *   seed = func.evaluate(seed, iter.next());
     * }
     * return seed;
     * </pre>
     * @param iter Iterator of right arguments
     * @param seed initial left argument
     * @param func BinaryFunction to apply
     * @return final production
     */
    public static final Object inject(Iterator iter, Object seed, BinaryFunction func) {
        while (iter.hasNext()) {
            seed = func.evaluate(seed, iter.next());
        }
        return seed;
    }

    /**
     * {@link BinaryFunction#evaluate Evaluate} the pair <i>( previousResult,
     * element )</i> for each element in the given {@link Generator} where
     * previousResult is initially <i>seed</i>, and thereafter the result of the
     * evaluation of the previous element in the iterator. Returns the result
     * of the final evaluation.
     *
     * <p>
     * In code:
     * <pre>
     * while (iter.hasNext()) {
     *   seed = func.evaluate(seed, iter.next());
     * }
     * return seed;
     * </pre>
     * @param gen Generator of right arguments
     * @param seed initial left argument
     * @param func BinaryFunction to apply
     * @return final production
     */
    public static final Object inject(Generator gen, final Object seed, final BinaryFunction func) {
        Injector injector = new Injector(seed, func);
        gen.run(injector);
        return injector.getResult();
    }

    /**
     * Returns an {@link Iterator} that will only return elements that DO
     * NOT match the given predicate.
     * @param iter Iterator to wrap
     * @param pred to test
     * @return filtered Iterator
     */
    public static Iterator reject(Iterator iter, UnaryPredicate pred) {
        return FilteredIterator.filter(iter, UnaryNot.not(pred));
    }

    /**
     * Returns a {@link Generator} that will only "generate" elements that DO
     * NOT match the given predicate.
     * @param gen Generator to wrap
     * @param pred to test
     * @return Generator that will not process matching elements
     */
    public static Generator reject(final Generator gen, final UnaryPredicate pred) {
        return new BaseGenerator(gen) {
            public void run(final UnaryProcedure proc) {
                gen.run(new ConditionalUnaryProcedure(pred, NoOp.instance(), proc));
            }
        };
    }

    /**
     * Returns an {@link Iterator} that will only return elements that DO
     * match the given predicate.
     * @param iter to wrap
     * @param pred to test
     * @return filtered Iterator
     */
    public static final Iterator select(Iterator iter, UnaryPredicate pred) {
        return FilteredIterator.filter(iter, pred);
    }

    /**
     * Returns a {@link Generator} that will only "generate" elements that DO
     * match the given predicate.
     * @param gen Generator to wrap
     * @param pred to test
     * @return Generator that will only process matching elements
     */
    public static final Generator select(final Generator gen, final UnaryPredicate pred) {
        return new BaseGenerator(gen) {
            public void run(final UnaryProcedure proc) {
                gen.run(new ConditionalUnaryProcedure(pred, proc, NoOp.instance()));
            }
        };
    }

    /**
     * Repeat <code>proc</code> until <code>pred</code> evaluates to <code>true</code> (test after).
     * @param proc to perform
     * @param pred exit test
     */
    public static final void dountil(Procedure proc, Predicate pred) {
        dowhile(proc, Not.not(pred));
    }

    /**
     * Repeat <code>proc</code> as long as <code>pred</code> evaluates to <code>true</code> (test after).
     * @param proc to perform
     * @param pred continuation test
     */
    public static final void dowhile(Procedure proc, Predicate pred) {
        do {
            proc.run();
        } while (pred.test());
    }

    /**
     * Repeat <code>proc</code> until <code>pred</code> evaluates to <code>true</code> (test before).
     * @param pred exit test
     * @param proc to perform
     */
    public static final void untildo(Predicate pred, Procedure proc) {
        whiledo(Not.not(pred), proc);
    }

    /**
     * Repeat <code>proc</code> as long as <code>pred</code> evaluates to <code>true</code> (test before).
     * @param pred continuation test
     * @param proc to perform
     */
    public static final void whiledo(Predicate pred, Procedure proc) {
        while (pred.test()) {
            proc.run();
        }
    }

    /**
     * Equivalent to
     * <code>{@link #reject(Iterator, UnaryPredicate) reject}(iter, pred)</code>.
     * @param iter Iterator to wrap
     * @param pred to test
     * @return filtered Iterator
     */
    public static final Iterator until(final Iterator iter, final UnaryPredicate pred) {
        return reject(iter, pred);
    }

    /**
     * Equivalent to
     * <code>{@link #reject(Generator, UnaryPredicate) reject}(gen, pred)</code>.
     * @param gen Generator to wrap
     * @param pred to test
     * @return Generator that will not process matching elements
     */
    public static final Generator until(final Generator gen, final UnaryPredicate pred) {
        return reject(gen, pred);
    }

    /**
     * Tail recursion for {@link Function functions}. If the {@link Function}
     * returns another function of the same type as the original, that function
     * is executed. Functions are executed until a non function value or a
     * function of a different type is returned.
     * @param function initial Function
     * @return final result
     */
    public static final Object recurse(Function function) {
        return recurse(function, function == null ? null : function.getClass());
    }

    /**
     * Tail recursion for {@link Function functions}. If the {@link Function}
     * returns another function of type <code>functionType</code>, that function
     * is executed. Functions are executed until a non function value or a
     * function of a different type is returned.
     * @param function initial Function
     * @param functionType as long as result is an instance, keep processing.
     * @return final result
     */
    public static final Object recurse(Function function, Class functionType) {
        Object result = null;

        // if the function returns another function, execute it. stop executing
        // when the function doesn't return another function of the same type.
        while (true) {
            result = function.evaluate();
            if (functionType.isInstance(result)) {
                function = (Function) result;
                continue;
            } else {
                break;
            }
        }
        return result;
    }

    // inner classes
    //---------------------------------------------------------------
    /**
     * Helper to search a Generator.
     */
    private static class FindWithinGenerator implements UnaryProcedure {
        private UnaryPredicate predicate = null;
        private boolean found = false;
        private Object foundObject = null;
        private Generator generator = null;

        /**
         * Create a new FindWithinGenerator.
         * @param gen to search
         * @param pred to test
         */
        FindWithinGenerator(Generator gen, UnaryPredicate pred) {
            this.generator = gen;
            this.predicate = pred;
            this.found = false;
            this.foundObject = null;
        }

        /**
         * {@inheritDoc}
         */
        public void run(Object obj) {
            if (predicate.test(obj)) {
                found = true;
                foundObject = obj;
                generator.stop();
            }
        }

        /**
         * Learn whether a match was found.
         * @return boolean
         */
        boolean wasFound() {
            return found;
        }

        /**
         * Return the first match, if any, that was found.
         * @return Object
         */
        Object getFoundObject() {
            return foundObject;
        }

    }

    /**
     * Helper class for {@link #inject(Generator, Object, BinaryFunction)}.
     */
    private static class Injector implements UnaryProcedure {
        private Object seed = null;
        private BinaryFunction function = null;

        /**
         * Create a new Injector.
         * @param seed initial left argument
         * @param function to apply
         */
        Injector(Object seed, BinaryFunction function) {
            this.seed = seed;
            this.function = function;
        }

        /**
         * {@inheritDoc}
         */
        public void run(Object obj) {
            seed = function.evaluate(seed, obj);
        }

        /**
         * Get current result.
         * @return Object
         */
        Object getResult() {
            return seed;
        }

    }

}