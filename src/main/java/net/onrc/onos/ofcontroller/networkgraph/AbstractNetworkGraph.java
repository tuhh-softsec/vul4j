package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import net.floodlightcontroller.util.MACAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractNetworkGraph implements NetworkGraph {
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory
	    .getLogger(AbstractNetworkGraph.class);

    // DPID -> Switch
    protected ConcurrentMap<Long, Switch> switches;

    protected ConcurrentMap<InetAddress, Set<Device>> addr2Device;
    protected ConcurrentMap<MACAddress, Set<Device>> mac2Device;

    public AbstractNetworkGraph() {
	// TODO: Does these object need to be stored in Concurrent Collection?
	switches = new ConcurrentHashMap<>();
	addr2Device = new ConcurrentHashMap<>();
	mac2Device = new ConcurrentHashMap<>();
    }

    @Override
    public Switch getSwitch(long dpid) {
	// TODO Check if it is safe to directly return this Object.
	return switches.get(dpid);
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
	    Iterable<Link> links = sw.getLinks();
	    for (Link l : links) {
		linklist.add(l);
	    }
	}
	return linklist;
    }

    @Override
    public Iterable<Link> getLinksFromSwitch(long dpid) {
	Switch sw = getSwitch(dpid);
	if (sw == null) {
	    return Collections.emptyList();
	}
	Iterable<Link> links = sw.getLinks();
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
	    return Collections.emptyList();
	}
	return Collections.unmodifiableCollection(devices);
    }

    @Override
    public Iterable<Device> getDeviceByMac(MACAddress address) {
	Set<Device> devices = mac2Device.get(address);
	if (devices == null) {
	    return Collections.emptyList();
	}
	return Collections.unmodifiableCollection(devices);
    }

}
