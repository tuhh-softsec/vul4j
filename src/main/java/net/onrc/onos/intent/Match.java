package net.onrc.onos.intent;

import java.util.Arrays;

import net.floodlightcontroller.util.MACAddress;
//import net.onrc.onos.ofcontroller.networkgraph.Port;
//import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.util.FlowEntryMatch;

/**
 *
 * @author Brian O'Connor <bocon@onlab.us>
 *
 */

public class Match {
	protected long sw;
	protected MACAddress srcMac;
	protected MACAddress dstMac;
	protected long srcPort;
	
	public Match(long sw, long srcPort, 
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
	    match.enableInPort(new net.onrc.onos.ofcontroller.util.Port((short) srcPort));
	    return match;
	}

	@Override
	public String toString() {
		return "Sw:" + sw + " (" + srcPort + "," + srcMac + "," + dstMac + ")";
	}
	
	@Override
	public int hashCode() {
	    long[] nums = new long[4];
	    nums[0] = sw;
	    nums[1] = srcPort;
	    nums[2] = srcMac.toLong();
	    nums[3] = dstMac.toLong();
	    return Arrays.hashCode(nums);
	}
}
