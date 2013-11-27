/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraph;
import com.tinkerpop.frames.FramedGraph;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.ISwitchObject;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author nickkaranatsios
 */
public class RamCloudDBOperation extends DBOperation {

    public RamCloudDBOperation() {
    }

    @Override
    public ISwitchObject searchSwitch(String dpid) {
        final FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();

        return searchSwitch(dpid, fg);
    }

    @Override
    public Iterable<ISwitchObject> getActiveSwitches() {
        final FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        
        return getActiveSwitches(fg);
    }

    @Override
    public Iterable<ISwitchObject> getAllSwitches() {
        return getAllSwitches(conn.getFramedGraph());
    }

    @Override
    public IPortObject searchPort(String dpid, Short number) {
        final FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return searchPort(dpid, number, fg);
    }

    @Override
    public void removePort(INetMapTopologyObjects.IPortObject port) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        if (fg != null) {
            fg.removeVertex(port.asVertex());
        }
    }

    @Override
    public IDeviceObject searchDevice(String macAddr) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return searchDevice(macAddr, fg);
    }

    @Override
    public Iterable<IDeviceObject> getDevices() {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return fg != null ? fg.getVertices("type", "device", IDeviceObject.class) : null;
    }

    @Override
    public void removeDevice(IDeviceObject dev) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        if (fg != null) {
            fg.removeVertex(dev.asVertex());
        }
    }


    @Override
    public IFlowPath searchFlowPath(FlowId flowId) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return searchFlowPath(flowId, fg);
    }

    @Override
    public Iterable<IFlowPath> getAllFlowPaths() {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return getAllFlowPaths(fg);
    }

    @Override
    public void removeFlowPath(IFlowPath flowPath) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        fg.removeVertex(flowPath.asVertex());
    }

    @Override
    public IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();

        return fg.getVertices("flow_entry_id", flowEntryId.toString()).iterator().hasNext()
                ? fg.getVertices("flow_entry_id", flowEntryId.toString(),
                IFlowEntry.class).iterator().next() : null;
    }

    @Override
    public Iterable<IFlowEntry> getAllFlowEntries() {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();

        return fg.getVertices("type", "flow_entry", IFlowEntry.class);
    }

    @Override
    public void removeFlowEntry(IFlowEntry flowEntry) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        fg.removeVertex(flowEntry.asVertex());
    }

    @Override
    public IDBConnection getDBConnection() {
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
    
    @Override
    public ISwitchObject newSwitch(final String dpid) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
	return super.newSwitch(dpid, fg);
    }
    
    @Override
    public IDeviceObject newDevice() {
	FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
	return super.newDevice(fg);
    }
    
    @Override
    public IPortObject newPort(String dpid, Short portNum) {
	FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
	return super.newPort(dpid, portNum, fg);
    }
}
