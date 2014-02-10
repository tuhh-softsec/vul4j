package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Collection;
import java.util.LinkedList;

import net.floodlightcontroller.util.MACAddress;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class DeviceImpl implements Device {
	LinkedList<Port> attachmentPoints = new LinkedList<Port>();
	MACAddress macAddr;

	public DeviceImpl(NetworkGraph graph, MACAddress macAddr) { 
		this.macAddr = macAddr;
	}
	
	@Override
	public MACAddress getMacAddress() {
		return macAddr;
	}

	@Override
	public Collection<InetAddress> getIpAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return ports attached to the device.
	 * The last added port is stored as the first element.
	 */
	@Override
	public Iterable<Port> getAttachmentPoints() {
		return attachmentPoints;
	}

	@Override
	public long getLastSeenTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void addAttachmentPoint(Port port) {
		attachmentPoints.add(0, port);
	}
	
	public void removeAttachmentPoint(Port port) {
		attachmentPoints.remove(port);
	}
	
	@Override
	public String toString() {
		return macAddr.toString();
	}
}
