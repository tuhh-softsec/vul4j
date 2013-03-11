package net.floodlightcontroller.routing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.floodlightcontroller.core.internal.SwitchStorageImpl;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.core.INetMapTopologyService.ITopoRouteService;
import net.floodlightcontroller.util.DataPath;
import net.floodlightcontroller.util.Dpid;
import net.floodlightcontroller.util.FlowEntry;
import net.floodlightcontroller.util.Port;
import net.floodlightcontroller.util.SwitchPort;

import org.openflow.util.HexString;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;
import com.tinkerpop.blueprints.Vertex;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import com.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopoRouteService implements IFloodlightModule, ITopoRouteService {

    /** The logger. */
    private static Logger log =
	LoggerFactory.getLogger(TopoRouteService.class);

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        Collection<Class<? extends IFloodlightService>> l = 
            new ArrayList<Class<? extends IFloodlightService>>();
        l.add(ITopoRouteService.class);
        return l;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> 
			       getServiceImpls() {
        Map<Class<? extends IFloodlightService>,
        IFloodlightService> m = 
            new HashMap<Class<? extends IFloodlightService>,
                IFloodlightService>();
        m.put(ITopoRouteService.class, this);
        return m;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> 
                                                    getModuleDependencies() {
	Collection<Class<? extends IFloodlightService>> l =
	    new ArrayList<Class<? extends IFloodlightService>>();
	// TODO: Add the appropriate dependencies
	// l.add(IRestApiService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context)
	throws FloodlightModuleException {
	// TODO: Add the appropriate initialization
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
	// TODO: Add the approprate setup
    }

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
    public DataPath getShortestPath(SwitchPort src, SwitchPort dest) {
	DataPath result_data_path = new DataPath();

	// Initialize the source and destination in the data path to return
	result_data_path.setSrcPort(src);
	result_data_path.setDstPort(dest);

	TitanGraph titanGraph = swStore.graph;

	String dpid_src = src.dpid().toString();
	String dpid_dest = dest.dpid().toString();

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
	if (! iter.hasNext()) {
	    // titanGraph.stopTransaction(Conclusion.SUCCESS);
	    return null;		// Source vertex not found
	}
	Vertex v_src = iter.next();

	// Get the destination vertex
	iter = titanGraph.getVertices("dpid", dpid_dest).iterator();
	if (! iter.hasNext()) {
	    // titanGraph.stopTransaction(Conclusion.SUCCESS);
	    return null;		// Destination vertex not found
	}
	Vertex v_dest = iter.next();

	//
	// Test whether we are computing a path from/to the same DPID.
	// If "yes", then just add a single flow entry in the return result.
	// NOTE: The return value will change in the future to return
	// a single hop/entry instead of two. Currently, we need
	// both entries to capture the source and destination ports.
	//
	if (dpid_src.equals(dpid_dest)) {
	    FlowEntry flowEntry = new FlowEntry();
	    flowEntry.setDpid(src.dpid());
	    flowEntry.setInPort(src.port());
	    flowEntry.setOutPort(dest.port());
	    result_data_path.flowEntries().add(flowEntry);
	    // titanGraph.stopTransaction(Conclusion.SUCCESS);
	    return result_data_path;
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
	    // titanGraph.stopTransaction(Conclusion.SUCCESS);
	    return null;
	}

	//
	// Loop through the result and collect the list
	// of <dpid, port> tuples.
	//
	long nodeId = 0;
	short portId = 0;
	Port inPort = new Port(src.port().value());
	Port outPort = new Port();
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
		idx++;
		if (idx == 1) {
		    continue;
		}
		int mod = idx % 3;
		if (mod == 0) {
		    // Setup the incoming port
		    inPort = new Port(portId);
		    continue;
		}
		if (mod == 2) {
		    // Setup the outgoing port, and add the Flow Entry
		    outPort = new Port(portId);

		    FlowEntry flowEntry = new FlowEntry();
		    flowEntry.setDpid(new Dpid(nodeId));
		    flowEntry.setInPort(inPort);
		    flowEntry.setOutPort(outPort);
		    result_data_path.flowEntries().add(flowEntry);
		    continue;
		}
	    }

	    if (idx > 0) {
		// Add the last Flow Entry
		FlowEntry flowEntry = new FlowEntry();
		flowEntry.setDpid(new Dpid(nodeId));
		flowEntry.setInPort(inPort);
		flowEntry.setOutPort(dest.port());
		result_data_path.flowEntries().add(flowEntry);
	    }
	}
	// titanGraph.stopTransaction(Conclusion.SUCCESS);
	if (result_data_path.flowEntries().size() > 0)
	    return result_data_path;

	return null;
    }

    @Override
    public Boolean routeExists(SwitchPort src, SwitchPort dest) {
	DataPath dataPath = getShortestPath(src, dest);
	if (dataPath != null)
	    return true;
	return false;
    }
}
