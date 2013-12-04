package net.onrc.onos.ofcontroller.flowprogrammer;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * FlowSyncService is a service to synchronize GraphDB and switch's flow table.
 * FlowSyncService offers APIs to trigger and interrupt synchronization explicitly.
 * @author Brian
 *
 */
public interface IFlowSyncService extends IFloodlightService {
    public void synchronize(IOFSwitch sw);
    
    public void interrupt(IOFSwitch sw);
}
