package net.onrc.onos.intent;

import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import net.onrc.onos.intent.Intent.IntentState;

/**
 * @author Toshio Koide (t-koide@onlab.us)
 */
public class IntentMap {
	public enum ChangedEventType {
		/**
		 * Added new intent.
		 */
		ADDED,

		/**
		 * Removed existing intent.
		 * The specified intent is an instance of Intent class (not a child class)
		 * Only id and state are valid.
		 */
		REMOVED,

		/**
		 * Changed state of existing intent.
		 * The specified intent is an instance of Intent class (not a child class)
		 * Only id and state are valid.
		 */
		STATE_CHANGED,
	}

	public class ChangedEvent {
		public ChangedEvent(ChangedEventType eventType, Intent intent) {
			this.eventType = eventType;
			this.intent = intent;
		}
		public ChangedEventType eventType;
		public Intent intent;
	}

	public interface ChangedListener extends EventListener {
		void intentsChange(LinkedList<ChangedEvent> events);
	}

	private HashSet<ChangedListener> listeners = new HashSet<>();
	private HashMap<String, Intent> intents = new HashMap<>();

	protected void putIntent(Intent intent) {
		if (intents.containsKey(intent.getId()))
			removeIntent(intent.getId());
		intents.put(intent.getId(), intent);
	}

	protected void removeIntent(String intentId) {
		intents.remove(intentId);		
	}

	public Intent getIntent(String intentId) {
		return intents.get(intentId);
	}

	public void executeOperations(IntentOperationList operations) {
		LinkedList<ChangedEvent> events = new LinkedList<>();
		for (IntentOperation operation: operations) {
			switch (operation.operator) {
			case ADD:
				putIntent(operation.intent);
				events.add(new ChangedEvent(ChangedEventType.ADDED, operation.intent));
				break;
			case REMOVE:
				Intent intent = getIntent(operation.intent.getId());
				if (intent == null) {
					// TODO throw exception
				}
				else {
					intent.setState(Intent.IntentState.DEL_REQ);
					events.add(new ChangedEvent(ChangedEventType.STATE_CHANGED,
							new Intent(intent.getId(), Intent.IntentState.DEL_REQ)));
				}
				break;
			}
		}
		for (ChangedListener listener: listeners) {
			listener.intentsChange(events);
		}
	}

	public void purge() {
		LinkedList<String> removeIds = new LinkedList<>();
		for (Entry<String, Intent> entry: intents.entrySet()) {
			Intent intent = entry.getValue();
			if (intent.getState() == IntentState.DEL_ACK
					|| intent.getState() == IntentState.INST_NACK) {
				removeIds.add(intent.getId());
			}			
		}
		for (String intentId: removeIds) {
			removeIntent(intentId);
		}
	}

	public Collection<Intent> getAllIntents() {
		return intents.values();
	}

	public void addChangeListener(ChangedListener listener) {
		listeners.add(listener);
	}

	public void removeChangedListener(ChangedListener listener) {
		listeners.remove(listener);
	}
}
