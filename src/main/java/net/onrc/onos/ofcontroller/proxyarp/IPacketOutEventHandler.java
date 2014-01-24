package net.onrc.onos.ofcontroller.proxyarp;

/**
 * Classes may implement this interface if they wish to subscribe to 
 * packet out notifications from the datagrid service. Packet out notifications
 * are used to direct other ONOS instances to send packets out particular
 * ports under their control.
 *
 */
public interface IPacketOutEventHandler {

	/**
	 * Notify the packet out event handler that an packet out notification has
	 * been received.
	 * @param packetOutNotification An object describing the notification
	 */
	public void packetOutNotification(PacketOutNotification packetOutNotification);
}
