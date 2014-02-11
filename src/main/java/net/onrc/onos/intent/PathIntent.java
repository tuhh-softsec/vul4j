package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.networkgraph.Path;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntent extends Intent {
	protected Path path;
	protected Double bandwidth;
	protected Intent parentIntent;

	/**
	 * 
	 * @param graph
	 * @param path
	 * @param bandwidth bandwidth which should be allocated for the path.
	 * If null, it means no intent for bandwidth allocation (best effort).
	 * @param parentIntent parent intent. If null, it means this is root intent.
	 */
	public PathIntent(Path path, Double bandwidth, Intent parentIntent) {
		this.path = path;
		this.bandwidth = bandwidth;
		this.parentIntent = parentIntent;
	}

	public Double getBandwidth() {
		return bandwidth;
	}

	public Path getPath() {
		return path;
	}

	public Intent getParentIntent() {
		return parentIntent;
	}
}
