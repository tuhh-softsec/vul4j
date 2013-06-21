package net.onrc.onos.ofcontroller.routing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.branch.LoopPipe.LoopBundle;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;


/**
 * A class for testing the TopoRouteService class.
 * @see net.onrc.onos.ofcontroller.routing.TopoRouteService
 * @author Pavlin Radoslavov (pavlin@onlab.us)
 */
public class TopoRouteServiceTest {

	private TitanGraph titanGraph;
	
	@Before
	public void setUp() throws Exception {
		titanGraph = TestDatabaseManager.getTestDatabase();
		TestDatabaseManager.populateTestData(titanGraph);
	}

	@After
	public void tearDown() throws Exception {
		titanGraph.shutdown();
	}

	@Ignore @Test
	public void testUpdate() {
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
		if (! bundle.getObject().getProperty("dpid").equals(dpid)) {
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
	    // the following Gremlin CLI code:
	    //   v_src.as("x").out("on").out("link").in("on").dedup().loop("x"){it.object.dpid != v_dest.dpid}.path(){it.dpid}{it.number}{it.number}
	    // The equivalent code used here is:
	    //   results = []; v_src.as("x").out("on").out("link").in("on").dedup().loop("x"){it.object.dpid != v_dest.dpid}.path().fill(results)
	    //

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
	    // NOTE: This mechanism is slower. The code is kept here
	    // for future reference.
	    //
	    /*
	    String gremlin = "v_src.as(\"x\").out(\"on\").out(\"link\").in(\"on\").dedup().loop(\"x\"){it.object.dpid != v_dest.dpid}.path().fill(results)";

	    String gremlin_nopath = "v_src.as(\"x\").out(\"on\").out(\"link\").in(\"on\").dedup().loop(\"x\"){it.object.dpid != \"NO-SUCH-DPID\"}.path().fill(results)";

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

	    for (ArrayList<Vertex> lv : results) {
		...
	    }
	    */

	    MyLoopFunction whileFunction = new MyLoopFunction(dpid_dest);
	    GremlinPipeline<Vertex, Vertex> pipe = new GremlinPipeline<Vertex, Vertex>();
	    Collection<List> results = new ArrayList<List>();
	    GremlinPipeline<Vertex, List> path;
	    path = pipe.start(v_src).as("x").out("on").out("link").in("on").dedup().loop("x", whileFunction).path();
	    path.fill(results);

	    //
	    // Extract the result and compose it into a string
	    //
	    String results_str = "";
	    // System.out.println("BEGIN " + results.size());
	    for (List l : results) {
		for (Object o: l) {
		    Vertex v = (Vertex)(o);
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

	    //
	    // Check the result
	    //
	    String expected_result = "[type: switch dpid: 00:00:00:00:00:00:0a:01][type: port number: 2][type: port number: 1][type: switch dpid: 00:00:00:00:00:00:0a:03][type: port number: 2][type: port number: 2][type: switch dpid: 00:00:00:00:00:00:0a:04][type: port number: 3][type: port number: 1][type: switch dpid: 00:00:00:00:00:00:0a:06]";

	    assertEquals(results_str, expected_result);

	    //
	    // Test Shortest-Path computation to non-existing destination
	    //
	    results.clear();
	    MyLoopFunction noDestWhileFunction = new MyLoopFunction("NO-SUCH-DPID");
	    path = pipe.start(v_src).as("x").out("on").out("link").in("on").dedup().loop("x", noDestWhileFunction).path();
	    path.fill(results);
	    assertTrue(results.size() == 0);
	}
}
