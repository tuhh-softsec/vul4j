package net.onrc.onos.intent;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

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
					this.srcMac == other.srcMac &&
					this.dstMac == other.dstMac &&
					this.srcPort == other.srcPort;
		}
		else {
			return false;
		}
	}
	
	public String toString() {
		return "(" + srcPort + "," + srcMac + "," + dstMac + ")";
	}
}
