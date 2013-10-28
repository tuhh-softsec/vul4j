package org.esigate.aggregator;

import org.esigate.Driver;

/**
 * Element Attributes.
 * 
 * @author athaveau
 */
class ElementAttributes {

    /**
     * Driver attribut.
     */
    private Driver driver;

    /**
     * page attribut.
     */
    private String page;

    /**
     * name attribut.
     */
    private String name;

    /**
     * Constructor.
     * 
     * @param driver
     * @param page
     * @param name
     */
    public ElementAttributes(Driver driver, String page, String name) {
        this.driver = driver;
        this.page = page;
        this.name = name;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getName() {
        return name;
    }

    public String getPage() {
        return page;
    }

}
