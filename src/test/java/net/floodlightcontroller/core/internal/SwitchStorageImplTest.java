package net.floodlightcontroller.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.floodlightcontroller.core.ISwitchStorage;
import net.floodlightcontroller.core.ISwitchStorage.SwitchState;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openflow.protocol.OFPhysicalPort;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.groovy.Gremlin;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.Pipe;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;
import com.tinkerpop.pipes.filter.FilterPipe.Filter;
import com.tinkerpop.pipes.util.PipesFluentPipeline;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;


public class SwitchStorageImplTest {

	private ISwitchStorage switchStorage;
	private TitanGraph titanGraph;
	
	@Before
	public void setUp() throws Exception {
		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);
		
		switchStorage = new TestableSwitchStorageImpl(titanGraph);
	}

	@After
	public void tearDown() throws Exception {
		titanGraph.shutdown();
	}

	@Ignore @Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddPort() {
		
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNumber = 5;
		
		OFPhysicalPort portToAdd = new OFPhysicalPort();
		portToAdd.setName("port 5 at SEA switch");
		portToAdd.setCurrentFeatures(OFPhysicalPort.OFPortFeatures.OFPPF_100MB_FD.getValue());
		portToAdd.setPortNumber(portNumber);
		
		switchStorage.addPort(dpid, portToAdd);
		
		Vertex sw = titanGraph.getVertices("dpid", dpid).iterator().next();
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		pipe.start(sw).out("on").has("number", portNumber);
		
		assertTrue(pipe.hasNext());
		Vertex addedPort = pipe.next();
		assertFalse(pipe.hasNext());
		
		assertEquals(addedPort.getProperty("number"), portNumber);
	}

	@Ignore @Test
	public void testGetPorts() {
		fail("Not yet implemented");
	}

	@Ignore @Test
	public void testGetPortStringShort() {
		fail("Not yet implemented");
	}

	@Ignore @Test
	public void testGetPortStringString() {
		fail("Not yet implemented");
	}

	@Test
	public void testAddSwitch() {
		String dpid = "00:00:00:00:00:00:0a:07";
		
		switchStorage.addSwitch(dpid);
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", dpid).iterator();
		assertTrue(it.hasNext());
		Vertex addedSwitch = it.next();
		assertFalse(it.hasNext());
		
		assertEquals(addedSwitch.getProperty("type"), "switch");
		assertEquals(addedSwitch.getProperty("dpid"), dpid);
		assertEquals(addedSwitch.getProperty("state"), SwitchState.ACTIVE.toString());
	}

	
	@Test
	public void testDeleteSwitch() {
		String dpid = "00:00:00:00:00:00:0a:01";
		
		switchStorage.deleteSwitch(dpid);
		
		Iterator<Vertex> it = titanGraph.getVertices("dpid", dpid).iterator();
		assertFalse(it.hasNext());
	}

	@Test
	public void testDeletePortByPortNum() {
		//FIXME fails because query for the port is wrong in SwitchStorageImpl
		
		String dpid = "00:00:00:00:00:00:0a:01";
		short portNum = 3;
		
		switchStorage.deletePort(dpid, portNum);
		
		Vertex sw = titanGraph.getVertices("dpid", dpid).iterator().next();
		
		/*
		Iterator<Vertex> it = sw.getVertices(Direction.OUT, "on").iterator();
		
		while (it.hasNext()){
			System.out.println(it.next());
		}
		*/
		
		GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
		pipe.start(sw).out("on").has("number", portNum);
		assertFalse(pipe.hasNext());
	}

	@Ignore @Test
	public void testDeletePortStringString() {
		fail("Not yet implemented");
	}

	@Ignore @Test
	public void testGetActiveSwitches() {
		fail("Not yet implemented");
	}

	static class MyLoopFunction implements PipeFunction<LoopBundle<Vertex>, Boolean> {
	    String dpid;
	    public MyLoopFunction(String dpid) {
		super();
		this.dpid = dpid;
	    }
	    public Boolean compute(LoopBundle<Vertex> bundle) {
		Boolean output = false;
		if ((bundle.getObject().getProperty("dpid") != dpid) && 
		    (bundle.getLoops() < 10)) {
		    output = true;
		}
		return output;
	    }
	}

	@Test
	public void testShortestPath() {
	    String dpid_src = "00:00:00:00:00:00:0a:01";
	    String dpid_dest = "00:00:00:00:00:00:0a:06";

	    //
	    // Implement the Shortest Path between two vertices by using
	    // the following Gremlin code:
	    //   results = []; v_src.as('x').out.out.in.has("type", "switch").dedup().loop('x'){it.object.dpid != v_dest.dpid & it.loops < 10}.path().fill(results)
	    //

	    String gremlin = "v_src.as(\"x\").out.out.in.has(\"type\", \"switch\").dedup().loop(\"x\"){it.object.dpid != v_dest.dpid & it.loops < 10}.path().fill(results)";

	    // Get the source vertex
	    Iterator<Vertex> iter = titanGraph.getVertices("dpid", dpid_src).iterator();
	    if (! iter.hasNext())
		return;			// Source vertex not found
	    Vertex v_src = iter.next();

	    // Get the destination vertex
	    iter = titanGraph.getVertices("dpid", dpid_dest).iterator();
	    if (! iter.hasNext())
		return;			// Destination vertex not found
	    Vertex v_dest = iter.next();
	
	    //
	    // Implement the Gremlin script and run it
	    //
	    ScriptEngine engine = new GremlinGroovyScriptEngine();

	    ArrayList<ArrayList<Vertex>> results = new ArrayList<ArrayList<Vertex>>();
	    engine.getBindings(ScriptContext.ENGINE_SCOPE).put("g", titanGraph);
	    engine.getBindings(ScriptContext.ENGINE_SCOPE).put("v_src", v_src);
	    engine.getBindings(ScriptContext.ENGINE_SCOPE).put("v_dest", v_dest);
	    engine.getBindings(ScriptContext.ENGINE_SCOPE).put("results", results);

	    try {
		engine.eval(gremlin);
	    } catch (ScriptException e) {
		System.err.println("Caught ScriptException running Gremlin script: " + e.getMessage());
		return;
	    }

	    //
	    // Extract the result and compose it into a string
	    //
	    String results_str = "";
	    // System.out.println("BEGIN " + results.size());
	    for (ArrayList<Vertex> lv : results) {
		// System.out.println(lv);
		for (Vertex v: lv) {
		    // System.out.println(v);
		    String type = v.getProperty("type").toString();
		    results_str += "[type: " + type;
		    // System.out.println("type: " + type);
		    if (type.equals("port")) {
			String number = v.getProperty("number").toString();
			// System.out.println("number: " + number);
			results_str += " number: " + number + "]";
		    }
		    if (type.equals("switch")) {
			String dpid = v.getProperty("dpid").toString();
			// System.out.println("dpid: " + dpid);
			results_str += " dpid: " + dpid + "]";
		    }
		}
	    }
	    // System.out.println("END\n");
	    System.out.println(results_str);
	    
	    String expected_result = "[type: switch dpid: 00:00:00:00:00:00:0a:01][type: port number: 2][type: port number: 1][type: switch dpid: 00:00:00:00:00:00:0a:03][type: port number: 2][type: port number: 2][type: switch dpid: 00:00:00:00:00:00:0a:04][type: port number: 3][type: port number: 1][type: switch dpid: 00:00:00:00:00:00:0a:06]";


	    // Pipe<Vertex, Vertex> pipe = Gremlin.compile(gremlin);
	    // pipe.setStarts(new SingleIterator<Vertex>(v1));

	    //
	    // XXX: An alternative (faster?) solution that fails to compile
	    //
	    MyLoopFunction whileFunction = new MyLoopFunction(dpid_dest);
	    GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
	    // pipe.start(v_src).as("x").out().out().in().has("type", "switch").dedup().loop("x", whileFunction);

	    // Check the result
	    assertEquals(results_str, expected_result);
	}
}
