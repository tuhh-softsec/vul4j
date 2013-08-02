package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;

import net.floodlightcontroller.packet.ARP;

public class HostArpRequester implements IArpRequester {

	private IProxyArpService arpService;
	private ARP arpRequest;
	private long dpid;
	private short port;
	
	public HostArpRequester(IProxyArpService arpService, ARP arpRequest, 
			long dpid, short port) {
		
		this.arpService = arpService;
		this.arpRequest = arpRequest;
		this.dpid = dpid;
		this.port = port;
	}

	@Override
	public void arpResponse(InetAddress ipAddress, byte[] macAddress) {
		arpService.sendArpReply(arpRequest, dpid, port, macAddress);
	}

}
