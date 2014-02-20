package net.onrc.onos.intent;

import java.util.LinkedList;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentOperationList extends LinkedList<IntentOperation> {
	private static final long serialVersionUID = -3894081461861052610L;

	public boolean add(IntentOperation.Operator op, Intent intent) {
		return add(new IntentOperation(op, intent));
	}
}
