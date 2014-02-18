package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.util.FlowEntryAction;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

class ForwardAction extends Action {
	protected Port dstPort;
	
	public ForwardAction(Port dstPort) {
		this.dstPort = dstPort;
	}
	
	public String toString() {
		return dstPort.toString();
	}

	@Override
	public FlowEntryAction getFlowEntryAction() {
	    FlowEntryAction action = new FlowEntryAction();
	    action.setActionOutput(new net.onrc.onos.ofcontroller.util.Port(dstPort.getNumber().shortValue()));
	    return action;
	}
	
}