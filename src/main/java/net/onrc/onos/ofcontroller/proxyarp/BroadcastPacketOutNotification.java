package net.onrc.onos.ofcontroller.proxyarp;

/**
 * Notification to all ONOS instances to broadcast this packet out the edge of
 * the network. The edge is defined as any port that doesn't have a link to
 * another switch. The one exception is the port that the packet was received
 * on.
 *
 */
public class BroadcastPacketOutNotification extends
		PacketOutNotification {
	
	private static final long serialVersionUID = 1L;
	
	private final long inSwitch;
	private final short inPort;

	public BroadcastPacketOutNotification(byte[] packet, long inSwitch, 
			short inPort) {
		super(packet);
		
		this.inSwitch = inSwitch;
		this.inPort = inPort;
	}

	public long getInSwitch() {
		return inSwitch;
	}

	public short getInPort() {
		return inPort;
	}

}
