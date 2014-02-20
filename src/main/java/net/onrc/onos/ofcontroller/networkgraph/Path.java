package net.onrc.onos.ofcontroller.networkgraph;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Base class for Path representation
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class Path extends LinkedList<LinkEvent> {
	private static final long serialVersionUID = 7127274096495173415L;

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Iterator<LinkEvent> i = this.iterator();
		while (i.hasNext()) {
			builder.append(i.next().toString());
			if (i.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
}
