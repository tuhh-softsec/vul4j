package net.onrc.onos.util;

import java.util.ArrayList;
import java.util.List;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.floodlightcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.floodlightcontroller.core.ISwitchStorage.SwitchState;
import net.floodlightcontroller.util.FlowEntryId;
import net.floodlightcontroller.util.FlowId;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedVertexIterable;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class GraphDBUtils implements IDBUtils {
	
	@Override
	public ISwitchObject newSwitch(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		ISwitchObject obj = fg.addVertex(null,ISwitchObject.class);
		return obj;
	}

	@Override
	public void removeSwitch(GraphDBConnection conn, ISwitchObject sw) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		fg.removeVertex(sw.asVertex());		
	}
	
	@Override
	public ISwitchObject searchSwitch(GraphDBConnection conn, String dpid) {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("dpid",dpid).iterator().hasNext() ? 
				fg.getVertices("dpid",dpid,ISwitchObject.class).iterator().next() : null;
    			
	}

	@Override
	public IDeviceObject searchDevice(GraphDBConnection conn, String macAddr) {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		return fg.getVertices("dl_address",macAddr).iterator().hasNext() ? fg.getVertices("dl_address",macAddr,
    			IDeviceObject.class).iterator().next() : null;
    			
	}

	@Override
	public IPortObject searchPort(GraphDBConnection conn, String dpid, short number) {
		ISwitchObject sw = searchSwitch(conn, dpid);
//		if (sw != null) {
//			
//			IPortObject port = null;
//			try {
//				port = sw.getPort(number);
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			return port;
//		}
//		return null;
		GremlinPipeline<Vertex, IPortObject> pipe = new GremlinPipeline<Vertex, IPortObject>();
		pipe.start(sw.asVertex());
	    pipe.out("on").has("number", number);
	    FramedVertexIterable<IPortObject> r = new FramedVertexIterable<IPortObject>(conn.getFramedGraph(), (Iterable) pipe, IPortObject.class);
	    return r.iterator().hasNext() ? r.iterator().next() : null;
	}

	@Override
	public IPortObject newPort(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IPortObject obj = fg.addVertex(null,IPortObject.class);
		return obj;
	}
	
	@Override
	public IDeviceObject newDevice(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IDeviceObject obj = fg.addVertex(null,IDeviceObject.class);
		return obj;
	}
	
	@Override
	public void removePort(GraphDBConnection conn, IPortObject port) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
//		EventGraph<TitanGraph> eg = conn.getEventGraph();
		fg.removeVertex(port.asVertex());		
	}

	@Override
	public void removeDevice(GraphDBConnection conn, IDeviceObject dev) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		fg.removeVertex(dev.asVertex());		
	}

	@Override
	public Iterable<IDeviceObject> getDevices(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		return fg.getVertices("type","device",IDeviceObject.class);
	}

	@Override
	public IFlowPath searchFlowPath(GraphDBConnection conn,
					FlowId flowId) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("flow_id", flowId.toString()).iterator().hasNext() ? 
		    fg.getVertices("flow_id", flowId.toString(),
				   IFlowPath.class).iterator().next() : null;
	}

	@Override
	public IFlowPath newFlowPath(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IFlowPath flowPath = fg.addVertex(null, IFlowPath.class);
		return flowPath;
	}

	@Override
	public void removeFlowPath(GraphDBConnection conn,
				   IFlowPath flowPath) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		fg.removeVertex(flowPath.asVertex());
	}

	@Override
	public IFlowPath getFlowPathByFlowEntry(GraphDBConnection conn,
						IFlowEntry flowEntry) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		GremlinPipeline<Vertex, IFlowPath> pipe = new GremlinPipeline<Vertex, IFlowPath>();
		pipe.start(flowEntry.asVertex());
		pipe.out("flow");
		FramedVertexIterable<IFlowPath> r = new FramedVertexIterable(conn.getFramedGraph(), (Iterable) pipe, IFlowPath.class);
		return r.iterator().hasNext() ? r.iterator().next() : null;
	}

	@Override
    public Iterable<IFlowPath> getAllFlowPaths(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<IFlowPath> flowPaths = fg.getVertices("type", "flow", IFlowPath.class);
		
		List<IFlowPath> nonNullFlows = new ArrayList<IFlowPath>();

		for (IFlowPath fp: flowPaths) {
			if (fp.getFlowId() != null) {
				nonNullFlows.add(fp);
			}
		}
		return nonNullFlows;
	}

	@Override
	public IFlowEntry searchFlowEntry(GraphDBConnection conn,
					  FlowEntryId flowEntryId) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("flow_entry_id", flowEntryId.toString()).iterator().hasNext() ? 
		    fg.getVertices("flow_entry_id", flowEntryId.toString(),
				   IFlowEntry.class).iterator().next() : null;
	}

	@Override
	public IFlowEntry newFlowEntry(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IFlowEntry flowEntry = fg.addVertex(null, IFlowEntry.class);
		return flowEntry;
	}

	@Override
	public void removeFlowEntry(GraphDBConnection conn,
				    IFlowEntry flowEntry) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		fg.removeVertex(flowEntry.asVertex());
	}

	@Override
        public Iterable<IFlowEntry> getAllFlowEntries(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("type", "flow_entry", IFlowEntry.class);
	}

	@Override
	public Iterable<ISwitchObject> getActiveSwitches(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		List<ISwitchObject> activeSwitches = new ArrayList<ISwitchObject>();

		for (ISwitchObject sw: switches) {
			if(sw.getState().equals(SwitchState.ACTIVE.toString())) {
				activeSwitches.add(sw);
			}
		}
		return activeSwitches;
	}

	@Override
	public Iterable<ISwitchObject> getAllSwitches(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		return switches;
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		List<ISwitchObject> inactiveSwitches = new ArrayList<ISwitchObject>();

		for (ISwitchObject sw: switches) {
			if(sw.getState().equals(SwitchState.INACTIVE.toString())) {
				inactiveSwitches.add(sw);
			}
		}
		return inactiveSwitches;
	}

	@Override
	public ISwitchObject searchActiveSwitch(GraphDBConnection conn, String dpid) {
		// TODO Auto-generated method stub
		return null;
	}
}
