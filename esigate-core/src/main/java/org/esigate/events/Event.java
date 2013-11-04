package org.esigate.events;

/**
 * This class contains the common attributes for all events.
 * 
 * @author Nicolas Richeton
 * 
 */
public abstract class Event {

    /**
     * Immediately stop request processing. Depending of the current state, this may render the current response (if
     * available) or else render an error page.
     */
    public boolean exit = false;

}
