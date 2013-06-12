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
import com.tinkerpop.frames.structures.FramedVertexIterable;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class GraphDBOperation implements IDBOperation {
	private GraphDBConnection conn;
	
	public GraphDBOperation(GraphDBConnection dbConnection) {
		this.conn = dbConnection;
	}
	
	@Override
	public ISwitchObject newSwitch() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		ISwitchObject obj = fg.addVertex(null,ISwitchObject.class);
		return obj;
	}

	@Override
	public void removeSwitch(ISwitchObject sw) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		fg.removeVertex(sw.asVertex());		
	}
	
	@Override
	public ISwitchObject searchSwitch(String dpid) {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return (fg != null && fg.getVertices("dpid",dpid).iterator().hasNext()) ? 
				fg.getVertices("dpid",dpid,ISwitchObject.class).iterator().next() : null;
    			
	}

	@Override
	public IDeviceObject searchDevice(String macAddr) {
		// TODO Auto-generated method stub
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		return (fg != null && fg.getVertices("dl_address",macAddr).iterator().hasNext()) ? fg.getVertices("dl_address",macAddr,
    			IDeviceObject.class).iterator().next() : null;
    			
	}

	@Override
	public IPortObject searchPort(String dpid, short number) {
		ISwitchObject sw = searchSwitch(dpid);
		if (sw != null) {
			
			IPortObject port = null;
			
			// Requires Frames 2.3.0
			
			try {
				port = sw.getPort(number);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return port;
		}
		
//		if (sw != null) {
//			GremlinPipeline<Vertex, IPortObject> pipe = new GremlinPipeline<Vertex, IPortObject>();
//			pipe.start(sw.asVertex());
//			pipe.out("on").has("number", number);
//			FramedVertexIterable<IPortObject> r = new FramedVertexIterable<IPortObject>(conn.getFramedGraph(), (Iterable) pipe, IPortObject.class);
//			return r != null && r.iterator().hasNext() ? r.iterator().next() : null;
//		}
		return null;
	}

	@Override
	public IPortObject newPort() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IPortObject obj = fg.addVertex(null,IPortObject.class);
		return obj;
	}
	
	@Override
	public IDeviceObject newDevice() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IDeviceObject obj = fg.addVertex(null,IDeviceObject.class);
		return obj;
	}
	
	@Override
	public void removePort(IPortObject port) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
//		EventGraph<TitanGraph> eg = conn.getEventGraph();
		if (fg != null) fg.removeVertex(port.asVertex());		
	}

	@Override
	public void removeDevice(IDeviceObject dev) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		if (fg != null) fg.removeVertex(dev.asVertex());		
	}

	@Override
	public Iterable<IDeviceObject> getDevices() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		return fg != null ? fg.getVertices("type","device",IDeviceObject.class) : null;
	}

	@Override
	public IFlowPath searchFlowPath(FlowId flowId) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("flow_id", flowId.toString()).iterator().hasNext() ? 
		    fg.getVertices("flow_id", flowId.toString(),
				   IFlowPath.class).iterator().next() : null;
	}

	@Override
	public IFlowPath newFlowPath() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IFlowPath flowPath = fg.addVertex(null, IFlowPath.class);
		return flowPath;
	}

	@Override
	public void removeFlowPath(IFlowPath flowPath) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		fg.removeVertex(flowPath.asVertex());
	}

	@Override
	public IFlowPath getFlowPathByFlowEntry(IFlowEntry flowEntry) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		GremlinPipeline<Vertex, IFlowPath> pipe = new GremlinPipeline<Vertex, IFlowPath>();
		pipe.start(flowEntry.asVertex());
		pipe.out("flow");
		FramedVertexIterable<IFlowPath> r = new FramedVertexIterable(conn.getFramedGraph(), (Iterable) pipe, IFlowPath.class);
		return r.iterator().hasNext() ? r.iterator().next() : null;
	}

	@Override
    public Iterable<IFlowPath> getAllFlowPaths() {
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
	public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("flow_entry_id", flowEntryId.toString()).iterator().hasNext() ? 
		    fg.getVertices("flow_entry_id", flowEntryId.toString(),
				   IFlowEntry.class).iterator().next() : null;
	}

	@Override
	public IFlowEntry newFlowEntry() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IFlowEntry flowEntry = fg.addVertex(null, IFlowEntry.class);
		return flowEntry;
	}

	@Override
	public void removeFlowEntry(IFlowEntry flowEntry) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		fg.removeVertex(flowEntry.asVertex());
	}

	@Override
        public Iterable<IFlowEntry> getAllFlowEntries() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		
		return fg.getVertices("type", "flow_entry", IFlowEntry.class);
	}
	
	@Override
	public Iterable<IFlowEntry> getAllSwitchNotUpdatedFlowEntries() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		//TODO: Should use an enum for flow_switch_state
		return fg.getVertices("switch_state", "FE_SWITCH_NOT_UPDATED", IFlowEntry.class);
	}

	@Override
	public Iterable<ISwitchObject> getActiveSwitches() {
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
	public Iterable<ISwitchObject> getAllSwitches() {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();
		Iterable<ISwitchObject> switches =  fg.getVertices("type","switch",ISwitchObject.class);
		return switches;
	}

	@Override
	public Iterable<ISwitchObject> getInactiveSwitches() {
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
	public ISwitchObject searchActiveSwitch(String dpid) {

        ISwitchObject sw = searchSwitch(dpid);
        if ((sw != null) &&
            sw.getState().equals(SwitchState.ACTIVE.toString())) {
            return sw;
        }
        return null;
	}
}
