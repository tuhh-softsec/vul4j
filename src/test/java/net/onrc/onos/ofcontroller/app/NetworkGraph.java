package net.onrc.onos.ofcontroller.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class NetworkGraph {
	protected HashSet<Flow> flows;
	protected HashMap<String, Switch> switches;
	
	public NetworkGraph() {
		flows = new HashSet<Flow>();
		switches = new HashMap<String, Switch>();
	}

	// Switch operations
	
	public Switch addSwitch(String name) {
		if (switches.containsKey(name)) {
			return null; // should throw exception
		}
		Switch sw = new Switch(this, name);
		switches.put(sw.getName(), sw);
		return sw;
		
	}

	public Switch getSwitch(String switchName) {
		return switches.get(switchName);
	}
	
	// Link operations
	
	public Link addLink(String srcSwitchName, Integer srcPortNo, String dstSwitchName, Integer dstPortNo) {
		return new Link(
				getSwitch(srcSwitchName).getPort(srcPortNo),
				getSwitch(dstSwitchName).getPort(dstPortNo));
	}
	
	public Link[] addBidirectionalLinks(String srcSwitchName, Integer srcPortNo, String dstSwitchName, Integer dstPortNo) {
		Link[] links = new Link[2];
		links[0] = addLink(srcSwitchName, srcPortNo, dstSwitchName, dstPortNo);
		links[1] = addLink(dstSwitchName, dstPortNo, srcSwitchName, srcPortNo);
		
		return links;
	}
	
	public Collection<Link> getLinks() {
		LinkedList<Link> links = new LinkedList<Link>();
		for (Switch sw: switches.values()) {
			for (SwitchPort port: sw.getPorts()) {
				Link link = port.outgoingLink;
				if (link != null) {
					links.add(link);
				}
			}
		}
		return links;
	}

	// Flow operations
	
	public boolean addFlow(Flow flow) {
		return flows.add(flow);
	}
}
