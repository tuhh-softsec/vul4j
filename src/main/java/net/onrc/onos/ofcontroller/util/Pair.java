package net.onrc.onos.ofcontroller.util;

/**
 * A generic class representing a pair of two values.
 */
public class Pair<F, S> {
    public F first;		// The first value in the pair
    public S second;		// The second value in the pair

    /**
     * Constructor for a pair of two values.
     *
     * @param first the first value in the pair.
     * @param second the second value in the pair.
     */
    public Pair(F first, S second) {
	this.first = first;
	this.second = second;
    }

    @Override
    public String toString() {
	return String.format("<%s, %s>", first, second);
    }
}
