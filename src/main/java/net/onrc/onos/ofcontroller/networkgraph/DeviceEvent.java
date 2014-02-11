package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.floodlightcontroller.util.MACAddress;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent.SwitchPort;

/**
 * Self-contained Device object for event
 *
 * TODO: We probably want common base class/interface for Self-Contained Event Object
 *
 */
public class DeviceEvent {
    private final MACAddress mac;
    protected List<SwitchPort> attachmentPoints;
    protected Set<InetAddress> ipAddresses;

    public DeviceEvent(MACAddress mac) {
	this.mac = mac;
	this.attachmentPoints = new LinkedList<>();
	this.ipAddresses = new HashSet<>();
    }

    public MACAddress getMac() {
	return mac;
    }

    public List<SwitchPort> getAttachmentPoints() {
	return attachmentPoints;
    }

    public void setAttachmentPoints(List<SwitchPort> attachmentPoints) {
	this.attachmentPoints = attachmentPoints;
    }

    boolean addIpAddress(InetAddress addr) {
	return this.ipAddresses.add(addr);
    }

    boolean removeIpAddress(InetAddress addr) {
	return this.ipAddresses.remove(addr);
    }

    @Override
    public String toString() {
	return "[DeviceEvent " + mac + " attachmentPoints:" + attachmentPoints + " ipAddr:" + ipAddresses + "]";
    }


}
