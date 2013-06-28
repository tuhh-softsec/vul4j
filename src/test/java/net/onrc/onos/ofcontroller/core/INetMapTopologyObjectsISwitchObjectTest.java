package net.onrc.onos.ofcontroller.core;

import static org.junit.Assert.*;

import java.util.HashMap;

import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
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
public class INetMapTopologyObjectsISwitchObjectTest {
	
	//The test network is ./titan/schema/test-network.xml

	protected static org.slf4j.Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	String conf;
    private GraphDBConnection conn = null;
    private GraphDBOperation ope = null;
    private TitanGraph titanGraph = null;
	
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
	}

	@After
	public void tearDown() throws Exception {
		titanGraph.shutdown();
		TestDatabaseManager.deleteTestDatabase();
	}
	
	/**
	 * Desc:
	 *  Test method for get and set state method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the status of the switch.
	 * 2. Should get the status of the switch.
	 */
	@Test
	public void testSetGetState() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String state = "ACTIVE";
		ISwitchObject swObj = ope.newSwitch(dpid);
		swObj.setState(state);
		assertEquals(swObj.getState(), state);
	}
	
	/**
	 * Desc:
	 *  Test method for get and set Type method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the Type of the switch.
	 * 2. Should get the Type of the switch.
	 */
	@Test
	public void testSetGetType() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String type = "Switch";
		ISwitchObject swObj = ope.newSwitch(dpid);
		swObj.setType("Switch");
		assertEquals(swObj.getType(), type);
	}
	
	/**
	 * Desc:
	 *  Test method for getDPID method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get the dpid of the switch.
	 */
	@Test
	public void testGetDPID() {
		String dpid = "00:00:00:00:00:00:0a:07";
		ISwitchObject swObj = ope.newSwitch(dpid);
		
		assertEquals(swObj.getDPID(), dpid);
	}
	
	/**
	 * Desc:
	 *  Test method for setDPID method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the dpid of the switch.
	 */
	@Test
	public void testSetDPID() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String dpid2 = "00:00:00:00:00:00:0a:08";
		ISwitchObject obj = ope.newSwitch(dpid);
		assertEquals(obj.getDPID(), dpid);
		
		obj.setDPID(dpid2);
		assertEquals(obj.getDPID(), dpid2);
	}
	
	/**
	 * Desc:
	 *  Test method for getPorts method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get all of ports taken by the switch.
	 */
	@Test
	public void testGetPorts() {
		String dpid = "00:00:00:00:00:00:0a:07";
		Short portNumber = 1;
		int testSwitchPortNumber = 1;
		ISwitchObject swObj = ope.newSwitch(dpid);
		IPortObject portObj = ope.newPort(dpid, portNumber);

		swObj.addPort(portObj);
		int i = 0;
		for(IPortObject port : swObj.getPorts()){
			i++;
		}
		assertEquals(testSwitchPortNumber, 1);
	}
	
	/**
	 * Desc:
	 *  Test method for add and getPort method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should add the port.
	 * 1. Should get the port.
	 */
	@Test
	public void testGetPort() {
		String dpid = "00:00:00:00:00:00:0a:07";
		Short portNumber = 1;
		ISwitchObject swObj = ope.newSwitch(dpid);
		IPortObject portObj = ope.newPort(dpid, portNumber);
		
		swObj.addPort(portObj);
		IPortObject portObj2 = swObj.getPort(portNumber);
		assertEquals(portObj, portObj2);
	}
	
	/**
	 * Desc:
	 *  Test method for add and removePort method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should add a port to the switch.
	 * 1. Should remove a port from the switch.
	 */
	@Test
	public void testAddRemovePorts() {
		String dpid = "00:00:00:00:00:00:0a:07";
		Short portNum = 1;
		ISwitchObject swObj = ope.newSwitch(dpid);
		IPortObject portObj = ope.newPort(dpid, portNum);
		swObj.addPort(portObj);
		
		IPortObject portObj2 = swObj.getPort(portNum);
		assertEquals(portObj2, portObj);
		swObj.removePort(portObj);
		assertNull(swObj.getPort(portNum));
	}
	
	/**
	 * Desc:
	 *  Test method for getDevices method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get all devices attached to the switch.
	 */
	@Test
	public void testGetDevices() {
		String dpid = "00:00:00:00:00:00:0a:07";
		Short portNum = 1;
		String devMac = "00:00:00:00:00:11";
		int numOfDev = 1;
		
		ISwitchObject swObj = ope.newSwitch(dpid);
		IPortObject portObj = ope.newPort(dpid, portNum);
		IDeviceObject devObj = ope.newDevice();
		devObj.setMACAddress(devMac);
		swObj.addPort(portObj);
		portObj.setDevice(devObj);
		
		int i = 0;
		for(IDeviceObject dev : swObj.getDevices()){
			i++;
		}
		assertEquals(i, numOfDev);
	}
	
	/**
	 * Desc:
	 *  Test method for getFlowEntries method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get all flowEntries attached to the switch.
	 */
	@Test
	public void testGetFlowEntries() {
		String dpid = "00:00:00:00:00:00:0a:07";
		Short number = 1;	
		Short number2 = 2;
		Short number3 = 3;
		ISwitchObject swObj = ope.newSwitch(dpid);
		IPortObject portObj = ope.newPort(dpid, number);
		IPortObject portObj2 = ope.newPort(dpid, number2);
		IPortObject portObj3 = ope.newPort(dpid, number3);

		swObj.addPort(portObj);	
		swObj.addPort(portObj2);
		swObj.addPort(portObj3);
		
		IFlowPath flowPathObj = ope.newFlowPath();
		
		String flowEId = "1";
		IFlowEntry flowEntryObj = ope.newFlowEntry();		
		flowEntryObj.setFlowEntryId(flowEId);
		flowEntryObj.setInPort(portObj);
		flowEntryObj.setOutPort(portObj2);
		flowEntryObj.setSwitch(swObj);
		flowEntryObj.setFlow(flowPathObj);
		
		String flowEId2 = "2";
		IFlowEntry flowEntryObj2 = ope.newFlowEntry();		
		flowEntryObj2.setFlowEntryId(flowEId2);
		flowEntryObj2.setInPort(portObj);
		flowEntryObj2.setOutPort(portObj3);
		flowEntryObj2.setSwitch(swObj);
		flowEntryObj2.setFlow(flowPathObj);
		
		HashMap<String, IFlowEntry> flowEntryList = new HashMap<String, IFlowEntry>();
		for(IFlowEntry flowEnt : swObj.getFlowEntries())
		{				
			flowEntryList.put(flowEnt.getFlowEntryId(), flowEnt);
		}
		
		assertTrue(flowEntryList.containsValue(flowEntryObj));
		assertTrue(flowEntryList.containsValue(flowEntryObj2));
	}

}
