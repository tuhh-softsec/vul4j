package net.onrc.onos.ofcontroller.networkgraph;

import org.openflow.protocol.OFPhysicalPort;

import net.floodlightcontroller.core.IOFSwitch;

public class FloodlightToOnosMappers {

	public static Switch map(NetworkGraph graph, IOFSwitch sw) {
		SwitchImpl onosSwitch = new SwitchImpl(graph, sw.getId());

		for (OFPhysicalPort port : sw.getPorts()) {
			onosSwitch.addPort(map(graph, onosSwitch, port));
		}

		return onosSwitch;
	}

	public static Port map(NetworkGraph graph, SwitchImpl sw, OFPhysicalPort port) {
		PortImpl onosPort = new PortImpl(graph, sw, new Long(port.getPortNumber()));
		return onosPort;
	}

	public static Link map(NetworkGraph graph, net.floodlightcontroller.routing.Link link) {
		Port srcPort = graph.getSwitch(link.getSrc()).getPort(Long.valueOf(link.getSrcPort()));
		Port dstPort = graph.getSwitch(link.getDst()).getPort(Long.valueOf(link.getDstPort()));
		LinkImpl onosLink = new LinkImpl(graph, srcPort, dstPort);

		return onosLink;

	}
}
