package net.onrc.onos.ofcontroller.networkgraph;

import org.openflow.protocol.OFPhysicalPort;

import net.floodlightcontroller.core.IOFSwitch;

public class FloodlightToOnosMappers {

	public static Switch map(NetworkGraph graph, IOFSwitch sw) {
		SwitchImpl onosSwitch = new SwitchImpl(graph);
		onosSwitch.setDpid(sw.getId());

		for (OFPhysicalPort port : sw.getPorts()) {
			onosSwitch.addPort(map(graph, port));
		}

		return onosSwitch;
	}

	public static Port map(NetworkGraph graph, OFPhysicalPort port) {
		PortImpl onosPort = new PortImpl(graph);
		onosPort.setPortNumber(port.getPortNumber());
		return onosPort;
	}

	public static Link map(NetworkGraph graph, net.floodlightcontroller.routing.Link link) {
		LinkImpl onosLink = new LinkImpl(graph);

		Switch srcSw = graph.getSwitch(link.getSrc());
		onosLink.setSrcSwitch(srcSw);
		onosLink.setSrcPort(srcSw.getPort(link.getSrcPort()));

		Switch dstSw = graph.getSwitch(link.getDst());
		onosLink.setDstSwitch(dstSw);
		onosLink.setDstPort(dstSw.getPort(link.getDstPort()));

		return onosLink;

	}
}
