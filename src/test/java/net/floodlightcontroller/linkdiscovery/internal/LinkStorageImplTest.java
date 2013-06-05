package net.floodlightcontroller.linkdiscovery.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import org.openflow.util.HexString;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class})
public class LinkStorageImplTest {
	private static ILinkStorage linkStorage;
	private static TitanGraph titanGraph;
	
	//private static IController

	//TODO Future ideas:
	//Test add links with CREATE and UPDATE
	//Test adding existing link again
	
	@Before
	public void setUp() throws Exception{
		TestDatabaseManager.deleteTestDatabase();

		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);

		// replace TitanFactory.open() return value to dummy DB
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(titanGraph);
		PowerMock.replay(TitanFactory.class);
		
		linkStorage = new LinkStorageImpl();
		
		// initialize with dummy string
		linkStorage.init("/dummy/path/to/db");
	}
	
	@After
	public void tearDown() throws Exception {
		// finish code
		linkStorage.close();
		
		titanGraph.shutdown();
		TestDatabaseManager.deleteTestDatabase();
	}
	
	// TODO: remove @Ignore after UPDATE method will be implemented
	@Ignore @Test
	public void testUpdate_UpdateSingleLink() {
		Link linkToUpdate= createExistingLink();
		LinkInfo infoToUpdate = new LinkInfo(
				System.currentTimeMillis(),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                0, 0);

		linkStorage.update(linkToUpdate, infoToUpdate, ILinkStorage.DM_OPERATION.UPDATE);
		
		// TODO: get LinkInfo from titanGraph and verify
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

	/*
	 * Add a link between port 1.102 and 2.104
	 * i.e SEA switch port 3 to LAX switch port 1
	 */
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
	
	// TODO: remove @Ignore after UPDATE method will be implemented
	@Ignore @Test
	public void testUpdate_UpdateLinks(){
		List<Link> linksToUpdate= createExistingLinks();

		// Who calls this method like this way? 
		linkStorage.update(linksToUpdate, ILinkStorage.DM_OPERATION.UPDATE);

		// TODO: verification of update result
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
	public void testUpdate_CreateLinksMixuture(){
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
	public void testUpdate_InsertLinksMixuture(){
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
	public void testUpdate_DeleteLinksMixuture(){
		List<Link> linksToDelete = new ArrayList<Link>();
		linksToDelete.add(createFeasibleLink());
		linksToDelete.add(createExistingLink());
		
		// Test deletion of mixture of new/existing links
		linkStorage.update(linksToDelete, ILinkStorage.DM_OPERATION.DELETE);
		doTestLinkIsNotInGraph(createFeasibleLink());
		doTestLinkIsNotInGraph(createExistingLink());
	}
	
	@Test
	public void testAddOrUpdateLink() {
		Link linkToDelete = createExistingLink();

	}
	
	@Test
	public void testUpdateInsertMultipleLinks() {
		List<Link> linksToAdd = createFeasibleLinks();
		List<Link> linksToVerify = createFeasibleLinks();
		
		linkStorage.update(linksToAdd, ILinkStorage.DM_OPERATION.INSERT);
		
		for(Link l : linksToVerify) {
			doTestLinkIsInGraph(l);
		}
	}
	
	@Test
	public void testGetLinks(){
		Link linkToVerify = createExistingLink();
		
		List<Link> list = linkStorage.getLinks(linkToVerify.getSrc(), (short)linkToVerify.getSrcPort());
		
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
	public void testGetLinksByDpid() {
		Link linkToVeryfy = createExistingLink();
		
		List<Link> links = linkStorage.getLinks(HexString.toHexString(linkToVeryfy.getSrc()));
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
	 * Test if titanGraph has specific link (no more than one link)
	 * @param link
	 */
	private void doTestLinkIsInGraph(Link link) {
		String src_dpid = HexString.toHexString(link.getSrc());
		String dst_dpid = HexString.toHexString(link.getDst());
		short src_port = link.getSrcPort();
		short dst_port = link.getDstPort();
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		Iterator<Vertex> it = titanGraph.getVertices("dpid", src_dpid).iterator();
		
		assertTrue(it.hasNext());
		Vertex sw = it.next();
		assertFalse(it.hasNext());
		
		pipe.start(sw).out("on").has("number", src_port).out("link").has("number", dst_port).in("on").has("dpid", dst_dpid);
		
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
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		Iterator<Vertex> it = titanGraph.getVertices("dpid", src_dpid).iterator();
		
		assertTrue(it.hasNext());
		Vertex sw = it.next();
		assertFalse(it.hasNext());
		
		pipe.start(sw).out("on").has("number", src_port).out("link").has("number", dst_port).in("on").has("dpid", dst_dpid);
		
		assertFalse(pipe.hasNext());
	}

	//----------------- Creation of test data -----------------------
	private Link createExistingLink() {
		return new Link(Long.decode("0x0000000000000a01"), 2, Long.decode("0x0000000000000a03"), 1);
	}
	
	private Link createFeasibleLink() {
		return new Link(Long.decode("0x0000000000000a01"), 4, Long.decode("0x0000000000000a02"), 1);
	}
	
	// make NO sense while test-network data doesn't define physical network
	@SuppressWarnings("unused")
	private Link createInfeasibleLink() {
		return new Link(Long.decode("0x0000000000000a01"), 1, Long.decode("0x0000000000000a02"), 1);
	}

	private List<Link> createExistingLinks() {
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(Long.decode("0x0000000000000a01"), 2, Long.decode("0x0000000000000a03"), 1));
		links.add(new Link(Long.decode("0x0000000000000a02"), 1, Long.decode("0x0000000000000a01"), 3));
		return links;
	}
	
	private List<Link> createFeasibleLinks() {
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(Long.decode("0x0000000000000a03"), 4, Long.decode("0x0000000000000a05"), 3));
		links.add(new Link(Long.decode("0x0000000000000a01"), 4, Long.decode("0x0000000000000a02"), 1));
		return links;
	}
	//---------------------------------------------------------------
}
