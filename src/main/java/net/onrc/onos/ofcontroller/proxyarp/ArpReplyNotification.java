package net.onrc.onos.ofcontroller.proxyarp;

import java.io.Serializable;
import java.net.InetAddress;

import net.floodlightcontroller.util.MACAddress;

public class ArpReplyNotification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private InetAddress targetAddress;
	private MACAddress targetMacAddress;
	
	public ArpReplyNotification(InetAddress targetAddress, MACAddress targetMacAddress) {
		this.targetAddress = targetAddress;
		this.targetMacAddress = targetMacAddress;
	}

	public InetAddress getTargetAddress() {
		return targetAddress;
	}

	public MACAddress getTargetMacAddress() {
		return targetMacAddress;
	}

}
