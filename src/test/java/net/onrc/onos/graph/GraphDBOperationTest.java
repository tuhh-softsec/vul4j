/**
 * 
 */
package net.onrc.onos.graph;

import static org.junit.Assert.*;

import java.util.*;

import junit.framework.TestCase;

import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.core.internal.TestDatabaseManager;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;

/**
 * @author Toshio Koide
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class})
public class GraphDBOperationTest extends TestCase {
	private static TitanGraph testdb;
	private static GraphDBOperation op;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		TestDatabaseManager.deleteTestDatabase();
		testdb = TestDatabaseManager.getTestDatabase();
//		TestDatabaseManager.populateTestData(titanGraph);

		String dummyPath = "/dummy/to/conf";
		// replace return value of TitanFactory.open() to dummy DB created above
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open(dummyPath)).andReturn(testdb);
		PowerMock.replay(TitanFactory.class);
		
		op = new GraphDBOperation(dummyPath);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		op.close();
		testdb.shutdown();
		PowerMock.verifyAll();
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#newSwitch(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testNewSwitch() {
		assertNull(op.searchSwitch("123"));

		ISwitchObject sw = op.newSwitch("123");
		assertEquals(sw.getDPID(), "123");
		op.commit();

		sw = op.searchSwitch("123");
		assertNotNull(sw);
		assertEquals("123", sw.getDPID());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#searchSwitch(net.onrc.onos.graph.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchSwitch() {
		op.newSwitch("123");
		op.newSwitch("456");
		op.commit();

		ISwitchObject sw = op.searchSwitch("123");
		assertNotNull(sw);
		assertEquals("123", sw.getDPID());

		sw = op.searchSwitch("789");
		assertNull(sw);
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#searchActiveSwitch(net.onrc.onos.graph.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchActiveSwitch() {
		op.newSwitch("111").setState(SwitchState.ACTIVE.toString());
		op.newSwitch("222").setState(SwitchState.INACTIVE.toString());
		op.commit();
		
		ISwitchObject sw = op.searchActiveSwitch("111");
		assertNotNull(sw);
		assertEquals("111", sw.getDPID());
		
		sw = op.searchActiveSwitch("222");
		assertNull(sw);	
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getActiveSwitches(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testGetActiveSwitches() {
		op.newSwitch("111").setState(SwitchState.ACTIVE.toString());
		op.newSwitch("222").setState(SwitchState.INACTIVE.toString());
		op.commit();
		
		Iterator<ISwitchObject> i = op.getActiveSwitches().iterator();
		
		assertTrue(i.hasNext());
		assertEquals("111", i.next().getDPID());
		assertFalse(i.hasNext());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getAllSwitches(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllSwitches() {
		List<String> dpids = Arrays.asList("111", "222", "333");
		Collections.sort(dpids);
		
		for (String dpid: dpids) op.newSwitch(dpid);
		op.commit();

		List<String> actual_ids = new ArrayList<String>();
		for (ISwitchObject switchObj: op.getAllSwitches()) actual_ids.add(switchObj.getDPID());
		Collections.sort(actual_ids);

		assertArrayEquals(dpids.toArray(), actual_ids.toArray());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getInactiveSwitches(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testGetInactiveSwitches() {
		op.newSwitch("111").setState(SwitchState.ACTIVE.toString());
		op.newSwitch("222").setState(SwitchState.INACTIVE.toString());
		op.commit();
		
		Iterator<ISwitchObject> i = op.getInactiveSwitches().iterator();
		
		assertTrue(i.hasNext());
		assertEquals("222", i.next().getDPID());
		assertFalse(i.hasNext());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getAllSwitchNotUpdatedFlowEntries(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllSwitchNotUpdatedFlowEntries() {
		FlowEntryId flowEntryId10 = new FlowEntryId(10);
		FlowEntryId flowEntryId20 = new FlowEntryId(20);
		IFlowEntry flowEntry10 = op.newFlowEntry();
		IFlowEntry flowEntry20 = op.newFlowEntry();
		flowEntry10.setFlowEntryId(flowEntryId10.toString());
		flowEntry20.setFlowEntryId(flowEntryId20.toString());
		flowEntry10.setSwitchState("FE_SWITCH_NOT_UPDATED");
		flowEntry20.setSwitchState("FE_SWITCH_UPDATED");
 		op.commit();
		
		Iterator<IFlowEntry> flowEntries = op.getAllSwitchNotUpdatedFlowEntries().iterator();
		assertNotNull(flowEntries);
		assertTrue(flowEntries.hasNext());
		assertEquals(flowEntryId10.toString(), flowEntries.next().getFlowEntryId());
		assertFalse(flowEntries.hasNext());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#removeSwitch(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject)}.
	 */
	@Test
	public final void testRemoveSwitch() {
		ISwitchObject sw = op.newSwitch("123");
		op.commit();	
		sw = op.searchSwitch("123");
		assertNotNull(sw);

		op.removeSwitch(sw);
		op.commit();
		
		assertNull(op.searchSwitch("123"));
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#newPort(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testNewPort() {
		assertFalse(testdb.getVertices("type", "port").iterator().hasNext());
		
		IPortObject port = op.newPort("1", (short) 10);
		assertTrue(port.getNumber() == 10);
		op.commit();
		
		Iterator<Vertex> vertices = testdb.getVertices("type", "port").iterator();
		assertTrue(vertices.hasNext());
		assertEquals(vertices.next().getProperty("number").toString(), "10");		
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#searchPort(net.onrc.onos.graph.GraphDBConnection, java.lang.String, short)}.
	 */
	@Test
	public final void testSearchPort() {
		ISwitchObject sw;
		IPortObject port;
		
		sw = op.newSwitch("1");
		sw.addPort(op.newPort("1", (short) 1));
		sw.addPort(op.newPort("1", (short) 2));
		
		sw = op.newSwitch("2");
		sw.addPort(op.newPort("2", (short) 1));
		sw.addPort(op.newPort("2", (short) 2));

		op.commit();

		assertNull(op.searchPort("3", (short) 1));
		assertNull(op.searchPort("1", (short) 3));

		port = op.searchPort("1", (short) 1);
		assertNotNull(port);
		assertTrue(port.getNumber() == 1);
		sw = port.getSwitch();
		assertNotNull(sw);
		assertEquals("1", sw.getDPID());

		port = op.searchPort("1", (short) 2);
		assertNotNull(port);
		assertTrue(port.getNumber() == 2);
		sw = port.getSwitch();
		assertNotNull(sw);
		assertEquals("1", sw.getDPID());

		port = op.searchPort("2", (short) 1);
		assertNotNull(port);
		assertTrue(port.getNumber() == 1);
		sw = port.getSwitch();
		assertNotNull(sw);
		assertEquals("2", sw.getDPID());

		port = op.searchPort("2", (short) 2);
		assertNotNull(port);
		assertTrue(port.getNumber() == 2);
		sw = port.getSwitch();
		assertNotNull(sw);
		assertEquals("2", sw.getDPID());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#removePort(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject)}.
	 */
	@Test
	public final void testRemovePort() {
		ISwitchObject sw;
		IPortObject port;
		
		sw = op.newSwitch("1");
		sw.addPort(op.newPort("1", (short) 1));
		sw.addPort(op.newPort("1", (short) 2));
		
		op.commit();

		port = op.searchPort("1", (short) 1);
		assertNotNull(port);
		assertNotNull(op.searchPort("1", (short) 2));
		assertNull(op.searchPort("1", (short) 3));

		op.removePort(port);
		op.commit();
		
		assertNull(op.searchPort("1", (short) 1));
		port = op.searchPort("1", (short) 2);
		assertNotNull(port);
		
		op.removePort(port);
		op.commit();

		assertNull(op.searchPort("1", (short) 1));
		assertNull(op.searchPort("1", (short) 2));
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#newDevice(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testNewDevice() {
		assertFalse(testdb.getVertices("type", "device").iterator().hasNext());
		
		IDeviceObject device = op.newDevice();
		device.setMACAddress("11:22:33:44:55:66");
		device.setIPAddress("192.168.1.1");
		op.commit();
		
		Iterator<Vertex> vertices = testdb.getVertices("type", "device").iterator();
		assertTrue(vertices.hasNext());
		Vertex v = vertices.next();
		assertEquals("11:22:33:44:55:66", v.getProperty("dl_addr").toString());
		assertEquals("192.168.1.1", v.getProperty("nw_addr").toString());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#searchDevice(net.onrc.onos.graph.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchDevice() {
		assertNull(op.searchDevice("11:22:33:44:55:66"));
		assertNull(op.searchDevice("66:55:44:33:22:11"));

		op.newDevice().setMACAddress("11:22:33:44:55:66");
		op.commit();
		
		IDeviceObject device = op.searchDevice("11:22:33:44:55:66");
		assertNotNull(device);
		assertEquals("11:22:33:44:55:66", device.getMACAddress());
		
		assertNull(op.searchDevice("66:55:44:33:22:11"));
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getDevices(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testGetDevices() {
		List<String> original_macs = Arrays.asList(
				"11:11:11:11:11:11",
				"22:22:22:22:22:22",
				"33:33:33:33:33:33"
				);
		
		for (String mac: original_macs) op.newDevice().setMACAddress(mac);
		op.commit();
		
		Iterable<IDeviceObject> devices = op.getDevices();
		List<String> macs = new ArrayList<String>();
		for (IDeviceObject device: devices) macs.add(device.getMACAddress());
		Collections.sort(macs);
		assertArrayEquals(original_macs.toArray(), macs.toArray());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#removeDevice(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject)}.
	 */
	@Test
	public final void testRemoveDevice() {
		op.newDevice().setMACAddress("11:22:33:44:55:66");
		op.commit();
		
		op.removeDevice(op.searchDevice("11:22:33:44:55:66"));
		op.commit();
		assertNull(op.searchDevice("11:22:33:44:55:66"));
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#newFlowPath(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testNewFlowPath() {
		FlowId flowId = new FlowId(10);
		IFlowPath flowPath = op.newFlowPath();
		flowPath.setFlowId(flowId.toString());
		op.commit();

		Iterator<IFlowPath> flows = op.getAllFlowPaths().iterator();
		assertTrue(flows.hasNext());
		assertEquals(flowId.toString(), flows.next().getFlowId());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#searchFlowPath(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.util.FlowId)}.
	 */
	@Test
	public final void testSearchFlowPath() {
		FlowId flowId = new FlowId(20);
		assertNull(op.searchFlowPath(flowId));

		op.newFlowPath().setFlowId(flowId.toString());
		op.commit();
		
		IFlowPath flowPath = op.searchFlowPath(flowId);
		assertNotNull(flowPath);
		assertEquals(flowId.toString(), flowPath.getFlowId());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getFlowPathByFlowEntry(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry)}.
	 */
	@Test
	public final void testGetFlowPathByFlowEntry() {
		FlowId flowId10 = new FlowId(10);
		FlowId flowId20 = new FlowId(20);
		IFlowPath flowPath10 = op.newFlowPath();
		IFlowPath flowPath20 = op.newFlowPath();
		IFlowEntry flowEntry10 = op.newFlowEntry();
		IFlowEntry flowEntry20 = op.newFlowEntry();
		IFlowEntry flowEntry30 = op.newFlowEntry();
		FlowEntryId flowEntryId10 = new FlowEntryId(10); 
		FlowEntryId flowEntryId20 = new FlowEntryId(20); 
		FlowEntryId flowEntryId30 = new FlowEntryId(30); 
		flowEntry10.setFlowEntryId(flowEntryId10.toString());
		flowEntry20.setFlowEntryId(flowEntryId20.toString());
		flowEntry30.setFlowEntryId(flowEntryId30.toString());
		flowPath10.setFlowId(flowId10.toString());
		flowPath10.addFlowEntry(flowEntry10);
		flowPath20.setFlowId(flowId20.toString());
		flowPath20.addFlowEntry(flowEntry20);
		op.commit();

		flowEntry10 = op.searchFlowEntry(flowEntryId10);
		IFlowPath obtainedFlowPath = op.getFlowPathByFlowEntry(flowEntry10);
		assertNotNull(obtainedFlowPath);
		assertEquals(flowId10.toString(), obtainedFlowPath.getFlowId());
		
		flowEntry20 = op.searchFlowEntry(flowEntryId20);
		obtainedFlowPath = op.getFlowPathByFlowEntry(flowEntry20);
		assertNotNull(obtainedFlowPath);
		assertEquals(flowId20.toString(), obtainedFlowPath.getFlowId());
		
		flowEntry30 = op.searchFlowEntry(flowEntryId30);
		obtainedFlowPath = op.getFlowPathByFlowEntry(flowEntry30);
		assertNull(obtainedFlowPath);
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getAllFlowPaths(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllFlowPaths() {
		List<FlowId> flowids = Arrays.asList(
				new FlowId(10), new FlowId(20), new FlowId(30)
				);
		
		for (FlowId flowId: flowids)
			op.newFlowPath().setFlowId(flowId.toString());
		op.commit();

		List<String> actual_ids = new ArrayList<String>();
		for (IFlowPath flowPath: op.getAllFlowPaths()) actual_ids.add(flowPath.getFlowId());
		Collections.sort(actual_ids);

		List<String> expected_ids = new ArrayList<String>();
		for (FlowId flowid: flowids) expected_ids.add(flowid.toString());
		Collections.sort(expected_ids);
		
		assertArrayEquals(expected_ids.toArray(), actual_ids.toArray());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#removeFlowPath(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath)}.
	 */
	@Test
	public final void testRemoveFlowPath() {
		FlowId flowId10 = new FlowId(10);
		FlowId flowId20 = new FlowId(20);
		op.newFlowPath().setFlowId(flowId10.toString());
		op.newFlowPath().setFlowId(flowId20.toString());
		op.commit();
		
		IFlowPath flowPath = op.searchFlowPath(flowId10);
		assertNotNull(flowPath);
		op.removeFlowPath(flowPath);
		op.commit();
		
		assertNull(op.searchFlowPath(flowId10));
		assertNotNull(op.searchFlowPath(flowId20));
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#newFlowEntry(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testNewFlowEntry() {
		IFlowEntry flowEntry = op.newFlowEntry();
		FlowEntryId flowEntryId = new FlowEntryId();
		flowEntryId.setValue(12345);
		flowEntry.setFlowEntryId(flowEntryId.toString());
		op.commit();
		
		flowEntry = op.searchFlowEntry(flowEntryId);
		assertNotNull(flowEntry);
		assertEquals(flowEntry.getFlowEntryId(), flowEntryId.toString());		
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#searchFlowEntry(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.util.FlowEntryId)}.
	 */
	@Test
	public final void testSearchFlowEntry() {
		FlowEntryId flowEntryId10 = new FlowEntryId();
		flowEntryId10.setValue(10);
		FlowEntryId flowEntryId20 = new FlowEntryId();
		flowEntryId20.setValue(20);
		FlowEntryId flowEntryId30 = new FlowEntryId();
		flowEntryId30.setValue(30);
		
		op.newFlowEntry().setFlowEntryId(flowEntryId10.toString());
		op.newFlowEntry().setFlowEntryId(flowEntryId20.toString());
		op.commit();
		
		assertNull(op.searchFlowEntry(flowEntryId30));
		IFlowEntry flowEntry = op.searchFlowEntry(flowEntryId10);
		assertEquals(flowEntry.getFlowEntryId(), flowEntryId10.toString());
		flowEntry = op.searchFlowEntry(flowEntryId20);
		assertEquals(flowEntry.getFlowEntryId(), flowEntryId20.toString());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#getAllFlowEntries(net.onrc.onos.graph.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllFlowEntries() {
		List<FlowEntryId> flowEntryIds = Arrays.asList(
				new FlowEntryId(10), new FlowEntryId(20), new FlowEntryId(30)
				);
		
		for (FlowEntryId flowEntryId: flowEntryIds)
			op.newFlowEntry().setFlowEntryId(flowEntryId.toString());
		op.commit();

		List<String> actual_ids = new ArrayList<String>();
		for (IFlowEntry flowEntry: op.getAllFlowEntries()) actual_ids.add(flowEntry.getFlowEntryId());
		Collections.sort(actual_ids);

		List<String> expected_ids = new ArrayList<String>();
		for (FlowEntryId flowEntryId: flowEntryIds) expected_ids.add(flowEntryId.toString());
		Collections.sort(expected_ids);
		
		assertArrayEquals(expected_ids.toArray(), actual_ids.toArray());
	}

	/**
	 * Test method for {@link net.onrc.onos.graph.GraphDBOperation#removeFlowEntry(net.onrc.onos.graph.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry)}.
	 */
	@Test
	public final void testRemoveFlowEntry() {
		FlowEntryId flowEntryId10 = new FlowEntryId(10);
		FlowEntryId flowEntryId20 = new FlowEntryId(20);
		op.newFlowEntry().setFlowEntryId(flowEntryId10.toString());
		op.newFlowEntry().setFlowEntryId(flowEntryId20.toString());
		op.commit();
		
		IFlowEntry flowEntry = op.searchFlowEntry(flowEntryId10);
		assertNotNull(flowEntry);
		op.removeFlowEntry(flowEntry);
		op.commit();
		
		assertNull(op.searchFlowEntry(flowEntryId10));
		assertNotNull(op.searchFlowEntry(flowEntryId20));
	}

}
