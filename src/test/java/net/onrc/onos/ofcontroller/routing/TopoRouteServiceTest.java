package net.onrc.onos.ofcontroller.routing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.TitanFactory;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import net.onrc.onos.graph.GraphDBConnection;
import net.onrc.onos.graph.GraphDBOperation;
import net.onrc.onos.ofcontroller.core.internal.TestDatabaseManager;
import net.onrc.onos.ofcontroller.routing.TopoRouteService;
import net.onrc.onos.ofcontroller.util.DataPath;
import net.onrc.onos.ofcontroller.util.Dpid;
import net.onrc.onos.ofcontroller.util.Port;
import net.onrc.onos.ofcontroller.util.SwitchPort;

/**
 * A class for testing the TopoRouteService class.
 * @see net.onrc.onos.ofcontroller.routing.TopoRouteService
 * @author Pavlin Radoslavov (pavlin@onlab.us)
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({TitanFactory.class, GraphDBConnection.class, GraphDBOperation.class, TopoRouteService.class})
public class TopoRouteServiceTest {
    String conf;
    private GraphDBConnection conn = null;
    private GraphDBOperation oper = null;
    private TitanGraph titanGraph = null;
    private TopoRouteService topoRouteService = null;

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

	// Prepare the TopoRouteService instance
	topoRouteService = new TopoRouteService();
	topoRouteService.setDbOperationHandler(oper);
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
     * Test method TopoRouteService.getTopoShortestPath()
     *
     * @see net.onrc.onos.ofcontroller.routing.TopoRouteService#getTopoShortestPath
     */
    @Test
    public void test_getTopoShortestPath() {
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
	Map<Long, ?> shortestPathTopo =
	    topoRouteService.prepareShortestPathTopo();
	dataPath = topoRouteService.getTopoShortestPath(shortestPathTopo,
							srcSwitchPort,
							dstSwitchPort);
	assertTrue(dataPath != null);
	String dataPathSummaryStr = dataPath.dataPathSummary();
	// System.out.println(dataPathSummaryStr);
	String expectedResult = "1/00:00:00:00:00:00:0a:01/2;1/00:00:00:00:00:00:0a:03/2;2/00:00:00:00:00:00:0a:04/3;1/00:00:00:00:00:00:0a:06/1;";
	assertEquals(dataPathSummaryStr, expectedResult);

	//
	// Test Shortest-Path computation to non-existing destination
	//
	String noSuchDpidStr = "ff:ff:00:00:00:00:0a:06";
	Dpid noSuchDstDpid = new Dpid(noSuchDpidStr);
	SwitchPort noSuchDstSwitchPort = new SwitchPort(noSuchDstDpid, dstPort);
	dataPath = topoRouteService.getTopoShortestPath(shortestPathTopo,
							srcSwitchPort,
							noSuchDstSwitchPort);
	assertTrue(dataPath == null);

	topoRouteService.dropShortestPathTopo(shortestPathTopo);
    }

    /**
     * Test method TopoRouteService.getShortestPath()
     *
     * @see net.onrc.onos.ofcontroller.routing.TopoRouteService#getShortestPath
     */
    @Test
    public void test_getShortestPath() {
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
	dataPath = topoRouteService.getShortestPath(srcSwitchPort,
						dstSwitchPort);
	assertTrue(dataPath != null);
	String dataPathSummaryStr = dataPath.dataPathSummary();
	// System.out.println(dataPathSummaryStr);
	String expectedResult = "1/00:00:00:00:00:00:0a:01/2;1/00:00:00:00:00:00:0a:03/2;2/00:00:00:00:00:00:0a:04/3;1/00:00:00:00:00:00:0a:06/1;";
	assertEquals(dataPathSummaryStr, expectedResult);

	//
	// Test Shortest-Path computation to non-existing destination
	//
	String noSuchDpidStr = "ff:ff:00:00:00:00:0a:06";
	Dpid noSuchDstDpid = new Dpid(noSuchDpidStr);
	SwitchPort noSuchDstSwitchPort = new SwitchPort(noSuchDstDpid, dstPort);

	dataPath = topoRouteService.getShortestPath(srcSwitchPort,
						    noSuchDstSwitchPort);
	assertTrue(dataPath == null);
    }

    /**
     * Test method TopoRouteService.routeExists()
     *
     * @see net.onrc.onos.ofcontroller.routing.TopoRouteService#routeExists
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
	result = topoRouteService.routeExists(srcSwitchPort, dstSwitchPort);
	assertTrue(result == true);

	//
	// Test a non-existing route
	//
	String noSuchDpidStr = "ff:ff:00:00:00:00:0a:06";
	Dpid noSuchDstDpid = new Dpid(noSuchDpidStr);
	SwitchPort noSuchDstSwitchPort = new SwitchPort(noSuchDstDpid, dstPort);
	result = topoRouteService.routeExists(srcSwitchPort,
					      noSuchDstSwitchPort);
	assertTrue(result != true);
    }
}
