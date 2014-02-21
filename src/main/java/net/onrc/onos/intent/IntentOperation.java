package net.onrc.onos.intent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentOperation {
	public enum Operator {
		/**
		 * Add new intent specified by intent field.
		 */
		ADD,

		/**
		 * Remove existing intent specified by intent field.
		 * The instance of intent field should be an instance of Intent class (not a child class)
		 */
		REMOVE,

		/**
		 * Do error handling.
		 * The instance of intent field should be an instance of ErrorIntent
		 */
		ERROR,
	}

	public Operator operator;
	public Intent intent;

	protected IntentOperation() {}

	public IntentOperation(Operator operator, Intent intent) {
		this.operator = operator;
		this.intent = intent;
	}

	@Override
	public String toString() {
		return operator.toString() + ", (" + intent.toString() + ")";
	}
}
