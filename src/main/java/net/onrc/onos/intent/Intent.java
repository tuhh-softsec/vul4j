package net.onrc.onos.intent;

import java.util.LinkedList;

import com.esotericsoftware.kryo.serializers.FieldSerializer.Optional;

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

	@Optional(value="logs")
	private LinkedList<String> logs = new LinkedList<>();

	/**
	 * Default constructor for Kryo deserialization
	 */
	protected Intent() {
		logs.add(String.format("created, time:%d", System.nanoTime())); // for measurement
	}

	public Intent(String id) {
		logs.add(String.format("created, time:%d", System.nanoTime())); // for measurement
		this.id = id;
	}

	public Intent(String id, IntentState state) {
		logs.add(String.format("created, time:%d", System.nanoTime())); // for measurement
		setState(state);
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public IntentState getState() {
		return state;
	}

	public IntentState setState(IntentState newState) {
		logs.add(String.format("setState, oldState:%s, newState:%s, time:%d",
				state, newState, System.nanoTime())); // for measurement
		if (logs.size() > 20) { // TODO this size should be configurable
			logs.removeFirst();
		}
		IntentState oldState = state;
		state = newState;
		return oldState;
	}

	public LinkedList<String> getLogs() {
		return logs;
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
