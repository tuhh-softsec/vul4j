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

import org.apache.commons.functor.UnaryProcedure;

import java.util.Iterator;

/**
 * Adapts an {@link Iterator} to the {@link Generator} interface.
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public final class IteratorToGeneratorAdapter<E> extends BaseGenerator<E> {
    // instance variables
    //-----------------------------------------------------

    private Iterator<? extends E> iter = null;

    // constructors
    //-----------------------------------------------------
    /**
     * Create a new IteratorToGeneratorAdapter.
     * @param iter Iterator to adapt
     */
    public IteratorToGeneratorAdapter(Iterator<? extends E> iter) {
        if (null == iter) {
            throw new IllegalArgumentException("Iterator argument was null");
        }
        this.iter = iter;
    }

    // instance methods
    //-----------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void run(UnaryProcedure<? super E> proc) {
        while (iter.hasNext()) {
            proc.run(iter.next());
            if (isStopped()) {
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IteratorToGeneratorAdapter<?> == false) {
            return false;
        }
        IteratorToGeneratorAdapter<?> that = (IteratorToGeneratorAdapter<?>) obj;
        return this.iter.equals(that.iter);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        int hash = "IteratorToGeneratorAdapater".hashCode();
        hash <<= 2;
        hash ^= iter.hashCode();
        return hash;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IteratorToGeneratorAdapter<" + iter + ">";
    }

    // static methods
    //-----------------------------------------------------
    /**
     * Adapt an Iterator to the Generator interface.
     * @param iter to adapt
     * @return IteratorToGeneratorAdapter
     */
    public static <E> IteratorToGeneratorAdapter<E> adapt(Iterator<? extends E> iter) {
        return null == iter ? null : new IteratorToGeneratorAdapter<E>(iter);
    }

}