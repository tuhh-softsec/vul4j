package net.onrc.onos.intent;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.util.FlowEntryMatch;

/**
 *
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public class Match {
	protected Switch sw;
	protected MACAddress srcMac;
	protected MACAddress dstMac;
	protected Port srcPort;

	public Match(Switch sw, Port srcPort,
				 MACAddress srcMac, MACAddress dstMac) {
		this.sw = sw;
		this.srcPort = srcPort;
		this.srcMac = srcMac;
		this.dstMac = dstMac;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Match) {
			Match other = (Match) obj;
			return this.sw == other.sw &&
					this.srcMac.equals(other.srcMac) &&
					this.dstMac.equals(other.dstMac) &&
					this.srcPort == other.srcPort;
		}
		else {
			return false;
		}
	}

	public FlowEntryMatch getFlowEntryMatch(){
	    FlowEntryMatch match = new FlowEntryMatch();
	    match.enableSrcMac(srcMac);
	    match.enableDstMac(dstMac);
	    match.enableInPort(new net.onrc.onos.ofcontroller.util.Port(srcPort.getNumber().shortValue()));
	    return match;
	}

	@Override
	public String toString() {
		return "(" + srcPort + "," + srcMac + "," + dstMac + ")";
	}
}
