package net.onrc.onos.intent;

import static org.junit.Assert.*;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

public class PathIntentTest {
	NetworkGraph g;

	@Before
	public void setUp() throws Exception {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology();
		g = graph;
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Kryo kryo = new Kryo();
		Output output = new Output(1024);

		ConstrainedShortestPathIntent cspIntent1 =
				new ConstrainedShortestPathIntent("1", 2L, 3L, 4L, 5L, 6L, 7L, 1000.0);

		Path path = new Path();
		path.add(g.getSwitch(1L).getPort(1L).getOutgoingLink());
		path.add(g.getSwitch(2L).getPort(1L).getOutgoingLink());
		path.add(g.getSwitch(3L).getPort(1L).getOutgoingLink());

		PathIntent pathIntent1 = new PathIntent("11", path, 123.45, cspIntent1);

		kryo.writeObject(output, pathIntent1);
		output.close();

		Input input = new Input(output.toBytes());

		// create pathIntent from bytes

		PathIntent pathIntent2 =
				kryo.readObject(input, PathIntent.class);
		input.close();

		// check
		
		assertEquals("11", pathIntent2.getId());
		Path path2 = pathIntent2.getPath(g);

		assertEquals(Long.valueOf(1L), path2.get(0).getSourceSwitch().getDpid());
		assertEquals(Long.valueOf(1L), path2.get(0).getSourcePort().getNumber());
		assertEquals(Long.valueOf(2L), path2.get(0).getDestinationSwitch().getDpid());
		assertEquals(Long.valueOf(2L), path2.get(0).getDestinationPort().getNumber());

		assertEquals(Long.valueOf(2L), path2.get(1).getSourceSwitch().getDpid());
		assertEquals(Long.valueOf(1L), path2.get(1).getSourcePort().getNumber());
		assertEquals(Long.valueOf(3L), path2.get(1).getDestinationSwitch().getDpid());
		assertEquals(Long.valueOf(2L), path2.get(1).getDestinationPort().getNumber());

		assertEquals(Long.valueOf(3L), path2.get(2).getSourceSwitch().getDpid());
		assertEquals(Long.valueOf(1L), path2.get(2).getSourcePort().getNumber());
		assertEquals(Long.valueOf(4L), path2.get(2).getDestinationSwitch().getDpid());
		assertEquals(Long.valueOf(2L), path2.get(2).getDestinationPort().getNumber());

		assertEquals(123.45, pathIntent2.getBandwidth(), 0.0);

		ConstrainedShortestPathIntent cspIntent2 = 
				(ConstrainedShortestPathIntent) pathIntent2.getParentIntent();
		
		assertEquals("1", cspIntent2.getId());
		assertEquals(2L, cspIntent2.getSrcSwitchDpid());
		assertEquals(3L, cspIntent2.getSrcPortNumber());
		assertEquals(4L, cspIntent2.getSrcMac());
		assertEquals(5L, cspIntent2.getDstSwitchDpid());
		assertEquals(6L, cspIntent2.getDstPortNumber());
		assertEquals(7L, cspIntent2.getDstMac());
		assertEquals(1000.0, cspIntent2.getBandwidth(), 0.0);
	}
}
