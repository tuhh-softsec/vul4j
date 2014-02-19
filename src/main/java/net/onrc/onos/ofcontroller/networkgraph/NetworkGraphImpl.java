package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.floodlightcontroller.util.MACAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkGraphImpl implements NetworkGraph {
	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(NetworkGraphImpl.class);

	// DPID -> Switch
	private ConcurrentMap<Long, Switch> switches;

	private ConcurrentMap<InetAddress, Set<Device>> addr2Device;
	private ConcurrentMap<MACAddress, Device> mac2Device;

	public NetworkGraphImpl() {
		// TODO: Does these object need to be stored in Concurrent Collection?
		switches = new ConcurrentHashMap<>();
		addr2Device = new ConcurrentHashMap<>();
		mac2Device = new ConcurrentHashMap<>();
	}

	@Override
	public Switch getSwitch(Long dpid) {
		// TODO Check if it is safe to directly return this Object.
		return switches.get(dpid);
	}

	protected void putSwitch(Switch sw) {
		switches.put(sw.getDpid(), sw);
	}

	protected void removeSwitch(Long dpid) {
		switches.remove(dpid);
	}

	@Override
	public Iterable<Switch> getSwitches() {
		// TODO Check if it is safe to directly return this Object.
		return Collections.unmodifiableCollection(switches.values());
	}

	@Override
	public Iterable<Link> getLinks() {
		List<Link> linklist = new LinkedList<>();

		for (Switch sw : switches.values()) {
			Iterable<Link> links = sw.getOutgoingLinks();
			for (Link l : links) {
				linklist.add(l);
			}
		}
		return linklist;
	}

	@Override
	public Iterable<Link> getOutgoingLinksFromSwitch(Long dpid) {
		Switch sw = getSwitch(dpid);
		if (sw == null) {
			return Collections.emptyList();
		}
		Iterable<Link> links = sw.getOutgoingLinks();
		if (links instanceof Collection) {
			return Collections.unmodifiableCollection((Collection<Link>) links);
		} else {
			List<Link> linklist = new LinkedList<>();
			for (Link l : links) {
				linklist.add(l);
			}
			return linklist;
		}
	}

	@Override
	public Iterable<Link> getIncomingLinksFromSwitch(Long dpid) {
		Switch sw = getSwitch(dpid);
		if (sw == null) {
			return Collections.emptyList();
		}
		Iterable<Link> links = sw.getIncomingLinks();
		if (links instanceof Collection) {
			return Collections.unmodifiableCollection((Collection<Link>) links);
		} else {
			List<Link> linklist = new LinkedList<>();
			for (Link l : links) {
				linklist.add(l);
			}
			return linklist;
		}
	}


	@Override
	public Iterable<Device> getDeviceByIp(InetAddress ipAddress) {
		Set<Device> devices = addr2Device.get(ipAddress);
		if (devices == null) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableCollection(devices);
	}

	@Override
	public Device getDeviceByMac(MACAddress address) {
		return mac2Device.get(address);
	}

	protected void putDevice(Device device) {
	    mac2Device.put(device.getMacAddress(), device);
	    for (InetAddress ipAddr : device.getIpAddress()) {
		Set<Device> devices = addr2Device.get(ipAddr);
		if (devices == null) {
		    devices = new HashSet<>();
		    addr2Device.put(ipAddr, devices);
		}
		devices.add(device);
	    }
	}

	protected void removeDevice(Device device) {
	    mac2Device.remove(device.getMacAddress());
	    for (InetAddress ipAddr : device.getIpAddress()) {
		Set<Device> devices = addr2Device.get(ipAddr);
		if (devices != null) {
		    devices.remove(device);
		    if (devices.isEmpty())
			addr2Device.remove(ipAddr);
		}
	    }
	}
}
