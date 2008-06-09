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

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.functor.generator.Generator;
import org.apache.commons.functor.generator.IteratorToGeneratorAdapter;

/**
 * Generator factory for each element of a "collection".
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author  Jason Horman (jason@jhorman.org)
 */
public final class EachElement {
    /**
     * Create a new EachElement for bean-dependent APIs.
     */
    public EachElement() {
    }

    /**
     * Get a Generator for each element of a Collection.
     * @param collection to iterate
     * @return Generator<E>
     */
    public static final <E> Generator<E> from(Collection<? extends E> collection) {
        return collection == null ? null : EachElement.from(collection.iterator());
    }

    /**
     * Get a Generator for each entry of a Map.
     * @param map to iterate
     * @return Generator
     */
    public static final <K, V> Generator<Map.Entry<K, V>> from(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return null;
        }
        return map == null ? null : EachElement.from(((Map<K, V>) map).entrySet().iterator());
    }

    /**
     * Get a Generator for each element of an Object[].
     * @param array to iterate
     * @return Generator
     */
    public static final <E> Generator<E> from(E[] array) {
        return array == null ? null : EachElement.from(Arrays.asList(array).iterator());
    }

    /**
     * Get a Generator for each element of an Iterator.
     * @param iter to iterate
     * @return Generator
     */
    public static final <E> Generator<E> from(Iterator<? extends E> iter) {
        return iter == null ? null : new IteratorToGeneratorAdapter<E>(iter);
    }
}