package net.onrc.onos.core.registry;

import net.onrc.onos.core.registry.IControllerRegistryService.ControlChangeCallback;

import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;

public class SwitchLeadershipData {
	
	private final LeaderLatch latch;
	private final ControlChangeCallback cb;
	private final LeaderLatchListener listener;

	public SwitchLeadershipData(LeaderLatch latch, ControlChangeCallback cb,
			LeaderLatchListener listener) {
		this.latch = latch;
		this.cb = cb;
		this.listener = listener;
	}
	
	public LeaderLatch getLatch(){
		return latch;
	}
	
	public ControlChangeCallback getCallback(){
		return cb;
	}
	
	public LeaderLatchListener getListener() {
		return listener;
	}

}
