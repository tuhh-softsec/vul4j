package net.onrc.onos.graph;

import java.util.Map;

import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thinkaurelius.titan.core.TitanEdge;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

public class LocalTopologyEventListener implements LocalGraphChangedListener {

	protected final static Logger log = LoggerFactory.getLogger(LocalTopologyEventListener.class);
	protected static DBConnection conn;

	public LocalTopologyEventListener(DBConnection conn) {
		LocalTopologyEventListener.conn = conn;
	}

	@Override
	public void edgeAdded(Edge arg0) {
		// TODO Auto-generated method stub
		// Convert this Event into NetMapEvent (LinkAdded, FlowEntryEnabled, HostAttached, PortEnabled)
	}


	@Override
	public void edgePropertyRemoved(Edge arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub
		// Currently not needed

	}

	@Override
	public void edgeRemoved(Edge e, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		// Fire NetMapEvents (LinkRemoved, FlowEntryRemoved, HostRemoved, PortRemoved)
		TitanEdge edge = (TitanEdge) e;
		log.debug("TopologyEvents: Received edge removed event: {}",edge);
		String label = edge.getLabel();
		if (label.equals("link")) {
			Vertex v = edge.getVertex(Direction.IN);
			IPortObject src_port = (IPortObject) conn.getFramedGraph().frame(v, IPortObject.class);
			v = edge.getVertex(Direction.OUT);
			IPortObject dest_port = (IPortObject) conn.getFramedGraph().frame(v, IPortObject.class);

			log.debug("TopologyEvents: link broken {}", new Object []{src_port.getSwitch().getDPID(),
																src_port.getNumber(),
																dest_port.getSwitch().getDPID(),
																dest_port.getNumber()});
			// TODO: Find the flows and add to reconcile queue
			//
			// NOTE: Old code/logic.
			//
			// IFlowService flowManager = ...
			// flowManager.reconcileFlows(src_port);
		}
	}

	@Override
	public void vertexAdded(Vertex arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexPropertyRemoved(Vertex arg0, String arg1, Object arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexRemoved(Vertex vertex, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		// Generate NetMapEvents
		String type = (String) vertex.getProperty("type");
		log.debug("TopologyEvents: Received vertex removed event: {}",vertex);
		if (type.equals("port")) {
			// port is removed...lets fire reconcile here directly for now

			IPortObject src_port = (IPortObject) conn.getFramedGraph().frame(vertex, IPortObject.class);
			log.debug("TopologyEvents: Port removed: {}:{}",src_port.getSwitch().getDPID(),src_port.getNumber());

			// NOTE: Old code/logic.
			//
			// IFlowService flowManager = ...
			// flowManager.reconcileFlows(src_port);
		}
	}


	@Override
	public void edgePropertyChanged(Edge arg0, String arg1, Object arg2,
			Object arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void vertexPropertyChanged(Vertex arg0, String arg1, Object arg2,
			Object arg3) {
		// TODO Auto-generated method stub

	}

}
