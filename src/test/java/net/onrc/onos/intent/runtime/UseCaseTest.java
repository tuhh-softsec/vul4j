package net.onrc.onos.intent.runtime;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.intent.MockNetworkGraph;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntentMap;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class UseCaseTest {
	private NetworkGraph g;
	private FloodlightModuleContext modContext;
	private IDatagridService datagridService;
	private INetworkGraphService networkGraphService;
	@SuppressWarnings("rawtypes")
	private IEventChannel eventChannel;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology();
		g = graph;

		datagridService = EasyMock.createMock(IDatagridService.class);
		networkGraphService = EasyMock.createMock(INetworkGraphService.class);
		modContext = EasyMock.createMock(FloodlightModuleContext.class);
		eventChannel = EasyMock.createMock(IEventChannel.class);

		EasyMock.expect(modContext.getServiceImpl(EasyMock.eq(IDatagridService.class)))
		.andReturn(datagridService).once();
		EasyMock.expect(modContext.getServiceImpl(EasyMock.eq(INetworkGraphService.class)))
		.andReturn(networkGraphService).once();
		
		networkGraphService.getNetworkGraph();
		EasyMock.expectLastCall().andReturn(g).anyTimes();
		
		EasyMock.expect(datagridService.createChannel("onos.pathintent", byte[].class, IntentOperationList.class))
		.andReturn(eventChannel).once();

		EasyMock.replay(datagridService);
		EasyMock.replay(networkGraphService);
		EasyMock.replay(modContext);
	}

	@After
	public void tearDown() {
		EasyMock.verify(datagridService);
		EasyMock.verify(networkGraphService);
		EasyMock.verify(modContext);
	}

	private void showResult(PathIntentMap intents) {
		for (Intent intent: intents.getAllIntents()) {
			PathIntent pathIntent = (PathIntent)intent;
			System.out.println("Parent intent: " + pathIntent.getParentIntent().toString());
			System.out.println("Path:");
			for (Link link: pathIntent.getPath(g)) {
				System.out.printf("%s --(%f/%f)--> %s\n",
						link.getSourcePort(),
						link.getCapacity() - intents.getAvailableBandwidth(link),
						link.getCapacity(),
						link.getDestinationPort());
			}
		}
	}

	@Test
	public void useCase1() throws FloodlightModuleException {
		// create shortest path intents
		IntentOperationList opList = new IntentOperationList();
		opList.add(Operator.ADD, new ShortestPathIntent("1", 1L, 20L, 1L, 4L, 20L, 4L));
		opList.add(Operator.ADD, new ShortestPathIntent("2", 2L, 20L, 2L, 6L, 20L, 5L));
		opList.add(Operator.ADD, new ShortestPathIntent("3", 4L, 20L, 3L, 8L, 20L, 6L));

		// compile high-level intent operations into low-level intent operations (calculate paths)
		PathCalcRuntimeModule runtime1 = new PathCalcRuntimeModule();
		runtime1.init(modContext);
		runtime1.startUp(modContext);
		IntentOperationList pathIntentOpList = runtime1.executeIntentOperations(opList);

		// compile low-level intents into flow entry installation plan
		PlanCalcRuntime runtime2 = new PlanCalcRuntime(g);
		runtime2.addIntents((PathIntentMap) runtime1.getPathIntents()); // TODO use pathIntentOpList

		// show results
		showResult((PathIntentMap) runtime1.getPathIntents());
		System.out.println(runtime2.getPlan());
	}

	@Test
	public void useCase2() throws FloodlightModuleException {
		// create constrained shortest path intents
		IntentOperationList opList = new IntentOperationList();
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("1", 1L, 20L, 1L, 4L, 20L, 17L, 400.0));
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("2", 2L, 20L, 2L, 6L, 20L, 18L, 400.0));
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("3", 4L, 20L, 3L, 8L, 20L, 19L, 400.0));
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("4", 3L, 20L, 4L, 8L, 20L, 20L, 400.0));
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("5", 4L, 20L, 5L, 8L, 20L, 21L, 400.0));

		// compile high-level intent operations into low-level intent operations (calculate paths)
		PathCalcRuntimeModule runtime1 = new PathCalcRuntimeModule();
		runtime1.init(modContext);
		runtime1.startUp(modContext);
		IntentOperationList pathIntentOpList = runtime1.executeIntentOperations(opList);

		// compile low-level intents into flow entry installation plan
		PlanCalcRuntime runtime2 = new PlanCalcRuntime(g);
		runtime2.addIntents((PathIntentMap) runtime1.getPathIntents()); // TODO use pathIntentOpList

		// show results
		showResult((PathIntentMap) runtime1.getPathIntents());
		System.out.println(runtime2.getPlan());
	}

	@Test
	public void useCase3() throws FloodlightModuleException {
		// create constrained & not best effort shortest path intents
		IntentOperationList opList = new IntentOperationList();
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("1", 1L, 20L, 1L, 4L, 20L, 6L, 600.0));
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("2", 2L, 20L, 2L, 6L, 20L, 7L, 600.0));
		opList.add(Operator.ADD, new ShortestPathIntent("3", 4L, 20L, 3L, 8L, 20L, 8L));
		opList.add(Operator.ADD, new ShortestPathIntent("4", 4L, 20L, 4L, 8L, 20L, 9L));
		opList.add(Operator.ADD, new ConstrainedShortestPathIntent("5", 4L, 20L, 5L, 8L, 20L, 10L, 600.0));

		// compile high-level intent operations into low-level intent operations (calculate paths)
		PathCalcRuntimeModule runtime1 = new PathCalcRuntimeModule();
		runtime1.init(modContext);
		runtime1.startUp(modContext);
		IntentOperationList pathIntentOpList = runtime1.executeIntentOperations(opList);

		// compile low-level intents into flow entry installation plan
		PlanCalcRuntime runtime2 = new PlanCalcRuntime(g);
		runtime2.addIntents((PathIntentMap) runtime1.getPathIntents()); // TODO use pathIntentOpList

		// show results
		showResult((PathIntentMap) runtime1.getPathIntents());
		System.out.println(runtime2.getPlan());
	}
}
