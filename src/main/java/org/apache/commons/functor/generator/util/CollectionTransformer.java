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

package org.apache.commons.functor.generator.util;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.functor.UnaryFunction;
import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.Generator;

/**
 * Transforms a generator into a collection. If a collection is not passed into
 * the constructor an ArrayList will be returned from the transform method.
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author Jason Horman (jason@jhorman.org)
 */
public class CollectionTransformer implements UnaryFunction {

    // instance methods
    //---------------------------------------------------
    private Collection toFill = null;

    // constructors
    //---------------------------------------------------
    /**
     * Create a new CollectionTransformer.
     */
    public CollectionTransformer() {
        toFill = new ArrayList();
    }

    /**
     * Create a new CollectionTransformer.
     * @param toFill Collection to fill
     */
    public CollectionTransformer(Collection toFill) {
        this.toFill = toFill;
    }

    // instance methods
    //---------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public Object evaluate(Object obj) {
        return evaluate((Generator) obj);
    }

    /**
     * Add the Generator's contents to a Collection.
     * @param generator to run
     * @return filled Collection
     */
    public Object evaluate(Generator generator) {
        generator.run(new UnaryProcedure() {
            public void run(Object obj) {
                toFill.add(obj);
            }
        });
        return toFill;
    }
}