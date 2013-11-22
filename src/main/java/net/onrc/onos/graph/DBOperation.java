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
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.core.ISwitchStorage;
import net.onrc.onos.ofcontroller.util.FlowId;

/**
 *
 * @author nickkaranatsios
 */
public abstract class DBOperation implements IDBOperation {

    protected DBConnection conn;

    @Override
    public ISwitchObject searchActiveSwitch(String dpid) {
        ISwitchObject sw = searchSwitch(dpid);
        if ((sw != null)
                && sw.getState().equals(ISwitchStorage.SwitchState.ACTIVE.toString())) {
            return sw;
        }
        return null;
    }

    @Override
    public ISwitchObject newSwitch(final String dpid) {
        ISwitchObject obj = (ISwitchObject) conn.getFramedGraph().addVertex(null, ISwitchObject.class);
        if (obj != null) {
            obj.setType("switch");
            obj.setDPID(dpid);
        }
        return obj;
    }

    @Override
    public Iterable<ISwitchObject> getAllSwitches() {
        Iterable<ISwitchObject> switches = conn.getFramedGraph().getVertices("type", "switch", ISwitchObject.class);
        return switches;
    }

    @Override
    public Iterable<ISwitchObject> getInactiveSwitches() {
        Iterable<ISwitchObject> switches = conn.getFramedGraph().getVertices("type", "switch", ISwitchObject.class);
        List<ISwitchObject> inactiveSwitches = new ArrayList<ISwitchObject>();

        for (ISwitchObject sw : switches) {
            if (sw.getState().equals(ISwitchStorage.SwitchState.INACTIVE.toString())) {
                inactiveSwitches.add(sw);
            }
        }
        return inactiveSwitches;
    }

    @Override
    public Iterable<INetMapTopologyObjects.IFlowEntry> getAllSwitchNotUpdatedFlowEntries() {
        //TODO: Should use an enum for flow_switch_state
        return conn.getFramedGraph().getVertices("switch_state", "FE_SWITCH_NOT_UPDATED", INetMapTopologyObjects.IFlowEntry.class);

    }

    @Override
    public void removeSwitch(ISwitchObject sw) {
        conn.getFramedGraph().removeVertex(sw.asVertex());
    }

    @Override
    public IPortObject newPort(String dpid, Short portNum) {
        IPortObject obj = (IPortObject) conn.getFramedGraph().addVertex(null, IPortObject.class);
        if (obj != null) {
            obj.setType("port");
            String id = dpid + portNum.toString();
            obj.setPortId(id);
            obj.setNumber(portNum);
        }
        return obj;
    }

    public IPortObject searchPort(String dpid, Short number, final FramedGraph fg) {
        String id = dpid + number.toString();
        return (fg != null && fg.getVertices("port_id", id).iterator().hasNext())
                ? (IPortObject) fg.getVertices("port_id", id, IPortObject.class).iterator().next() : null;

    }

    @Override
    public IDeviceObject newDevice() {
        IDeviceObject obj = (IDeviceObject) conn.getFramedGraph().addVertex(null, IDeviceObject.class);
        if (obj != null) {
            obj.setType("device");
        }
        return obj;
    }

    @Override
    public IFlowPath newFlowPath() {
        IFlowPath flowPath = (IFlowPath)conn.getFramedGraph().addVertex(null, IFlowPath.class);
        if (flowPath != null) {
            flowPath.setType("flow");
        }
        return flowPath;
    }
    
    @Override
    public IFlowPath getFlowPathByFlowEntry(INetMapTopologyObjects.IFlowEntry flowEntry) {
        GremlinPipeline<Vertex, IFlowPath> pipe = new GremlinPipeline<Vertex, IFlowPath>();
        pipe.start(flowEntry.asVertex());
        pipe.out("flow");
        FramedVertexIterable<IFlowPath> r = new FramedVertexIterable(conn.getFramedGraph(), (Iterable) pipe, IFlowPath.class);
        return r.iterator().hasNext() ? r.iterator().next() : null;
    }


    
    protected ISwitchObject searchSwitch(final String dpid, final FramedGraph fg) {
        return (fg != null && fg.getVertices("dpid", dpid).iterator().hasNext())
                ? (ISwitchObject) (fg.getVertices("dpid", dpid, ISwitchObject.class).iterator().next()) : null;
    }

    protected Iterable<ISwitchObject> getActiveSwitches(final FramedGraph fg) {
        Iterable<ISwitchObject> switches = fg.getVertices("type", "switch", ISwitchObject.class);
        List<ISwitchObject> activeSwitches = new ArrayList<ISwitchObject>();

        for (ISwitchObject sw : switches) {
            if (sw.getState().equals(ISwitchStorage.SwitchState.ACTIVE.toString())) {
                activeSwitches.add(sw);
            }
        }
        return activeSwitches;
    }

    protected Iterable<ISwitchObject> getAllSwitches(final FramedGraph fg) {
        Iterable<ISwitchObject> switches = fg.getVertices("type", "switch", ISwitchObject.class);
        return switches;
    }

    protected IDeviceObject searchDevice(String macAddr, final FramedGraph fg) {
        return (fg != null && fg.getVertices("dl_addr", macAddr).iterator().hasNext())
                ? (IDeviceObject) fg.getVertices("dl_addr", macAddr, IDeviceObject.class).iterator().next() : null;

    }
    
    protected IFlowPath searchFlowPath(final FlowId flowId, final FramedGraph fg) {
        return fg.getVertices("flow_id", flowId.toString()).iterator().hasNext()
                ? (IFlowPath) fg.getVertices("flow_id", flowId.toString(),
                IFlowPath.class).iterator().next() : null;
    }
    
    protected Iterable<IFlowPath> getAllFlowPaths(final FramedGraph fg) {
        Iterable<IFlowPath> flowPaths = fg.getVertices("type", "flow", IFlowPath.class);

        List<IFlowPath> nonNullFlows = new ArrayList<IFlowPath>();

        for (IFlowPath fp : flowPaths) {
            if (fp.getFlowId() != null) {
                nonNullFlows.add(fp);
            }
        }
        return nonNullFlows;
    }
    
    protected IFlowEntry newFlowEntry(final FramedGraph fg) {
        IFlowEntry flowEntry = (IFlowEntry) fg.addVertex(null, IFlowEntry.class);
        if (flowEntry != null) {
            flowEntry.setType("flow_entry");
        }
        return flowEntry;
    }

}
