package net.onrc.onos.intent.runtime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.onrc.onos.datagrid.IDatagridService;
import net.onrc.onos.datagrid.IEventChannel;
import net.onrc.onos.intent.ConstrainedShortestPathIntent;
import net.onrc.onos.intent.FlowEntry;
import net.onrc.onos.intent.Intent;
import net.onrc.onos.intent.Intent.IntentState;
import net.onrc.onos.intent.IntentOperation.Operator;
import net.onrc.onos.intent.IntentOperation;
import net.onrc.onos.intent.IntentOperationList;
import net.onrc.onos.intent.MockNetworkGraph;
import net.onrc.onos.intent.PathIntent;
import net.onrc.onos.intent.PathIntentMap;
import net.onrc.onos.intent.ShortestPathIntent;
import net.onrc.onos.intent.persist.PersistIntent;
import net.onrc.onos.ofcontroller.networkgraph.DeviceEvent;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphListener;
import net.onrc.onos.ofcontroller.networkgraph.INetworkGraphService;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.PortEvent;
import net.onrc.onos.ofcontroller.networkgraph.SwitchEvent;
import net.onrc.onos.registry.controller.IControllerRegistryService;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(PathCalcRuntimeModule.class)
public class UseCaseTest {
	private NetworkGraph g;
	private FloodlightModuleContext modContext;
	private IDatagridService datagridService;
	private INetworkGraphService networkGraphService;
	private IControllerRegistryService controllerRegistryService;
	private PersistIntent persistIntent;
	@SuppressWarnings("rawtypes")
	private IEventChannel eventChannel;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockNetworkGraph graph = new MockNetworkGraph();
		graph.createSampleTopology();
		g = graph;

		datagridService = EasyMock.createMock(IDatagridService.class);
		networkGraphService = EasyMock.createMock(INetworkGraphService.class);
		controllerRegistryService = EasyMock.createMock(IControllerRegistryService.class);
		modContext = EasyMock.createMock(FloodlightModuleContext.class);
		eventChannel = EasyMock.createMock(IEventChannel.class);
		persistIntent = PowerMock.createMock(PersistIntent.class);

		PowerMock.expectNew(PersistIntent.class,
				EasyMock.anyObject(IControllerRegistryService.class),
				EasyMock.anyObject(INetworkGraphService.class)).andReturn(persistIntent);

		EasyMock.expect(modContext.getServiceImpl(EasyMock.eq(IDatagridService.class)))
		.andReturn(datagridService).once();
		EasyMock.expect(modContext.getServiceImpl(EasyMock.eq(INetworkGraphService.class)))
		.andReturn(networkGraphService).once();
		EasyMock.expect(modContext.getServiceImpl(EasyMock.eq(IControllerRegistryService.class)))
		.andReturn(controllerRegistryService).once();
		EasyMock.expect(persistIntent.getKey()).andReturn(1L).anyTimes();
		EasyMock.expect(persistIntent.persistIfLeader(EasyMock.eq(1L),
				EasyMock.anyObject(IntentOperationList.class))).andReturn(true).anyTimes();

		EasyMock.expect(networkGraphService.getNetworkGraph()).andReturn(g).anyTimes();
		networkGraphService.registerNetworkGraphListener(EasyMock.anyObject(INetworkGraphListener.class));
		EasyMock.expectLastCall();

		EasyMock.expect(datagridService.createChannel("onos.pathintent", Long.class, IntentOperationList.class))
		.andReturn(eventChannel).once();

		EasyMock.replay(datagridService);
		EasyMock.replay(networkGraphService);
		EasyMock.replay(modContext);
		EasyMock.replay(controllerRegistryService);
		PowerMock.replay(persistIntent, PersistIntent.class);
	}

	@After
	public void tearDown() {
		EasyMock.verify(datagridService);
		EasyMock.verify(networkGraphService);
		EasyMock.verify(modContext);
		EasyMock.verify(controllerRegistryService);
		PowerMock.verify(persistIntent, PersistIntent.class);
	}

	private void showResult(PathIntentMap intents) {
		for (Intent intent: intents.getAllIntents()) {
			PathIntent pathIntent = (PathIntent)intent;
			System.out.println("Parent intent: " + pathIntent.getParentIntent().toString());
			System.out.println("Path:");
			for (LinkEvent linkEvent: pathIntent.getPath()) {
				System.out.println(linkEvent);
			}
		}
	}

	@Test
	public void createShortestPaths() throws FloodlightModuleException {
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
		List<Set<FlowEntry>> plan = runtime2.computePlan(pathIntentOpList);

		// show results
		showResult((PathIntentMap) runtime1.getPathIntents());
		System.out.println(plan);
	}

	@Test
	public void createConstrainedShortestPaths() throws FloodlightModuleException {
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
		List<Set<FlowEntry>> plan = runtime2.computePlan(pathIntentOpList);

		// show results
		showResult((PathIntentMap) runtime1.getPathIntents());
		System.out.println(plan);
	}

	@Test
	public void createMixedShortestPaths() throws FloodlightModuleException {
		// create constrained & best effort shortest path intents
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
		List<Set<FlowEntry>> plan = runtime2.computePlan(pathIntentOpList);

		// show results
		showResult((PathIntentMap) runtime1.getPathIntents());
		System.out.println(plan);
	}

	@Test
	public void rerouteShortestPaths() throws FloodlightModuleException {
		List<SwitchEvent> addedSwitchEvents = new LinkedList<>();
		List<SwitchEvent> removedSwitchEvents = new LinkedList<>();
		List<PortEvent> addedPortEvents = new LinkedList<>();
		List<PortEvent> removedPortEvents = new LinkedList<>();
		List<LinkEvent> addedLinkEvents = new LinkedList<>();
		List<LinkEvent> removedLinkEvents = new LinkedList<>();
		List<DeviceEvent> addedDeviceEvents = new LinkedList<>();
		List<DeviceEvent> removedDeviceEvents = new LinkedList<>();

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
		List<Set<FlowEntry>> plan = runtime2.computePlan(pathIntentOpList);

		// show results step1
		showResult((PathIntentMap) runtime1.getPathIntents());
		System.out.println(plan);

		// TODO this state changes should be triggered by notification of plan module
		HashMap<String, IntentState> states = new HashMap<>();
		states.put("1", IntentState.INST_ACK);
		states.put("2", IntentState.INST_ACK);
		states.put("3", IntentState.INST_ACK);
		runtime1.getHighLevelIntents().changeStates(states);

		// link down
		((MockNetworkGraph)g).removeLink(1L, 2L, 9L, 1L); // This link is used by the intent "1"
		((MockNetworkGraph)g).removeLink(9L, 1L, 1L, 2L);
		LinkEvent linkEvent1 = new LinkEvent(1L, 2L, 9L, 1L);
		LinkEvent linkEvent2 = new LinkEvent(9L, 1L, 1L, 2L);
		removedLinkEvents.clear();
		removedLinkEvents.add(linkEvent1);
		removedLinkEvents.add(linkEvent2);
		runtime1.networkGraphEvents(
				addedSwitchEvents,
				removedSwitchEvents,
				addedPortEvents,
				removedPortEvents,
				addedLinkEvents,
				removedLinkEvents,
				addedDeviceEvents,
				removedDeviceEvents);
		System.out.println("Link goes down.");

		// show results step2
		showResult((PathIntentMap) runtime1.getPathIntents());
		// TODO: show results of plan computation
	}
}
