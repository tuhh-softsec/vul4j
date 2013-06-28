package net.onrc.onos.ofcontroller.core.internal;

import static org.easymock.EasyMock.*;

import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;
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
public class SwitchStorageImplTest {

	protected static org.slf4j.Logger log = LoggerFactory.getLogger(SwitchStorageImpl.class);

	String conf;
    private GraphDBConnection mockConn = null;
    private GraphDBOperation mockOpe = null;
    private GraphDBOperation realOpe = null;
    private TitanGraph titanGraph = null;
    ISwitchStorage swSt = null;
    
	@Before
	public void setUp() throws Exception {
		
		swSt = new SwitchStorageImpl();
		conf = "/dummy/path/to/db";
		
        // Make mock cassandra DB
		// Replace TitanFactory.open() to return mock DB

		PowerMock.mockStatic(GraphDBConnection.class);
		mockConn = createMock(GraphDBConnection.class);
		PowerMock.suppress(PowerMock.constructor(GraphDBConnection.class));
		EasyMock.expect(GraphDBConnection.getInstance((String)EasyMock.anyObject())).andReturn(mockConn);
		PowerMock.replay(GraphDBConnection.class);
		
		PowerMock.mockStatic(GraphDBOperation.class);
		mockOpe = PowerMock.createStrictMock(GraphDBOperation.class);
		PowerMock.expectNew(GraphDBOperation.class, mockConn).andReturn(mockOpe);
		PowerMock.replay(GraphDBOperation.class);
        // Replace the conf to dummy conf
		// String conf = "/tmp/cassandra.titan";
		

	}

	@After
	public void tearDown() throws Exception {
		swSt.close();
		swSt = null;
		
	}
	
	/**
	 * Desc:
	 *  Test method for addSwitch method.
	 * Condition:
	 *  Normal
	 * Expect:
	 * 	Call SwitchStorageImpl.addSwitch func with proper properties.
	 */
	@Test
	public void testAddSwitch() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String state = "ACTIVE";
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.addSwitch(dpid);
	}
	
	/**
	 * Desc:
	 *  Test method for addSwitch method.
	 * Condition:
	 *  The switch is already existing.
	 * Expect:
	 * 	Call SwitchStorageImpl.addSwitch func with proper properties.
	 */
	//@Ignore 
	@Test
	public void testAddSwitchExisting() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String state = "ACTIVE";
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		mockISw.setState(state);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.addSwitch(dpid);
	}
	
	/**
	 * Desc:
	 *  Test method for addSwitch method.
	 * Condition:
	 *  The switch construction is fail and return null
	 * Expect:
	 * 	Write the status as info log.
	 */
	@Test
	public void testAddSwitchAbnormal() {
		String dpid = "00:00:00:00:00:00:0a:07";
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(null);
		mockOpe.rollback();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.addSwitch(dpid);
	}
	
	/**
	 * Desc:
	 *  Test method for addSwitch method.
	 * Condition:
	 *  Throw runtimeException. 
	 * Expect:
	 * 	The rollback method is called.
	 */
	//@Ignore 
	@Test
	public void testAddSwitchException() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String state = "ACTIVE";
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expectLastCall().andThrow(new RuntimeException());
		mockOpe.rollback();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.addSwitch(dpid);
	}
	
	/**
	 * Desc:
	 *  Test method for updateSwitch method.
	 * Condition:
	 *  SwitchState : INACTIVE
	 *  DMOPERATION : UPDATE
	 * Expect:
	 * 	Should call addSwitch function and commit.
	 */
	//@Ignore 
	@Test
	public void testUpdateUPDATE() {
		String dpid = "00:00:00:00:00:00:0a:07";
		SwitchState stateINACTIVE = SwitchState.INACTIVE;
		DM_OPERATION opUPDATE = DM_OPERATION.UPDATE;
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState("ACTIVE");
		mockISw.setState(stateINACTIVE.toString());
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);	
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.update(dpid, stateINACTIVE, opUPDATE);
	}
	
	/**
	 * Desc:
	 *  Test method for updateSwitch method.
	 * Condition:
	 *  SwitchState : INACTIVE
	 *  DMOPERATION : CREATE
	 * Expect:
	 * 	Should call addSwitch function and commit.
	 */
	//@Ignore 
	@Test
	public void testUpdateCREATE() {
		String dpid = "00:00:00:00:00:00:0a:07";
		SwitchState stateINACTIVE = SwitchState.INACTIVE;
		DM_OPERATION opCREATE = DM_OPERATION.CREATE;
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState("ACTIVE");
		mockISw.setState(stateINACTIVE.toString());
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);	
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.update(dpid, stateINACTIVE, opCREATE);
	}
	
	/**
	 * Desc:
	 *  Test method for updateSwitch method.
	 * Condition:
	 *  SwitchState : INACTIVE
	 *  DMOPERATION : INSERT
	 * Expect:
	 * 	Should call addSwitch function and commit.
	 */
	//@Ignore 
	@Test
	public void testUpdateINSERT() {
		String dpid = "00:00:00:00:00:00:0a:07";
		SwitchState stateINACTIVE = SwitchState.INACTIVE;
		DM_OPERATION opINSERT = DM_OPERATION.INSERT;
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState("ACTIVE");
		mockISw.setState(stateINACTIVE.toString());
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.update(dpid, stateINACTIVE, opINSERT);
	}
	
	/**
	 * Desc:
	 *  Test method for updateSwitch method.
	 * Condition:
	 *  SwitchState : ACTIVE
	 *  DMOPERATION : DELETE
	 * Expect:
	 * 	Should call removeSwitch function and commit.
	 */
	//@Ignore 
	@Test
	public void testUpdateDELETE() {
		String dpid = "00:00:00:00:00:00:0a:07";
		SwitchState stateACTIVE = SwitchState.ACTIVE;
		DM_OPERATION opDELETE = DM_OPERATION.DELETE;
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(stateACTIVE.toString());
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		mockOpe.removeSwitch(mockISw);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.update(dpid, stateACTIVE, opDELETE);
	}
	
	/**
	 * Desc:
	 *  Test method for deleteSwitch method.
	 * Condition:
	 *  The switch is existing.
	 * Expect:
	 * 	Should call removeSwitch function and commit.
	 */
	//@Ignore
	@Test
	public void testDeleteSwitch() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String state = "ACTIVE";
	
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
    	mockOpe.removeSwitch(mockISw);
    	mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.deleteSwitch(dpid);
		
		//Iterator<Vertex> it = titanGraph.getVertices("dpid", dpid).iterator();
		//assertFalse(it.hasNext());
	}
	
	/**
	 * Desc:
	 *  Test method for deleteSwitch method.
	 * Condition:
	 *  The commit func throw exception.
	 * Expect:
	 * 	Should call rollback.
	 */
	//@Ignore
	@Test
	public void testDeleteSwitchException() {
		String dpid = "00:00:00:00:00:00:0a:07";
		String state = "ACTIVE";
		String type = "";
		
		//Mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
    	mockOpe.removeSwitch(mockISw);
    	mockOpe.commit();
		expectLastCall().andThrow(new RuntimeException());
		mockOpe.rollback();
		mockOpe.close();
		replay(mockOpe);
		
		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.deleteSwitch(dpid);
	}
	
	/**
	 * Desc:
	 *  Test method for addPort method.
	 * Condition:
	 *  port is existing.
	 * Expect:
	 * 	Should call addPort and commit.
	 */
	//@Ignore
	@Test
	public void testAddPort() {
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		String state = "ACTIVE";
		String name = "port 5 at SEA switch";
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName(name);
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		portToAdd.setState(OFPortState.OFPPS_STP_FORWARD.getValue());
		
		//Expectation of  mock Port
		IPortObject mockIPort = createMock(IPortObject.class);
		mockIPort.setState(state);
		mockIPort.setPortState(OFPortState.OFPPS_STP_FORWARD.getValue());
		mockIPort.setDesc(name);
		replay(mockIPort);
		
		//Expectation of mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		mockISw.addPort(mockIPort);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(null);
		expect(mockOpe.newPort(dpid, portNumber)).andReturn(mockIPort);	
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);

		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.addPort(dpid, portToAdd);
	}
	
	/**
	 * Desc:
	 *  Test method for addPort method.
	 * Condition:
	 *  Port status is down.
	 * Expect:
	 * 	Should call removePort and commit.
	 */
	//@Ignore
	@Test
	public void testAddPortWithPortLinkDown() {
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		String state = "ACTIVE";
		String name = "port 5 at SEA switch";
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName(name);
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		portToAdd.setState(OFPortState.OFPPS_LINK_DOWN.getValue());
		
		//Expectation of  mock Port
		IPortObject mockIPort = createMock(IPortObject.class);
		mockIPort.setState(state);
		mockIPort.setPortState(OFPortState.OFPPS_STP_FORWARD.getValue());
		mockIPort.setDesc(name);
		replay(mockIPort);
		
		//Expectation of mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		mockISw.removePort(mockIPort);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);	
		mockOpe.commit();	
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(mockIPort);
		mockOpe.removePort(mockIPort);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);

		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.addPort(dpid, portToAdd);
	}
	
	/**
	 * Desc:
	 *  Test method for addPort method.
	 * Condition:
	 *  The switch is not existing.
	 * Expect:
	 * 	Nothing happens.
	 */
	//@Ignore
	@Test
	public void testAddPortAbnormalNoSwitch() {
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		String state = "ACTIVE";
		String name = "port 5 at SEA switch";
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName(name);
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		portToAdd.setState(OFPortState.OFPPS_STP_FORWARD.getValue());
		
		//Expectation of  mock Port
		IPortObject mockIPort = createStrictMock(IPortObject.class);
		replay(mockIPort);
		
		//Expectation of mock Switch
		ISwitchObject mockISw = createStrictMock(ISwitchObject.class);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		mockOpe.close();
		replay(mockOpe);

		swSt.init(conf);
		swSt.addPort(dpid, portToAdd);
	}
	
	/**
	 * Desc:
	 *  Test method for addPort method.
	 * Condition:
	 *  port is not existing.
	 * Expect:
	 * 	Should call addPort and commit.
	 */
	//@Ignore
	@Test
	public void testAddPortAbnormalNoPort() {
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		String state = "ACTIVE";
		String name = "port 5 at SEA switch";
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName(name);
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		portToAdd.setState(OFPortState.OFPPS_STP_FORWARD.getValue());
		
		//Expectation of  mock Port
		IPortObject mockIPort = createMock(IPortObject.class);
		mockIPort.setState(state);
		mockIPort.setPortState(OFPortState.OFPPS_STP_FORWARD.getValue());
		mockIPort.setDesc(name);
		replay(mockIPort);
		
		//Expectation of mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		mockISw.addPort(mockIPort);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(null);
		expect(mockOpe.newPort(dpid, portNumber)).andReturn(null);	
		mockOpe.rollback();
		mockOpe.close();
		replay(mockOpe);

		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.addPort(dpid, portToAdd);
	}
	
	/**
	 * Desc:
	 *  Test method for addPort method.
	 * Condition:
	 *  commit throw the exception.
	 * Expect:
	 * 	Should call rollback.
	 */
	//@Ignore
	@Test
	public void testAddPortWithException() {
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		String state = "ACTIVE";
		String name = "port 5 at SEA switch";
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName(name);
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		portToAdd.setState(OFPortState.OFPPS_STP_FORWARD.getValue());
		
		//Expectation of  mock Port
		IPortObject mockIPort = createMock(IPortObject.class);
		mockIPort.setState(state);
		mockIPort.setPortState(OFPortState.OFPPS_STP_FORWARD.getValue());
		mockIPort.setDesc(name);
		replay(mockIPort);
		
		//Expectation of mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		mockISw.addPort(mockIPort);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(null);
		expect(mockOpe.newPort(dpid, portNumber)).andReturn(mockIPort);	
		mockOpe.commit();
		expectLastCall().andThrow(new RuntimeException());
		mockOpe.rollback();
		mockOpe.close();
		replay(mockOpe);

		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.addPort(dpid, portToAdd);
	}

	/**
	 * Desc:
	 *  Test method for deletePort method.
	 * Condition:
	 *  port is existing.
	 * Expect:
	 * 	Should call removePort and commit.
	 */
	//@Ignore
	@Test
	public void testDeletePort() {
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		String state = "ACTIVE";
		String name = "port 5 at SEA switch";
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName(name);
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		portToAdd.setState(OFPortState.OFPPS_STP_FORWARD.getValue());
		
		//Expectation of  mock Port
		IPortObject mockIPort = createMock(IPortObject.class);
		mockIPort.setState(state);
		mockIPort.setPortState(OFPortState.OFPPS_STP_FORWARD.getValue());
		mockIPort.setDesc(name);
		replay(mockIPort);
		
		//Expectation of mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		mockISw.addPort(mockIPort);
		mockISw.removePort(mockIPort);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(null);
		expect(mockOpe.newPort(dpid, portNumber)).andReturn(mockIPort);	
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(mockIPort);
		mockOpe.removePort(mockIPort);
		mockOpe.commit();
		mockOpe.close();
		replay(mockOpe);

		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.addPort(dpid, portToAdd);
		swSt.deletePort(dpid, portNumber);
	}

	/**
	 * Desc:
	 *  Test method for addPort method.
	 * Condition:
	 *  commit throws the exception.
	 * Expect:
	 * 	Should call rollback.
	 */
	//@Ignore
	@Test
	public void testDeletePortException() {
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		String state = "ACTIVE";
		String name = "port 5 at SEA switch";
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName(name);
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		portToAdd.setState(OFPortState.OFPPS_STP_FORWARD.getValue());
		
		//Expectation of  mock Port
		IPortObject mockIPort = createMock(IPortObject.class);
		mockIPort.setState(state);
		mockIPort.setPortState(OFPortState.OFPPS_STP_FORWARD.getValue());
		mockIPort.setDesc(name);
		replay(mockIPort);
		
		//Expectation of mock Switch
		ISwitchObject mockISw = createMock(ISwitchObject.class);
		mockISw.setState(state);
		mockISw.addPort(mockIPort);
		mockISw.removePort(mockIPort);
		replay(mockISw);
		
		//Expectation of mock operation.
		expect(mockOpe.searchSwitch(dpid)).andReturn(null);
		expect(mockOpe.newSwitch(dpid)).andReturn(mockISw);
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(null);
		expect(mockOpe.newPort(dpid, portNumber)).andReturn(mockIPort);	
		mockOpe.commit();
		expect(mockOpe.searchSwitch(dpid)).andReturn(mockISw);
		expect(mockOpe.searchPort(dpid, portNumber)).andReturn(mockIPort);
		mockOpe.removePort(mockIPort);
		expectLastCall().andThrow(new RuntimeException());
		mockOpe.rollback();
		mockOpe.close();
		replay(mockOpe);
	
		swSt.init(conf);
		swSt.addSwitch(dpid);
		swSt.addPort(dpid, portToAdd);
		swSt.deletePort(dpid, portNumber);
	}
}