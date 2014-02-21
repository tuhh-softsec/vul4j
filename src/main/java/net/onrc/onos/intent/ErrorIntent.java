package net.onrc.onos.intent;

/**
 * This class is instantiated by Run-times to express intent calculation error
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class ErrorIntent extends Intent {
	public enum ErrorType {
		UNSUPPORTED_INTENT,
		SWITCH_NOT_FOUND,
		PATH_NOT_FOUND,
	}

	public ErrorType errorType;
	public String message;
	public Intent parentIntent;

	/**
	 * Default constructor for Kryo deserialization
	 */
	protected ErrorIntent() {
	}

	public ErrorIntent(ErrorType errorType, String message, Intent parentIntent) {
		super(parentIntent.getId());
		this.errorType = errorType;
		this.message = message;
		this.parentIntent = parentIntent;
	}
}
