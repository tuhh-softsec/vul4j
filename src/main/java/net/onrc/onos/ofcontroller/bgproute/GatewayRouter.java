package net.onrc.onos.ofcontroller.bgproute;

import net.floodlightcontroller.util.IPv4;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.SwitchPort;

public class GatewayRouter {
	private SwitchPort attachmentPoint;
	private MACAddress routerMac;
	private IPv4 routerIp;
	
	//For now, put in the IP and MAC of the SDN domain's router that this 
	//gateway will be communicating with
	private MACAddress sdnRouterMac;
	private IPv4 sdnRouterIp;
	
	public GatewayRouter(SwitchPort attachmentPoint, MACAddress routerMac, 
			IPv4 routerIp, MACAddress sdnRouterMac, IPv4 sdnRouterIp) {
		this.attachmentPoint = attachmentPoint;
		this.routerMac = routerMac;
		this.routerIp = routerIp;
		this.sdnRouterIp = sdnRouterIp;
		this.sdnRouterMac = sdnRouterMac;
	}

	public SwitchPort getAttachmentPoint() {
		return attachmentPoint;
	}

	public MACAddress getRouterMac() {
		return routerMac;
	}

	public IPv4 getRouterIp() {
		return routerIp;
	}
	
	//TODO delete if not needed
	public MACAddress getSdnRouterMac() {
		return sdnRouterMac;
	}
	
	public IPv4 getSdnRouterIp() {
		return sdnRouterIp;
	}
}
