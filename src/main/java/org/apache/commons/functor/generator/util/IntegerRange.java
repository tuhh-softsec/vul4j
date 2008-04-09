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
    // attributes
    //---------------------------------------------------------------

    private int from;
    private int to;
    private int step;

    // constructors
    //---------------------------------------------------------------
    /**
     * Create a new IntegerRange.
     * @param from start
     * @param to end
     */
    public IntegerRange(Number from, Number to) {
        this(from.intValue(), to.intValue());
    }

    /**
     * Create a new IntegerRange.
     * @param from start
     * @param to end
     * @param step increment
     */
    public IntegerRange(Number from, Number to, Number step) {
        this(from.intValue(), to.intValue(), step.intValue());
    }

    /**
     * Create a new IntegerRange.
     * @param from start
     * @param to end
     */
    public IntegerRange(int from, int to) {
        this(from, to, defaultStep(from, to));
    }

    /**
     * Create a new IntegerRange.
     * @param from start
     * @param to end
     * @param step increment
     */
    public IntegerRange(int from, int to, int step) {
        if (from != to && signOf(step) != signOf(to - from)) {
            throw new IllegalArgumentException("Will never reach " + to + " from " + from + " using step " + step);
        } else {
            this.from = from;
            this.to = to;
            this.step = step;
        }
    }

    // methods
    //---------------------------------------------------------------
    /**
     * {@inheritDoc}
     */
    public void run(UnaryProcedure proc) {
        if (signOf(step) == -1) {
            for (int i = from; i > to; i += step) {
                proc.run(new Integer(i));
            }
        } else {
            for (int i = from; i < to; i += step) {
                proc.run(new Integer(i));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IntegerRange<" + from + "," + to + "," + step + ">";
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntegerRange == false) {
            return false;
        }
        IntegerRange that = (IntegerRange) obj;
        return this.from == that.from && this.to == that.to && this.step == that.step;
    }

    /**
     * {@inheritDoc}
     */
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
    /**
     * Get <code>value/|value|</code> (0 when value == 0).
     * @param value to test
     * @return int
     */
    private static int signOf(int value) {
        if (value < 0) {
            return -1;
        } else if (value > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Calculate default step to get from <code>from</code> to <code>to</code>.
     * @param from start
     * @param to end
     * @return int
     */
    private static int defaultStep(int from, int to) {
        if (from > to) {
            return -1;
        } else {
            return 1;
        }
    }

}