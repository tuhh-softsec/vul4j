package net.onrc.onos.ofcontroller.bgproute;

public class RibUpdate {
	public enum Operation {UPDATE, DELETE}; 
	
	private Operation operation;
	private Prefix prefix;
	private RibEntry ribEntry;
	
	public RibUpdate(Operation operation, Prefix prefix, RibEntry ribEntry) {
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

	public RibEntry getRibEntry() {
		return ribEntry;
	}
}
