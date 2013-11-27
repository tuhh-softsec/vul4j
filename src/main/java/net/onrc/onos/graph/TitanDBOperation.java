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

    /**
     * Search and get a switch object with DPID.
     *
     * @param dpid DPID of the switch
     */
    @Override
    public ISwitchObject searchSwitch(String dpid) {
        final FramedGraph<TitanGraph> fg = conn.getFramedGraph();

        return searchSwitch(dpid, fg);
    }
    
    @Override
    public Iterable<ISwitchObject> getActiveSwitches() {
        final FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        
        return getActiveSwitches(fg);
    }

    @Override
    public IPortObject searchPort(String dpid, Short number) {
        final FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        return searchPort(dpid, number, fg);
    }


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
	
	/**
         * Create and return a flow path object.
         */
        public IFlowPath newFlowPath() {
                FramedGraph<TitanGraph> fg = conn.getFramedGraph();
                IFlowPath flowPath = fg.addVertex(null, IFlowPath.class);
                if (flowPath != null) flowPath.setType("flow");
                return flowPath;
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
    public IFlowEntry newFlowEntry() {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        return newFlowEntry(fg);
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
    public IDBConnection getDBConnection() {
	System.out.println("TitangetDBConnection");
        return conn;
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

    /**
     * Create a port having specified port number.
     *
     * @param portNumber port number
     */
    @Deprecated
    public IPortObject newPort(Short portNumber) {
        FramedGraph<TitanGraph> fg = conn.getFramedGraph();
        IPortObject obj = fg.addVertex(null, IPortObject.class);
        if (obj != null) {
            obj.setType("port");
            obj.setNumber(portNumber);
        }
        return obj;
    }
}