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
package org.apache.commons.functor.core.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * See Commons-Collections for a public version
 * of this class.
 *
 * @version $Revision$ $Date$
 * @author Rodney Waldhoff
 */
@SuppressWarnings("unchecked")
final class ComparableComparator implements Comparator, Serializable {
    /** Singleton */
    public static final ComparableComparator INSTANCE = new ComparableComparator();

    /**
     * Create a new ComparableComparator.
     */
    public ComparableComparator() {
    }

    /**
     * {@inheritDoc}
     */
    public int compare(Object o1, Object o2) {
        return ((Comparable) o1).compareTo(o2);
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        return (obj instanceof ComparableComparator);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return toString().hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "ComparableComparator";
    }

    /**
     * Get a ComparableComparator instance.
     * @return ComparableComparator
     */
    public static final ComparableComparator instance() {
        return INSTANCE;
    }

}
