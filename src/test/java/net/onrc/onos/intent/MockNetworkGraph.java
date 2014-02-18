package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.networkgraph.NetworkGraphImpl;
import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.LinkImpl;
import net.onrc.onos.ofcontroller.networkgraph.Switch;
import net.onrc.onos.ofcontroller.networkgraph.SwitchImpl;

public class MockNetworkGraph extends NetworkGraphImpl {
	public Switch addSwitch(Long switchId) {
		SwitchImpl sw = new SwitchImpl(this, switchId);
		switches.put(sw.getDpid(), sw);
		return sw;

	}

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
	
	public void createSampleTopology() {
		// add 10 switches (24 ports switch)
		for (Long dpid=1L; dpid<10L; dpid++) {
			SwitchImpl sw = (SwitchImpl) addSwitch(dpid);
			for (Long j=1L; j<=24L; j++) {
				sw.addPort(j);
			}
		}

		// add loop path
		addBidirectionalLinks(1L, 1L, 2L, 2L);
		addBidirectionalLinks(2L, 1L, 3L, 2L);
		addBidirectionalLinks(3L, 1L, 4L, 2L);
		addBidirectionalLinks(4L, 1L, 5L, 2L);
		addBidirectionalLinks(5L, 1L, 6L, 2L);
		addBidirectionalLinks(6L, 1L, 7L, 2L);
		addBidirectionalLinks(7L, 1L, 8L, 2L);
		addBidirectionalLinks(8L, 1L, 9L, 2L);
		addBidirectionalLinks(9L, 1L, 1L, 2L);

		// add other links
		addBidirectionalLinks(1L, 3L, 5L, 3L);
		addBidirectionalLinks(2L, 4L, 5L, 4L);
		addBidirectionalLinks(2L, 5L, 7L, 5L);
		addBidirectionalLinks(3L, 6L, 7L, 6L);
		addBidirectionalLinks(3L, 7L, 8L, 7L);
		addBidirectionalLinks(3L, 8L, 9L, 8L);
		addBidirectionalLinks(4L, 9l, 9L, 9L);

		// set capacity of all links to 1000Mbps
		for (Link link: getLinks()) {
			((LinkImpl)link).setCapacity(1000.0);
		}
	}
}
