package net.onrc.onos.ofcontroller.util;

/**
 * The Flow Entry state as set by the user (via the ONOS API).
 */
public enum FlowEntryUserState {
	FE_USER_UNKNOWN,		// Initialization value: state unknown
	FE_USER_ADD,			// Flow entry that is added
	FE_USER_MODIFY,			// Flow entry that is modified
	FE_USER_DELETE			// Flow entry that is deleted
}
