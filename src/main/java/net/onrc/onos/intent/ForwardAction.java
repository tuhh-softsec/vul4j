package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.networkgraph.Port;

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
	
}