package org.esigate.events;

/**
 * Defines an event, with id and type.
 * 
 * @author Nicolas Richeton
 */
public class EventDefinition {

    /**
     * Standard event type.
     */
    public static final int TYPE_DEFAULT = 1;
    /**
     * Events which are fired after the main event occurs. These events are identified separately because we need to
     * execute listeners in a reverse order.
     */
    public static final int TYPE_POST = 2;

    private String id;
    private int type;

    /**
     * Create event defintion.
     * 
     * @param id
     *            Unique identifier for event
     * @param type
     *            {@link #TYPE_DEFAULT} or {@link #TYPE_POST}
     */
    public EventDefinition(String id, int type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {

        String strType = Integer.toString(type);

        if (type == TYPE_POST) {
            strType = "Post";
        } else if (type == TYPE_DEFAULT) {
            strType = "Default";
        }

        return id + " (" + strType + ")";
    }

}
