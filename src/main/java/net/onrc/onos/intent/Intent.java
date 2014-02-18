package net.onrc.onos.intent;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class Intent {	
	public enum IntentState {
		CREATED,
		INST_REQ,
		INST_NACK,
		INST_ACK,
		DEL_REQ,
		DEL_PENDING,
		DEL_ACK,
	}

	protected String id;
	protected IntentState state = IntentState.CREATED;

	/**
	 * Default constructor for Kryo deserialization
	 */
	protected Intent() {
	}

	public Intent(String id) {
		this.id = id;
	}

	public Intent(String id, IntentState state) {
		this.id = id;
		this.state = state;
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
