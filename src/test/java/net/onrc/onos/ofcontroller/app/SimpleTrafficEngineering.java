package net.onrc.onos.ofcontroller.app;

/**
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class SimpleTrafficEngineering implements BaseApplication {
	NetworkGraph graph;
	
	/**
	 * Instantiate SimpleTrafficEngineering application
	 * 
	 * 1. store NetworkGraph as a cache
	 * 
	 * @param graph
	 */
	public SimpleTrafficEngineering(NetworkGraph graph) {
		this.graph = graph;
	}

	/**
	 * Allocate specified bandwidth between specified switch ports
	 * 
	 * @param srcPort
	 * @param dstPort
	 * @param bandWidth
	 * @return
	 */
	public ConstrainedFlow allocate(SwitchPort srcPort, SwitchPort dstPort, double bandWidth) {
		ConstrainedFlow flow = new ConstrainedFlow(this.graph, null, srcPort, dstPort, bandWidth);

		// 1. store Flow object to NetworkGraph
		if (!graph.addFlow(flow)) {
			return flow;
		}

		// 2. calculate path from srcPort to dstPort under condition of bandWidth
		if (!flow.calcPath()) {
			return flow;
		}
		
		// debug (show path)
		System.out.println("path was calculated:");
		System.out.println("[Flow] " + flow.toString());

		// 3. allocate bandwidth in NetworkGraph
		if (!flow.installPath()) {
			return flow;
		}

		// debug (show path)
		System.out.println("bandwidth was allocated.");
		System.out.println("[Flow] " + flow.toString());

		// (then, flow entries are created and installed from stored path information in the Flow object by another processes)
		return flow;
	}

	/**
	 * Release specified Flow object
	 * 
	 * @param flow
	 */
	public void release(ConstrainedFlow flow) {
		// 1. release bandwidth (remove property of links) in NetworkGraph
		flow.uninstallPath();

		// debug (show path)
		System.out.println("bandwidth was released.");
		System.out.println("[Flow] " + flow.toString());
		
		// 2. deactivate Flow object

		// (then, flow entries are removed by another processes)
		// (retain flow object in NetworkGraph as a removed flow object)
	}
}
