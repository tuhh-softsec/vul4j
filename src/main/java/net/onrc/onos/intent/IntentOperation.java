package net.onrc.onos.intent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentOperation {
	public enum Operator {
		/**
		 * Add new intent specified by intent field
		 */
		ADD,

		/**
		 * Remove existing intent specified by intent field.
		 * The specified intent should be an instance of Intent class (not a child class)
		 */
		REMOVE,
	}

	public IntentOperation() {}

	public IntentOperation(Operator operator, Intent intent) {
		this.operator = operator;
		this.intent = intent;
	}

	public Operator operator;
	public Intent intent;

	@Override
	public String toString() {
		return operator.toString() + ", (" + intent.toString() + ")";
	}
}
