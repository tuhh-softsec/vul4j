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

import java.util.Collections;
import java.util.Iterator;

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.IsInstance;
import org.apache.commons.functor.core.composite.UnaryAnd;

/**
 * Adds a fluent filtering API to any {@link Iterable}.
 *
 * @version $Revision$ $Date$
 *
 * @param <T>
 */
public class FilteredIterable<T> implements Iterable<T> {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    // type irrelevant for empty instance
    private static final FilteredIterable EMPTY = new FilteredIterable(Collections.EMPTY_LIST) {
        @Override
        public FilteredIterable retain(Class type) {
            return this;
        }

        @Override
        public synchronized FilteredIterable retain(UnaryPredicate predicate) {
            return this;
        }

        @Override
        public FilteredIterable retain(Class... ofType) {
            return this;
        }
    };

    private final Iterable<? extends T> iterable;
    private UnaryAnd<T> predicate;

    /**
     * Create a new FilteredIterable.
     * @param iterable wrapped
     */
    private FilteredIterable(Iterable<? extends T> iterable) {
        super();
        this.iterable = iterable;
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<T> iterator() {
        return FilteredIterator.filter(iterable.iterator(), predicate);
    }

    @Override
    public String toString() {
        return "FilteredIterable<" + iterable + ">";
    }

    /**
     * Retain only elements matching <code>predicate</code>.
     * @param predicate filter, non-<code>null</code>
     * @return <code>this</code>, fluently
     */
    public FilteredIterable<T> retain(UnaryPredicate<? super T> predicate) {
        if (predicate == null) {
            throw new NullPointerException("filtering predicate was null");
        }
        synchronized (this) {
            if (this.predicate == null) {
                this.predicate = new UnaryAnd<T>();
            }
            this.predicate.and(predicate);
        }
        return this;
    }

    /**
     * Retain elements of a given type with type-safety.
     * @param <U>
     * @param type filter, non-<code>null</code>
     * @return new FilteredIterable instance that delegates to <code>this</code>
     */
    public <U> FilteredIterable<U> retain(final Class<U> type) {
        if (type == null) {
            throw new NullPointerException("filtered type was null");
        }
        return new FilteredIterable<U>(new Iterable<U>() {

            public Iterator<U> iterator() {
                return TransformedIterator.transform(
                        FilteredIterator.filter(FilteredIterable.this.iterator(), IsInstance.of(type)),
                        new UnaryFunction<T, U>() {

                            @SuppressWarnings("unchecked")
                            // this is okay because of the isinstance check
                            public U evaluate(T obj) {
                                return (U) obj;
                            }
                        });
            }

        });
    }

    /**
     * Retain elements of any of specified types.
     * @param ofType filter, non-<code>null</code>
     * @return <code>this</code>, fluently
     */
    public FilteredIterable<T> retain(final Class<?>... ofType) {
        if (ofType == null) {
            throw new NullPointerException("array of filtered types was null");
        }
        return retain(new UnaryPredicate<T>() {

            public boolean test(T obj) {
                for (Class<?> type : ofType) {
                    if (type.isInstance(obj)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * Get a {@link FilteredIterable} of <code>iterable</code>.  If <code>wrapped</code> is <code>null</code>,
     * result will also be <code>null</code>.  A {@link FilteredIterable} argument will be passed back
     * directly; any other argument will be wrapped in a new {@link FilteredIterable} object.
     * @param <T>
     * @param iterable wrapped
     * @return FilteredIterable
     */
    public static <T> FilteredIterable<T> of(Iterable<T> iterable) {
        if (iterable == null) {
            return null;
        }
        if (iterable instanceof FilteredIterable<?>) {
            return (FilteredIterable<T>) iterable;
        }
        return new FilteredIterable<T>(iterable);
    }

    /**
     * Get an empty FilteredIterable.
     * @param <T>
     * @return FilteredIterable<T>
     */
    @SuppressWarnings("unchecked")
    public static <T> FilteredIterable<T> empty() {
        return EMPTY;
    }
}
