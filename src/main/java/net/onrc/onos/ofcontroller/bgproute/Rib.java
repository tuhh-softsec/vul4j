package net.onrc.onos.ofcontroller.bgproute;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Rib {
	protected InetAddress routerId;
	protected InetAddress nextHop;
	protected int masklen;
//	protected int distance;
	
	Rib(InetAddress router_id, InetAddress nexthop, int masklen) {
		this.routerId = router_id;
		this.nextHop = nexthop;
		this.masklen = masklen;
//		this.distance = distance;
	}
	
	Rib(String router_id, String nexthop, int masklen) {
		try {
			this.routerId = InetAddress.getByName(router_id);
		} catch (UnknownHostException e) {
			System.out.println("InetAddress exception");
		}
		try {
			this.nextHop = InetAddress.getByName(nexthop);
		} catch (UnknownHostException e) {
			System.out.println("InetAddress exception");
		}
		this.masklen = masklen;
	}
	
	public InetAddress getNextHop() {
	    return nextHop;
	}
	
	public int getMasklen(){
	    return masklen;
	}
	
	public boolean equals(Rib r) {
				
		return this.routerId.equals(r.routerId) && this.nextHop.equals(r.nextHop)  && this.masklen == r.masklen;
		
	}
}
