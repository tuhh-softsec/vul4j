package net.onrc.onos.ofcontroller.flowmanager;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.cmpEq;
import static org.powermock.api.easymock.PowerMock.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.restserver.IRestApiService;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.flowmanager.web.FlowWebRoutable;
import net.onrc.onos.ofcontroller.routing.TopoRouteService;
import net.onrc.onos.ofcontroller.util.*;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFType;
import org.openflow.protocol.factory.BasicFactory;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * @author Toshio Koide
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FlowManager.class, GraphDBOperation.class, System.class, Executors.class})
public class FlowManagerTest {
	private static FloodlightModuleContext context;
	private static IFloodlightProviderService floodlightProvider;
	private static TopoRouteService topoRouteService;
	private static IRestApiService restApi;
	private static GraphDBOperation op;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	private void expectInitWithContext() throws Exception {
		// create mock objects
		context = createMock(FloodlightModuleContext.class);
		floodlightProvider = createMock(IFloodlightProviderService.class);
		topoRouteService = createMock(TopoRouteService.class);
		restApi = createMock(IRestApiService.class);
		op = createMock(GraphDBOperation.class);

		// setup expectations
		expect(context.getServiceImpl(IFloodlightProviderService.class)).andReturn(floodlightProvider);
		expect(context.getServiceImpl(IRestApiService.class)).andReturn(restApi);
		expectNew(GraphDBOperation.class, new Class<?>[] {String.class}, EasyMock.isA(String.class)).andReturn(op);
		expectNew(TopoRouteService.class, new Class<?>[] {String.class}, EasyMock.isA(String.class)).andReturn(topoRouteService);
	}
	
	private IFlowPath createIFlowPathMock(long flowId, String installerID,
			long srcDpid, int srcPort, long dstDpid, int dstPort) {
		IFlowPath iFlowPath = createNiceMock(IFlowPath.class);
		expect(iFlowPath.getFlowId()).andReturn(new FlowId(flowId).toString()).anyTimes();
		expect(iFlowPath.getInstallerId()).andReturn(installerID).anyTimes();
		expect(iFlowPath.getSrcSwitch()).andReturn(new Dpid(srcDpid).toString()).anyTimes();
		expect(iFlowPath.getSrcPort()).andReturn(new Short((short)srcPort)).anyTimes();
		expect(iFlowPath.getDstSwitch()).andReturn(new Dpid(dstDpid).toString()).anyTimes();
		expect(iFlowPath.getDstPort()).andReturn(new Short((short)dstPort)).anyTimes();
		return iFlowPath;
	}
	
	private FlowPath createTestFlowPath(long flowId, String installerId,
			final long srcDpid, final int srcPort,
			final long dstDpid, final int dstPort			
			) {
		FlowPath flowPath = new FlowPath();
		flowPath.setFlowId(new FlowId(flowId));
		flowPath.setInstallerId(new CallerId(installerId));
		flowPath.setDataPath(new DataPath() {{
			setSrcPort(new SwitchPort(new Dpid(srcDpid), new Port((short)srcPort)));
			setDstPort(new SwitchPort(new Dpid(dstDpid), new Port((short)dstPort)));
		}});
		flowPath.setFlowEntryMatch(new FlowEntryMatch());
		return flowPath;
	}
	
	private ArrayList<FlowPath> createTestFlowPaths() {
		FlowPath flowPath1 = createTestFlowPath(1, "foo caller id", 1, 1, 2, 2); 
		FlowPath flowPath2 = createTestFlowPath(2, "caller id", 1, 1, 2, 2); 
		FlowPath flowPath3 = createTestFlowPath(3, "caller id", 1, 5, 2, 2); 

		ArrayList<FlowPath> flowPaths = new ArrayList<FlowPath>();
		flowPaths.add(flowPath1);
		flowPaths.add(flowPath2);
		flowPaths.add(flowPath3);
		
		return flowPaths;
	}
	

	// IFlowService methods


	/**
	 * Test method for {@link FlowManager#addFlow(FlowPath, FlowId, String)}.
	 * @throws Exception 
	 */
	@Test
	public final void testAddFlowFailGraphCreatesNoFlow() throws Exception {
		// instantiate required objects
		FlowId flowId = new FlowId(123);
		FlowPath flowPath = new FlowPath();
		flowPath.setFlowId(flowId);
		FlowManager fm = new FlowManager();
		
		// setup expectations
		expectInitWithContext();
		expect(op.searchFlowPath(flowId)).andReturn(null);
		expect(op.newFlowPath()).andReturn(null);
		op.rollback();

		// start the test
		replayAll();

		fm.init(context);
		Boolean result = fm.addFlow(flowPath, flowId, "");

		// verify the test
		verifyAll();
		assertFalse(result);
	}

	/**
	 * Test method for {@link FlowManager#addFlow(FlowPath, FlowId, String)}.
	 * @throws Exception 
	 */
	@Test
	public final void testAddFlowSuccessNormally() throws Exception {
		final String addFlowEntry = "addFlowEntry";
		// create mock objects
		IFlowPath createdFlowPath = createNiceMock(IFlowPath.class);
		IFlowEntry createdFlowEntry1 = createNiceMock(IFlowEntry.class);
		IFlowEntry createdFlowEntry2 = createNiceMock(IFlowEntry.class);
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, addFlowEntry);

		// instantiate required objects
		final FlowEntry flowEntry1 = new FlowEntry();
		final FlowEntry flowEntry2 = new FlowEntry();
		ArrayList<FlowEntry> flowEntries = new ArrayList<FlowEntry>();
		flowEntries.add(flowEntry1);
		flowEntries.add(flowEntry2);
		
		DataPath dataPath = new DataPath();
		dataPath.setSrcPort(new SwitchPort(new Dpid(0x1234), new Port((short)1)));
		dataPath.setDstPort(new SwitchPort(new Dpid(0x5678), new Port((short)2)));
		dataPath.setFlowEntries(flowEntries);

		FlowEntryMatch match = new FlowEntryMatch();
		
		FlowPath flowPath = new FlowPath();
		flowPath.setFlowId(new FlowId(0x100));
		flowPath.setInstallerId(new CallerId("installer id"));
		flowPath.setDataPath(dataPath);
		flowPath.setFlowEntryMatch(match);
		
		// setup expectations
		expectInitWithContext();
		expect(op.searchFlowPath(cmpEq(new FlowId(0x100)))).andReturn(null);
		expect(op.newFlowPath()).andReturn(createdFlowPath);
		createdFlowPath.setFlowId("0x100");
		createdFlowPath.setType("flow");
		createdFlowPath.setInstallerId("installer id");
		createdFlowPath.setSrcSwitch("00:00:00:00:00:00:12:34");
		createdFlowPath.setSrcPort(new Short((short)1));
		createdFlowPath.setDstSwitch("00:00:00:00:00:00:56:78");
		createdFlowPath.setDstPort(new Short((short)2));
		createdFlowPath.setDataPathSummary("data path summary");
		createdFlowPath.setUserState("FE_USER_ADD");
		
		expectPrivate(fm, addFlowEntry, createdFlowPath, flowEntry1)
			.andReturn(createdFlowEntry1);
		expectPrivate(fm, addFlowEntry, createdFlowPath, flowEntry2)
			.andReturn(createdFlowEntry2);
		
		op.commit();
		
		// start the test
		replayAll();
		
		fm.init(context);
		Boolean result = fm.addFlow(flowPath, new FlowId(0x100), "data path summary");

		// verify the test
		verifyAll();
		assertTrue(result);
	}
	
	/**
	 * Test method for {@link FlowManager#deleteAllFlows()}.
	 * @throws Exception 
	 */
	@Test
	public final void testDeleteAllFlowsSuccessNormally() throws Exception {
		// create mock objects
		IFlowPath flowPath1 = createNiceMock(IFlowPath.class);
		IFlowPath flowPath2 = createNiceMock(IFlowPath.class);
		
		// instantiate required objects
		ArrayList<IFlowPath> flowPaths = new ArrayList<IFlowPath>();
		flowPaths.add(flowPath1);
		flowPaths.add(flowPath2);
		FlowManager fm = new FlowManager();
		
		// setup expectations
		expectInitWithContext();
		expect(op.getAllFlowPaths()).andReturn(flowPaths);

		expect(flowPath1.getFlowId()).andReturn("1").anyTimes();
		expect(op.searchFlowPath(cmpEq(new FlowId(1)))).andReturn(flowPath1);
		expect(flowPath1.getFlowEntries()).andReturn(new ArrayList<IFlowEntry>());
		op.removeFlowPath(flowPath1);
		
		expect(flowPath2.getFlowId()).andReturn("2").anyTimes();
		expect(op.searchFlowPath(cmpEq(new FlowId(2)))).andReturn(flowPath2);
		expect(flowPath2.getFlowEntries()).andReturn(new ArrayList<IFlowEntry>());
		op.removeFlowPath(flowPath2);

		op.commit();
		expectLastCall().anyTimes();

		// start the test
		replayAll();
		
		fm.init(context);
		Boolean result = fm.deleteAllFlows();

		// verify the test
		verifyAll();
		assertTrue(result);
	}
	
	/**
	 * Test method for {@link FlowManager#deleteFlow(FlowId)}.
	 * @throws Exception
	 */
	@Test
	public final void testDeleteFlowSuccessEmptyFlowPath() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();
		
		// create mock objects
		IFlowPath flowObj = createNiceMock(IFlowPath.class);

		// setup expectations
		expectInitWithContext();
		expect(op.searchFlowPath(cmpEq(new FlowId(1)))).andReturn(flowObj);
		expect(flowObj.getFlowEntries()).andReturn(new ArrayList<IFlowEntry>());
		op.removeFlowPath(flowObj);
		op.commit();
		expectLastCall().anyTimes();
		
		// start the test
		replayAll();
		
		fm.init(context);
		Boolean result = fm.deleteFlow(new FlowId(1));
		
		// verify the test
		verifyAll();
		assertTrue(result);
	}
	
	/**
	 * Test method for {@link FlowManager#clearAllFlows()}.
	 * @throws Exception 
	 */
	@Test
	public final void testClearAllFlowsSuccessNormally() throws Exception {
		// create mock objects
		IFlowPath flowPath1 = createNiceMock(IFlowPath.class);
		IFlowPath flowPath2 = createNiceMock(IFlowPath.class);
		IFlowPath flowPath3 = createNiceMock(IFlowPath.class);
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, "clearFlow");
		
		// instantiate required objects
		ArrayList<IFlowPath> flowPaths = new ArrayList<IFlowPath>();
		flowPaths.add(flowPath1);
		flowPaths.add(flowPath2);
		flowPaths.add(null);
		flowPaths.add(flowPath3);
		
		// setup expectations
		expectInitWithContext();
		expect(op.getAllFlowPaths()).andReturn(flowPaths);
		expect(flowPath1.getFlowId()).andReturn(new FlowId(1).toString());
		expect(flowPath2.getFlowId()).andReturn(null);
		expect(flowPath3.getFlowId()).andReturn(new FlowId(3).toString());
		expect(fm.clearFlow(cmpEq(new FlowId(1)))).andReturn(true);
		expect(fm.clearFlow(cmpEq(new FlowId(3)))).andReturn(true);
		
		// start the test
		replayAll();

		fm.init(context);
		Boolean result = fm.clearAllFlows();
		
		//verify the test
		verifyAll();
		assertTrue(result);
	}
	
	/**
	 * Test method for {@link FlowManager#getFlow()}.
	 * @throws Exception 
	 */
	@Test
	public final void testGetFlowSuccessNormally() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();
		IFlowPath iFlowPath = createIFlowPathMock(1, "caller id", 1, 1, 2, 2); 

		// setup expectations
		expectInitWithContext();
		expect(op.searchFlowPath(cmpEq(new FlowId(1)))).andReturn(iFlowPath);
		expect(iFlowPath.getFlowEntries()).andReturn(new ArrayList<IFlowEntry>()).anyTimes();
		op.commit();
		
		// start the test
		replayAll();

		fm.init(context);
		String result = fm.getFlow(new FlowId(1)).installerId().toString();
		
		//verify the test
		verifyAll();
		assertEquals("caller id", result);
	}
	
	/**
	 * Test method for {@link FlowManager#getAllFlows(CallerId, DataPathEndpoints)}.
	 * @throws Exception 
	 */ 
	@Test
	public final void testGetAllFlowsWithCallerIdAndDataPathEndpointsSuccessNormally() throws Exception {
		final String getAllFlows = "getAllFlows";
		// create mock objects
		FlowManager fm = createPartialMock(FlowManager.class, getAllFlows,
				new Class<?>[]{}, new Object[]{});

		// instantiate required objects
		DataPathEndpoints dataPathEndpoints = new DataPathEndpoints(
				new SwitchPort(new Dpid(1), new Port((short)1)),
				new SwitchPort(new Dpid(2), new Port((short)2)));

		ArrayList<FlowPath> obtainedAllFlows = createTestFlowPaths();
			
		//setup expectations
		expectInitWithContext();
		expectPrivate(fm, getAllFlows).andReturn(obtainedAllFlows);
		
		//start the test
		replayAll();
		
		fm.init(context);
		ArrayList<FlowPath> flows = fm.getAllFlows(new CallerId("caller id"), dataPathEndpoints);

		// verify the test
		verifyAll();
		assertEquals(1, flows.size());
		assertEquals(obtainedAllFlows.get(1), flows.get(0));
	}
	
	/**
	 * Test method for {@link FlowManager#getAllFlows(DataPathEndpoints)}.
	 * @throws Exception 
	 */
	@Test
	public final void testGetAllFlowsWithDataPathEndpointsSuccessNormally() throws Exception {
		final String getAllFlows = "getAllFlows";
		// create mock objects
		FlowManager fm = createPartialMock(FlowManager.class, getAllFlows,
				new Class<?>[]{}, new Object[]{});

		// instantiate required objects
		DataPathEndpoints dataPathEndpoints = new DataPathEndpoints(
				new SwitchPort(new Dpid(1), new Port((short)1)),
				new SwitchPort(new Dpid(2), new Port((short)2)));

		ArrayList<FlowPath> obtainedAllFlows = createTestFlowPaths();
			
		//setup expectations
		expectInitWithContext();
		expectPrivate(fm, getAllFlows).andReturn(obtainedAllFlows);
		
		//start the test
		replayAll();
		
		fm.init(context);
		ArrayList<FlowPath> flows = fm.getAllFlows(dataPathEndpoints);

		// verify the test
		verifyAll();
		assertEquals(2, flows.size());
		assertEquals(obtainedAllFlows.get(0), flows.get(0));
		assertEquals(obtainedAllFlows.get(1), flows.get(1));
		// TODO: ignore the order of flows in the list
	}
	
	/**
	 * Test method for {@link FlowManager#getAllFlowsSummary(FlowId, int)}.
	 * @throws Exception 
	 */
	@Test
	public final void testGetAllFlowsSummarySuccessNormally() throws Exception {
		final String getAllFlowsWithoutFlowEntries = "getAllFlowsWithoutFlowEntries";
		// create mock objects
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, getAllFlowsWithoutFlowEntries);
		IFlowPath flowPath1 = createIFlowPathMock(1, "", 1, 2, 3, 4);
		IFlowPath flowPath2 = createIFlowPathMock(5, "", 2, 3, 4, 5);
		IFlowPath flowPath3 = createIFlowPathMock(10, "", 3, 4, 5, 6);

		// instantiate required objects
		ArrayList<IFlowPath> flows = new ArrayList<IFlowPath>();
		flows.add(flowPath3);
		flows.add(flowPath1);
		flows.add(flowPath2);
		
		// setup expectations
		expectInitWithContext();
		expectPrivate(fm, getAllFlowsWithoutFlowEntries).andReturn(flows);

		// start the test
		replayAll();
		
		fm.init(context);
		ArrayList<IFlowPath> returnedFlows = fm.getAllFlowsSummary(null, 0);
		
		// verify the test
		verifyAll();
		assertEquals(3, returnedFlows.size());
		assertEquals(1, new FlowId(returnedFlows.get(0).getFlowId()).value());
		assertEquals(5, new FlowId(returnedFlows.get(1).getFlowId()).value());
		assertEquals(10, new FlowId(returnedFlows.get(2).getFlowId()).value());
	}

	/**
	 * Test method for {@link FlowManager#getAllFlows()}.
	 * @throws Exception 
	 */
	@Test
	public final void testGetAllFlowsSuccessNormally() throws Exception {
		// create mock objects
		IFlowPath iFlowPath1 = createIFlowPathMock(1, "caller id", 1, 1, 2, 2); 
		IFlowPath iFlowPath2 = createIFlowPathMock(2, "caller id", 2, 5, 3, 5);
		
		// instantiate required objects
		ArrayList<IFlowPath> flowPaths = new ArrayList<IFlowPath>();
		flowPaths.add(iFlowPath1);
		flowPaths.add(iFlowPath2);
		FlowManager fm = new FlowManager();

		// setup expectations
		expectInitWithContext();
		expect(op.getAllFlowPaths()).andReturn(flowPaths);
		expect(iFlowPath1.getFlowEntries()).andReturn(new ArrayList<IFlowEntry>()).anyTimes();
		expect(iFlowPath2.getFlowEntries()).andReturn(new ArrayList<IFlowEntry>()).anyTimes();
		op.commit();
		
		// start the test
		replayAll();
		
		fm.init(context);
		ArrayList<FlowPath> flows = fm.getAllFlows();

		// verify the test
		verifyAll();
		assertEquals(2, flows.size());
		assertEquals(new SwitchPort(new Dpid(1), new Port((short)1)).toString(),
				flows.get(0).dataPath().srcPort().toString());
		assertEquals(new SwitchPort(new Dpid(2), new Port((short)5)).toString(),
				flows.get(1).dataPath().srcPort().toString());
		// TODO: more asserts
		// TODO: ignore seq. of the list
	}
	
	/**
	 * Test method for {@link FlowManager#addAndMaintainShortestPathFlow(FlowPath)}.
	 * @throws Exception 
	 */
	@Test
	public final void testAddAndMaintainShortestPathFlowSuccessNormally() throws Exception {
		final String addFlow = "addFlow";

		// create mock objects
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, addFlow);

		// instantiate required objects
		DataPath dataPath = new DataPath();
		dataPath.setSrcPort(new SwitchPort(new Dpid(1), new Port((short)3)));
		dataPath.setDstPort(new SwitchPort(new Dpid(2), new Port((short)4)));
		FlowEntryMatch match = new FlowEntryMatch();
		FlowPath paramFlow = new FlowPath();
		paramFlow.setFlowId(new FlowId(100));
		paramFlow.setInstallerId(new CallerId("installer id"));
		paramFlow.setDataPath(dataPath);
		paramFlow.setFlowEntryMatch(match);
		
		// setup expectations
		expectInitWithContext();
		expectPrivate(fm, addFlow,
				EasyMock.anyObject(FlowPath.class),
				EasyMock.anyObject(FlowId.class),
				EasyMock.anyObject(String.class)
				).andAnswer(new IAnswer<Object>() {
					public Object answer() throws Exception {
						FlowPath flowPath = (FlowPath)EasyMock.getCurrentArguments()[0];
						assertEquals(flowPath.flowId().value(), 100);
						assertEquals(flowPath.installerId().toString(), "installer id");
						assertEquals(flowPath.dataPath().srcPort().toString(),
								new SwitchPort(new Dpid(1), new Port((short)3)).toString());

						String dataPathSummary = (String)EasyMock.getCurrentArguments()[2];
						assertEquals(dataPathSummary, "X");
						
						return true;
					}
				});
		
		// start the test
		replayAll();

		fm.init(context);
		FlowPath resultFlow = fm.addAndMaintainShortestPathFlow(paramFlow);
				
		// verify the test
		verifyAll();
		assertEquals(paramFlow.flowId().value(), resultFlow.flowId().value());
		assertEquals(paramFlow.installerId().toString(), resultFlow.installerId().toString());
		assertEquals(paramFlow.dataPath().toString(), resultFlow.dataPath().toString());
		assertEquals(paramFlow.flowEntryMatch().toString(), resultFlow.flowEntryMatch().toString());
	}
		
	/**
	 * Test method for {@link FlowManager#measurementStorePathFlow(FlowPath)}.
	 * @throws Exception 
	 */
	@Test
	public final void testMeasurementStorePathFlowSuccessNormally() throws Exception {
		// instantiate required objects
		FlowPath paramFlow = createTestFlowPath(100, "installer id", 1, 3, 2, 4);
		Map<Long, Object> shortestPathMap = new HashMap<Long, Object>();
		FlowManager fm = new FlowManager();

		// setup expectations
		expectInitWithContext();
		expect((Map<Long,Object>)topoRouteService.prepareShortestPathTopo()
				).andReturn(shortestPathMap);
		expect(topoRouteService.getTopoShortestPath(
				shortestPathMap,
				paramFlow.dataPath().srcPort(),
				paramFlow.dataPath().dstPort())).andReturn(null);
		
		// start the test
		replayAll();
		
		fm.init(context);
		FlowPath resultFlowPath = fm.measurementStorePathFlow(paramFlow);
		
		// verify the test
		verifyAll();
		assertEquals(paramFlow.flowId().value(), resultFlowPath.flowId().value());
		assertEquals(paramFlow.installerId().toString(), resultFlowPath.installerId().toString());
		assertEquals(paramFlow.dataPath().toString(), resultFlowPath.dataPath().toString());
		assertEquals(paramFlow.flowEntryMatch().toString(), resultFlowPath.flowEntryMatch().toString());
	}
	
	/**
	 * Test method for {@link FlowManager#measurementInstallPaths(Integer)}.
	 * @throws Exception 
	 */
	@Test
	public final void testMeasurementInstallPathsSuccessNormally() throws Exception {
		final String addFlow = "addFlow";

		// create mock objects
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, addFlow);

		// instantiate required objects
		FlowPath flow1 = createTestFlowPath(1, "installer id", 1, 2, 3, 4);
		FlowPath flow2 = createTestFlowPath(2, "installer id", 2, 3, 4, 5);
		FlowPath flow3 = createTestFlowPath(3, "installer id", 3, 4, 5, 6);
		Map<Long, Object> shortestPathMap = new HashMap<Long, Object>();

		// setup expectations
		expectInitWithContext();
		expect((Map<Long,Object>)topoRouteService.prepareShortestPathTopo()
				).andReturn(shortestPathMap);

		expect(topoRouteService.getTopoShortestPath(
				shortestPathMap,
				flow1.dataPath().srcPort(),
				flow1.dataPath().dstPort())).andReturn(null);
		
		expect(topoRouteService.getTopoShortestPath(
				shortestPathMap,
				flow2.dataPath().srcPort(),
				flow2.dataPath().dstPort())).andReturn(null);

		expect(topoRouteService.getTopoShortestPath(
				shortestPathMap,
				flow3.dataPath().srcPort(),
				flow3.dataPath().dstPort())).andReturn(null);

		expectPrivate(fm, addFlow,
				EasyMock.cmpEq(flow1),
				EasyMock.anyObject(FlowId.class),
				EasyMock.anyObject(String.class)).andReturn(true);

		expectPrivate(fm, addFlow,
				EasyMock.cmpEq(flow2),
				EasyMock.anyObject(FlowId.class),
				EasyMock.anyObject(String.class)).andReturn(true);

		expectPrivate(fm, addFlow,
				EasyMock.cmpEq(flow3),
				EasyMock.anyObject(FlowId.class),
				EasyMock.anyObject(String.class)).andReturn(true);

		// start the test
		replayAll();

		fm.init(context);
		fm.measurementStorePathFlow(flow1);
		fm.measurementStorePathFlow(flow2);
		fm.measurementStorePathFlow(flow3);
		Boolean result = fm.measurementInstallPaths(3);
		
		// verify the test
		verifyAll();
		assertTrue(result);
	}
	
	/**
	 * Test method for {@link FlowManager#measurementGetInstallPathsTimeNsec()}.
	 * @throws Exception 
	 */
	@Test
	public final void testMeasurementGetInstallPathsTimeNsecSuccessNormally() throws Exception {
		final String addFlow = "addFlow";

		// create mock objects
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, addFlow);
		mockStaticPartial(System.class, "nanoTime");

		// instantiate required objects
		FlowPath flow1 = createTestFlowPath(1, "installer id", 1, 2, 3, 4);
		Map<Long, Object> shortestPathMap = new HashMap<Long, Object>();

		// setup expectations
		expectInitWithContext();
		expect(System.nanoTime()).andReturn(new Long(100000));
		expect(System.nanoTime()).andReturn(new Long(110000));
		expect((Map<Long,Object>)topoRouteService.prepareShortestPathTopo()
				).andReturn(shortestPathMap);
		expect(topoRouteService.getTopoShortestPath(
				shortestPathMap,
				flow1.dataPath().srcPort(),
				flow1.dataPath().dstPort())).andReturn(null);
		expectPrivate(fm, addFlow,
				EasyMock.cmpEq(flow1),
				EasyMock.anyObject(FlowId.class),
				EasyMock.anyObject(String.class)).andReturn(true);
		
		// start the test
		replayAll();

		fm.init(context);
		fm.measurementStorePathFlow(flow1).toString();
		fm.measurementInstallPaths(1);
		Long result = fm.measurementGetInstallPathsTimeNsec();
		
		// verify the test
		verifyAll();
		assertEquals(new Long(10000), result);
	}

	/**
	 * Test method for {@link FlowManager#measurementGetPerFlowInstallTime()}.
	 * @throws Exception 
	 */
	@Test
	public final void testMeasurementGetPerFlowInstallTimeSuccessNormally() throws Exception {
		final String addFlow = "addFlow";

		// create mock objects
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, addFlow);
		
		// instantiate required objects
		FlowPath flow1 = createTestFlowPath(1, "installer id", 1, 2, 3, 4);
		Map<Long, Object> shortestPathMap = new HashMap<Long, Object>();

		// setup expectations
		expectInitWithContext();
		expect((Map<Long,Object>)topoRouteService.prepareShortestPathTopo()
				).andReturn(shortestPathMap);

		expect(topoRouteService.getTopoShortestPath(
				shortestPathMap,
				flow1.dataPath().srcPort(),
				flow1.dataPath().dstPort())).andReturn(null);

		expectPrivate(fm, addFlow,
				EasyMock.cmpEq(flow1),
				EasyMock.anyObject(FlowId.class),
				EasyMock.anyObject(String.class)).andReturn(true);


		// start the test
		replayAll();

		fm.init(context);
		fm.measurementStorePathFlow(flow1);
		fm.measurementInstallPaths(10);
		String result = fm.measurementGetPerFlowInstallTime();
				
		// verify the test
		verifyAll();
		assertTrue(result.startsWith("ThreadAndTimePerFlow"));
	}

	/**
	 * Test method for {@link FlowManager#measurementClearAllPaths()}.
	 * @throws Exception 
	 */
	@Test
	public final void testMeasurementClearAllPathsSuccessNormally() throws Exception {
		// instantiate required objects
		FlowPath paramFlow = createTestFlowPath(100, "installer id", 1, 3, 2, 4);
		Map<Long, Object> shortestPathMap = new HashMap<Long, Object>();
		FlowManager fm = new FlowManager();

		// setup expectations
		expectInitWithContext();
		expect((Map<Long,Object>)topoRouteService.prepareShortestPathTopo()
				).andReturn(shortestPathMap);
		expect(topoRouteService.getTopoShortestPath(
				shortestPathMap,
				paramFlow.dataPath().srcPort(),
				paramFlow.dataPath().dstPort())).andReturn(null);
		topoRouteService.dropShortestPathTopo(shortestPathMap);
		
		// start the test
		replayAll();
		
		fm.init(context);
		fm.measurementStorePathFlow(paramFlow);
		Boolean result = fm.measurementClearAllPaths();
				
		// verify the test
		verifyAll();
		assertTrue(result);
		assertEquals(new Long(0), fm.measurementGetInstallPathsTimeNsec());
		assertEquals("", fm.measurementGetPerFlowInstallTime());
	}
	
		
	// INetMapStorage methods
	
	
	/**
	 * Test method for {@link FlowManager#init(String)}.
	 * @throws Exception 
	 */
	@Test
	public final void testInitSuccessNormally() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();

		// create mock objects
		op = createMock(GraphDBOperation.class);

		// setup expectations
		expectNew(GraphDBOperation.class, "/dummy/path").andReturn(op);
		
		// start the test
		replayAll();
		
		fm.init("/dummy/path");
		
		// verify the test
		verifyAll();
	}
	
	/**
	 * Test method for {@link FlowManager#close()}.
	 * @throws Exception 
	 */
	@Test
	public final void testCloseSuccessNormally() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();

		// setup expectations
		expectInitWithContext();
		op.close();
		
		// start the test
		replayAll();
		
		fm.init(context);
		fm.close();
		
		// verify the test
		verifyAll();
	}
	
	
	// IFloodlightModule methods
	
	
	/**
	 * Test method for {@link FlowManager#getModuleServices()}.
	 * @throws Exception 
	 */
	@Test
	public final void testGetModuleServicesSuccessNormally() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();

		// setup expectations
		expectInitWithContext();

		// start the test
		replayAll();
		
		fm.init(context);
		Collection<Class<? extends IFloodlightService>> l = fm.getModuleServices();

		// verify the test
		verifyAll();
		assertEquals(1, l.size());
		assertEquals(IFlowService.class, l.iterator().next());
	}

	/**
	 * Test method for {@link FlowManager#getServiceImpls()}.
	 * @throws Exception 
	 */
	@Test
	public final void testGetServiceImplsSuccessNormally() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();

		// setup expectations
		expectInitWithContext();

		// start the test
		replayAll();
		
		fm.init(context);
		Map<Class<? extends IFloodlightService>, IFloodlightService> si = fm.getServiceImpls();

		// verify the test
		verifyAll();
		assertEquals(1, si.size());
		assertTrue(si.containsKey(IFlowService.class));
		assertEquals(fm, si.get(IFlowService.class));	
	}

	/**
	 * Test method for {@link FlowManager#getModuleDependencies()}.
	 * @throws Exception
	 */
	@Test
	public final void testGetModuleDependenciesSuccessNormally() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();

		// setup expectations
		expectInitWithContext();

		// start the test
		replayAll();
		
		fm.init(context);
		Collection<Class<? extends IFloodlightService>> md = fm.getModuleDependencies();

		// verify the test
		verifyAll();
		assertEquals(2, md.size());
		assertTrue(md.contains(IFloodlightProviderService.class));
		assertTrue(md.contains(IRestApiService.class));
	}

	/**
	 * Test method for {@link FlowManager#init(FloodlightModuleContext)}.
	 * @throws Exception 
	 */
	@Test
	public final void testInitWithFloodlightModuleContextSuccessNormally() throws Exception {
		// instantiate required objects
		FlowManager fm = new FlowManager();
		
		// setup expectations
		expectInitWithContext();

		// start the test
		replayAll();
		
		fm.init(context);

		// verify the test
		verifyAll();
	}

	/**
	 * Test method for {@link FlowManager#startUp(FloodlightModuleContext)}.
	 * @throws Exception
	 */
	@Test
	public final void testStartupSuccessNormally() throws Exception {
		// create mock objects
		mockStaticPartial(Executors.class, "newScheduledThreadPool");
		ScheduledExecutorService scheduler = createMock(ScheduledExecutorService.class);

		// instantiate required objects
		FlowManager fm = new FlowManager();
		
		// setup expectations
		expectInitWithContext();
		expect(Executors.newScheduledThreadPool(1)).andReturn(scheduler);
		expect(Executors.newScheduledThreadPool(1)).andReturn(scheduler);
		expect(scheduler.scheduleAtFixedRate(
				EasyMock.anyObject(Runnable.class),
				EasyMock.anyLong(),
				EasyMock.anyLong(),
				EasyMock.anyObject(TimeUnit.class))).andReturn(null).times(2);
		restApi.addRestletRoutable(EasyMock.anyObject(FlowWebRoutable.class));

		// start the test
		replayAll();
		
		fm.init(context);
		fm.startUp(context);

		// verify the test
		verifyAll();
	}
	
	
	// other methods
	
	
	/**
	 * Test method for {@link FlowManager#clearFlow(FlowId)}.
	 * @throws Exception
	 */
	@Test
	public final void testClearFlowSuccessNormally() throws Exception {
		// create mock objects
		IFlowPath flowPath = createIFlowPathMock(123, "id", 1, 2, 3, 4);
		IFlowEntry flowEntry1 = createMock(IFlowEntry.class);
		IFlowEntry flowEntry2 = createMock(IFlowEntry.class);
		IFlowEntry flowEntry3 = createMock(IFlowEntry.class);
		
		// instantiate required objects
		FlowManager fm = new FlowManager();
		FlowId flowId = new FlowId(123);
		ArrayList<IFlowEntry> flowEntries = new ArrayList<IFlowEntry>();
		flowEntries.add(flowEntry1);
		flowEntries.add(flowEntry2);
		flowEntries.add(flowEntry3);

		// setup expectations
		expectInitWithContext();
		expect(op.searchFlowPath(cmpEq(flowId))).andReturn(flowPath);
		expect(flowPath.getFlowEntries()).andReturn(flowEntries);
		flowPath.removeFlowEntry(flowEntry1);
		flowPath.removeFlowEntry(flowEntry2);
		flowPath.removeFlowEntry(flowEntry3);
		op.removeFlowEntry(flowEntry1);
		op.removeFlowEntry(flowEntry2);
		op.removeFlowEntry(flowEntry3);
		op.removeFlowPath(flowPath);
		op.commit();

		// start the test
		replayAll();
		
		fm.init(context);
		fm.clearFlow(flowId);

		// verify the test
		verifyAll();
	}
	
	/**
	 * Test method for {@link FlowManager#getAllFlowsWithoutFlowEntries()}.
	 * @throws Exception 
	 */
	@Test
	public final void testGetAllFlowsWithoutFlowEntriesSuccessNormally() throws Exception {
		// create mock objects
		IFlowPath iFlowPath1 = createIFlowPathMock(1, "caller id", 1, 1, 2, 2); 
		IFlowPath iFlowPath2 = createIFlowPathMock(2, "caller id", 2, 5, 3, 5);
		
		// instantiate required objects
		ArrayList<IFlowPath> flowPaths = new ArrayList<IFlowPath>();
		flowPaths.add(iFlowPath1);
		flowPaths.add(iFlowPath2);
		FlowManager fm = new FlowManager();
		
		// setup expectations
		expectInitWithContext();
		op.commit();
		expect(op.getAllFlowPaths()).andReturn(flowPaths);
		
		// start the test
		replayAll();
		
		fm.init(context);
		ArrayList<IFlowPath> result = fm.getAllFlowsWithoutFlowEntries();
		
		// verify the test
		verifyAll();
		assertEquals(iFlowPath1, result.get(0));
		assertEquals(iFlowPath2, result.get(1));
		
		// TODO: does this method just return the replica of the flow paths?
	}
	
	/**
	 * Test method for {@link FlowManager#reconcileFlow(IFlowPath, DataPath)}.
	 * @throws Exception
	 */
	@Test
	public final void testReconcileFlowWithFlowPathAndDataPathSuccessNormally() throws Exception {
		final String addFlowEntry = "addFlowEntry";
		
		// create mock objects
		IFlowPath iFlowPath1 = createIFlowPathMock(1, "caller id", 1, 1, 2, 2);
		IFlowEntry iFlowEntry1 = createMock(IFlowEntry.class);
		IFlowEntry iFlowEntry2 = createMock(IFlowEntry.class);
		FlowManager fm = createPartialMockAndInvokeDefaultConstructor(FlowManager.class, addFlowEntry);
		
		// instantiate required objects
		FlowEntry flowEntry1 = new FlowEntry();
		flowEntry1.setDpid(new Dpid(1));
		flowEntry1.setFlowId(new FlowId(1));
		flowEntry1.setInPort(new Port((short) 1));
		flowEntry1.setOutPort(new Port((short) 11));
		flowEntry1.setFlowEntryId(new FlowEntryId(1));
		flowEntry1.setFlowEntryMatch(new FlowEntryMatch());
		flowEntry1.setFlowEntryActions(new ArrayList<FlowEntryAction>());
		flowEntry1.setFlowEntryErrorState(new FlowEntryErrorState());
		
		FlowEntry flowEntry2 = new FlowEntry();
		flowEntry2.setDpid(new Dpid(2));
		flowEntry2.setFlowId(new FlowId(2));
		flowEntry2.setInPort(new Port((short) 22)); 
		flowEntry2.setOutPort(new Port((short) 2));
		flowEntry2.setFlowEntryId(new FlowEntryId(2));
		flowEntry2.setFlowEntryMatch(new FlowEntryMatch());
		flowEntry2.setFlowEntryActions(new ArrayList<FlowEntryAction>());
		flowEntry2.setFlowEntryErrorState(new FlowEntryErrorState());
		
		DataPath dataPath = new DataPath();
		ArrayList<FlowEntry> flowEntries = new ArrayList<FlowEntry>();
		flowEntries.add(flowEntry1);
		flowEntries.add(flowEntry2);
		dataPath.setFlowEntries(flowEntries);
		
		ArrayList<IFlowEntry> oldFlowEntries = new ArrayList<IFlowEntry>();
		oldFlowEntries.add(iFlowEntry1);
		oldFlowEntries.add(iFlowEntry2);

		// setup expectations
		expectInitWithContext();
		expect(floodlightProvider.getSwitches()).andReturn(null); // TODO: why is this needed?
		expect(iFlowPath1.getFlowEntries()).andReturn(oldFlowEntries);
		iFlowEntry1.setUserState("FE_USER_DELETE");
		iFlowEntry1.setSwitchState("FE_SWITCH_NOT_UPDATED");
		iFlowEntry2.setUserState("FE_USER_DELETE");
		iFlowEntry2.setSwitchState("FE_SWITCH_NOT_UPDATED");
		expectPrivate(fm, addFlowEntry, iFlowPath1, flowEntry1).andReturn(null);
		expectPrivate(fm, addFlowEntry, iFlowPath1, flowEntry2).andReturn(null);

		// start the test
		replayAll();
		
		fm.init(context);
		Boolean result = fm.reconcileFlow(iFlowPath1, dataPath);
		
		// verify the test
		verifyAll();
		assertTrue(result);
		// TODO: write more asserts
	}
	
	/**
	 * Test method for {@link FlowManager#installFlowEntry(net.floodlightcontroller.core.IOFSwitch, IFlowPath, IFlowEntry)}.
	 * @throws Exception 
	 */
	@Test
	public final void testInstallFlowEntryWithIFlowPathSuccessNormally() throws Exception {
		// create mock object
		IOFSwitch iofSwitch = createNiceMock(IOFSwitch.class);
		IFlowPath iFlowPath = createIFlowPathMock(1, "id", 1, 2, 3, 4); 
		IFlowEntry iFlowEntry = createMock(IFlowEntry.class);
		BasicFactory basicFactory = createMock(BasicFactory.class);
		
		// instantiate required objects
		FlowManager fm = new FlowManager();
		
		// setup expectations
		expectInitWithContext();
		expect(iFlowEntry.getFlowEntryId()).andReturn(new FlowEntryId(123).toString());
		expect(iFlowEntry.getUserState()).andReturn("FE_USER_ADD");
		iFlowEntry.setSwitchState("FE_SWITCH_UPDATED");
		expect(iFlowEntry.getMatchInPort()).andReturn(new Short((short) 1));
		expect(iFlowEntry.getMatchEthernetFrameType()).andReturn(new Short((short)0x0800));
		expect(iFlowEntry.getMatchSrcIPv4Net()).andReturn("192.168.0.1");
		expect(iFlowEntry.getMatchDstIPv4Net()).andReturn("192.168.0.2");
		expect(iFlowEntry.getMatchSrcMac()).andReturn("01:23:45:67:89:01");
		expect(iFlowEntry.getMatchDstMac()).andReturn("01:23:45:67:89:02");
		expect(iFlowEntry.getActionOutput()).andReturn(new Short((short) 2));
		expect(floodlightProvider.getOFMessageFactory()).andReturn(basicFactory);
		expect(basicFactory.getMessage(OFType.FLOW_MOD)).andReturn(new OFFlowMod());
		expect(iofSwitch.getStringId()).andReturn(new Dpid(100).toString());

		// start the test
		replayAll();
		
		fm.init(context);
		Boolean result = fm.installFlowEntry(iofSwitch, iFlowPath, iFlowEntry);
		
		// verify the test
		verifyAll();
		assertTrue(result);
		// TODO: write more asserts
	}

	/**
	 * Test method for {@link FlowManager#installFlowEntry(net.floodlightcontroller.core.IOFSwitch, FlowPath, FlowEntry)}.
	 * The method seems to be not used for now.
	 */
	@Ignore @Test
	public final void testInstallFlowEntryWithFlowPathSuccessNormally() {
		fail("not yet implemented");
	}

	/**
	 * Test method for {@link FlowManager#removeFlowEntry(net.floodlightcontroller.core.IOFSwitch, FlowPath, FlowEntry)}.
	 * The method seems to be not implemented and not used for now.
	 */
	@Ignore @Test
	public final void testRemoveFlowEntrySuccessNormally() {
		fail("not yet implemented");
	}

	/**
	 * Test method for {@link FlowManager#installRemoteFlowEntry(FlowPath, FlowEntry)}.
	 * The method seems to be not implemented and not used for now.
	 */
	@Ignore @Test
	public final void testInstallRemoteFlowEntrySuccessNormally() {
		fail("not yet implemented");
	}

	/**
	 * Test method for {@link FlowManager#removeRemoteFlowEntry(FlowPath, FlowEntry)}.
	 * The method seems to be not implemented and not used for now.
	 */
	@Ignore @Test
	public final void testRemoveRemoteFlowEntrySuccessNormally() {
		fail("not yet implemented");
	}
}
