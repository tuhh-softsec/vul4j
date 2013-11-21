package net.onrc.onos.ofcontroller.flowprogrammer;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * @author bocon
 *
 */
public interface IFlowSyncService extends IFloodlightService {
    public void synchronize(IOFSwitch sw);
}
