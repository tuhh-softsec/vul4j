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

/**
 * A functor that takes two arguments and returns an <code>Object</code> value.
 * <p>
 * Implementors are encouraged but not required to make their functors
 * {@link java.io.Serializable Serializable}.
 * </p>
 * @since 1.0
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
public interface BinaryFunction<L, R, T> extends BinaryFunctor<L, R> {
    /**
     * Evaluate this function.
     *
     * @param left the L element of the ordered pair of arguments
     * @param right the R element of the ordered pair of arguments
     * @return the T result of this function for the given arguments
     */
    T evaluate(L left, R right);
}
