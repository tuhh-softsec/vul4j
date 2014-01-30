package net.onrc.onos.ofcontroller.app;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Base class for Path representation
 * This code is valid for the architectural study purpose only.
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class Path extends LinkedList<Link> {
	private static final long serialVersionUID = 7127274096495173415L;
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Iterator<Link> i = this.iterator();
		while (i.hasNext()) {
			builder.append(i.next().toString());
			if (i.hasNext())
				builder.append(", ");
		}
		return builder.toString();
	}
}
