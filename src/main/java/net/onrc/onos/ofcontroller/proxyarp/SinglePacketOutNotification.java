package net.onrc.onos.ofcontroller.proxyarp;

/**
 * Notification to another ONOS instance to send a packet out a single port.
 *
 */
public class SinglePacketOutNotification extends PacketOutNotification {

	private static final long serialVersionUID = 1L;
	
	private final long outSwitch;
	private final short outPort;
	
	public SinglePacketOutNotification(byte[] packet, long outSwitch, 
			short outPort) {
		super(packet);
		
		this.outSwitch = outSwitch;
		this.outPort = outPort;
	}

	public long getOutSwitch() {
		return outSwitch;
	}

	public short getOutPort() {
		return outPort;
	}

}
