package net.onrc.onos.ofcontroller.core.internal;

import java.util.Set;

import net.onrc.onos.ofcontroller.core.internal.SwitchStorageImpl;

import com.thinkaurelius.titan.core.TitanGraph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.TransactionalGraph.Conclusion;

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
