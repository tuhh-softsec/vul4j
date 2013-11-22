/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.onrc.onos.graph;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.impls.ramcloud.RamCloudGraph;
import com.tinkerpop.frames.FramedGraph;
import java.io.File;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IDeviceObject;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowEntry;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IFlowPath;
import net.onrc.onos.ofcontroller.core.INetMapTopologyObjects.IPortObject;
import net.onrc.onos.ofcontroller.util.FlowEntryId;
import net.onrc.onos.ofcontroller.util.FlowId;
import org.apache.commons.configuration.Configuration;

/**
 *
 * @author nickkaranatsios
 */
public class RamCloudDBOperation extends DBOperation {

    public RamCloudDBOperation() {
        //Configuration configuration= getConfiguration(new File(dbConfigFile));
        //final String coordinatorURL = configuration.getProperty("connect.coordinator");
    }

    @Override
    public INetMapTopologyObjects.ISwitchObject searchSwitch(String dpid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public INetMapTopologyObjects.ISwitchObject searchActiveSwitch(String dpid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<INetMapTopologyObjects.ISwitchObject> getActiveSwitches() {
        return getActiveSwitches(conn.getFramedGraph());
    }

    @Override
    public Iterable<INetMapTopologyObjects.ISwitchObject> getAllSwitches() {
        return getAllSwitches(conn.getFramedGraph());
    }

    @Override
    public Iterable<INetMapTopologyObjects.ISwitchObject> getInactiveSwitches() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<INetMapTopologyObjects.IFlowEntry> getAllSwitchNotUpdatedFlowEntries() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeSwitch(INetMapTopologyObjects.ISwitchObject sw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public INetMapTopologyObjects.IPortObject newPort(Short portNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public INetMapTopologyObjects.IPortObject newPort(String dpid, Short portNum) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public INetMapTopologyObjects.IFlowPath searchFlowPath(FlowId flowId) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return searchFlowPath(flowId, fg);
    }

    @Override
    public Iterable<IFlowPath> getAllFlowPaths() {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return getAllFlowPaths(fg);
    }

    @Override
    public void removeFlowPath(INetMapTopologyObjects.IFlowPath flowPath) {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        fg.removeVertex(flowPath.asVertex());
    }

    @Override
    public IFlowEntry newFlowEntry() {
        FramedGraph<RamCloudGraph> fg = conn.getFramedGraph();
        return newFlowEntry(fg);
    }

    @Override
    public INetMapTopologyObjects.IFlowEntry searchFlowEntry(FlowEntryId flowEntryId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Iterable<INetMapTopologyObjects.IFlowEntry> getAllFlowEntries() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeFlowEntry(INetMapTopologyObjects.IFlowEntry flowEntry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public IDBConnection getDBConnection() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void commit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void rollback() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
