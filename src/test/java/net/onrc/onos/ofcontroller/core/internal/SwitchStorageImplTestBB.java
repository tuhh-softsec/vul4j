package net.onrc.onos.ofcontroller.core.internal;

import static org.junit.Assert.*;

import net.floodlightcontroller.core.internal.TestDatabaseManager;
import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;
import net.onrc.onos.ofcontroller.core.INetMapStorage;
import net.onrc.onos.ofcontroller.core.INetMapStorage.DM_OPERATION;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPhysicalPort.OFPortState;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;

//Add Powermock preparation
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, SwitchStorageImpl.class})
public class SwitchStorageImplTestBB {

	protected static org.slf4j.Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	String conf;
    private GraphDBConnection conn = null;
    private GraphDBOperation ope = null;
    private TitanGraph titanGraph = null;
    ISwitchStorage swSt = null;
    
	@Before
	public void setUp() throws Exception {
		
		swSt = new SwitchStorageImpl();
		conf = "/dummy/path/to/db";
		
		// Make mock cassandra DB
		// Replace TitanFactory.open() to return mock DB
		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(titanGraph);
		PowerMock.replay(TitanFactory.class);
		
		conn = GraphDBConnection.getInstance(conf);
		ope = new GraphDBOperation(conn);
		
		swSt.init(conf);
	}

	@After
	public void tearDown() throws Exception {
		
		titanGraph.shutdown();
		TestDatabaseManager.deleteTestDatabase();

		swSt.close();
		swSt = null;
	}
	
	/**
	 * Desc:
	 *  Test method for addSwitch method.
	 * Condition:
	 *  Normal
	 * Expect:
	 * 1. Switch should be generated.
	 * 2. The status of switch should be ACTIVE
	 */
	//@Ignore 
	@Test
	public void testAddSwitch() {
		String dpid = "00:00:00:00:00:00:0a:07";
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw == null);
		swSt.addSwitch(dpid);
		ISwitchObject sw2 = ope.searchSwitch(dpid);
		assertTrue(sw2 != null);
		assertEquals(sw2.getState(), "ACTIVE");
	}
	
	/**
	 * Desc:
	 *  Test method for addSwitch method.
	 * Condition:
	 *  The existing switch status is INACTIVE.
	 *  The switch is already existing.
	 * Expect:
	 * 1. After add the same switch, the status of switch should be ACTIVE
	 */
	//@Ignore 
	@Test
	public void testAddSwitchExisting() {
		String dpid = "00:00:00:00:00:00:0a:06";
		
		swSt.update(dpid, SwitchState.INACTIVE, DM_OPERATION.UPDATE);
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw != null);
		assertEquals(sw.getState(), SwitchState.INACTIVE.toString());
		swSt.addSwitch(dpid);
		ISwitchObject sw2 = ope.searchSwitch(dpid);
		assertTrue(sw2 != null);
		assertEquals(sw2.getState(), SwitchState.ACTIVE.toString());
	}
	
	/**
	 * Desc:
	 *  Test method for testUpdate method.
	 * Condition:
	 *  The switch is not existing.
	 *  The status of added switch is INACTIVE.
	 *  DM_OPERATION is CREATE.
	 * Expect:
	 * 1. Switch should be created.
	 * 2. The status of switch should be INACTIVE.
	 */
	//@Ignore 
	@Test
	public void testUpdate() {
		String dpid = "00:00:00:00:00:00:0a:07";
		SwitchState state = ISwitchStorage.SwitchState.INACTIVE;
		DM_OPERATION dmope = INetMapStorage.DM_OPERATION.CREATE;
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw == null);
		swSt.update(dpid, state, dmope);
		ISwitchObject sw2 = ope.searchSwitch(dpid);
		assertTrue(sw2 != null);
		assertEquals(sw2.getState(), state.toString());
	}
	
	/**
	 * Desc:
	 *  Test method for testUpdate method.
	 * Condition:
	 *  The switch is existing.
	 *  The status of added switch is ACTIVE.
	 *  DM_OPERATION is DELETE.
	 * Expect:
	 * 1. Switch should be deleted.
	 */
	//@Ignore 
	@Test
	public void testUpdateWithDELETE() {
		String dpid = "00:00:00:00:00:00:0a:06";
		SwitchState state = ISwitchStorage.SwitchState.ACTIVE;
		DM_OPERATION dmope = INetMapStorage.DM_OPERATION.DELETE;
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw != null);
		swSt.update(dpid, state, dmope);
		ISwitchObject sw2 = ope.searchSwitch(dpid);
		assertTrue(sw2 == null);
	}
	
	/**
	 * Desc:
	 *  Test method for delete switch method.
	 * Condition:
	 *  The switch is existing.
	 * Expect:
	 * 1. Switch should be deleted.
	 */
	//@Ignore 
	@Test
	public void testDeleteSwitch() {
		String dpid = "00:00:00:00:00:00:0a:06";
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw != null);
		swSt.deleteSwitch(dpid);
		ISwitchObject sw2 = ope.searchSwitch(dpid);
		assertTrue(sw2 == null);
	}
	
	/**
	 * Desc:
	 *  Test method for delete switch method.
	 * Condition:
	 *  The switch is not existing.
	 * Expect:
	 * Nothing happens.
	 */
	//@Ignore 
	@Test
	public void testDeleteNonExistingSwitch() {
		String dpid = "00:00:00:00:00:00:0a:07";
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw == null);
		swSt.deleteSwitch(dpid);
		ISwitchObject sw2 = ope.searchSwitch(dpid);
		assertTrue(sw2 == null);
	}
	
	/**
	 * Desc:
	 *  Test method for delete port method.
	 * Condition:
	 *  The port is existing.
	 * Expect:
	 *  Deleted the port.
	 */
	//@Ignore 
	@Test
	public void testDeletePort() {
		String dpid = "00:00:00:00:00:00:0a:06";
		short portNumber = 3;
		
		IPortObject portObj1 = ope.searchPort(dpid, portNumber);
		assertTrue(portObj1 != null);
		swSt.deletePort(dpid, portNumber);
		IPortObject portObj2 = ope.searchPort(dpid, portNumber);
		assertTrue(portObj2 == null);
	}
	
	/**
	 * Desc:
	 *  Test method for delete port method.
	 * Condition:
	 *  The port is not existing.
	 * Expect:
	 *  Nothing happens.
	 */
	//@Ignore 
	@Test
	public void testDeleteNonExistingPort() {
		String dpid = "00:00:00:00:00:00:0a:06";
		short portNumber = 4;
		
		IPortObject portObj1 = ope.searchPort(dpid, portNumber);
		assertTrue(portObj1 == null);
		swSt.deletePort(dpid, portNumber);
		IPortObject portObj2 = ope.searchPort(dpid, portNumber);
		assertTrue(portObj2 == null);
	}
	
	/**
	 * Desc:
	 *  Test method for add port method.
	 * Condition:
	 *  The port is not existing.
	 * Expect:
	 *  The port should be added.
	 *  The desc of IPortObject is the same as the name of OFPhysicalPort.
	 */
	//@Ignore 
	@Test
	public void testAddPort() {
		String dpid = "00:00:00:00:00:00:0a:06";
		short portNumber = 4;
		String name = "port 4 at ATL Switch";
		int state = OFPortState.OFPPS_STP_FORWARD.getValue();
		OFPhysicalPort port = new OFPhysicalPort(); 
		port.setPortNumber(portNumber);
		port.setName(name);
		port.setState(state);
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw != null);
		swSt.addPort(dpid, port);
		IPortObject portObj = ope.searchPort(dpid, portNumber);
		assertTrue(portObj != null);
		assertEquals(portObj.getDesc(), name);
	}
	
	/**
	 * Desc:
	 *  Test method for add method.
	 * Condition:
	 *  The port is existing.
	 * Expect:
	 *  Nothing happens.
	 */
	//@Ignore 
	@Test
	public void testAddExistingPort() {
		String dpid = "00:00:00:00:00:00:0a:06";
		short portNumber = 3;
		String name = "xxx";
		int state = OFPortState.OFPPS_STP_FORWARD.getValue();
		OFPhysicalPort port = new OFPhysicalPort(); 
		port.setPortNumber(portNumber);
		port.setName(name);
		port.setState(state);
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw != null);
		swSt.addPort(dpid, port);
		IPortObject portObj = ope.searchPort(dpid, portNumber);
		assertTrue(portObj != null);
	}
	
	/**
	 * Desc:
	 *  Test method for add method.
	 * Condition:
	 *  The port status is down.
	 * Expect:
	 *  Delete the port.
	 */
	//@Ignore 
	@Test
	public void testAddDownPort() {
		String dpid = "00:00:00:00:00:00:0a:06";
		short portNumber = 3;
		String name = "port 3 at ATL Switch";
		int state = OFPortState.OFPPS_LINK_DOWN.getValue();
		OFPhysicalPort port = new OFPhysicalPort(); 
		port.setPortNumber(portNumber);
		port.setName(name);
		port.setState(state);
		
		ISwitchObject sw = ope.searchSwitch(dpid);
		assertTrue(sw != null);
		swSt.addPort(dpid, port);
		IPortObject portObj = ope.searchPort(dpid, portNumber);
		assertTrue(portObj == null);
	}
}
