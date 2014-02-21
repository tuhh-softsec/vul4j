package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.util.FlowEntryAction;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

class ForwardAction extends Action {
	protected long dstPort;
	
	public ForwardAction(long dstPort) {
		this.dstPort = dstPort;
	}
	
	public String toString() {
		return Long.toString(dstPort);
	}

	@Override
	public FlowEntryAction getFlowEntryAction() {
	    FlowEntryAction action = new FlowEntryAction();
	    action.setActionOutput(new net.onrc.onos.ofcontroller.util.Port((short) dstPort));
	    return action;
	}
	
}