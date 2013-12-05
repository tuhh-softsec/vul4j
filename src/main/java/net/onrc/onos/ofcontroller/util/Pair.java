package net.onrc.onos.ofcontroller.util;

/**
 * A generic class representing a pair of two values.
 */
public class Pair<L, R> {
    public L left;		// The first value in the pair
    public R right;		// The second value in the pair

    /**
     * Constructor for a pair of two values.
     *
     * @param left the first value in the pair.
     * @param right the second value in the pair.
     */
    public Pair(L left, R right) {
	this.left = left;
	this.right = right;
    }
}
