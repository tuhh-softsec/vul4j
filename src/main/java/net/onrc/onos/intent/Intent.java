package net.onrc.onos.intent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public abstract class Intent {
	enum IntentState {
		// TODO;
	}
	protected String id;
	protected IntentState state;

	/**
	 * Default constructor for Kryo deserialization
	 */
	@Deprecated
	public Intent() {
	}

	public Intent(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public IntentState getState() {
		return state;
	}
	
	public IntentState setState(IntentState newState) {
		IntentState oldState = state;
		state = newState;
		return oldState;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}
}
