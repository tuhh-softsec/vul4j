package net.onrc.onos.ofcontroller.proxyarp;

import java.io.Serializable;

/**
 * A PacketOutNotification contains data sent between ONOS instances that
 * directs other instances to send a packet out a set of ports.
 * This is an abstract base class that will be subclassed by specific
 * types of notifications.
 *
 */
public abstract class PacketOutNotification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	protected final byte[] packet;

	public PacketOutNotification(byte[] packet) {
		this.packet = packet;
	}
}
