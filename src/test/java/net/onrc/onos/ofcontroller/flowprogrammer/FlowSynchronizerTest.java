package net.onrc.onos.ofcontroller.flowprogrammer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import io.netty.util.concurrent.Future;
import net.floodlightcontroller.core.IOFSwitch;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.flowmanager.FlowDatabaseOperation;
import net.onrc.onos.ofcontroller.util.FlowEntry;
import net.onrc.onos.ofcontroller.util.FlowEntryId;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.OFType;
import org.openflow.protocol.statistics.OFFlowStatisticsReply;
import org.openflow.protocol.statistics.OFStatistics;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({FlowSynchronizer.class, GraphDBOperation.class, FlowDatabaseOperation.class})
public class FlowSynchronizerTest {
	private FlowPusher pusher;
	private FlowSynchronizer sync;
	private List<Long> idAdded;
	private List<Long> idRemoved;

	@Before
	public void setUp() throws Exception {
		idAdded = new ArrayList<Long>();
		idRemoved = new ArrayList<Long>();
		
		pusher = EasyMock.createMock(FlowPusher.class);
		pusher.add(EasyMock.anyObject(IOFSwitch.class), EasyMock.anyObject(OFMessage.class));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
				@Override
				public Object answer() throws Throwable {
					OFMessage msg = (OFMessage)EasyMock.getCurrentArguments()[1];
					if (msg.getType().equals(OFType.FLOW_MOD)) {
						OFFlowMod fm = (OFFlowMod)msg;
						if (fm.getCommand() == OFFlowMod.OFPFC_DELETE_STRICT) {
							idRemoved.add(fm.getCookie());
						}
					}
					return null;
				}
			}).anyTimes();
		pusher.pushFlowEntry(EasyMock.anyObject(IOFSwitch.class), EasyMock.anyObject(FlowEntry.class));
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				FlowEntry flow = (FlowEntry)EasyMock.getCurrentArguments()[1];
				idAdded.add(flow.flowEntryId().value());
				return null;
			}
		}).anyTimes();
		EasyMock.replay(pusher);
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test that synchronization doesn't affect anything in case either DB and
	 * flow table has the same entries.
	 */
	@Test
	public void testStable() {
		// Create mock of flow table : flow 1
		IOFSwitch sw = createMockSwitch(new long[] {1});
		
		// Create mock of flow entries : flow 1
		initMockGraph(new long[] {1});
		
		// synchronize
		doSynchronization(sw,1000);
		
		// check if flow is not changed
		assertEquals(0, idAdded.size());
		assertEquals(0, idRemoved.size());
	}

	/**
	 * Test that an flow is added in case DB has an extra FlowEntry.
	 */
	@Test
	public void testSingleAdd() {
		// Create mock of flow table : null
		IOFSwitch sw = createMockSwitch(new long[] {});
		
		// Create mock of flow entries : flow 1
		initMockGraph(new long[] {1});
		
		// synchronize
		doSynchronization(sw,1000);
		
		// check if single flow is installed
		assertEquals(1, idAdded.size());
		assertTrue(idAdded.contains((long)1));
		assertEquals(0, idRemoved.size());
	}

	/**
	 * Test that an flow is deleted in case switch has an extra FlowEntry.
	 */
	@Test
	public void testSingleDelete() {
		// Create mock of flow table : flow 1
		IOFSwitch sw = createMockSwitch(new long[] {1});
		
		// Create mock of flow entries : null
		initMockGraph(new long[] {});
		
		// synchronize
		doSynchronization(sw,1000);
		
		// check if single flow is deleted
		assertEquals(0, idAdded.size());
		assertEquals(1, idRemoved.size());
		assertTrue(idRemoved.contains((long)1));
	}
	
	/**
	 * Test that appropriate flows are added and other appropriate flows are deleted
	 * in case flows in DB are overlapping flows in switch.
	 */
	@Test
	public void testMixed() {
		// Create mock of flow table : flow 1,2,3
		IOFSwitch sw = createMockSwitch(new long[] {1,2,3});
		
		// Create mock of flow entries : flow 2,3,4,5
		initMockGraph(new long[] {2,3,4,5});
		
		// synchronize
		doSynchronization(sw,1000);
		
		// check if two flows {4,5} is installed and one flow {1} is deleted
		assertEquals(2, idAdded.size());
		assertTrue(idAdded.contains((long)4));
		assertTrue(idAdded.contains((long)5));
		assertEquals(1, idRemoved.size());
		assertTrue(idRemoved.contains((long)1));
	}
	

	@Test
	public void testMassive() {
		// Create mock of flow table : flow 0-1999
		long [] swIdList = new long [2000];
		for (long i = 0; i < 2000; ++i) {
			swIdList[(int)i] = i;
		}
		IOFSwitch sw = createMockSwitch(swIdList);
		
		// Create mock of flow entries : flow 1500-3499
		long [] dbIdList = new long [2000];
		for (long i = 0; i < 2000; ++i) {
			dbIdList[(int)i] = 1500 + i;
		}
		initMockGraph(dbIdList);

		// synchronize
		doSynchronization(sw, 3000);
		
		// check if 1500 flows {2000-3499} is installed and 1500 flows {0,...,1499} is deleted
		assertEquals(1500, idAdded.size());
		for (long i = 2000; i < 3500; ++i) {
			assertTrue(idAdded.contains(i));
		}
		assertEquals(1500, idRemoved.size());
		for (long i = 0; i < 1500; ++i) {
			assertTrue(idRemoved.contains(i));
		}
	}

	/**
	 * Create mock IOFSwitch with flow table which has arbitrary flows.
	 * @param cookieList List of FlowEntry IDs switch has.
	 * @return Mock object.
	 */
	private IOFSwitch createMockSwitch(long[] cookieList) {
		IOFSwitch sw = EasyMock.createMock(IOFSwitch.class);
		EasyMock.expect(sw.getId()).andReturn((long)1).anyTimes();
		
		List<OFStatistics> stats = new ArrayList<OFStatistics>();
		for (long cookie : cookieList) {
			stats.add(createReply(cookie));
		}
		
		@SuppressWarnings("unchecked")
		Future<List<OFStatistics>> future = EasyMock.createMock(Future.class);
		try {
			EasyMock.expect(future.get()).andReturn(stats).once();
		} catch (InterruptedException e1) {
			fail("Failed in Future#get()");
		} catch (ExecutionException e1) {
			fail("Failed in Future#get()");
		}
		EasyMock.replay(future);
		
		try {
			EasyMock.expect(sw.getStatistics(EasyMock.anyObject(OFStatisticsRequest.class)))
				.andReturn(future).once();
		} catch (IOException e) {
			fail("Failed in IOFSwitch#getStatistics()");
		}
		
		EasyMock.replay(sw);
		return sw;
	}
	
	/**
	 * Create single OFFlowStatisticsReply object which is actually obtained from switch.
	 * @param cookie Cookie value, which indicates ID of FlowEntry installed to switch.
	 * @return Created object.
	 */
	private OFFlowStatisticsReply createReply(long cookie) {
		OFFlowStatisticsReply stat = new OFFlowStatisticsReply();
		OFMatch match = new OFMatch();
		
		stat.setCookie(cookie);
		stat.setMatch(match);
		stat.setPriority((short)1);

		return stat;
	}
	
	/**
	 * Create mock GraphDBOperation and FlowDatabaseOperation to mock DB.
	 * @param idList List of FlowEntry IDs stored in DB.
	 */
	private void initMockGraph(long[] idList) {
		List<IFlowEntry> flowEntryList = new ArrayList<IFlowEntry>();
		
		for (long id : idList) {
			IFlowEntry entry = EasyMock.createMock(IFlowEntry.class);
			EasyMock.expect(entry.getFlowEntryId()).andReturn(String.valueOf(id)).anyTimes();
			EasyMock.replay(entry);
			flowEntryList.add(entry);
		}
		
		ISwitchObject swObj = EasyMock.createMock(ISwitchObject.class);
		EasyMock.expect(swObj.getFlowEntries()).andReturn(flowEntryList).once();
		EasyMock.replay(swObj);
		
		GraphDBOperation mockOp = PowerMock.createMock(GraphDBOperation.class);
		EasyMock.expect(mockOp.searchSwitch(EasyMock.anyObject(String.class))).andReturn(swObj).once();
		
		PowerMock.mockStatic(FlowDatabaseOperation.class);
		for (IFlowEntry entry : flowEntryList) {
			EasyMock.expect(FlowDatabaseOperation.extractFlowEntry(EasyMock.eq(entry)))
				.andAnswer(new IAnswer<FlowEntry>() {
					@Override
					public FlowEntry answer() throws Throwable {
						IFlowEntry iflow = (IFlowEntry)EasyMock.getCurrentArguments()[0];
						long flowEntryId = Long.valueOf(iflow.getFlowEntryId());
						
						FlowEntry flow = EasyMock.createMock(FlowEntry.class);
						EasyMock.expect(flow.flowEntryId()).andReturn(new FlowEntryId(flowEntryId)).anyTimes();
						EasyMock.replay(flow);
						return flow;
					}
					
				}).anyTimes();
			EasyMock.expect(mockOp.searchFlowEntry(EasyMock.eq(new FlowEntryId(entry.getFlowEntryId()))))
				.andReturn(entry);
		}
		PowerMock.replay(FlowDatabaseOperation.class);
		EasyMock.replay(mockOp);
		
		try {
			PowerMock.expectNew(GraphDBOperation.class, "").andReturn(mockOp);
		} catch (Exception e) {
			fail("Failed to create GraphDBOperation");
		}
		PowerMock.replay(GraphDBOperation.class);
	}
	
	/**
	 * Instantiate FlowSynchronizer and sync flows.
	 * @param sw Target IOFSwitch object
	 */
	private void doSynchronization(IOFSwitch sw, long wait) {
		sync = new FlowSynchronizer();
		sync.init(pusher);
		sync.synchronize(sw);
		
		try {
			Thread.sleep(wait);
		} catch (InterruptedException e) {
			fail("Failed to sleep");
		}
	}
}
