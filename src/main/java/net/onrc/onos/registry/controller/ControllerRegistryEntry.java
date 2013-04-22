package net.onrc.onos.registry.controller;

import org.codehaus.jackson.annotate.JsonProperty;


public class ControllerRegistryEntry implements Comparable<ControllerRegistryEntry> {

	private String controllerId;
	private int sequenceNumber;
	
	public ControllerRegistryEntry(String controllerId, int sequenceNumber) {
		this.controllerId = controllerId;
		this.sequenceNumber = sequenceNumber;
	}
	
	@JsonProperty("controllerId")
	public String getControllerId(){
		return controllerId;
	}

	@Override
	public int compareTo(ControllerRegistryEntry o) {
		return sequenceNumber - o.sequenceNumber;
		//return 0;
	}

}
