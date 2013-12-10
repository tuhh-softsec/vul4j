package net.onrc.onos.ofcontroller.forwarding;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.onrc.onos.ofcontroller.util.FlowPath;

/**
 * Temporary interface that allows the Forwarding module to be
 * notified when a flow has been installed by the FlowManager.
 * 
 * This should be refactored to a listener framework in the future.
 * @author jono
 *
 */
public interface IForwardingService extends IFloodlightService {
	/**
	 * Notify the Forwarding module that a flow has been installed
	 * in the network. 
	 * @param flowPath The FlowPath object describing the installed flow
	 */
	public void flowInstalled(FlowPath flowPath);
}
