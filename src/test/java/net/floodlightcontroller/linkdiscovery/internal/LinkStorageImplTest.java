package net.floodlightcontroller.linkdiscovery.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import net.floodlightcontroller.core.INetMapStorage.DM_OPERATION;
import net.floodlightcontroller.core.internal.TestDatabaseManager;
import net.floodlightcontroller.linkdiscovery.ILinkStorage;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.routing.Link;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.protocol.OFPhysicalPort.OFPortState;
import org.openflow.util.HexString;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.transform.PathPipe;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class})
public class LinkStorageImplTest {
	private static ILinkStorage linkStorage;
	private static TitanGraph titanGraph;
	
	//private static IController

	/**
	 * Setup code called before each tests.
	 * Read test graph data and replace DB access by test graph data.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		TestDatabaseManager.deleteTestDatabase();

		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);

		// replace return value of TitanFactory.open() to dummy DB created above
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(titanGraph);
		PowerMock.replay(TitanFactory.class);
		
		linkStorage = new LinkStorageImpl();
		
		// initialize with dummy string
		linkStorage.init("/dummy/path/to/db");
	}
	
	/**
	 * Closing code called after each tests.
	 * Discard test graph data.
	 * @throws Exception
	 */
	@After
	public void tearDown() throws Exception {
		// finish code
		linkStorage.close();
		
		titanGraph.shutdown();
		TestDatabaseManager.deleteTestDatabase();
	}
	
	// TODO: remove @Ignore after UPDATE method is implemented
	@Ignore @Test
	public void testUpdate_UpdateSingleLink() {
		Link linkToUpdate= createExistingLink();
		long currentTime = System.currentTimeMillis();
		LinkInfo infoToUpdate = createFeasibleLinkInfo(currentTime);
		LinkInfo infoToVerify = createFeasibleLinkInfo(currentTime);

		linkStorage.update(linkToUpdate, infoToUpdate, ILinkStorage.DM_OPERATION.UPDATE);
		
		doTestLinkHasStateOf(linkToUpdate, infoToVerify);
	}
	
	@Test
	public void testUpdate_CreateSingleLink() {
		Link linkToCreate = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.update(linkToCreate, ILinkStorage.DM_OPERATION.CREATE);
		doTestLinkIsInGraph(linkToVerify);

		// Add same link
		Link linkToCreateTwice = createFeasibleLink();
		linkStorage.update(linkToCreateTwice, ILinkStorage.DM_OPERATION.CREATE);
		
		// this occurs assertion failure if there are two links in titanGraph
		doTestLinkIsInGraph(linkToVerify);
	}

	@Test
	public void testUpdate_InsertSingleLink(){
		Link linkToInsert = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.update(linkToInsert, ILinkStorage.DM_OPERATION.INSERT);

		doTestLinkIsInGraph(linkToVerify);
		
		// Add same link
		Link linkToInsertTwice = createFeasibleLink();
		linkStorage.update(linkToInsertTwice, ILinkStorage.DM_OPERATION.INSERT);

		// this occurs assertion failure if there are two links in titanGraph
		doTestLinkIsInGraph(linkToVerify);
	}
	
	@Test
	public void testUpdate_DeleteSingleLink(){
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();

		// Test deletion of existing link
		linkStorage.update(linkToDelete, DM_OPERATION.DELETE);
		doTestLinkIsNotInGraph(linkToVerify);
		
		linkToDelete = createFeasibleLink();
		linkToVerify = createFeasibleLink();

		// Test deletion of not-existing link
		linkStorage.update(linkToDelete, DM_OPERATION.DELETE);
		doTestLinkIsNotInGraph(linkToVerify);
	}
		
	@Test
	public void testUpdate_CreateLinks(){
		List<Link> linksToCreate = createFeasibleLinks();
		List<Link> linksToVerify = createFeasibleLinks();

		// Test creation of new links
		linkStorage.update(linksToCreate, ILinkStorage.DM_OPERATION.CREATE);
		for(Link l : linksToVerify) {
			doTestLinkIsInGraph(l);
		}
		
		// Test creation of existing links
		linksToCreate = createFeasibleLinks();
		linkStorage.update(linksToCreate, ILinkStorage.DM_OPERATION.CREATE);
		for(Link l : linksToVerify) {
			doTestLinkIsInGraph(l);
		}
	}
	
	@Test
	public void testUpdate_CreateLinks_Mixuture(){
		List<Link> linksToCreate = new ArrayList<Link>();
		linksToCreate.add(createFeasibleLink());
		linksToCreate.add(createExistingLink());
		
		// Test creation of mixture of new/existing links
		linkStorage.update(linksToCreate, ILinkStorage.DM_OPERATION.CREATE);
		doTestLinkIsInGraph(createFeasibleLink());
		doTestLinkIsInGraph(createExistingLink());
	}

	@Test
	public void testUpdate_InsertLinks(){
		List<Link> linksToInsert = createFeasibleLinks();
		List<Link> linksToVerify = createFeasibleLinks();
		
		// Test insertion of new links
		linkStorage.update(linksToInsert, ILinkStorage.DM_OPERATION.INSERT);
		for(Link l : linksToVerify) {
			doTestLinkIsInGraph(l);
		}
		
		// Test insertion of existing links
		linksToInsert = createFeasibleLinks();
		linkStorage.update(linksToInsert, ILinkStorage.DM_OPERATION.INSERT);
		for(Link l : linksToVerify) {
			doTestLinkIsInGraph(l);
		}
	}
	
	@Test
	public void testUpdate_InsertLinks_Mixuture(){
		List<Link> linksToInsert = new ArrayList<Link>();
		linksToInsert.add(createFeasibleLink());
		linksToInsert.add(createExistingLink());
		
		// Test insertion of mixture of new/existing links
		linkStorage.update(linksToInsert, ILinkStorage.DM_OPERATION.INSERT);
		doTestLinkIsInGraph(createFeasibleLink());
		doTestLinkIsInGraph(createExistingLink());
	}


	@Test
	public void testUpdate_DeleteLinks(){
		List<Link> linksToDelete = createExistingLinks();
		List<Link> linksToVerify = createExistingLinks();
		
		// Test deletion of existing links
		linkStorage.update(linksToDelete, ILinkStorage.DM_OPERATION.DELETE);
		for(Link l : linksToVerify) {
			doTestLinkIsNotInGraph(l);
		}
		
		// Test deletion of not-existing links
		linksToDelete = createExistingLinks();
		linkStorage.update(linksToDelete, ILinkStorage.DM_OPERATION.DELETE);
		for(Link l : linksToVerify) {
			doTestLinkIsNotInGraph(l);
		}
	}
	
	@Test
	public void testUpdate_DeleteLinks_Mixuture(){
		List<Link> linksToDelete = new ArrayList<Link>();
		linksToDelete.add(createFeasibleLink());
		linksToDelete.add(createExistingLink());
		
		// Test deletion of mixture of new/existing links
		linkStorage.update(linksToDelete, ILinkStorage.DM_OPERATION.DELETE);
		doTestLinkIsNotInGraph(createFeasibleLink());
		doTestLinkIsNotInGraph(createExistingLink());
	}
	
	// TODO: remove @Ignore after UPDATE method is implemented
	@Ignore @Test
	public void testAddOrUpdateLink_Update() {
		Link linkToUpdate= createExistingLink();
		long currentTime = System.currentTimeMillis();
		LinkInfo infoToUpdate = createFeasibleLinkInfo(currentTime);
		LinkInfo infoToVerify = createFeasibleLinkInfo(currentTime);

		linkStorage.addOrUpdateLink(linkToUpdate, infoToUpdate, ILinkStorage.DM_OPERATION.UPDATE);
		
		doTestLinkHasStateOf(linkToUpdate, infoToVerify);
	}
	
	@Test
	public void testAddOrUpdateLink_Create() {
		Link linkToCreate = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.addOrUpdateLink(linkToCreate, null, ILinkStorage.DM_OPERATION.CREATE);
		doTestLinkIsInGraph(linkToVerify);

		// Add same link
		Link linkToCreateTwice = createFeasibleLink();
		linkStorage.addOrUpdateLink(linkToCreateTwice, null, ILinkStorage.DM_OPERATION.CREATE);
		
		// this occurs assertion failure if there are two links in titanGraph
		doTestLinkIsInGraph(linkToVerify);
	}
	
	@Test
	public void testAddOrUpdateLink_Insert() {
		Link linkToInsert = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.addOrUpdateLink(linkToInsert, null, ILinkStorage.DM_OPERATION.INSERT);

		doTestLinkIsInGraph(linkToVerify);
		
		// Add same link
		Link linkToInsertTwice = createFeasibleLink();
		linkStorage.addOrUpdateLink(linkToInsertTwice, null, ILinkStorage.DM_OPERATION.INSERT);

		// this occurs assertion failure if there are two links in titanGraph
		doTestLinkIsInGraph(linkToVerify);
	}
	
	// TODO: Check if addOrUpdateLink() should accept DELETE operation. If not, remove this test.
	@Ignore @Test
	public void testAddOrUpdateLink_Delete() {
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();

		// Test deletion of existing link
		linkStorage.addOrUpdateLink(linkToDelete, null, DM_OPERATION.DELETE);
		doTestLinkIsNotInGraph(linkToVerify);
		
		linkToDelete = createFeasibleLink();
		linkToVerify = createFeasibleLink();

		// Test deletion of not-existing link
		linkStorage.addOrUpdateLink(linkToDelete, null, DM_OPERATION.DELETE);
		doTestLinkIsNotInGraph(linkToVerify);
	}
	
	@Test
	public void testGetLinks_ByDpidPort(){
		Link linkToVerify = createExistingLink();
		Long dpid = linkToVerify.getSrc();
		short port = (short)linkToVerify.getSrcPort();
		
		List<Link> list = linkStorage.getLinks(dpid, port);
		
		assertEquals(list.size(), 1);
		
		Link l = list.get(0);
		assertEquals(l.getSrc(), linkToVerify.getSrc());
		assertEquals(l.getSrcPort(), linkToVerify.getSrcPort());
		assertEquals(l.getDst(), linkToVerify.getDst());
		assertEquals(l.getDstPort(), linkToVerify.getDstPort());
		
		Link linkToVerifyNot = createFeasibleLink();
		
		List<Link> list2 = linkStorage.getLinks(linkToVerifyNot.getSrc(), (short)linkToVerifyNot.getSrcPort());
		
		assertEquals(list2.size(), 0);
	}
	
	@Test
	public void testGetLinks_ByString() {
		Link linkToVeryfy = createExistingLink();
		String dpid = HexString.toHexString(linkToVeryfy.getSrc());
		
		List<Link> links = linkStorage.getLinks(dpid);
		assertTrue(links.contains(linkToVeryfy));

		Link linkToVerifyNot = createFeasibleLink();
		assertFalse(links.contains(linkToVerifyNot));
	}
	
	@Test
	public void testDeleteLink() {
		// Deletion of existing link
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();
		
		linkStorage.deleteLink(linkToDelete);
		doTestLinkIsNotInGraph(linkToVerify);
		
		// Deletion of not existing link
		linkToDelete = createFeasibleLink();
		linkToVerify = createFeasibleLink();
		
		linkStorage.deleteLink(linkToDelete);
		doTestLinkIsNotInGraph(linkToVerify);
	}
	
	@Test
	public void testDeleteLinks(){
		List<Link> linksToDelete = createExistingLinks();
		List<Link> linksToVerify = createExistingLinks();
		
		linkStorage.deleteLinks(linksToDelete);
		for(Link l : linksToVerify) {
			doTestLinkIsNotInGraph(l);
		}
	}
	
	@Test
	public void testDeleteLinks_Mixture(){
		List<Link> linksToDelete = new ArrayList<Link>();
		linksToDelete.add(createFeasibleLink());
		linksToDelete.add(createExistingLink());
		
		// Test deletion of mixture of new/existing links
		linkStorage.deleteLinks(linksToDelete);
		doTestLinkIsNotInGraph(createFeasibleLink());
		doTestLinkIsNotInGraph(createExistingLink());
	}

	@Test
	public void testGetActiveLinks() {
		Link existingLink = createExistingLink();
		Link notExistingLink = createFeasibleLink();

		List<Link> links = linkStorage.getActiveLinks();
		
		assertTrue(links.contains(existingLink));
		assertFalse(links.contains(notExistingLink));
	}
	
	@Test
	public void testDeleteLinksOnPort() {
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();
		
		linkStorage.deleteLinksOnPort(linkToDelete.getSrc(), linkToDelete.getSrcPort());
		
		doTestLinkIsNotInGraph(linkToVerify);
	}

	/**
	 * Test if titanGraph has specific link
	 * @param link 
	 */
	private void doTestLinkIsInGraph(Link link) {
		String src_dpid = HexString.toHexString(link.getSrc());
		String dst_dpid = HexString.toHexString(link.getDst());
		short src_port = link.getSrcPort();
		short dst_port = link.getDstPort();
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", src_dpid).iterator();
		
		// Test if just one switch is found in the graph
		assertTrue(it.hasNext());
		Vertex sw = it.next();
		assertFalse(it.hasNext());
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		pipe.start(sw).out("on").has("number", src_port).out("link").has("number", dst_port).in("on").has("dpid", dst_dpid);
		
		// Test if just one link is found in the graph
		assertTrue(pipe.hasNext());
		pipe.next();
		assertFalse(pipe.hasNext());
	}
	
	/**
	 * Test if titanGraph doesn't have specific link
	 * @param link
	 */
	private void doTestLinkIsNotInGraph(Link link) {
		String src_dpid = HexString.toHexString(link.getSrc());
		String dst_dpid = HexString.toHexString(link.getDst());
		short src_port = link.getSrcPort();
		short dst_port = link.getDstPort();
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", src_dpid).iterator();
		
		// Test if just one switch is found in the graph
		assertTrue(it.hasNext());
		Vertex sw = it.next();
		assertFalse(it.hasNext());
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		pipe.start(sw).out("on").has("number", src_port).out("link").has("number", dst_port).in("on").has("dpid", dst_dpid);
		
		// Test if no link is found in the graph
		assertFalse(pipe.hasNext());
	}
	
	/**
	 * Test if titanGraph has specific Link with specific LinkInfo
	 * @param link 
	 */
	private void doTestLinkHasStateOf(Link link, LinkInfo info) {
		String src_dpid = HexString.toHexString(link.getSrc());
		String dst_dpid = HexString.toHexString(link.getDst());
		short src_port = link.getSrcPort();
		short dst_port = link.getDstPort();
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", src_dpid).iterator();
		
		// Test if just one switch is found in the graph
		assertTrue(it.hasNext());
		Vertex sw = it.next();
		assertFalse(it.hasNext());
		
		GremlinPipeline<Vertex, Edge> pipe = new GremlinPipeline<Vertex, Edge>();
		pipe.start(sw);
		pipe.enablePath();
		pipe.out("on").has("number", src_port).out("link").has("number", dst_port).in("on").has("dpid", dst_dpid)
			.path().step(new PipeFunction<PathPipe<Vertex>, Edge>() {
			@Override
			public Edge compute(PathPipe<Vertex> pipepath) {
				List<Vertex> V = pipepath.next();

				Vertex port_src = V.get(1);
				Vertex port_dst = V.get(2);
				
				for(Edge e : port_src.getEdges(Direction.OUT)) {
					if(e.getVertex(Direction.IN).equals(port_dst)) {
						return e;
					}
				}
				
				return null;
			}
		});
		
		// Test if just one link is found in the graph
		assertTrue(pipe.hasNext());
		Edge edge = pipe.next();
		assertTrue(edge != null);
		assertFalse(pipe.hasNext());

		// TODO: implement test code to check if update is correctly done.
		int portStateSrc = edge.getVertex(Direction.OUT).getProperty("port_state");
		int portStateDst = edge.getVertex(Direction.IN).getProperty("port_state");
		
		assertTrue(portStateSrc == info.getSrcPortState());
		assertTrue(portStateDst == info.getDstPortState());

//		long firstSeenTime = edge.getProperty("first_seen_time");
//		long lastLldpReceivedTime = edge.getProperty("last_lldp_received_time");
//		long lastBddpReceivedTime = edge.getProperty("last_bddp_received_time");
		long firstSeenTime = edge.getVertex(Direction.OUT).getProperty("first_seen_time");
		long lastLldpReceivedTime = edge.getVertex(Direction.OUT).getProperty("last_lldp_received_time");
		long lastBddpReceivedTime = edge.getVertex(Direction.OUT).getProperty("last_bddp_received_time");
		assertTrue(firstSeenTime == info.getFirstSeenTime());		
		assertTrue(lastLldpReceivedTime == info.getUnicastValidTime());
		assertTrue(lastBddpReceivedTime == info.getMulticastValidTime());
	}


	//----------------- Creation of test data -----------------------
	/**
	 * Returns new Link object which has information of existing link in titanGraph
	 * @return new Link object
	 */
	private Link createExistingLink() {
		// Link from SEA switch port 2 to CHI switch port 1
		return new Link(Long.decode("0x0000000000000a01"), 2, Long.decode("0x0000000000000a03"), 1);
	}
	
	/**
	 * Returns new Link object which has information of not-existing but feasible link in titanGraph
	 * @return new Link object
	 */
	private Link createFeasibleLink() {
		// Link from SEA switch port 1 to LAX switch port 1
		return new Link(Long.decode("0x0000000000000a01"), 4, Long.decode("0x0000000000000a02"), 1);
	}
	
	// make NO sense while test-network data doesn't define physical network (i.e. any link is feasible)
	@SuppressWarnings("unused")
	private Link createInfeasibleLink() {
		return new Link(Long.decode("0x0000000000000a01"), 1, Long.decode("0x0000000000000a02"), 1);
	}

	/**
	 * Returns list of Link objects which all has information of existing link in titanGraph
	 * @return ArrayList of new Link objects
	 */
	private List<Link> createExistingLinks() {
		List<Link> links = new ArrayList<Link>();
		// Link from SEA switch port 2 to CHI switch port 1
		links.add(new Link(Long.decode("0x0000000000000a01"), 2, Long.decode("0x0000000000000a03"), 1));
		// Link from LAX switch port 1 to SEA switch port 3
		links.add(new Link(Long.decode("0x0000000000000a02"), 1, Long.decode("0x0000000000000a01"), 3));
		return links;
	}
	
	/**
	 * Returns list of Link objects which all has information of not-existing but feasible link
	 * @return ArrayList of new Link objects
	 */
	private List<Link> createFeasibleLinks() {
		List<Link> links = new ArrayList<Link>();
		// Link from CHI switch port 4 to NYC switch port 3
		links.add(new Link(Long.decode("0x0000000000000a03"), 4, Long.decode("0x0000000000000a05"), 3));
		// Link from SEA switch port 4 to LAX switch port 1
		links.add(new Link(Long.decode("0x0000000000000a01"), 4, Long.decode("0x0000000000000a02"), 1));
		return links;
	}
	
	/**
	 * Returns new LinkInfo object with convenient values.
	 * @return LinkInfo object
	 */
	private LinkInfo createFeasibleLinkInfo(long time) {
		long time_first = time;
		long time_last_lldp = time + 50;
		long time_last_bddp = time + 100;
		int state_src = OFPhysicalPort.OFPortState.OFPPS_STP_FORWARD.getValue();
		int state_dst = OFPhysicalPort.OFPortState.OFPPS_STP_LISTEN.getValue();

		return new LinkInfo(time_first,
				time_last_lldp,
				time_last_bddp,
				state_src,
				state_dst);
	}
	//---------------------------------------------------------------
}
