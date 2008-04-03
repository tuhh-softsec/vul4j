/*
 * Copyright 2003,2004 The Apache Software Foundation.
 *
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

import org.apache.commons.functor.UnaryProcedure;
import org.apache.commons.functor.generator.BaseGenerator;


/**
 * A generator for the range <i>from</i> (inclusive) to <i>to</i> (exclusive).
 *
 * @since 1.0
 * @version $Revision$ $Date$
 * @author Jason Horman (jason@jhorman.org)
 * @author Rodney Waldhoff
 */
public final class IntegerRange extends BaseGenerator {

    // constructors
    //---------------------------------------------------------------

    public IntegerRange(Number from, Number to) {
        this(from.intValue(),to.intValue());
    }

    public IntegerRange(Number from, Number to, Number step) {
        this(from.intValue(),to.intValue(),step.intValue());
    }

    public IntegerRange(int from, int to) {
        this(from,to,defaultStep(from,to));
    }

    public IntegerRange(int from, int to, int step) {
        if (from != to && signOf(step) != signOf(to-from)) {
            throw new IllegalArgumentException("Will never reach " + to + " from " + from + " using step " + step);
        } else {
            this.from = from;
            this.to = to;
            this.step = step;
        }
    }

    // methods
    //---------------------------------------------------------------

    public void run(UnaryProcedure proc) {
        if (signOf(step) == -1) {
            for (int i=from; i > to; i += step) {
                proc.run(new Integer(i));
            }
        } else {
            for (int i=from; i < to; i += step) {
                proc.run(new Integer(i));
            }
        }
    }

    public String toString() {
        return "IntegerRange<" + from + "," + to + "," + step + ">";
    }

    public boolean equals(Object obj) {
        if (obj instanceof IntegerRange) {
            IntegerRange that = (IntegerRange) obj;
            return this.from == that.from && this.to == that.to && this.step == that.step;
        } else {
            return false;
        }
    }

    public int hashCode() {
        int hash = "IntegerRange".hashCode();
        hash <<= 2;
        hash ^= from;
        hash <<= 2;
        hash ^= to;
        hash <<= 2;
        hash ^= step;
        return hash;
    }

    // private methods
    //---------------------------------------------------------------

    private static int signOf(int value) {
        if (value < 0) {
            return -1;
        } else if (value > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    private static int defaultStep(int from, int to) {
        if (from > to) {
            return -1;
        } else {
            return 1;
        }
    }

    // attributes
    //---------------------------------------------------------------

    private int from;
    private int to;
    private int step;


}