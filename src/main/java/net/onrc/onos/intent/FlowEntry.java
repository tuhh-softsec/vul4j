package net.onrc.onos.intent;

import java.util.HashSet;
import java.util.Set;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

/**
 * 
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public class FlowEntry {
	protected Switch sw;
	protected Match match;
	protected Set<Action> actions;
	
	public FlowEntry(Switch sw, Port srcPort, Port dstPort, 
					 MACAddress srcMac, MACAddress dstMac) {
		this.sw = sw;
		this.match = new Match(sw, srcPort, srcMac, dstMac);
		this.actions = new HashSet<Action>();
		this.actions.add(new ForwardAction(dstPort));
	}
	
	public String toString() {
		return match + "->" + actions;
	}
}