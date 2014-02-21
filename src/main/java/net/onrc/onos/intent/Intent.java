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
		REROUTE_REQ,
	}

	private String id;
	private IntentState state = IntentState.CREATED;

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
		return (id == null) ? 0 : id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Intent other = (Intent) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id.toString() + ", " + state.toString();
	}
}
