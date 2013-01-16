package net.floodlightcontroller.core.internal;


import java.util.Iterator;

import junit.framework.TestCase;
import net.floodlightcontroller.core.ISwitchStorage;
import net.floodlightcontroller.core.ISwitchStorage.SwitchState;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openflow.protocol.OFPhysicalPort;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class SwitchStorageImplTest extends TestCase {

	private static ISwitchStorage switchStorage;
	private static TitanGraph titanGraph;
	
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		
		TestDatabaseManager.deleteTestDatabase();
		
		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);
		
		switchStorage = new TestableSwitchStorageImpl(titanGraph);
	}

	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		//TODO reenable once test debugging is finished
		TestDatabaseManager.deleteTestDatabase();
	}

	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPort() {
		
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName("port 5 at SEA switch");
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		
		switchStorage.addPort(dpid, portToAdd);
		
		Vertex sw = titanGraph.getVertices("dpid", dpid).iterator().next();
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		pipe.start(sw).out("on").has("number", portNumber);
		
		assertTrue(pipe.hasNext());
		Vertex addedPort = pipe.next();
		assertFalse(pipe.hasNext());
		
		assertEquals(addedPort.getProperty("number"), portNumber);
	}

	@Test
	public void testGetPorts() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPortStringShort() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPortStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddSwitch() {
		String dpid = "00:00:00:00:00:00:0a:07";
		
		switchStorage.addSwitch(dpid);
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", dpid).iterator();
		assertTrue(it.hasNext());
		Vertex addedSwitch = it.next();
		assertFalse(it.hasNext());
		
		assertEquals(addedSwitch.getProperty("type"), "switch");
		assertEquals(addedSwitch.getProperty("dpid"), dpid);
		assertEquals(addedSwitch.getProperty("state"), SwitchState.ACTIVE.toString());
	}

	
	//FIXME something causes this test to fail when run in the suite. Probably something
	//to do with not properly refreshing our DB connection for each test
	@Test
	public void testDeleteSwitch() {
		String dpid = "00:00:00:00:00:00:0a:01";
		
		switchStorage.deleteSwitch(dpid);
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", dpid).iterator();
		assertFalse(it.hasNext());
	}

	//TODO there's an issue with the datatypes for things link port numbers.
	//There should be a standard type in the DB for everything, however there
	//are discrepancies for example when I read data in from a file port numbers
	//are ints but if they're put in by the API they're shorts.
	
	@Test
	public void testDeletePortByPortNum() {
		//FIXME fails because query for the port is wrong in SwitchStorageImpl
		
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNum = 3;
		
		switchStorage.deletePort(dpid, portNum);
		
		Vertex sw = titanGraph.getVertices("dpid", dpid).iterator().next();
		
		Iterator<Vertex> it = sw.getVertices(Direction.OUT, "on").iterator();
		
		while (it.hasNext()){
			System.out.println(it.next());
		}
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		pipe.start(sw).out("on").has("number", (int)portNum);
		assertFalse(pipe.hasNext());
	}

	@Test
	public void testDeletePortStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActiveSwitches() {
		fail("Not yet implemented");
	}

}
