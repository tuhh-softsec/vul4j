package net.onrc.onos.ofcontroller.proxyarp;

import net.floodlightcontroller.packet.ARP;

public class HostArpRequester implements IArpRequester {

	private IProxyArpService arpService;
	private ARP arpRequest;
	private long dpid;
	private short port;
	//private long requestTime; //in ms
	
	public HostArpRequester(IProxyArpService arpService, ARP arpRequest, 
			long dpid, short port) {
		
		this.arpService = arpService;
		this.arpRequest = arpRequest;
		this.dpid = dpid;
		this.port = port;
		//this.requestTime = System.currentTimeMillis();
	}

	@Override
	public void arpResponse(byte[] mac) {
		arpService.sendArpReply(arpRequest, dpid, port, mac);
	}

}
