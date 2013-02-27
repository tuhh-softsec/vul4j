package net.onrc.onos.util;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.FramedVertexIterable;
import com.tinkerpop.gremlin.java.GremlinPipeline;

import net.floodlightcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.IPortObject;
import net.floodlightcontroller.core.INetMapTopologyObjects.ISwitchObject;

public class GraphDBUtils implements IDBUtils {

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
		GremlinPipeline<Vertex, IPortObject> pipe = new GremlinPipeline<Vertex, IPortObject>();
		pipe.start(sw.asVertex());
	    pipe.out("on").has("number", number);
	    FramedVertexIterable<IPortObject> r = new FramedVertexIterable(conn.getFramedGraph(), pipe, IPortObject.class);
	    return r.iterator().hasNext() ? r.iterator().next() : null;		
	}

	@Override
	public IDeviceObject newDevice(GraphDBConnection conn) {
		FramedGraph<TitanGraph> fg = conn.getFramedGraph();	
		IDeviceObject obj = fg.addVertex(null,IDeviceObject.class);
		return obj;
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

}
