package net.onrc.onos.ofcontroller.proxyarp;

import java.net.InetAddress;

// TODO This class is too generic to be handled by ProxyArpService.
/**
 * Notification to another ONOS instance to send a packet out a single port.
 *
 */
public class SinglePacketOutNotification extends PacketOutNotification {

	private static final long serialVersionUID = 1L;
	
	private final InetAddress address;
	private final long outSwitch;
	private final short outPort;
	
	public SinglePacketOutNotification(byte[] packet, InetAddress address,
			long outSwitch, short outPort) {
		super(packet);
		
		this.address = address;
		this.outSwitch = outSwitch;
		this.outPort = outPort;
	}

	public long getOutSwitch() {
		return outSwitch;
	}

	public short getOutPort() {
		return outPort;
	}

	public InetAddress getTargetAddress() {
		return address;
	}
}
