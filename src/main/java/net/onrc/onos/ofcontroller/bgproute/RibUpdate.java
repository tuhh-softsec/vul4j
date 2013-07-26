package net.onrc.onos.ofcontroller.bgproute;

public class RibUpdate {
	public enum Operation {UPDATE, DELETE}; 
	
	private Operation operation;
	private Prefix prefix;
	private Rib ribEntry;
	
	public RibUpdate(Operation operation, Prefix prefix, Rib ribEntry) {
		this.operation = operation;
		this.prefix = prefix;
		this.ribEntry = ribEntry;
	}

	public Operation getOperation() {
		return operation;
	}

	public Prefix getPrefix() {
		return prefix;
	}

	public Rib getRibEntry() {
		return ribEntry;
	}
}
