package net.onrc.onos.ofcontroller.bgproute;

import org.openflow.protocol.OFFlowMod;

/**
 * Wraps up a DPID and a OFFlowMod so we know how to delete
 * the flow if we have to.
 * 
 * TODO This functionality should be handled by ONOS's flow layer in future.
 *
 */
public class PushedFlowMod {
	private long dpid;
	private OFFlowMod flowMod;
	
	public PushedFlowMod(long dpid, OFFlowMod flowMod) {
		this.dpid = dpid;
		try {
			this.flowMod = flowMod.clone();
		} catch (CloneNotSupportedException e) {
			this.flowMod = flowMod;
		}
	}
	
	public long getDpid() {
		return dpid;
	}
	
	public OFFlowMod getFlowMod() {
		return flowMod;
	}
}