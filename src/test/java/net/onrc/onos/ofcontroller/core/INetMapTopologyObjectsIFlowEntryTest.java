package net.onrc.onos.ofcontroller.core;

import static org.junit.Assert.*;

import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.TestDatabaseManager;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.LoggerFactory;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

//Add Powermock preparation
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, SwitchStorageImpl.class})
public class INetMapTopologyObjectsIFlowEntryTest {
	
	//The test network is ./titan/schema/test-network.xml

	protected static org.slf4j.Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	String conf;
    private GraphDBConnection conn = null;
    private GraphDBOperation ope = null;
    private TitanGraph titanGraph = null;
	private IFlowEntry flowEntry = null;
	
	@Before
	public void setUp() throws Exception {
		conf = "/dummy/path/to/db";
		
		// Make mock cassandra DB
		// Replace TitanFactory.open() to return mock DB
		TestDatabaseManager.deleteTestDatabase();
		titanGraph = TestDatabaseManager.getTestDatabase();
		//TestDatabaseManager.populateTestData(titanGraph);
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(titanGraph);
		PowerMock.replay(TitanFactory.class);
		
		conn = GraphDBConnection.getInstance(conf);
		ope = new GraphDBOperation(conn);
		
		flowEntry = ope.newFlowEntry();
	}

	@After
	public void tearDown() throws Exception {
		titanGraph.shutdown();
		TestDatabaseManager.deleteTestDatabase();
	}
	
	/**
	 * Desc:
	 *  Test method for set and get FlowEntryId.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set FlowEntryId.
	 * 2. Should get FlowEntryId.
	 */
	@Test
	public void testSetGetFlowEntryId() {
		String flowEntryId = "xx";
		flowEntry.setFlowEntryId(flowEntryId);
		assertEquals(flowEntry.getFlowEntryId(), flowEntryId);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get SwitchDpid.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set SwitchDpid.
	 * 2. Should get SwitchDpid.
	 */
	@Test
	public void testSetGetSwitchDpid() {
		String switchDpid = "00:00:00:00:00:11";
		flowEntry.setSwitchDpid(switchDpid);
		assertEquals(flowEntry.getSwitchDpid(), switchDpid);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get UserState.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set UserState.
	 * 2. Should get UserState.
	 */
	@Test
	public void testSetGetUserState() {
		String userStete = "good";
		flowEntry.setUserState(userStete);
		assertEquals(flowEntry.getUserState(), userStete);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get SwitchState.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set SwitchState.
	 * 2. Should get SwitchState.
	 */
	@Test
	public void testSetGetSwitchState() {
		String switchStete = "ACTIVE";
		flowEntry.setSwitchState(switchStete);
		assertEquals(flowEntry.getSwitchState(), switchStete);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get ErrorStateType.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set ErrorStateType.
	 * 2. Should get ErrorStateType.
	 */
	@Test
	public void testSetGetErrorStateType() {
		String errorSteteType = "error";
		flowEntry.setErrorStateType(errorSteteType);
		assertEquals(flowEntry.getErrorStateType(), errorSteteType);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get ErrorStateCode.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set ErrorStateCode.
	 * 2. Should get ErrorStateCode.
	 */
	@Test
	public void testSetGetErrorStateCode() {
		String errorSteteCode = "error";
		flowEntry.setErrorStateCode(errorSteteCode);
		assertEquals(flowEntry.getErrorStateCode(), errorSteteCode);
	}	
	
	/**
	 * Desc:
	 *  Test method for set and get MatchInPort.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set MatchInPort.
	 * 2. Should get MatchInPort.
	 */
	@Test
	public void testSetGetMatchInPort() {
		Short inPort = 1;
		flowEntry.setMatchInPort(inPort);
		assertEquals(flowEntry.getMatchInPort(), inPort);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get MatchEthernetFrameType.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set MatchEthernetFrameType.
	 * 2. Should get MatchEthernetFrameType.
	 */
	@Test
	public void testSetGetMatchEthernetFrameType() {
		Short matchEthernetFrameType = 1;
		flowEntry.setMatchEthernetFrameType(matchEthernetFrameType);
		assertEquals(flowEntry.getMatchEthernetFrameType(), matchEthernetFrameType);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get MatchSrcMac.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set MatchSrcMac.
	 * 2. Should get MatchSrcMac.
	 */
	@Test
	public void testSetGetMatchSrcMac() {
		String matchSrcMac = "00:00:00:00:00:11";
		flowEntry.setMatchSrcMac(matchSrcMac);
		assertEquals(flowEntry.getMatchSrcMac(), matchSrcMac);
	}	
	
	/**
	 * Desc:
	 *  Test method for set and get MatchDstMac.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set MatchDstMac.
	 * 2. Should get MatchDstMac.
	 */
	@Test
	public void testSetGetMatchDstMac() {
		String matchDstMac = "00:00:00:00:00:11";
		flowEntry.setMatchDstMac(matchDstMac);
		assertEquals(flowEntry.getMatchDstMac(), matchDstMac);
	}	
	
	/**
	 * Desc:
	 *  Test method for set and get SrcIPv4Net.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set SrcIPv4Net.
	 * 2. Should get SrcIPv4Net.
	 */
	@Test
	public void testSetGetMatchSrcIPv4Net() {
		String srcIPv4Net = "192.168.0.1";
		flowEntry.setMatchSrcIPv4Net(srcIPv4Net);
		assertEquals(flowEntry.getMatchSrcIPv4Net(), srcIPv4Net);
	}	
	
	/**
	 * Desc:
	 *  Test method for set and get MatchDstIPv4Net.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set MatchDstIPv4Net.
	 * 2. Should get MatchDstIPv4Net.
	 */
	@Test
	public void testSetGetMatchDstIPv4Net() {
		String dstIPv4Net = "192.168.0.1";
		flowEntry.setMatchDstIPv4Net(dstIPv4Net);
		assertEquals(flowEntry.getMatchDstIPv4Net(), dstIPv4Net);
	}	
	
	/**
	 * Desc:
	 *  Test method for set and get ActionOutput.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set ActionOutput.
	 * 2. Should get ActionOutput.
	 */
	@Test
	public void testSetGetActionOutput() {
		Short actionOutput = 1;
		flowEntry.setActionOutput(actionOutput);
		assertEquals(flowEntry.getActionOutput(), actionOutput);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get FlowPath.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set FlowPath.
	 * 2. Should get FlowPath.
	 */
	@Test
	public void testSetGetFlowPath() {
		IFlowPath fp = ope.newFlowPath();
		String flowId = "xx";
		fp.setFlowId(flowId);
		flowEntry.setFlow(fp);
		IFlowPath fp2 = flowEntry.getFlow();
		assertEquals(fp2.getFlowId(), flowId);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get Switch.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set Switch.
	 * 2. Should get Switch.
	 */
	@Test
	public void testSetGetSwitch() {
		String dpid = "00:00:00:00:00:22";
		ISwitchObject sw1 = ope.newSwitch(dpid);
		flowEntry.setSwitch(sw1);
		ISwitchObject sw2 = flowEntry.getSwitch();
		assertEquals(sw2, sw1);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get InPort.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set InPort.
	 * 2. Should get InPort.
	 */
	@Test
	public void testSetGetInPort() {
		String dpid = "00:00:00:00:00:22";
		Short portNum = 4;
		IPortObject port1 = ope.newPort(dpid, portNum);
		flowEntry.setInPort(port1);
		IPortObject port2 = flowEntry.getInPort();
		assertEquals(port2, port1);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get OutPort.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set OutPort.
	 * 2. Should get OutPort.
	 */
	@Test
	public void testSetGetOutPort() {
		String dpid = "00:00:00:00:00:22";
		Short portNum = 4;
		IPortObject port1 = ope.newPort(dpid, portNum);
		flowEntry.setOutPort(port1);
		IPortObject port2 = flowEntry.getOutPort();
		assertEquals(port2, port1);
	}
}
