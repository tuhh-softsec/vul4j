package net.onrc.onos.ofcontroller.app;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class Switch extends NetworkGraphEntity {
	protected HashMap<Integer, SwitchPort> ports;
	protected String name;
	
	public Switch(NetworkGraph graph, String name) {
		super(graph);
		this.name = name;
		ports = new HashMap<Integer, SwitchPort>();
	}

	public SwitchPort addPort(Integer i) {
		SwitchPort port = new SwitchPort(this, i);
		ports.put(port.getPortNumber(), port);
		return port;
	}
	
	public SwitchPort getPort(Integer i) {
		return ports.get(i);
	}
	
	public Collection<SwitchPort> getPorts() {
		return ports.values();
	}

	public Collection<Link> getAdjLinks() {
		LinkedList<Link> links = new LinkedList<Link>();
		for (SwitchPort port: getPorts()) {
			Link link = port.getOutgointLink(); 
			if (link != null) {
				links.add(link);
			}
		}
		return links;
	}

	public String getName() {
		return name;
	}

}
