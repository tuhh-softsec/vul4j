package net.onrc.onos.intent.runtime;

import java.util.LinkedList;

import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntents;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.ofcontroller.networkgraph.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class UseCaseTest {
	public class MutableNetworkGraph extends AbstractNetworkGraph {
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
	}

	NetworkGraph g;

	@Before
	public void setUp() {
		MutableNetworkGraph g = new MutableNetworkGraph();

		// add 10 switches (24 ports switch)
		for (Long dpid=1L; dpid<10L; dpid++) {
			SwitchImpl sw = (SwitchImpl) g.addSwitch(dpid);
			for (Long j=1L; j<=24L; j++) {
				sw.addPort(j);
			}
		}

		// add loop path
		g.addBidirectionalLinks(1L, 1L, 2L, 2L);
		g.addBidirectionalLinks(2L, 1L, 3L, 2L);
		g.addBidirectionalLinks(3L, 1L, 4L, 2L);
		g.addBidirectionalLinks(4L, 1L, 5L, 2L);
		g.addBidirectionalLinks(5L, 1L, 6L, 2L);
		g.addBidirectionalLinks(6L, 1L, 7L, 2L);
		g.addBidirectionalLinks(7L, 1L, 8L, 2L);
		g.addBidirectionalLinks(8L, 1L, 9L, 2L);
		g.addBidirectionalLinks(9L, 1L, 1L, 2L);

		// add other links
		g.addBidirectionalLinks(1L, 3L, 5L, 3L);
		g.addBidirectionalLinks(2L, 4L, 5L, 4L);
		g.addBidirectionalLinks(2L, 5L, 7L, 5L);
		g.addBidirectionalLinks(3L, 6L, 7L, 6L);
		g.addBidirectionalLinks(3L, 7L, 8L, 7L);
		g.addBidirectionalLinks(3L, 8L, 9L, 8L);
		g.addBidirectionalLinks(4L, 9l, 9L, 9L);

		// set capacity of all links to 1000Mbps
		for (Link link: g.getLinks()) {
			((LinkImpl)link).setCapacity(1000.0);
		}

		/*
		// add Devices
		for (Long l=1L; l<=9L; l++) {
			DeviceImpl d = new DeviceImpl(g, MACAddress.valueOf(l));
			d.addAttachmentPoint(g.getSwitch(l).getPort(20L));
			g.addDevice(d);
		}
		*/

		this.g = g;
	}

	@After
	public void tearDown() {
	}

	private void showResult(PathIntents intents) {
		for (PathIntent pathIntent: intents.getIntents()) {
			System.out.println("Parent intent: " + pathIntent.getParentIntent().toString());
			System.out.println("Path:");
			for (Link link: pathIntent.getPath()) {
				System.out.printf("%s --(%f/%f)--> %s\n",
						link.getSourcePort(),
						link.getCapacity() - intents.getAvailableBandwidth(link),
						link.getCapacity(),
						link.getDestinationPort());
			}
		}
	}

	@Test
	public void useCase1() {
		// create shortest path intents
		LinkedList<Intent> intents = new LinkedList<Intent>();
		intents.add(new ShortestPathIntent(g, 1L, 20L, 1L, 4L, 20L, 4L));
		intents.add(new ShortestPathIntent(g, 2L, 20L, 2L, 6L, 20L, 5L));
		intents.add(new ShortestPathIntent(g, 4L, 20L, 3L, 8L, 20L, 6L));

		// compile high-level intents into low-level intents (calculate paths)
		PathCalcRuntime runtime1 = new PathCalcRuntime(g);
		runtime1.addInputIntents(intents);

		// show results
		showResult(runtime1.getOutputIntents());
	}

	@Test
	public void useCase2() {
		// create constrained shortest path intents
		LinkedList<Intent> intents = new LinkedList<Intent>();
		intents.add(new ConstrainedShortestPathIntent(g, 1L, 20L, 1L, 4L, 20L, 17L, 400.0));
		intents.add(new ConstrainedShortestPathIntent(g, 2L, 20L, 2L, 6L, 20L, 18L, 400.0));
		intents.add(new ConstrainedShortestPathIntent(g, 4L, 20L, 3L, 8L, 20L, 19L, 400.0));
		intents.add(new ConstrainedShortestPathIntent(g, 3L, 20L, 4L, 8L, 20L, 20L, 400.0));
		intents.add(new ConstrainedShortestPathIntent(g, 4L, 20L, 5L, 8L, 20L, 21L, 400.0));

		// compile high-level intents into low-level intents (calculate paths)
		PathCalcRuntime runtime1 = new PathCalcRuntime(g);
		runtime1.addInputIntents(intents);

		// show results
		showResult(runtime1.getOutputIntents());
	}

	@Test
	public void useCase3() {
		// create constrained & not best effort shortest path intents
		LinkedList<Intent> intents = new LinkedList<Intent>();
		intents.add(new ConstrainedShortestPathIntent(g, 1L, 20L, 1L, 4L, 20L, 6L, 600.0));
		intents.add(new ConstrainedShortestPathIntent(g, 2L, 20L, 2L, 6L, 20L, 7L, 600.0));
		intents.add(new ShortestPathIntent(g, 4L, 20L, 3L, 8L, 20L, 8L));
		intents.add(new ShortestPathIntent(g, 4L, 20L, 4L, 8L, 20L, 9L));
		intents.add(new ConstrainedShortestPathIntent(g, 4L, 20L, 5L, 8L, 20L, 10L, 600.0));

		// compile high-level intents into low-level intents (calculate paths)
		PathCalcRuntime runtime1 = new PathCalcRuntime(g);
		runtime1.addInputIntents(intents);

		// show results
		showResult(runtime1.getOutputIntents());
	}
}
