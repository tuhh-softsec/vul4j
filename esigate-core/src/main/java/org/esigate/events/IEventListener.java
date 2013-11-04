package org.esigate.events;

/**
 * This interface must be implements to listen to events.
 * 
 * @see EventManager
 * 
 * @author Nicolas Richeton
 * 
 */
public interface IEventListener {

    /**
     * Handle an event.
     * 
     * @param id
     * @param event
     * @return false to stop the processing of event listeners for this event
     */
    boolean event(EventDefinition id, Event event);

}
