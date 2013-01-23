package net.floodlightcontroller.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import net.floodlightcontroller.core.ISwitchStorage;
import net.floodlightcontroller.core.ISwitchStorage.SwitchState;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openflow.protocol.OFPhysicalPort;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class SwitchStorageImplTest {

	private ISwitchStorage switchStorage;
	private TitanGraph titanGraph;
	
	@Before
	public void setUp() throws Exception {
		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);
		
		switchStorage = new TestableSwitchStorageImpl(titanGraph);
	}

	@After
	public void tearDown() throws Exception {
		titanGraph.shutdown();
	}

	@Ignore @Test
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

	@Ignore @Test
	public void testGetPorts() {
		fail("Not yet implemented");
	}

	@Ignore @Test
	public void testGetPortStringShort() {
		fail("Not yet implemented");
	}

	@Ignore @Test
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

	
	@Test
	public void testDeleteSwitch() {
		String dpid = "00:00:00:00:00:00:0a:01";
		
		switchStorage.deleteSwitch(dpid);
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", dpid).iterator();
		assertFalse(it.hasNext());
	}

	@Test
	public void testDeletePortByPortNum() {
		//FIXME fails because query for the port is wrong in SwitchStorageImpl
		
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNum = 3;
		
		switchStorage.deletePort(dpid, portNum);
		
		Vertex sw = titanGraph.getVertices("dpid", dpid).iterator().next();
		
		/*
		Iterator<Vertex> it = sw.getVertices(Direction.OUT, "on").iterator();
		
		while (it.hasNext()){
			System.out.println(it.next());
		}
		*/
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		pipe.start(sw).out("on").has("number", portNum);
		assertFalse(pipe.hasNext());
	}

	@Ignore @Test
	public void testDeletePortStringString() {
		fail("Not yet implemented");
	}

	@Ignore @Test
	public void testGetActiveSwitches() {
		fail("Not yet implemented");
	}
}
