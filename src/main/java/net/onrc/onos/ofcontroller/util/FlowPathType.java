package net.onrc.onos.ofcontroller.util;

/**
 * The Flow Path types.
 */
public enum FlowPathType {
    FP_TYPE_UNKNOWN,			// Initialization value: state unknown
    FP_TYPE_SHORTEST_PATH,		// Shortest path flow
    FP_TYPE_EXPLICIT_PATH		// Flow path with explicit flow entries
}
