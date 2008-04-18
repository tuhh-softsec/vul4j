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
 * The Generator interface defines a number of useful actions applying UnaryFunctors
 * to each in a series of argument Objects.
 *
 * @version $Revision$ $Date$
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public interface Generator {
    /**
     * Generators must implement this method.
     * @param proc UnaryProcedure to run
     */
    public abstract void run(UnaryProcedure proc);

    /**
     * Stop the generator. Will stop the wrapped generator if one was set.
     */
    public abstract void stop();

    /**
     * Check if the generator is stopped.
     * @return true if stopped
     */
    public abstract boolean isStopped();

    /**
     * See {@link org.apache.commons.functor.Algorithms#apply}.
     * @param func UnaryFunction to apply
     * @return this
     */
    public abstract Generator apply(UnaryFunction func);

    /**
     * See {@link org.apache.commons.functor.Algorithms#contains}.
     * @param pred UnaryPredicate to apply
     * @return true if a match was found
     */
    public abstract boolean contains(UnaryPredicate pred);

    /**
     * See {@link org.apache.commons.functor.Algorithms#detect}.
     * @param pred UnaryPredicate to apply
     * @return first match or <code>null</code>
     */
    public abstract Object detect(UnaryPredicate pred);

    /**
     * See {@link org.apache.commons.functor.Algorithms#detect}.
     * @param pred UnaryPredicate to apply
     * @param ifNone default result
     * @return first match or <code>null</code>
     */
    public abstract Object detect(UnaryPredicate pred, Object ifNone);

    /**
     * Synonym for run.
     * @param proc UnaryProcedure to run against each element
     */
    public abstract void foreach(UnaryProcedure proc);

    /**
     * See {@link org.apache.commons.functor.Algorithms#inject}.
     * @param seed seed Object
     * @param func BinaryFunction to apply
     * @return final result
     */
    public abstract Object inject(Object seed, BinaryFunction func);

    /**
     * See {@link org.apache.commons.functor.Algorithms#reject}.
     * @param pred UnaryPredicate to apply
     * @return a Generator of non-matching elements
     */
    public abstract Generator reject(UnaryPredicate pred);

    /**
     * See {@link org.apache.commons.functor.Algorithms#select}.
     * @param pred UnaryPredicate to apply
     * @return Generator of matching elements
     */
    public abstract Generator select(UnaryPredicate pred);

    /**
     * See {@link org.apache.commons.functor.Algorithms#select}.
     * @param pred UnaryPredicate to apply
     * @return Generator of matching elements
     */
    public abstract Generator where(UnaryPredicate pred);

    /**
     * See {@link org.apache.commons.functor.Algorithms#until}.
     * @param pred UnaryPredicate to apply
     * @return a Generator of non-matching elements
     */
    public abstract Generator until(UnaryPredicate pred);

    /**
     * Transforms this generator using the passed in
     * transformer. An example transformer might turn the contents of the
     * generator into a {@link Collection} of elements.
     * @param transformer UnaryFunction to apply to this
     * @return transformation result
     */
    public abstract Object to(UnaryFunction transformer);

    /**
     * Same as to(new CollectionTransformer(collection)).
     * @param collection Collection to which my elements should be added
     * @return <code>collection</code>
     */
    public abstract Collection to(Collection collection);

    /**
     * Same as to(new CollectionTransformer()).
     * @return Collection
     */
    public abstract Collection toCollection();
}
