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

import org.apache.commons.functor.Predicate;
import org.apache.commons.functor.Procedure;

/**
 * Base class for predicated procedure algorithms.
 *
 * @version $Revision$ $Date$
 */
abstract class PredicatedLoop implements Procedure, Serializable {
    private Procedure body;
    private Predicate test;

    /**
     * Create a new PredicatedLoop.
     * @param body to execute
     * @param test whether to keep going
     */
    protected PredicatedLoop(Procedure body, Predicate test) {
        this.body = body;
        this.test = test;
    }

    /**
     * Get the body of this loop.
     * @return Procedure
     */
    protected Procedure getBody() {
        return body;
    }

    /**
     * Get the test for this loop.
     * @return Predicate
     */
    protected Predicate getTest() {
        return test;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        PredicatedLoop other = (PredicatedLoop) obj;
        return other.body.equals(body) && other.test.equals(test);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        String classname = getClass().getName();
        int dot = classname.lastIndexOf('.');
        int result = classname.substring(dot + 1).hashCode();
        result <<= 2;
        result ^= body.hashCode();
        result <<= 2;
        result ^= test.hashCode();
        return result;
    }
}
