package net.onrc.onos.ofcontroller.core.internal;

import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;

/**
 * Seam that allows me to set up a testable instance of SwitchStorageImpl that 
 * writes to a file database rather than a Cassandra cluster. 
 * It seems the init() API on SwitchStorageImpl might change so I won't rely
 * on it yet.
 * 
 * @author jono
 *
 */

public class TestableSwitchStorageImpl extends SwitchStorageImpl {
	
	public TestableSwitchStorageImpl(){
	}
	
	@Override
	public void init(String conf){
        
		super.init(conf);
		
	}
}
