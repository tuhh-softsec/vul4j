package net.onrc.onos.registry.controller;

import net.onrc.onos.registry.controller.IControllerRegistryService.ControlChangeCallback;

import com.netflix.curator.framework.recipes.leader.LeaderLatch;

public class SwitchLeadershipData {
	
	private LeaderLatch latch;
	private ControlChangeCallback cb;

	public SwitchLeadershipData(LeaderLatch latch, ControlChangeCallback cb) {
		this.latch = latch;
		this.cb = cb;
	}
	
	public LeaderLatch getLatch(){
		return latch;
	}
	
	public ControlChangeCallback getCallback(){
		return cb;
	}

}
