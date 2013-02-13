package net.floodlightcontroller.routing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.floodlightcontroller.core.internal.SwitchStorageImpl;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.floodlightcontroller.topology.NodePortTuple;

import org.openflow.util.HexString;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;

public class TopoRouteService implements ITopoRouteService {

	ThreadLocal<SwitchStorageImpl> store = new ThreadLocal<SwitchStorageImpl>() {
	    @Override
	    protected SwitchStorageImpl initialValue() {
		SwitchStorageImpl swStore = new SwitchStorageImpl();
		// NOTE: This is the file path from global properties
		swStore.init("/tmp/cassandra.titan");
		return swStore;
	    }
	};

	SwitchStorageImpl swStore = store.get();

	@Override
	public List<NodePortTuple> GetShortestPath(NodePortTuple src,
			NodePortTuple dest) {
	    List<NodePortTuple> result_list = new ArrayList<NodePortTuple>();

	    TitanGraph titanGraph = swStore.graph;

	    String dpid_src = HexString.toHexString(src.getNodeId());
	    String dpid_dest = HexString.toHexString(dest.getNodeId());

	    //
	    // Implement the Shortest Path between two vertices by using
	    // the following Gremlin CLI code:
	    //   v_src.as("x").out("on").out("link").in("on").dedup().loop("x"){it.object.dpid != v_dest.dpid}.path(){it.dpid}{it.number}{it.number}
	    // The equivalent code used here is:
	    //   results = []; v_src.as("x").out("on").out("link").in("on").dedup().loop("x"){it.object.dpid != v_dest.dpid}.path().fill(results)
	    //

	    String gremlin = "v_src.as(\"x\").out(\"on\").out(\"link\").in(\"on\").dedup().loop(\"x\"){it.object.dpid != v_dest.dpid}.path().fill(results)";

	    // Get the source vertex
	    Iterator<Vertex> iter = titanGraph.getVertices("dpid", dpid_src).iterator();
	    if (! iter.hasNext())
		return null;		// Source vertex not found
	    Vertex v_src = iter.next();

	    // Get the destination vertex
	    iter = titanGraph.getVertices("dpid", dpid_dest).iterator();
	    if (! iter.hasNext())
		return null;		// Destination vertex not found
	    Vertex v_dest = iter.next();

	    //
	    // Test whether we are computing a path from/to the same DPID.
	    // If "yes", then just list the "src" and "dest" in the return
	    // result.
	    //
	    if (dpid_src.equals(dpid_dest)) {
		result_list.add(new NodePortTuple(src));
		result_list.add(new NodePortTuple(dest));
		return result_list;
	    }

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
		return null;
	    }

	    //
	    // Loop through the result and return the list
	    // of <dpid, port> tuples.
	    //
	    long nodeId = 0;
	    short portId = 0;
	    for (ArrayList<Vertex> lv : results) {
		int idx = 0;
		for (Vertex v: lv) {
		    String type = v.getProperty("type").toString();
		    System.out.println("type: " + type);
		    if (type.equals("port")) {
                        String number = v.getProperty("number").toString();
                        System.out.println("number: " + number);

			Object obj = v.getProperty("number");
			// String class_str = obj.getClass().toString();
			if (obj instanceof Short) {
			    portId = (Short)obj;
			} else if (obj instanceof Integer) {
			    Integer int_nodeId = (Integer)obj;
			    portId = int_nodeId.shortValue();
			    // int int_nodeId = (Integer)obj;
			    // portId = (short)int_nodeId.;
			}
		    } else if (type.equals("switch")) {
			String dpid = v.getProperty("dpid").toString();
			nodeId = HexString.toLong(dpid);

                        System.out.println("dpid: " + dpid);
		    }
		    if (idx == 0) {
			idx++;
			continue;
		    }
		    int mod = (idx - 1) % 3;
		    if ((mod == 0) || (mod == 2))  {
			result_list.add(new NodePortTuple(nodeId, portId));
		    }
		    idx++;
		}
	    }
	    if (result_list.size() > 0)
		return result_list;

	    return null;
	}

	@Override
	public Boolean RouteExists(NodePortTuple src, NodePortTuple dest) {
		List<NodePortTuple> route = GetShortestPath(src, dest);
		if (route != null)
		    return true;
		return false;
	}
}
