package net.onrc.onos.ofcontroller.util;

/**
 * The Flow Path state as set by the user (via the ONOS API).
 */
public enum FlowPathUserState {
    FP_USER_UNKNOWN,			// Initialization value: state unknown
    FP_USER_ADD,			// Flow path that is added
    FP_USER_MODIFY,			// Flow path that is modified
    FP_USER_DELETE			// Flow path that is deleted
}
