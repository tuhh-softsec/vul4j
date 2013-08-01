package net.onrc.onos.ofcontroller.core;

import static org.junit.Assert.*;

import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;
import net.onrc.onos.ofcontroller.core.internal.TestDatabaseManager;
import net.onrc.onos.ofcontroller.flowmanager.FlowManager;
import net.onrc.onos.ofcontroller.util.FlowId;
import net.onrc.onos.ofcontroller.util.FlowPath;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.protocol.OFPhysicalPort.OFPortState;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.slf4j.LoggerFactory;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import java.util.ArrayList;
import java.util.HashMap;

//Add Powermock preparation
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, SwitchStorageImpl.class})
public class INetMapTopologyObjectsIPortObjectTest {
	
	//The test network is ./titan/schema/test-network.xml

	protected static org.slf4j.Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	String conf;
    private GraphDBConnection conn = null;
    private GraphDBOperation ope = null;
    private TitanGraph titanGraph = null;
    
    private ISwitchObject swObj;
    private IPortObject portObj;
    private IPortObject portObj2;
    String dpid;
    Short number;
    Short number2;
    
    private ISwitchObject swObjParty;
    private IPortObject portObjParty1;
    private IPortObject portObjParty2;  
    String dpidParty;
    Short numberParty1;
    Short numberParty2;
    
    
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
		
		dpid = "00:00:00:00:00:00:0a:07";
		number = 1;	
		number2 = 2;
		swObj = ope.newSwitch(dpid);
		portObj = ope.newPort(dpid, number);
		portObj2 = ope.newPort(dpid, number2);

		swObj.addPort(portObj);	
		swObj.addPort(portObj2);
		
		dpidParty = "00:00:00:00:00:00:0a:08";
		numberParty1 = 1;
		numberParty2 = 2;
		swObjParty = ope.newSwitch(dpidParty);
		portObjParty1 = ope.newPort(dpidParty, numberParty1);
		portObjParty2 = ope.newPort(dpidParty, numberParty2);
		swObjParty.addPort(portObjParty1);	
		swObjParty.addPort(portObjParty2);
	}

	@After
	public void tearDown() throws Exception {
		titanGraph.shutdown();
		TestDatabaseManager.deleteTestDatabase();
	}
	
	/**
	 * Desc:
	 *  Test method for set and get port number property.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the port number.
	 * 2. Should get the port number.
	 */
	@Test
	public void testSetGetNumber() {
		assertEquals(portObj.getNumber(), number);
		Short testedNumber = 4;
		portObj.setNumber(testedNumber);
		assertEquals(portObj.getNumber(), testedNumber);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get port id property.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the port id.
	 * 2. Should get the port id.
	 */
	@Test
	public void testSetGetPortId() {
		String portId = "test1";
		portObj.setPortId(portId);
		assertEquals(portObj.getPortId(), portId);
	}
	
	/**
	 * Desc:
	 *  Test method for set and get port desc property.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the port desc.
	 * 2. Should get the port desc.
	 */
	@Test
	public void testSetGetDesc() {
		String testedDesc = "port 4 at ATL Switch";
		portObj.setDesc(testedDesc);
		assertEquals(portObj.getDesc(), testedDesc);
	}
	
	
	/**
	 * Desc:
	 *  Test method for set and get port status property.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the port status.
	 * 2. Should get the port status.
	 */
	@Test
	public void testSetGetPortState() {
		Integer portState = OFPortState.OFPPS_STP_FORWARD.getValue();
		portObj.setPortState(portState);
		assertEquals(portObj.getPortState(), portState);
	}
	
	/**
	 * Desc:
	 *  Test method for get switch object.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get the switch status.
	 */
	@Test
	public void testGetSwitch() {
		ISwitchObject sw = portObj.getSwitch();
		assertEquals(sw.getDPID(), dpid);
	}
	
	private boolean checkIDeviceObject(IPortObject IportObj, String mac)
	{
		HashMap<String, IDeviceObject> devList = new HashMap<String, IDeviceObject>();
		for(IDeviceObject IdevObj : IportObj.getDevices())
		{
			devList.put(IdevObj.getMACAddress(), IdevObj);
		}
		return devList.containsKey(mac);
	}
	
	/**
	 * Desc:
	 *  Test method for set and remove device object.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the device object.
	 * 2. SHould remove the device object.
	 */
	@Test
	public void testSetAndRemoveDevice() {
		IDeviceObject devObj = ope.newDevice();
		String devMac = "00:00:00:00:00:11";
		devObj.setMACAddress(devMac);
		
		boolean b = checkIDeviceObject(portObj, devMac);
		assertTrue(!b);
		portObj.setDevice(devObj);
		boolean b2 = checkIDeviceObject(portObj, devMac);
		assertTrue(b2);
		
		portObj.removeDevice(devObj);
		boolean b3 = checkIDeviceObject(portObj, devMac);
		assertTrue(!b3);
		
	}	
	
	/**
	 * Desc:
	 *  Test method for get devices object.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get the device objects.
	 */
	@Test
	public void testGetDevices() {
		IDeviceObject devObj = ope.newDevice();
		String devMac = "58:55:ca:c4:1b:a0";
		devObj.setMACAddress(devMac);
		
		portObj.setDevice(devObj);
		boolean b = checkIDeviceObject(portObj, devMac);
		assertTrue(b);
	}
	
	/**
	 * Desc:
	 *  Test method for set, get and remove linked port.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the linked objects.
	 * 2. Should get the linked objects.
	 * 3. SHould remove the liked objects.
	 */
	@Test
	public void testSetGetRemoveLinkedPorts() {
		String dpidParty = "00:00:00:00:00:00:00:08";
		ISwitchObject swParty = ope.newSwitch(dpidParty);
		Short poShort = 1;
		IPortObject poParty = ope.newPort(dpidParty, poShort);
		swParty.addPort(poParty);
		
		portObj.setLinkPort(poParty);
		
		ArrayList<IPortObject> iPortList = new ArrayList<IPortObject>();
		for(IPortObject port : portObj.getLinkedPorts()) {
			iPortList.add(port);
		}	
		assertTrue(iPortList.contains(poParty));	
		
		portObj.removeLink(poParty);
		
		ArrayList<IPortObject> iPortList2 = new ArrayList<IPortObject>();
		for(IPortObject port : portObj.getLinkedPorts()) {
			iPortList2.add(port);
		}	
		
		assertTrue(!iPortList2.contains(poParty));
	}
	
	/**
	 * Desc:
	 *  Test method for get inbound flowEntry
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get the inbound flowEntry.
	 */
	@Test
	public void testGetInFlowEntries() {

		portObj.setLinkPort(portObj2);
		
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
		flowEntryObj2.setInPort(portObjParty1);
		flowEntryObj2.setOutPort(portObjParty2);
		flowEntryObj.setSwitch(swObjParty);
		flowEntryObj2.setFlow(flowPathObj);
		
		HashMap<String, IFlowEntry> flowEntryList = new HashMap<String, IFlowEntry>();
		for(IFlowEntry flowEnt : portObj.getInFlowEntries())
		{				
			flowEntryList.put(flowEnt.getFlowEntryId(), flowEnt);
		}
		
		assertTrue(flowEntryList.containsValue(flowEntryObj));
		
	}
	
	/**
	 * Desc:
	 *  Test method for get outbound flowEntry
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get the outbound flowEntry.
	 */
	@Test
	public void testGetOutFlowEntries() {
		
		portObj.setLinkPort(portObj2);
		
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
		flowEntryObj2.setInPort(portObjParty1);
		flowEntryObj2.setOutPort(portObjParty2);
		flowEntryObj.setSwitch(swObjParty);
		flowEntryObj2.setFlow(flowPathObj);
		
		HashMap<String, IFlowEntry> flowEntryList = new HashMap<String, IFlowEntry>();
		for(IFlowEntry flowEnt : portObj2.getOutFlowEntries())
		{				
			flowEntryList.put(flowEnt.getFlowEntryId(), flowEnt);
		}
		
		assertTrue(flowEntryList.containsValue(flowEntryObj));
		
	}

}
