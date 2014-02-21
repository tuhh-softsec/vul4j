package net.onrc.onos.intent;

import net.onrc.onos.ofcontroller.networkgraph.Path;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntent extends Intent {
	protected Path path;
	protected double bandwidth;
	protected Intent parentIntent;

	public static String createFirstId(String parentId) {
		return String.format("pi%s___0", parentId);
	}

	public static String createNextId(String currentId) {
		String parts[] = currentId.split("___");
		return String.format("%s___%d", parts[0], Long.valueOf(parts[1])+1);
	}

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
		this.path = path;
		this.bandwidth = bandwidth;
		this.parentIntent = parentIntent;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public Path getPath() {
		return path;
	}

	public Intent getParentIntent() {
		return parentIntent;
	}
}
