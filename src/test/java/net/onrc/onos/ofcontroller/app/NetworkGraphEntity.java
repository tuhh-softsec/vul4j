package net.onrc.onos.ofcontroller.app;

/**
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class NetworkGraphEntity {
	protected NetworkGraph graph;

	public NetworkGraphEntity(NetworkGraph graph) {
		this.graph = graph;
	}
	
	public NetworkGraph getNetworkGraph() {
		return graph;
	}
}
