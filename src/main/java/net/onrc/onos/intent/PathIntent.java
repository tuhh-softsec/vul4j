package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.networkgraph.Path;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntent extends Intent {
	protected Path path;
	protected double bandwidth;
	protected Intent parentIntent;
	protected int id;

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
