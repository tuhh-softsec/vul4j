package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Path;
import net.onrc.onos.ofcontroller.networkgraph.Port;
import net.onrc.onos.ofcontroller.networkgraph.Switch;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntent extends Intent {
	protected long pathData[];
	protected double bandwidth;
	protected Intent parentIntent;

	/**
	 * Default constructor for Kryo deserialization
	 */
	protected PathIntent() {
	}

	/**
	 * 
	 * @param graph
	 * @param path
	 * @param bandwidth bandwidth which should be allocated for the path.
	 * If 0, no intent for bandwidth allocation (best effort).
	 * @param parentIntent parent intent. If null, this is root intent.
	 * @param id
	 */
	public PathIntent(String id, Path path, double bandwidth, Intent parentIntent) {
		super(id);
		pathData = new long[path.size() * 4];
		for (int i=0; i<path.size(); i++) {
			Link link = path.get(i);
			this.pathData[i*4] = link.getSourceSwitch().getDpid();
			this.pathData[i*4+1] = link.getSourcePort().getNumber();
			this.pathData[i*4+2] = link.getDestinationSwitch().getDpid();
			this.pathData[i*4+3] = link.getDestinationPort().getNumber();
		}
		this.bandwidth = bandwidth;
		this.parentIntent = parentIntent;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public long[] getPathData() {
		return pathData;
	}

	/**
	 * Get Path object.
	 * @param graph
	 * @return path object. If there is no path in the specified graph, returns null.
	 */
	public Path getPath(NetworkGraph graph) {
		Path path = new Path();
		Switch srcSwitch;
		Port srcPort;
		Link link;
		for (int i=0; i<pathData.length; i+=4) {
			if ((srcSwitch = graph.getSwitch(pathData[i])) == null) return null;
			if ((srcPort = srcSwitch.getPort(pathData[i+1])) == null) return null;
			if ((link = srcPort.getOutgoingLink()) == null) return null;
			if (link.getDestinationSwitch().getDpid() != pathData[i+2]) return null;
			if (link.getDestinationPort().getNumber() != pathData[i+3]) return null;
			path.add(link);
		}
		return path;
	}

	public Intent getParentIntent() {
		return parentIntent;
	}
}
