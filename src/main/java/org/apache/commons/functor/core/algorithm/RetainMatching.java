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
package org.apache.commons.functor.core.algorithm;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.functor.BinaryProcedure;
import org.apache.commons.functor.UnaryPredicate;
import org.apache.commons.functor.core.composite.UnaryNot;

/**
 * Retain elements in left Iterator that match right UnaryPredicate.
 *
 * @version $Revision$ $Date$
 */
public final class RetainMatching<T> implements BinaryProcedure<Iterator<? extends T>, UnaryPredicate<? super T>>, Serializable {
    private static final RetainMatching<Object> INSTANCE = new RetainMatching<Object>();
    
    private RemoveMatching<T> removeMatching = new RemoveMatching<T>();

    /**
     * {@inheritDoc}
     * @param left {@link Iterator}
     * @param right {@link UnaryPredicate}
     */
    public void run(Iterator<? extends T> left, UnaryPredicate<? super T> right) {
        removeMatching.run(left, UnaryNot.not(right));
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return obj == this || obj != null && obj.getClass().equals(getClass());
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return System.identityHashCode(INSTANCE);
    }

    /**
     * Get a static {@link RetainMatching} instance.
     * @return {@link RetainMatching}
     */
    public static final RetainMatching<Object> instance() {
        return INSTANCE;
    }
}
