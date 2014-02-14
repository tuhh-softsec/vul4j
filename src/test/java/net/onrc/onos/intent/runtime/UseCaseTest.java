package net.onrc.onos.intent.runtime;

import java.util.LinkedList;

import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.MockNetworkGraph;
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
	NetworkGraph g;

	@Before
	public void setUp() {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology();
		g = graph;
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
		intents.add(new ShortestPathIntent("1", 1L, 20L, 1L, 4L, 20L, 4L));
		intents.add(new ShortestPathIntent("2", 2L, 20L, 2L, 6L, 20L, 5L));
		intents.add(new ShortestPathIntent("3", 4L, 20L, 3L, 8L, 20L, 6L));

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
		intents.add(new ConstrainedShortestPathIntent("1", 1L, 20L, 1L, 4L, 20L, 17L, 400.0));
		intents.add(new ConstrainedShortestPathIntent("2", 2L, 20L, 2L, 6L, 20L, 18L, 400.0));
		intents.add(new ConstrainedShortestPathIntent("3", 4L, 20L, 3L, 8L, 20L, 19L, 400.0));
		intents.add(new ConstrainedShortestPathIntent("4", 3L, 20L, 4L, 8L, 20L, 20L, 400.0));
		intents.add(new ConstrainedShortestPathIntent("5", 4L, 20L, 5L, 8L, 20L, 21L, 400.0));

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
		intents.add(new ConstrainedShortestPathIntent("1", 1L, 20L, 1L, 4L, 20L, 6L, 600.0));
		intents.add(new ConstrainedShortestPathIntent("2", 2L, 20L, 2L, 6L, 20L, 7L, 600.0));
		intents.add(new ShortestPathIntent("3", 4L, 20L, 3L, 8L, 20L, 8L));
		intents.add(new ShortestPathIntent("4", 4L, 20L, 4L, 8L, 20L, 9L));
		intents.add(new ConstrainedShortestPathIntent("5", 4L, 20L, 5L, 8L, 20L, 10L, 600.0));

		// compile high-level intents into low-level intents (calculate paths)
		PathCalcRuntime runtime1 = new PathCalcRuntime(g);
		runtime1.addInputIntents(intents);

		// show results
		showResult(runtime1.getOutputIntents());
	}
}
