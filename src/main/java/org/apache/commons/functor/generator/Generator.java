/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.functor.generator;

import java.util.Collection;

import org.apache.commons.functor.BinaryFunction;
import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.UnaryProcedure;

/**
 * @version $Revision$ $Date$
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public interface Generator {
    /** Generators must implement this method. */
    public abstract void run(UnaryProcedure proc);
    /** Stop the generator. Will stop the wrapped generator if one was set. */
    public abstract void stop();
    /** Check if the generator is stopped. */
    public abstract boolean isStopped();
    /*** See {@link org.apache.commons.functor.Algorithms#apply}. */
    public abstract Generator apply(UnaryFunction func);
    /** See {@link org.apache.commons.functor.Algorithms#contains}. */
    public abstract boolean contains(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#detect}. */
    public abstract Object detect(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#detect}. */
    public abstract Object detect(UnaryPredicate pred, Object ifNone);
    /** Synonym for run. */
    public abstract void foreach(UnaryProcedure proc);
    /** See {@link org.apache.commons.functor.Algorithms#inject}. */
    public abstract Object inject(Object seed, BinaryFunction func);
    /** See {@link org.apache.commons.functor.Algorithms#reject}. */
    public abstract Generator reject(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#select}. */
    public abstract Generator select(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#select}. */
    public abstract Generator where(UnaryPredicate pred);
    /** See {@link org.apache.commons.functor.Algorithms#until}. */
    public abstract Generator until(UnaryPredicate pred);
    /**
     * Transforms this generator using the passed in
     * transformer. An example transformer might turn the contents of the
     * generator into a {@link Collection} of elements.
     */
    public abstract Object to(UnaryFunction transformer);
    /** Same as to(new CollectionTransformer(collection)). */
    public abstract Collection to(Collection collection);
    /** Same as to(new CollectionTransformer()). */
    public abstract Collection toCollection();
}