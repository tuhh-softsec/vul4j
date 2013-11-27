/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.FramedGraph;
import com.tinkerpop.frames.structures.FramedVertexIterable;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.*;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;

/**
 *
 * @author nickkaranatsios
 */
public class TitanDBOperation extends DBOperation {

    @Override
    public void removePort(IPortObject port) {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        if (fg != null) {
            fg.removeVertex(port.asVertex());
        }
    }

    @Override
    public IDeviceObject searchDevice(String macAddr) {
        // TODO Auto-generated method stub
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        return searchDevice(macAddr, fg);
    }

    @Override
    public Iterable<IDeviceObject> getDevices() {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        return fg != null ? fg.getVertices("type", "device", IDeviceObject.class) : null;
    }

    @Override
    public void removeDevice(IDeviceObject dev) {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        if (fg != null) {
            fg.removeVertex(dev.asVertex());
        }
    }
	
    @Override
    public IFlowPath searchFlowPath(FlowId flowId) {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        return searchFlowPath(flowId, fg);
    }


    @Override
    public Iterable<IFlowPath> getAllFlowPaths() {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        return getAllFlowPaths(fg);
    }

    @Override
    public void removeFlowPath(IFlowPath flowPath) {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        fg.removeVertex(flowPath.asVertex());
    }

    @Override
    public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();

        return fg.getVertices("flow_entry_id", flowEntryId.toString()).iterator().hasNext()
                ? fg.getVertices("flow_entry_id", flowEntryId.toString(),
                IFlowEntry.class).iterator().next() : null;
    }

    @Override
    public Iterable<IFlowEntry> getAllFlowEntries() {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();

        return fg.getVertices("type", "flow_entry", IFlowEntry.class);
    }

    @Override
    public void removeFlowEntry(IFlowEntry flowEntry) {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        fg.removeVertex(flowEntry.asVertex());
    }

    @Override
    public void commit() {
        conn.commit();
    }

    @Override
    public void rollback() {
        conn.rollback();
    }

    @Override
    public void close() {
        conn.close();
    }
}