package net.floodlightcontroller.util;

import net.floodlightcontroller.util.serializers.FlowEntryActionsSerializer;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * The class representing the Flow Entry set of actions.
 *
 * The Flow Entry set of actions need to be applied to each packet.
 *
 * NOTE: This is just an empty placeholder (for now). The implied action is
 * forwarding on a single port.
 */
@JsonSerialize(using=FlowEntryActionsSerializer.class)
public class FlowEntryActions {

    /**
     * Default constructor.
     */
    public FlowEntryActions() {
    }

    /**
     * Convert the set of actions to a string.
     *
     * @return the set of actions as a string.
     */
    @Override
    public String toString() {
	String ret = "";
	// TODO: Implement it!
	return ret;
    }
}
