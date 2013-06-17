/**
 * 
 */
package net.onrc.onos.util;

import static org.junit.Assert.*;

import java.util.Iterator;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage.SwitchState;
import net.onrc.onos.ofcontroller.core.internal.TestDatabaseManager;

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
public class GraphDBOperationTest {
	private static TitanGraph testdb;
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
		testdb = TestDatabaseManager.getTestDatabase();
//		TestDatabaseManager.populateTestData(titanGraph);
		
		// replace return value of TitanFactory.open() to dummy DB created above
		PowerMock.mockStatic(TitanFactory.class);
		EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(testdb);
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
		testdb.shutdown();
	}

	private Iterator<Vertex> enumerateVertices(String vertexType) {
		return testdb.getVertices("type", vertexType).iterator();
	}
	
	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#newSwitch(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testNewSwitch() {
		Iterator<Vertex> vertices;
		assertFalse(enumerateVertices("switch").hasNext());

		ISwitchObject sw = op.newSwitch("123");
		
		assertEquals("123", sw.getDPID());
		op.commit();

		vertices = enumerateVertices("switch");
		assertTrue(vertices.hasNext());
		assertEquals(vertices.next().getProperty("dpid").toString(), "123");		
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchSwitch(net.onrc.onos.util.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchSwitch() {
		ISwitchObject sw = op.newSwitch("123");
		op.commit();
		
		sw = op.searchSwitch("123");
		
		assertNotNull(sw);
		assertEquals("123", sw.getDPID());
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#searchActiveSwitch(net.onrc.onos.util.GraphDBConnection, java.lang.String)}.
	 */
	@Test
	public final void testSearchActiveSwitch() {
		ISwitchObject sw = op.newSwitch("111");
		sw.setState(SwitchState.ACTIVE.toString());
		sw = op.newSwitch("222");
		sw.setState(SwitchState.INACTIVE.toString());
		op.commit();
		
		sw = op.searchActiveSwitch("111");
		assertNotNull(sw);
		assertEquals("111", sw.getDPID());
		
		sw = op.searchActiveSwitch("222");
		assertNull(sw);	
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#getActiveSwitches(net.onrc.onos.util.GraphDBConnection)}.
	 */
	@Test
	public final void testGetActiveSwitches() {
		ISwitchObject sw = op.newSwitch("111");
		sw.setState(SwitchState.ACTIVE.toString());
		sw = op.newSwitch("222");
		sw.setState(SwitchState.INACTIVE.toString());
		op.commit();
		
		Iterator<ISwitchObject> i = op.getActiveSwitches().iterator();
		assertTrue(i.hasNext());
		assertEquals("111", i.next().getDPID());
		assertFalse(i.hasNext());		
	}

	/**
	 * Test method for {@link net.onrc.onos.util.GraphDBOperation#removeSwitch(net.onrc.onos.util.GraphDBConnection, net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject)}.
	 */
	@Test
	public final void testRemoveSwitch() {
		ISwitchObject sw = op.newSwitch("123");
		op.commit();	
		sw = op.searchSwitch("123");
		
		op.removeSwitch(sw);

		assertFalse(enumerateVertices("switch").hasNext());
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

}
