package net.floodlightcontroller.bgproute;

import net.floodlightcontroller.util.IPv4;
import net.floodlightcontroller.util.MACAddress;
import net.floodlightcontroller.util.SwitchPort;

public class GatewayRouter {
	private SwitchPort attachmentPoint;
	private MACAddress routerMac;
	private IPv4 routerIp;
	
	public GatewayRouter(SwitchPort attachmentPoint, MACAddress routerMac, IPv4 routerIp) {
		this.attachmentPoint = attachmentPoint;
		this.routerMac = routerMac;
		this.routerIp = routerIp;
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
}
