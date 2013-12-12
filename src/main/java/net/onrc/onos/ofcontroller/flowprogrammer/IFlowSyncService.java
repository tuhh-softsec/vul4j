package net.onrc.onos.ofcontroller.flowprogrammer;

import java.util.concurrent.Future;

import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.IFloodlightService;

/**
 * FlowSyncService is a service to synchronize GraphDB and switch's flow table.
 * FlowSyncService offers APIs to trigger and interrupt synchronization explicitly.
 * @author Brian
 *
 */
public interface IFlowSyncService extends IFloodlightService {
    public Future<SyncResult> synchronize(IOFSwitch sw);
    
    public void interrupt(IOFSwitch sw);
    
    public class SyncResult {
    	public final int flowAdded;
    	public final int flowRemoved;
    	public final int flowSkipped;
    	
    	public SyncResult(int added, int removed, int skipped) {
    		flowAdded = added;
    		flowRemoved = removed;
    		flowSkipped = skipped;
    	}
    }
}
