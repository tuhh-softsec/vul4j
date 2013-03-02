package net.floodlightcontroller.util;

/**
 * The Flow Entry state as set by the controller.
 */
public enum FlowEntrySwitchState {
	FE_SWITCH_UNKNOWN,		// Initialization value: state unknown
	FE_SWITCH_NOT_UPDATED,		// Switch not updated with this entry
	FE_SWITCH_UPDATE_IN_PROGRESS,	// Switch update in progress
	FE_SWITCH_UPDATED,		// Switch updated with this entry
	FE_SWITCH_UPDATE_FAILED	// Error updating the switch with this entry
}
