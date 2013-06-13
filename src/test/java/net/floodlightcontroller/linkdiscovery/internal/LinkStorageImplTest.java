package net.floodlightcontroller.linkdiscovery.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.routing.Link;
import net.onrc.onos.ofcontroller.core.INetMapStorage.DM_OPERATION;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
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
	private static GraphDBOperation ope;
	
	// Uncommitted actions executed in LinkStorageImpl
	private static ArrayList<LinkEvent> actions;
	
	// Dictionary of mock IPortObject to information of port
	// -> Used to refer DPID from IPortObject
	private static Map<Object,PortInfo> mockToPortInfoMap;
	
	// Links existing in virtual graph
	private List<Link> links;
	
	
	//================ Utility classes for logging actions in LinkStorageImpl ===========
	// Not used for now
	private enum LinkEventType {
		ADD("ADD", 1),
		DELETE("DELETE", 2);
		
		private String name;
		private int number;

		LinkEventType(String name, int number) {
			this.name = name;
			this.number = number;
		}
		public String getName() { return name; }
		public int getNumber() { return number; }
	}
	
	private class LinkEvent {
		private Long src_dpid = null;
		private Long dst_dpid = null;
		private Short src_port = null;
		private Short dst_port = null;
		
		public LinkEventType type;
		
		public LinkEvent(Link link, LinkEventType type) {
			this.src_dpid = link.getSrc();
			this.src_port = link.getSrcPort();
			this.dst_dpid = link.getDst();
			this.dst_port = link.getDstPort();
			
			this.type = type;
		}
		
		public LinkEvent(Long src_dpid, Short src_port, Long dst_dpid, Short dst_port, LinkEventType type) {
			this.src_dpid = src_dpid;
			this.src_port = src_port;
			this.dst_dpid = dst_dpid;
			this.dst_port = dst_port;
			
			this.type = type;
		}

		public Long getSrcDpid() { return src_dpid; }
		public Short getSrcPort() { return src_port; }
		public Long getDstDpid() { return dst_dpid; }
		public Short getDstPort() { return dst_port; }
		public LinkEventType getType() { return type; }
	}
	
	private class PortInfo {
		public Long dpid = null;
		public Short port = null;
		
		public PortInfo(Long dpid, Short port) { this.dpid = dpid; this.port = port; }
	}

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
		
		ope = createMockGraphDBOperation();
		PowerMock.expectNew(GraphDBOperation.class, (GraphDBConnection)EasyMock.anyObject()).andReturn(ope).anyTimes();
		PowerMock.replay(GraphDBOperation.class);
		
		actions = new ArrayList<LinkEvent>();
		mockToPortInfoMap = new HashMap<Object,PortInfo>();
		
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
		doTestLinkIsInGraph(linkToVerify);

		// Avoiding duplication is out of scope. DBOperation is responsible for this.
//		// Add same link
//		Link linkToCreateTwice = createFeasibleLink();
//		linkStorage.update(linkToCreateTwice, ILinkStorage.DM_OPERATION.CREATE);
//		
//		// this occurs assertion failure if there are two links in titanGraph
//		doTestLinkIsInGraph(linkToVerify);
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
		doTestLinkIsInGraph(linkToVerify);
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
		doTestLinkIsNotInGraph(linkToVerify);
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
			doTestLinkIsInGraph(l);
		}
	
		// Out of scope: DBOperation is responsible for avoiding duplication.
//		// Test creation of existing links
//		linksToCreate = createFeasibleLinks();
//		linkStorage.update(linksToCreate, ILinkStorage.DM_OPERATION.CREATE);
//		for(Link l : linksToVerify) {
//			doTestLinkIsInGraph(l);
//		}
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
		doTestLinkIsInGraph(createFeasibleLink());
		doTestLinkIsInGraph(createExistingLink());
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
			doTestLinkIsInGraph(l);
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
		doTestLinkIsInGraph(createFeasibleLink());
		doTestLinkIsInGraph(createExistingLink());
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
			doTestLinkIsNotInGraph(l);
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
		doTestLinkIsNotInGraph(createFeasibleLink());
		doTestLinkIsNotInGraph(createExistingLink());
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
		doTestLinkIsInGraph(linkToVerify);
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

		doTestLinkIsInGraph(linkToVerify);
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
		doTestLinkIsNotInGraph(linkToVerify);
		
		linkToDelete = createFeasibleLink();
		linkToVerify = createFeasibleLink();

		// Test deletion of not-existing link
		linkStorage.updateLink(linkToDelete, null, DM_OPERATION.DELETE);
		doTestLinkIsNotInGraph(linkToVerify);
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
		doTestLinkIsNotInGraph(linkToVerify);
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
			doTestLinkIsNotInGraph(l);
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
		doTestLinkIsNotInGraph(createFeasibleLink());
		doTestLinkIsNotInGraph(createExistingLink());
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
		
		doTestLinkIsNotInGraph(linkToVerify);
	}

	/**
	 * Test if titanGraph has specific link
	 * @param link 
	 */
	private void doTestLinkIsInGraph(Link link) {
		int count = 0;
		for(Link lt : links) {
			if(lt.equals(link)) {
				++count;
			}
		}
		
		assertTrue(count == 1);
	}
	
	/**
	 * Test if titanGraph doesn't have specific link
	 * @param link
	 */
	private void doTestLinkIsNotInGraph(Link link) {
		assertFalse(links.contains(link));
	}
	
	/**
	 * Test if titanGraph has specific Link with specific LinkInfo
	 * @param link 
	 */
	private void doTestLinkHasStateOf(Link link, LinkInfo info) {
	}
	
	private class RemoveLinkCallback implements IAnswer<Object> {
		private long dpid;
		private short port;
		public RemoveLinkCallback(long dpid, short port) {
			this.dpid = dpid; this.port = port;
		}
		
		@Override
		public Object answer() throws Throwable {
			IPortObject dstPort = (IPortObject) EasyMock.getCurrentArguments()[0];
			PortInfo dst = mockToPortInfoMap.get(dstPort);

			Link linkToRemove = new Link(this.dpid,this.port,dst.dpid,dst.port);
			actions.add(new LinkEvent(linkToRemove,LinkEventType.DELETE));
			
			return null;
		}
	}
	
	private class SetLinkPortCallback implements IAnswer<Object> {
		private long dpid;
		private short port;
		public SetLinkPortCallback(long dpid, short port) {
			this.dpid = dpid; this.port = port;
		}

		@Override
		public Object answer() throws Throwable {
			IPortObject dstPort = (IPortObject) EasyMock.getCurrentArguments()[0];
			PortInfo dst = mockToPortInfoMap.get(dstPort);

			Link linkToAdd = new Link(this.dpid,this.port,dst.dpid,dst.port);
			actions.add(new LinkEvent(linkToAdd,LinkEventType.ADD));

			return null;
		}
		
	}
	
	// ------------------------Creation of Mock-----------------------------
	/**
	 * Create mock of GraphDBOperation which hooks port-related methods.
	 * @return
	 */
	private GraphDBOperation createMockGraphDBOperation() {
		GraphDBOperation mockDBOpe = EasyMock.createMock(GraphDBOperation.class);
		
		// Mock searchPort() method to create new mock IPortObject.
		EasyMock.expect(mockDBOpe.searchPort((String)EasyMock.anyObject(), EasyMock.anyShort())).
			andAnswer(new IAnswer<IPortObject>() {
			@Override
			public IPortObject answer() throws Throwable {
				long dpid = HexString.toLong((String)EasyMock.getCurrentArguments()[0]);
				short port = (Short) EasyMock.getCurrentArguments()[1];
				
				IPortObject ret = createMockPort(dpid,port);
				mockToPortInfoMap.put(ret, new PortInfo(dpid,port));
				
				return ret;
			}
		}).anyTimes();
		
		// Mock commit() method to remember "COMMIT" event.
		mockDBOpe.commit();
		EasyMock.expectLastCall().andAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				for(LinkEvent action : actions) {
					if(action.getType().equals(LinkEventType.ADD)) {
						Link linkToAdd = new Link(
								action.getSrcDpid(),
								action.getSrcPort(),
								action.getDstDpid(),
								action.getDstPort());
						links.add(linkToAdd);
					} else if(action.getType().equals(LinkEventType.DELETE)) {
						Link linkToRemove = new Link(
								action.getSrcDpid(),
								action.getSrcPort(),
								action.getDstDpid(),
								action.getDstPort());
						links.remove(linkToRemove);
					} else {
						log.error("mock commit(): unexpected action {}", new Object[]{action.getType()});
					}
				}
				actions.clear();
				return null;
			}
		}).anyTimes();
		
		EasyMock.replay(mockDBOpe);
		return mockDBOpe;
	}
	
	private IPortObject createMockPort(long dpid, short number) {
		IPortObject mockPort = EasyMock.createMock(IPortObject.class);
		
		EasyMock.expect(mockPort.getNumber()).andReturn(number);
		
		// Mock removeLink() method
		mockPort.removeLink((IPortObject) EasyMock.anyObject());
		EasyMock.expectLastCall().andAnswer(new RemoveLinkCallback(dpid, number)).anyTimes();
		
		// Mock setLinkPort() method
		mockPort.setLinkPort((IPortObject) EasyMock.anyObject());
		EasyMock.expectLastCall().andAnswer(new SetLinkPortCallback(dpid, number)).anyTimes();
		
		// Mock getLinkPorts() method
		// -> Always empty list for now
		EasyMock.expect(mockPort.getLinkedPorts()).andAnswer(new IAnswer< Iterable<IPortObject> >() {
			@Override
			public Iterable<IPortObject> answer() throws Throwable {
				return new Iterable<IPortObject> () {
					@Override
					public Iterator<IPortObject> iterator() {
						// create empty list and returns its iterator
						return new ArrayList<IPortObject>().iterator();
					}
				};
			}
		}).anyTimes();
		
		EasyMock.replay(mockPort);
		return mockPort;
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
		links = new ArrayList<Link>();
		
		links.add(new Link(Long.decode("0x0000000000000a01"), 1, Long.decode("0x0000000000000a02"), 1));
		links.add(new Link(Long.decode("0x0000000000000a01"), 3, Long.decode("0x0000000000000a03"), 2));
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
