/**
 * 
 */
package net.onrc.onos.util;

import static org.junit.Assert.*;

import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.ISwitchStorage.SwitchState;
import net.floodlightcontroller.core.internal.TestDatabaseManager;
import net.onrc.onos.util.GraphDBConnection.Transaction;

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

/**
 * @author Toshio Koide
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class})
public class GraphDBOperationTest {
	private static TitanGraph titanGraph;
	private static GraphDBConnection conn;
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
		titanGraph = TestDatabaseManager.getTestDatabase();
//		TestDatabaseManager.populateTestData(titanGraph);
		
		// replace return value of TitanFactory.open() to dummy DB created above
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(titanGraph);
		PowerMock.replay(TitanFactory.class);
		
		conn = GraphDBConnection.getInstance("/dummy/to/conf");
		op = new GraphDBOperation(conn);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		conn.close();
		titanGraph.shutdown();
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#newSwitch(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testNewSwitch() {
		Iterable<ISwitchObject> switches;

		switches = op.getAllSwitches();
		assertFalse(switches.iterator().hasNext());

		ISwitchObject sw = op.newSwitch();
		sw.setDPID("123");
		sw.setState(SwitchState.ACTIVE.toString());
		conn.endTx(Transaction.COMMIT);

		switches = op.getAllSwitches();
		assertTrue(switches.iterator().hasNext());
		
		ISwitchObject obtained_sw = switches.iterator().next();
		String obtained_dpid = obtained_sw.getDPID(); 
		assertEquals("123", obtained_dpid);
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#removeSwitch(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject)}.
	 */
	@Test
	public final void testRemoveSwitch() {
		Iterable<ISwitchObject> switches;

		// make sure there is no switch
		switches = op.getAllSwitches();
		assertFalse(switches.iterator().hasNext());
		
		ISwitchObject sw = op.newSwitch();
		sw.setDPID("123");
		sw.setState(SwitchState.ACTIVE.toString());
		conn.endTx(Transaction.COMMIT);
		
		sw = op.searchSwitch("123");
		op.removeSwitch(sw);

		assertNull(op.searchSwitch("123"));
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchSwitch(net.onrc.onos.util.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchSwitch() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchDevice(net.onrc.onos.util.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchDevice() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchPort(net.onrc.onos.util.GraphDBConnection, java.lang.String, short)}.
	 */
	@Test
	public final void testSearchPort() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#newPort(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testNewPort() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#newDevice(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testNewDevice() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#removePort(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject)}.
	 */
	@Test
	public final void testRemovePort() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#removeDevice(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject)}.
	 */
	@Test
	public final void testRemoveDevice() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getDevices(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetDevices() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchFlowPath(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.util.FlowId)}.
	 */
	@Test
	public final void testSearchFlowPath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#newFlowPath(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testNewFlowPath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#removeFlowPath(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath)}.
	 */
	@Test
	public final void testRemoveFlowPath() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getFlowPathByFlowEntry(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry)}.
	 */
	@Test
	public final void testGetFlowPathByFlowEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getAllFlowPaths(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllFlowPaths() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchFlowEntry(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.util.FlowEntryId)}.
	 */
	@Test
	public final void testSearchFlowEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#newFlowEntry(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testNewFlowEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#removeFlowEntry(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry)}.
	 */
	@Test
	public final void testRemoveFlowEntry() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getAllFlowEntries(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllFlowEntries() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getAllSwitchNotUpdatedFlowEntries(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllSwitchNotUpdatedFlowEntries() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getActiveSwitches(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetActiveSwitches() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getAllSwitches(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetAllSwitches() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getInactiveSwitches(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetInactiveSwitches() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchActiveSwitch(net.onrc.onos.util.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchActiveSwitch() {
		fail("Not yet implemented");
	}

}
