package net.onrc.onos.intent;

import java.util.LinkedList;
import java.util.List;

import net.onrc.onos.ofcontroller.networkgraph.Link;
import net.onrc.onos.ofcontroller.networkgraph.LinkEvent;
import net.onrc.onos.ofcontroller.networkgraph.NetworkGraph;
import net.onrc.onos.ofcontroller.networkgraph.Path;
import net.onrc.onos.ofcontroller.networkgraph.Port;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class PathIntent extends Intent {
	protected List<LinkEvent> path;
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
		this.path = new LinkedList<LinkEvent>();
		for (Link link: path) {
			this.path.add(new LinkEvent(
					link.getSourceSwitch().getDpid(),
					link.getSourcePort().getNumber(),
					link.getDestinationSwitch().getDpid(),
					link.getDestinationPort().getNumber()));
		}
		this.bandwidth = bandwidth;
		this.parentIntent = parentIntent;
	}

	public double getBandwidth() {
		return bandwidth;
	}

	public List<LinkEvent> getPathByLinkEvent() {
		return path;
	}

	/**
	 * Get Path object.
	 * @param graph
	 * @return path object. If there is no path in the specified graph, returns null.
	 */
	public Path getPath(NetworkGraph graph) {
		Path pathObj = new Path();
		for (LinkEvent linkEvent: path) {
			Link link = linkEvent.getLink(graph);
			if (link == null) return null;
			pathObj.add(link);
		}
		return pathObj;
	}

	public Intent getParentIntent() {
		return parentIntent;
	}
}
