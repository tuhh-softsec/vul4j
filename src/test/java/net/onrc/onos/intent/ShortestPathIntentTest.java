package net.onrc.onos.intent;

import static org.junit.Assert.*;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ShortestPathIntentTest {
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
		ShortestPathIntent intent1 =
				new ShortestPathIntent(g, "1", 1L, 20L, 1L, 4L, 20L, 4L);

		byte b[] = intent1.toBytes();

		ShortestPathIntent intent2 =
				ShortestPathIntent.fromBytes(g, b);

		assertEquals("1", intent2.getId());
		assertEquals(Long.valueOf(1), intent2.getSourcePort().getSwitch().getDpid());
		assertEquals(Long.valueOf(20), intent2.getSourcePort().getNumber());
		assertEquals(1L, intent2.getSourceMac().toLong());
		assertEquals(Long.valueOf(4), intent2.getDestinationPort().getSwitch().getDpid());
		assertEquals(Long.valueOf(20), intent2.getDestinationPort().getNumber());
		assertEquals(4L, intent2.getDestinationMac().toLong());
	}

}
