package net.onrc.onos.ofcontroller.app;

import static org.junit.Assert.*;

import net.onrc.onos.ofcontroller.app.Flow.FlowState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class SimpleTrafficEngineeringTest {
	NetworkGraph g;

	@Before
	public void setUp() {
		g = new NetworkGraph();

		// add 10 switches (24 ports switch)
		for (Integer i=1; i<10; i++) {
			Switch sw = g.addSwitch("v" + i.toString());
			for (Integer j=1; j<=24; j++) {
				sw.addPort(j);
			}
		}

		// add loop path
		g.addBidirectionalLinks("v1", 1, "v2", 2);
		g.addBidirectionalLinks("v2", 1, "v3", 2);
		g.addBidirectionalLinks("v3", 1, "v4", 2);
		g.addBidirectionalLinks("v4", 1, "v5", 2);
		g.addBidirectionalLinks("v5", 1, "v6", 2);
		g.addBidirectionalLinks("v6", 1, "v7", 2);
		g.addBidirectionalLinks("v7", 1, "v8", 2);
		g.addBidirectionalLinks("v8", 1, "v9", 2);
		g.addBidirectionalLinks("v9", 1, "v1", 2);

		// add other links
		g.addBidirectionalLinks("v1", 3, "v2", 3);
		g.addBidirectionalLinks("v2", 4, "v3", 4);
		g.addBidirectionalLinks("v4", 5, "v3", 5);
		g.addBidirectionalLinks("v5", 6, "v6", 6);
		g.addBidirectionalLinks("v7", 7, "v6", 7);
		g.addBidirectionalLinks("v8", 8, "v1", 8);
		g.addBidirectionalLinks("v9", 9, "v1", 9);
		
		// set capacity of all links to 1000Mbps
		for (Link link: g.getLinks()) {
			link.setCapacity(1000.0);
		}
	}

	@After
	public void tearDown() {
	}

	@Test
	public void useCase1() {
		// load TE algorithm
		SimpleTrafficEngineering te = new SimpleTrafficEngineering(g);

		// get edge ports
		SwitchPort srcPort = g.getSwitch("v1").getPort(20);
		SwitchPort dstPort = g.getSwitch("v6").getPort(20);
		
		// specify bandwidth (Mbps)
		double bandWidth = 1000.0;

		// allocate flow
		ConstrainedFlow flow = te.allocate(srcPort, dstPort, bandWidth);
		assertTrue(flow.isState(FlowState.PathInstalled));

		// release flow
		te.release(flow);
		assertTrue(flow.isState(FlowState.PathRemoved));
	}
}
