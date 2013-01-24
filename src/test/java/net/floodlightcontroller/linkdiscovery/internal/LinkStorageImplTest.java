package net.floodlightcontroller.linkdiscovery.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.List;

import net.floodlightcontroller.core.INetMapStorage.DM_OPERATION;
import net.floodlightcontroller.core.internal.TestDatabaseManager;
import net.floodlightcontroller.linkdiscovery.ILinkStorage;
import net.floodlightcontroller.routing.Link;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class LinkStorageImplTest {
	private static ILinkStorage linkStorage;
	private static TitanGraph titanGraph;
	
	//TODO Future ideas:
	//Test add links with CREATE and UPDATE
	//Test adding existing link again
	
	@Before
	public void setUp() throws Exception{
		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);
		
		linkStorage = new TestableLinkStorageImpl(titanGraph);
	}
	
	@After
	public void tearDown() throws Exception {		
		titanGraph.shutdown();
	}
	
	/*
	 * Add a link between port 1.102 and 2.104
	 * i.e SEA switch port 3 to LAX switch port 1
	 */
	@Test
	public void testAddSingleLink(){
		Link linkToAdd = new Link(Long.decode("0x0000000000000a01"), 3, Long.decode("0x0000000000000a02"), 1);
		
		//Use the link storage API to add the link
		linkStorage.update(linkToAdd, ILinkStorage.DM_OPERATION.INSERT);
		
		//Test if it was added correctly with the Gremlin API
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		Iterator<Vertex> it = titanGraph.getVertices("dpid", "00:00:00:00:00:00:0a:01").iterator();
		
		assertTrue(it.hasNext());
		Vertex sw1 = it.next();
		assertFalse(it.hasNext());
		
		pipe.start(sw1).out("on").has("number", (short)3).out("link").in("on");
		
		assertTrue(pipe.hasNext());
		Vertex sw2 = pipe.next();
		assertFalse(pipe.hasNext());
		
		//Check we ended up at the right vertex
		assertEquals((String)sw2.getProperty("dpid"), "00:00:00:00:00:00:0a:02");
	}
	
	//TODO enable once method is written
	@Ignore @Test
	public void testGetLinks(){
		//TODO Make sure this works when the implementation is written
		List<Link> list = linkStorage.getLinks(Long.decode("0x0000000000000a01"), (short)2);
		
		assertEquals(list.size(), 1);
		
		Link l = list.get(0);
		assertEquals(l.getSrc(), 2561L);
		assertEquals(l.getSrcPort(), (short)2);
		assertEquals(l.getDst(), 2563L);
		assertEquals(l.getDstPort(), (short)1);
	}
	
	//TODO enable once method is written
	@Ignore @Test
	public void testUpdateDelete(){
		Link linkToDelete = new Link(Long.decode("0x0000000000000a01"), 2, Long.decode("0x0000000000000a03"), 1);
		
		linkStorage.update(linkToDelete, DM_OPERATION.DELETE);
		
		//Test if it was deleted correctly with the Gremlin API
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		Iterator<Vertex> it = titanGraph.getVertices("dpid", "00:00:00:00:00:00:0a:01").iterator();
		
		assertTrue(it.hasNext());
		Vertex sw1 = it.next();
		assertFalse(it.hasNext());
		
		pipe.start(sw1).out("on").has("number", 2).out("link");
		
		assertFalse(pipe.hasNext());
	}
	
	//TODO enable once method is written
	@Ignore @Test
	public void testDeleteLinks(){
		//TODO Make sure this works when the implementation is written
		
		linkStorage.deleteLinksOnPort(Long.decode("0x0000000000000a01"), (short)2);
		
		//Test if it was deleted correctly with the Gremlin API
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		Iterator<Vertex> it = titanGraph.getVertices("dpid", "00:00:00:00:00:00:0a:01").iterator();
		
		assertTrue(it.hasNext());
		Vertex sw1 = it.next();
		assertFalse(it.hasNext());
		
		pipe.start(sw1).out("on").has("number", 2).out("link");
		
		assertFalse(pipe.hasNext());
	}
	
}
