package net.onrc.onos.intent;

import static org.junit.Assert.*;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.Path;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ConstrainedBFSTreeTest {
	static long LOCAL_PORT = 0xFFFEL;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreate() {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology1();
		ConstrainedBFSTree tree = new ConstrainedBFSTree(graph.getSwitch(1L));
		assertNotNull(tree);
	}

	@Test
	public void testCreateConstrained() {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology1();
		PathIntentMap intents = new PathIntentMap();
		ConstrainedBFSTree tree = new ConstrainedBFSTree(graph.getSwitch(1L), intents, 1000.0);
		assertNotNull(tree);
	}

	@Test
	public void testGetPath() {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology1();
		ConstrainedBFSTree tree = new ConstrainedBFSTree(graph.getSwitch(1L));
		Path path11 = tree.getPath(graph.getSwitch(1L));
		Path path12 = tree.getPath(graph.getSwitch(2L));
		Path path13 = tree.getPath(graph.getSwitch(3L));
		Path path14 = tree.getPath(graph.getSwitch(4L));

		assertNotNull(path11);
		assertEquals(0, path11.size());

		assertNotNull(path12);
		assertEquals(1, path12.size());
		assertEquals(new LinkEvent(graph.getLink(1L, 12L)), path12.get(0));

		assertNotNull(path13);
		assertEquals(2, path13.size());
		if (path13.get(0).getDst().getDpid() == 2L) {
			assertEquals(new LinkEvent(graph.getLink(1L, 12L)), path13.get(0));
			assertEquals(new LinkEvent(graph.getLink(2L, 23L)), path13.get(1));
		}
		else {
			assertEquals(new LinkEvent(graph.getLink(1L, 14L)), path13.get(0));
			assertEquals(new LinkEvent(graph.getLink(4L, 43L)), path13.get(1));
		}

		assertNotNull(path14);
		assertEquals(1, path14.size());
		assertEquals(new LinkEvent(graph.getLink(1L, 14L)), path14.get(0));
	}

	@Test
	public void testGetPathNull() {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology1();
		graph.removeLink(1L, 12L, 2L, 21L);
		graph.removeLink(1L, 14L, 4L, 41L);
		// now, there is no path from switch 1, but to switch1

		ConstrainedBFSTree tree1 = new ConstrainedBFSTree(graph.getSwitch(1L));
		Path path12 = tree1.getPath(graph.getSwitch(2L));
		Path path13 = tree1.getPath(graph.getSwitch(3L));
		Path path14 = tree1.getPath(graph.getSwitch(4L));

		ConstrainedBFSTree tree2 = new ConstrainedBFSTree(graph.getSwitch(2L));
		Path path21 = tree2.getPath(graph.getSwitch(1L));

		assertNull(path12);
		assertNull(path13);
		assertNull(path14);
		assertNotNull(path21);
		assertEquals(1, path21.size());
		assertEquals(new LinkEvent(graph.getLink(2L, 21L)), path21.get(0));
	}

	@Test
	public void testGetConstrainedPath() {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology1();
		PathIntentMap intents = new PathIntentMap();
		IntentOperationList intentOps = new IntentOperationList();

		// create constrained shortest path intents that have the same source destination ports
		ConstrainedShortestPathIntent intent1 = new ConstrainedShortestPathIntent(
				"1", 1L, LOCAL_PORT, 0x111L, 2L, LOCAL_PORT, 0x222L, 600.0);
		ConstrainedShortestPathIntent intent2 = new ConstrainedShortestPathIntent(
				"2", 1L, LOCAL_PORT, 0x333L, 2L, LOCAL_PORT, 0x444L, 600.0);

		// calculate path of the intent1
		ConstrainedBFSTree tree = new ConstrainedBFSTree(graph.getSwitch(1L), intents, 600.0);
		Path path1 = tree.getPath(graph.getSwitch(2L));

		assertNotNull(path1);
		assertEquals(1, path1.size());
		assertEquals(new LinkEvent(graph.getLink(1L, 12L)), path1.get(0));

		PathIntent pathIntent1 = new PathIntent("pi1", path1, 600.0, intent1);
		intentOps.add(Operator.ADD, pathIntent1);
		intents.executeOperations(intentOps);

		// calculate path of the intent2
		tree = new ConstrainedBFSTree(graph.getSwitch(1L), intents, 600.0);
		Path path2 = tree.getPath(graph.getSwitch(2L));

		assertNotNull(path2);
		assertEquals(2, path2.size());
		assertEquals(new LinkEvent(graph.getLink(1L, 14L)), path2.get(0));
		assertEquals(new LinkEvent(graph.getLink(4L, 42L)), path2.get(1));

		PathIntent pathIntent2 = new PathIntent("pi2", path2, 600.0, intent2);
		intentOps.add(Operator.ADD, pathIntent2);
		intents.executeOperations(intentOps);

		// calculate path of the intent3
		tree = new ConstrainedBFSTree(graph.getSwitch(1L), intents, 600.0);
		Path path3 = tree.getPath(graph.getSwitch(2L));

		assertNull(path3);
	}
}