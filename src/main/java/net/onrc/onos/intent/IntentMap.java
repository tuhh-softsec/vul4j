package net.onrc.onos.intent;

import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
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
	protected HashMap<String, Intent> intents = new HashMap<>();

	public void executeOperations(List<IntentOperation> operations) {
		LinkedList<ChangedEvent> events = new LinkedList<>();
		for (IntentOperation operation: operations) {
			switch (operation.operator) {
			case ADD:
				intents.put(operation.intent.getId(), operation.intent);
				events.add(new ChangedEvent(ChangedEventType.ADDED, operation.intent));
				break;
			case REMOVE:
				Intent intent = intents.get(operation.intent.getId());
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
		Iterator<Entry<String, Intent>> i = intents.entrySet().iterator();
		while (i.hasNext()) {
			Entry<String, Intent> entry = i.next();
			Intent intent = entry.getValue();
			if (intent.getState() == IntentState.DEL_ACK
					|| intent.getState() == IntentState.INST_NACK) {
				i.remove();
			}
		}
	}

	public Collection<Intent> getAllIntents() {
		return intents.values();
	}

	public Intent getIntent(String key) {
		return intents.get(key);
	}


	public void addChangeListener(ChangedListener listener) {
		listeners.add(listener);
	}

	public void removeChangedListener(ChangedListener listener) {
		listeners.remove(listener);
	}
}
