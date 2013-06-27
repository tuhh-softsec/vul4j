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
public class INetMapTopologyObjectsIDeviceObjectTest {
	
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
	 *  Test method for get and set MacAddress method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the mac address.
	 * 2. Should get the mac address.
	 */
	@Test
	public void testSetGetMacAddress() {
		String macaddr = "00:00:00:00:00:00:0a:07";
		IDeviceObject devObj = ope.newDevice();
		devObj.setMACAddress(macaddr);
		assertEquals(devObj.getMACAddress(), macaddr);
	}
	
	/**
	 * Desc:
	 *  Test method for get and set IPAddress method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set the ip address.
	 * 2. Should get the ip address.
	 */
	@Test
	public void testSetGetIPAddress() {
		String ipaddr = "192.168.0.1";
		IDeviceObject devObj = ope.newDevice();
		devObj.setIPAddress(ipaddr);
		assertEquals(devObj.getIPAddress(), ipaddr);
	}
	
	/**
	 * Desc:
	 *  Test method for get attached port.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get the attached ports.
	 */
	@Test
	public void testGetAttachedPort() {
		String dpid = "00:00:00:00:00:00:0a:07";
		Short number = 1;	
		Short number2 = 2;
		IPortObject portObj = ope.newPort(dpid, number);
		IPortObject portObj2 = ope.newPort(dpid, number2);
		
		String ipaddr = "192.168.0.1";
		IDeviceObject devObj = ope.newDevice();
		
		portObj.setDevice(devObj);
		portObj2.setDevice(devObj);
		
		HashMap<Short, IPortObject> portObjectList = new HashMap<Short, IPortObject>();
		for(IPortObject port : devObj.getAttachedPorts())
		{
			portObjectList.put(port.getNumber(), port);
		}
		
		assertTrue(portObjectList.containsValue(portObj));
		assertTrue(portObjectList.containsValue(portObj2));
		
	}
	
	/**
	 * Desc:
	 *  Test method for set and remove host port method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should set host port from the device.
	 * 2. Should remove host port from the device.
	 */
	@Test
	public void testSetRemoveHostPort() {
		String dpid = "00:00:00:00:00:00:0a:07";
		Short number = 1;	
		Short number2 = 2;
		IPortObject portObj = ope.newPort(dpid, number);
		IPortObject portObj2 = ope.newPort(dpid, number2);
		
		String ipaddr = "192.168.0.1";
		IDeviceObject devObj = ope.newDevice();
		
		devObj.setHostPort(portObj);
		
		HashMap<String, IDeviceObject> portObjectList = new HashMap<String, IDeviceObject>();
		for(IDeviceObject dev : portObj.getDevices())
		{
			portObjectList.put(dev.getMACAddress(), dev);
		}
		assertTrue(portObjectList.containsValue(devObj));
		
		devObj.removeHostPort(portObj);
		
		HashMap<String, IDeviceObject> portObjectList2 = new HashMap<String, IDeviceObject>();
		for(IDeviceObject dev : portObj.getDevices())
		{
			portObjectList2.put(dev.getMACAddress(), dev);
		}
		assertTrue(!portObjectList2.containsValue(devObj));
	}
	
	/**
	 * Desc:
	 *  Test method for getSwitch method.
	 * Condition:
	 *  N/A
	 * Expect:
	 * 1. Should get the switch connected to the device.
	 */
	@Test
	public void testGetSwitches() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String dpid2 = "00:00:00:00:00:00:0a:08";
		Short number = 1;	
		Short number2 = 2;	
		ISwitchObject swObj = ope.newSwitch(dpid);
		ISwitchObject swObj2 = ope.newSwitch(dpid2);
		IPortObject portObj = ope.newPort(dpid, number);
		IPortObject portObj2 = ope.newPort(dpid2, number2);
		swObj.addPort(portObj);
		swObj2.addPort(portObj2);
		IDeviceObject devObj = ope.newDevice();
		portObj.setDevice(devObj);
		portObj2.setDevice(devObj);
		
		HashMap<String, ISwitchObject> switchObjectList = new HashMap<String, ISwitchObject>();
		for(ISwitchObject sw : devObj.getSwitch())
		{
			switchObjectList.put(sw.getDPID(), sw);
		}
		assertTrue(switchObjectList.containsValue(swObj));
		assertTrue(switchObjectList.containsValue(swObj2));
	}
	
	
}