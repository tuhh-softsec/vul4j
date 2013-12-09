package net.onrc.onos.ofcontroller.util;

/**
 * Class for encapsulating events with event-related data entry.
 */
public class EventEntry<T> {
    /**
     * The event types.
     */
    public enum Type {
	ENTRY_ADD,			// Add or update an entry
	ENTRY_REMOVE			// Remove an entry
    }

    private Type	eventType;	// The event type
    private T		eventData;	// The relevant event data entry

    /**
     * Constructor for a given event type and event-related data entry.
     *
     * @param eventType the event type.
     * @param eventData the event data entry.
     */
    public EventEntry(EventEntry.Type eventType, T eventData) {
	this.eventType = eventType;
	this.eventData = eventData;
    }

    /**
     * Test whether the event type is ENTRY_ADD.
     *
     * @return true if the event type is ENTRY_ADD, otherwise false.
     */
    public boolean isAdd() {
	return (this.eventType == Type.ENTRY_ADD);
    }

    /**
     * Test whether the event type is ENTRY_REMOVE.
     *
     * @return true if the event type is ENTRY_REMOVE, otherwise false.
     */
    public boolean isRemove() {
	return (this.eventType == Type.ENTRY_REMOVE);
    }

    /**
     * Get the event type.
     *
     * @return the event type.
     */
    public EventEntry.Type eventType() {
	return this.eventType;
    }

    /**
     * Get the event-related data entry.
     *
     * @return the event-related data entry.
     */
    public T eventData() {
	return this.eventData;
    }
}
