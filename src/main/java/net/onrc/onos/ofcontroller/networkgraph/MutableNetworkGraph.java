package net.onrc.onos.ofcontroller.networkgraph;

import java.net.InetAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import net.floodlightcontroller.util.MACAddress;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class MutableNetworkGraph implements NetworkGraph {
	protected HashMap<Long, SwitchImpl> switches;
	
	public MutableNetworkGraph() {
		switches = new HashMap<Long, SwitchImpl>();
	}

	// Switch operations
	
	public Switch addSwitch(Long switchId) {
		if (switches.containsKey(switchId)) {
			return null; // should throw exception
		}
		SwitchImpl sw = new SwitchImpl(this, switchId);
		switches.put(sw.getDpid(), sw);
		return sw;
		
	}

	@Override
	public Switch getSwitch(Long dpid) {
		return switches.get(dpid);
	}
	
	@Override
	public Iterable<? extends Switch> getSwitches() {
		return switches.values();
	}
	
	// Link operations
	
	public Link addLink(Long srcDpid, Long srcPortNo, Long dstDpid, Long dstPortNo) {
		return new LinkImpl(
				this,
				getSwitch(srcDpid).getPort(srcPortNo),
				getSwitch(dstDpid).getPort(dstPortNo));
	}
	
	public Link[] addBidirectionalLinks(Long srcDpid, Long srcPortNo, Long dstDpid, Long dstPortNo) {
		Link[] links = new Link[2];
		links[0] = addLink(srcDpid, srcPortNo, dstDpid, dstPortNo);
		links[1] = addLink(dstDpid, dstPortNo, srcDpid, srcPortNo);
		
		return links;
	}
	
	@Override
	public Collection<Link> getLinks() {
		LinkedList<Link> links = new LinkedList<Link>();
		for (Switch sw: switches.values()) {
			for (Port port: sw.getPorts()) {
				Link link = port.getOutgoingLink();
				if (link != null) {
					links.add(link);
				}
			}
		}
		return links;
	}

	@Override
	public Iterable<Link> getOutgoingLinksFromSwitch(Long dpid) {
		LinkedList<Link> links = new LinkedList<Link>();
		for (Port port: getSwitch(dpid).getPorts()) {
			Link link = port.getOutgoingLink();
			if (link != null) {
				links.add(link);
			}
		}
		return links;
	}

	@Override
	public Iterable<Link> getIncomingLinksFromSwitch(Long dpid) {
		LinkedList<Link> links = new LinkedList<Link>();
		for (Port port: getSwitch(dpid).getPorts()) {
			Link link = port.getIncomingLink();
			if (link != null) {
				links.add(link);
			}
		}
		return links;
	}
	
	// Device operations

	@Override
	public Iterable<Device> getDeviceByIp(InetAddress ipAddress) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Device> getDeviceByMac(MACAddress address) {
		// TODO Auto-generated method stub
		return null;
	}
}
