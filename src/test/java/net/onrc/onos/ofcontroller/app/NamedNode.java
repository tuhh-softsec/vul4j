package net.onrc.onos.ofcontroller.app;

public class NamedNode {
	protected String name;
	protected NetworkGraph graph;

	public NamedNode(NetworkGraph graph, String name) {
		this.graph = graph;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public NetworkGraph getNetworkGraph() {
		return graph;
	}
}
