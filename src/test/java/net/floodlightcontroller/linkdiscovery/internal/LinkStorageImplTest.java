package net.floodlightcontroller.linkdiscovery.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.linkdiscovery.internal.TestGraphDBOperation.TestPortObject;
import net.floodlightcontroller.routing.Link;
import net.onrc.onos.ofcontroller.core.INetMapStorage.DM_OPERATION;
import net.onrc.onos.ofcontroller.linkdiscovery.ILinkStorage;
import net.onrc.onos.util.GraphDBConnection;
import net.onrc.onos.util.GraphDBOperation;

import org.easymock.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflow.protocol.OFPhysicalPort;
import org.openflow.util.HexString;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest({LinkStorageImpl.class, GraphDBConnection.class, GraphDBOperation.class})
public class LinkStorageImplTest {
	protected static Logger log = LoggerFactory.getLogger(LinkStorageImplTest.class);

	private static ILinkStorage linkStorage;
	
	// Mock GraphDBConnection (do nothing)
	private static GraphDBConnection conn;
	
	// Mock GraphDBOperation (mocks port-related methods only)
	private static TestGraphDBOperation ope;

	/**
	 * Setup code called before each tests.
	 * Read test graph data and replace DB access by test graph data.
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception{
		PowerMock.mockStatic(GraphDBConnection.class);
		PowerMock.suppress(PowerMock.constructor(GraphDBConnection.class));
		conn = PowerMock.createNiceMock(GraphDBConnection.class);
		EasyMock.expect(GraphDBConnection.getInstance((String)EasyMock.anyObject())).andReturn(conn).anyTimes();
		PowerMock.replay(GraphDBConnection.class);
		
		ope = new TestGraphDBOperation();
		PowerMock.expectNew(GraphDBOperation.class, (GraphDBConnection)EasyMock.anyObject()).andReturn(ope).anyTimes();
		PowerMock.replay(GraphDBOperation.class);

		linkStorage = new LinkStorageImpl();
		linkStorage.init("/dummy/path/to/conf");
		
		initLinks();
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
		ope.close();
	}
	
	// TODO: remove @Ignore after UPDATE method is implemented
	/**
	 * Test if update() can correctly updates LinkInfo for a Link.
	 */
	@Ignore @Test
	public void testUpdate_UpdateSingleLink() {
		Link linkToUpdate= createExistingLink();
		long currentTime = System.currentTimeMillis();
		LinkInfo infoToUpdate = createFeasibleLinkInfo(currentTime);
		LinkInfo infoToVerify = createFeasibleLinkInfo(currentTime);

		linkStorage.update(linkToUpdate, infoToUpdate, ILinkStorage.DM_OPERATION.UPDATE);
		
		doTestLinkHasStateOf(linkToUpdate, infoToVerify);
	}
	
	/**
	 * Test if update() can correctly creates a Link.
	 */
	@Test
	public void testUpdate_CreateSingleLink() {
		Link linkToCreate = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.update(linkToCreate, ILinkStorage.DM_OPERATION.CREATE);
		doTestLinkExist(linkToVerify);
	}

	/**
	 * Test if update() can correctly inserts a Link.
	 */
	@Test
	public void testUpdate_InsertSingleLink(){
		Link linkToInsert = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.update(linkToInsert, ILinkStorage.DM_OPERATION.INSERT);
		doTestLinkExist(linkToVerify);
	}
	
	/**
	 * Test if update() can correctly deletes a Link.
	 */
	@Test
	public void testUpdate_DeleteSingleLink(){
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();

		// Test deletion of existing link
		linkStorage.update(linkToDelete, DM_OPERATION.DELETE);
		doTestLinkNotExist(linkToVerify);
	}

	/**
	 * Test if update() can correctly creates multiple Links.
	 */
	@Test
	public void testUpdate_CreateLinks(){
		List<Link> linksToCreate = createFeasibleLinks();
		List<Link> linksToVerify = createFeasibleLinks();

		// Test creation of new links
		linkStorage.update(linksToCreate, ILinkStorage.DM_OPERATION.CREATE);
		for(Link l : linksToVerify) {
			doTestLinkExist(l);
		}
	}
	
	/**
	 * Test if update() can handle mixture of normal/abnormal input for creation of Links.
	 * Deprecated: DBOperation is responsible.
	 */
	@Ignore @Test
	public void testUpdate_CreateLinks_Mixuture(){
		List<Link> linksToCreate = new ArrayList<Link>();
		linksToCreate.add(createFeasibleLink());
		linksToCreate.add(createExistingLink());
		
		// Test creation of mixture of new/existing links
		linkStorage.update(linksToCreate, ILinkStorage.DM_OPERATION.CREATE);
		doTestLinkExist(createFeasibleLink());
		doTestLinkExist(createExistingLink());
	}

	/**
	 * Test if update() can correctly inserts multiple Links.
	 */
	@Test
	public void testUpdate_InsertLinks(){
		List<Link> linksToInsert = createFeasibleLinks();
		List<Link> linksToVerify = createFeasibleLinks();
		
		// Test insertion of new links
		linkStorage.update(linksToInsert, ILinkStorage.DM_OPERATION.INSERT);
		for(Link l : linksToVerify) {
			doTestLinkExist(l);
		}
	}
	
	/**
	 * Test if update() can handle mixture of normal/abnormal input for creation of Links.
	 */
	@Ignore @Test
	public void testUpdate_InsertLinks_Mixuture(){
		List<Link> linksToInsert = new ArrayList<Link>();
		linksToInsert.add(createFeasibleLink());
		linksToInsert.add(createExistingLink());
		
		// Test insertion of mixture of new/existing links
		linkStorage.update(linksToInsert, ILinkStorage.DM_OPERATION.INSERT);
		doTestLinkExist(createFeasibleLink());
		doTestLinkExist(createExistingLink());
	}

	/**
	 * Test if update() can correctly deletes multiple Links.
	 */
	@Test
	public void testUpdate_DeleteLinks(){
		List<Link> linksToDelete = createExistingLinks();
		List<Link> linksToVerify = createExistingLinks();
		
		// Test deletion of existing links
		linkStorage.update(linksToDelete, ILinkStorage.DM_OPERATION.DELETE);
		for(Link l : linksToVerify) {
			doTestLinkNotExist(l);
		}
	}
	
	/**
	 * Test if update() can handle mixture of normal/abnormal input for deletion of Links.
	 */
	@Ignore @Test
	public void testUpdate_DeleteLinks_Mixuture(){
		List<Link> linksToDelete = new ArrayList<Link>();
		linksToDelete.add(createFeasibleLink());
		linksToDelete.add(createExistingLink());
		
		// Test deletion of mixture of new/existing links
		linkStorage.update(linksToDelete, ILinkStorage.DM_OPERATION.DELETE);
		doTestLinkNotExist(createFeasibleLink());
		doTestLinkNotExist(createExistingLink());
	}
	
	// TODO: remove @Ignore after UPDATE method is implemented
	/**
	 * Test if updateLink() can correctly updates LinkInfo for a Link.
	 */
	@Ignore @Test
	public void testUpdateLink_Update() {
		Link linkToUpdate= createExistingLink();
		long currentTime = System.currentTimeMillis();
		LinkInfo infoToUpdate = createFeasibleLinkInfo(currentTime);
		LinkInfo infoToVerify = createFeasibleLinkInfo(currentTime);

		linkStorage.updateLink(linkToUpdate, infoToUpdate, ILinkStorage.DM_OPERATION.UPDATE);
		
		doTestLinkHasStateOf(linkToUpdate, infoToVerify);
	}
	
	/**
	 * Test if updateLink() can correctly creates a Link.
	 */
	@Test
	public void testUpdateLink_Create() {
		Link linkToCreate = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.updateLink(linkToCreate, null, ILinkStorage.DM_OPERATION.CREATE);
		doTestLinkExist(linkToVerify);
	}
	
	/**
	 * Test if updateLink() can correctly inserts a Link.
	 */
	@Test
	public void testUpdateLink_Insert() {
		Link linkToInsert = createFeasibleLink();
		Link linkToVerify = createFeasibleLink();
		
		//Use the link storage API to add the link
		linkStorage.updateLink(linkToInsert, null, ILinkStorage.DM_OPERATION.INSERT);

		doTestLinkExist(linkToVerify);
	}
	
	// TODO: Check if addOrUpdateLink() should accept DELETE operation. If not, remove this test.
	/**
	 * Test if updateLink() can correctly deletes a Link.
	 */
	@Ignore @Test
	public void testUpdateLink_Delete() {
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();

		// Test deletion of existing link
		linkStorage.updateLink(linkToDelete, null, DM_OPERATION.DELETE);
		doTestLinkNotExist(linkToVerify);
		
		linkToDelete = createFeasibleLink();
		linkToVerify = createFeasibleLink();

		// Test deletion of not-existing link
		linkStorage.updateLink(linkToDelete, null, DM_OPERATION.DELETE);
		doTestLinkNotExist(linkToVerify);
	}
	
	/**
	 * Test if getLinks() can correctly return Links connected to specific DPID and port.
	 */
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
	
	/**
	 * Test if getLinks() can correctly return Links connected to specific MAC address.
	 */
	@Test
	public void testGetLinks_ByString() {
		Link linkToVeryfy = createExistingLink();
		String dpid = HexString.toHexString(linkToVeryfy.getSrc());
		
		List<Link> links = linkStorage.getLinks(dpid);
		assertTrue(links.contains(linkToVeryfy));

		Link linkToVerifyNot = createFeasibleLink();
		assertFalse(links.contains(linkToVerifyNot));
	}
	
	/**
	 * Test if deleteLink() can correctly delete a Link.
	 */
	@Test
	public void testDeleteLink() {
		// Deletion of existing link
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();
		
		linkStorage.deleteLink(linkToDelete);
		doTestLinkNotExist(linkToVerify);
	}
	
	/**
	 * Test if deleteLinks() can correctly delete Links.
	 */
	@Test
	public void testDeleteLinks(){
		List<Link> linksToDelete = createExistingLinks();
		List<Link> linksToVerify = createExistingLinks();
		
		linkStorage.deleteLinks(linksToDelete);
		for(Link l : linksToVerify) {
			doTestLinkNotExist(l);
		}
	}
	
	/**
	 * Test if deleteLinks() can handle mixture of normal/abnormal input.
	 */
	@Ignore @Test
	public void testDeleteLinks_Mixture(){
		List<Link> linksToDelete = new ArrayList<Link>();
		linksToDelete.add(createFeasibleLink());
		linksToDelete.add(createExistingLink());
		
		// Test deletion of mixture of new/existing links
		linkStorage.deleteLinks(linksToDelete);
		doTestLinkNotExist(createFeasibleLink());
		doTestLinkNotExist(createExistingLink());
	}

	/**
	 * Test if getActiveLinks() can correctly return active Links.
	 */
	@Test
	public void testGetActiveLinks() {
		Link existingLink = createExistingLink();
		Link notExistingLink = createFeasibleLink();

		List<Link> links = linkStorage.getActiveLinks();
		
		assertTrue(links.contains(existingLink));
		assertFalse(links.contains(notExistingLink));
	}
	
	/**
	 * Test if deleteLinksOnPort() can delete Links.
	 */
	@Test
	public void testDeleteLinksOnPort() {
		Link linkToDelete = createExistingLink();
		Link linkToVerify = createExistingLink();
		
		linkStorage.deleteLinksOnPort(linkToDelete.getSrc(), linkToDelete.getSrcPort());
		
		doTestLinkNotExist(linkToVerify);
	}

	/**
	 * Test if specific link is existing
	 * @param link 
	 */
	private void doTestLinkExist(Link link) {
		assertTrue(ope.hasLinkBetween(HexString.toHexString(link.getSrc()),
				link.getSrcPort(),
				HexString.toHexString(link.getDst()),
				link.getDstPort()));
	}
	
	/**
	 * Test if titanGraph doesn't have specific link
	 * @param link
	 */
	private void doTestLinkNotExist(Link link) {
		assertFalse(ope.hasLinkBetween(HexString.toHexString(link.getSrc()),
				link.getSrcPort(),
				HexString.toHexString(link.getDst()),
				link.getDstPort()));
	}
	
	/**
	 * Test if titanGraph has specific Link with specific LinkInfo
	 * @param link 
	 */
	private void doTestLinkHasStateOf(Link link, LinkInfo info) {
	}
	
	//----------------- Creation of test data -----------------------
	// Assume a network shown below.
	//
	// [dpid1]--+--[port:1]----[port:1]--+--[dpid2]
	//          |                        |
	//          +--[port:2]    [port:2]--+
	//          |
	//          +--[port:3]    [port:1]--+--[dpid3]
	//          |                        |
	//          +--[port:4]----[port:2]--+
	//
	// dpid1 : 00:00:00:00:0a:01
	// dpid2 : 00:00:00:00:0a:02
	// dpid3 : 00:00:00:00:0a:03
	
	private void initLinks() {
		final String dpid1 = "00:00:00:00:0a:01";
		final String dpid2 = "00:00:00:00:0a:02";
		final String dpid3 = "00:00:00:00:0a:03";
		
		ope.createNewSwitchForTest(dpid1);
		ope.createNewSwitchForTest(dpid2);
		ope.createNewSwitchForTest(dpid3);

		TestPortObject ports1 [] = {
				ope.createNewPortForTest(dpid1, (short)1),
				ope.createNewPortForTest(dpid1, (short)2),
				ope.createNewPortForTest(dpid1, (short)3),
				ope.createNewPortForTest(dpid1, (short)4),
		};

		TestPortObject ports2 [] = {
				ope.createNewPortForTest(dpid2, (short)1),
				ope.createNewPortForTest(dpid2, (short)2),
		};

		TestPortObject ports3 [] = {
				ope.createNewPortForTest(dpid3, (short)1),
				ope.createNewPortForTest(dpid3, (short)2),
		};
		
		ope.setLinkBetweenPortsForTest(ports1[0], ports2[0]);
		ope.setLinkBetweenPortsForTest(ports1[3], ports3[1]);
	}
	
	/**
	 * Returns new Link object of existing link
	 * @return new Link object
	 */
	private Link createExistingLink() {
		return new Link(Long.decode("0x0000000000000a01"), 1, Long.decode("0x0000000000000a02"), 1);
	}
	
	/**
	 * Returns new Link object of not-existing but feasible link
	 * @return new Link object
	 */
	private Link createFeasibleLink() {
		return new Link(Long.decode("0x0000000000000a01"), 3, Long.decode("0x0000000000000a03"), 1);
	}
	
	// make NO sense while test-network data doesn't define physical network (i.e. any link is feasible)
	@SuppressWarnings("unused")
	private Link createInfeasibleLink() {
		return new Link(Long.decode("0x0000000000000a01"), 1, Long.decode("0x0000000000000a03"), 3);
	}

	/**
	 * Returns list of Link objects which all has information of existing link in titanGraph
	 * @return ArrayList of new Link objects
	 */
	private List<Link> createExistingLinks() {
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(Long.decode("0x0000000000000a01"), 1, Long.decode("0x0000000000000a02"), 1));
		links.add(new Link(Long.decode("0x0000000000000a01"), 4, Long.decode("0x0000000000000a03"), 2));
		return links;
	}
	
	/**
	 * Returns list of Link objects which all has information of not-existing but feasible link
	 * @return ArrayList of new Link objects
	 */
	private List<Link> createFeasibleLinks() {
		List<Link> links = new ArrayList<Link>();
		links.add(new Link(Long.decode("0x0000000000000a01"), 2, Long.decode("0x0000000000000a02"), 2));
		links.add(new Link(Long.decode("0x0000000000000a01"), 3, Long.decode("0x0000000000000a03"), 1));
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
