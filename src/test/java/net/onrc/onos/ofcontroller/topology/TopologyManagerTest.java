package net.onrc.onos.ofcontroller.topology;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Map;

import org.easymock.EasyMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanFactory;
import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.internal.TestDatabaseManager;
import net.onrc.onos.ofcontroller.topology.TopologyManager;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.FlowPathFlags;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

/**
 * A class for testing the TopologyManager class.
 * @see net.onrc.onos.ofcontroller.topology.TopologyManager
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, TopologyManager.class})
public class TopologyManagerTest {
    String conf;
    private GraphDBConnection conn = null;
    private GraphDBOperation oper = null;
    private TitanGraph titanGraph = null;
    private TopologyManager topologyManager = null;

    /**
     * Setup the tests.
     */
    @Before
    public void setUp() throws Exception {
	conf = "/dummy/path/to/db";

	//
	// Make mock database.
	// Replace TitanFactory.open() to return the mock database.
	//
	titanGraph = TestDatabaseManager.getTestDatabase();
	PowerMock.mockStatic(TitanFactory.class);
	EasyMock.expect(TitanFactory.open((String)EasyMock.anyObject())).andReturn(titanGraph);
	PowerMock.replay(TitanFactory.class);

	// Create the connection to the database
	conn = GraphDBConnection.getInstance(conf);
	oper = new GraphDBOperation(conn);

	// Populate the database
	TestDatabaseManager.populateTestData(titanGraph);

	// Prepare the TopologyManager instance
	topologyManager = new TopologyManager(oper);
    }

    /**
     * Cleanup after the tests.
     */
    @After
    public void tearDown() throws Exception {
	titanGraph.shutdown();
	TestDatabaseManager.deleteTestDatabase();
    }

    /**
     * Test method TopologyManager.getTopologyShortestPath()
     *
     * @see net.onrc.onos.ofcontroller.topology.TopologyManager#getTopologyShortestPath
     */
    @Test
    public void test_getTopologyShortestPath() {
	DataPath dataPath = null;
	String srcDpidStr = "00:00:00:00:00:00:0a:01";
	String dstDpidStr = "00:00:00:00:00:00:0a:06";
	short srcPortShort = 1;
	short dstPortShort = 1;

	//
	// Initialize the source and destination points
	//
	Dpid srcDpid = new Dpid(srcDpidStr);
	Port srcPort = new Port(srcPortShort);
	Dpid dstDpid = new Dpid(dstDpidStr);
	Port dstPort = new Port(dstPortShort);
	SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
	SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);

	//
	// Test a valid Shortest-Path computation
	//
	Topology topology = topologyManager.newDatabaseTopology();
	dataPath = topologyManager.getTopologyShortestPath(topology,
							   srcSwitchPort,
							   dstSwitchPort);
	assertTrue(dataPath != null);
	String dataPathSummaryStr = dataPath.dataPathSummary();
	// System.out.println(dataPathSummaryStr);
	String expectedResult = "1/00:00:00:00:00:00:0a:01/2;1/00:00:00:00:00:00:0a:03/2;2/00:00:00:00:00:00:0a:04/3;1/00:00:00:00:00:00:0a:06/1;";
	assertEquals(dataPathSummaryStr, expectedResult);

	// Test if we apply various Flow Path Flags
	String expectedResult2 = "1/00:00:00:00:00:00:0a:03/2;2/00:00:00:00:00:00:0a:04/3;1/00:00:00:00:00:00:0a:06/1;";
	String expectedResult3 = "1/00:00:00:00:00:00:0a:03/2;";
	FlowPathFlags flowPathFlags2 = new FlowPathFlags("DISCARD_FIRST_HOP_ENTRY");
	FlowPathFlags flowPathFlags3 = new FlowPathFlags("KEEP_ONLY_FIRST_HOP_ENTRY");
	//
	dataPath.applyFlowPathFlags(flowPathFlags2);
	dataPathSummaryStr = dataPath.dataPathSummary();
	assertEquals(dataPathSummaryStr, expectedResult2);
	//
	dataPath.applyFlowPathFlags(flowPathFlags3);
	dataPathSummaryStr = dataPath.dataPathSummary();
	assertEquals(dataPathSummaryStr, expectedResult3);

	//
	// Test Shortest-Path computation to non-existing destination
	//
	String noSuchDpidStr = "ff:ff:00:00:00:00:0a:06";
	Dpid noSuchDstDpid = new Dpid(noSuchDpidStr);
	SwitchPort noSuchDstSwitchPort = new SwitchPort(noSuchDstDpid, dstPort);
	dataPath = topologyManager.getTopologyShortestPath(topology,
							   srcSwitchPort,
							   noSuchDstSwitchPort);
	assertTrue(dataPath == null);

	topologyManager.dropTopology(topology);
    }

    /**
     * Test method TopologyManager.getDatabaseShortestPath()
     *
     * @see net.onrc.onos.ofcontroller.routing.TopologyManager#getDatabaseShortestPath
     */
    @Test
    public void test_getDatabaseShortestPath() {
	DataPath dataPath = null;
	String srcDpidStr = "00:00:00:00:00:00:0a:01";
	String dstDpidStr = "00:00:00:00:00:00:0a:06";
	short srcPortShort = 1;
	short dstPortShort = 1;

	//
	// Initialize the source and destination points
	//
	Dpid srcDpid = new Dpid(srcDpidStr);
	Port srcPort = new Port(srcPortShort);
	Dpid dstDpid = new Dpid(dstDpidStr);
	Port dstPort = new Port(dstPortShort);
	SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
	SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);

	//
	// Test a valid Shortest-Path computation
	//
	dataPath = topologyManager.getDatabaseShortestPath(srcSwitchPort,
							   dstSwitchPort);
	assertTrue(dataPath != null);
	String dataPathSummaryStr = dataPath.dataPathSummary();
	// System.out.println(dataPathSummaryStr);
	String expectedResult = "1/00:00:00:00:00:00:0a:01/2;1/00:00:00:00:00:00:0a:03/2;2/00:00:00:00:00:00:0a:04/3;1/00:00:00:00:00:00:0a:06/1;";
	assertEquals(dataPathSummaryStr, expectedResult);

	// Test if we apply various Flow Path Flags
	String expectedResult2 = "1/00:00:00:00:00:00:0a:03/2;2/00:00:00:00:00:00:0a:04/3;1/00:00:00:00:00:00:0a:06/1;";
	String expectedResult3 = "1/00:00:00:00:00:00:0a:03/2;";
	FlowPathFlags flowPathFlags2 = new FlowPathFlags("DISCARD_FIRST_HOP_ENTRY");
	FlowPathFlags flowPathFlags3 = new FlowPathFlags("KEEP_ONLY_FIRST_HOP_ENTRY");
	//
	dataPath.applyFlowPathFlags(flowPathFlags2);
	dataPathSummaryStr = dataPath.dataPathSummary();
	assertEquals(dataPathSummaryStr, expectedResult2);
	//
	dataPath.applyFlowPathFlags(flowPathFlags3);
	dataPathSummaryStr = dataPath.dataPathSummary();
	assertEquals(dataPathSummaryStr, expectedResult3);

	//
	// Test Shortest-Path computation to non-existing destination
	//
	String noSuchDpidStr = "ff:ff:00:00:00:00:0a:06";
	Dpid noSuchDstDpid = new Dpid(noSuchDpidStr);
	SwitchPort noSuchDstSwitchPort = new SwitchPort(noSuchDstDpid, dstPort);

	dataPath = topologyManager.getDatabaseShortestPath(srcSwitchPort,
							   noSuchDstSwitchPort);
	assertTrue(dataPath == null);
    }

    /**
     * Test method TopologyManager.routeExists()
     *
     * @see net.onrc.onos.ofcontroller.routing.TopologyManager#routeExists
     */
    @Test
    public void test_routeExists() {
	Boolean result;
	String srcDpidStr = "00:00:00:00:00:00:0a:01";
	String dstDpidStr = "00:00:00:00:00:00:0a:06";
	short srcPortShort = 1;
	short dstPortShort = 1;

	//
	// Initialize the source and destination points
	//
	Dpid srcDpid = new Dpid(srcDpidStr);
	Port srcPort = new Port(srcPortShort);
	Dpid dstDpid = new Dpid(dstDpidStr);
	Port dstPort = new Port(dstPortShort);
	SwitchPort srcSwitchPort = new SwitchPort(srcDpid, srcPort);
	SwitchPort dstSwitchPort = new SwitchPort(dstDpid, dstPort);

	//
	// Test a valid route
	//
	result = topologyManager.routeExists(srcSwitchPort, dstSwitchPort);
	assertTrue(result == true);

	//
	// Test a non-existing route
	//
	String noSuchDpidStr = "ff:ff:00:00:00:00:0a:06";
	Dpid noSuchDstDpid = new Dpid(noSuchDpidStr);
	SwitchPort noSuchDstSwitchPort = new SwitchPort(noSuchDstDpid, dstPort);
	result = topologyManager.routeExists(srcSwitchPort,
					     noSuchDstSwitchPort);
	assertTrue(result != true);
    }
}
