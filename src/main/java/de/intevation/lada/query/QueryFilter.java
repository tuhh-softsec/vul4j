/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.query;


/**
 * Container for data filter.
 * Stores filter defined in the SQL query configuration.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class QueryFilter {
    private String dataIndex;
    private String type;
    private String label;
    private boolean multiSelect;

    /**
     * Default constructor.
     */
    public QueryFilter() {
    }

    /**
     * Constructor to create a filled filter.
     *
     * @param di The dataIndex.
     * @param t  The filter type.
     * @param l  The label.
     */
    public QueryFilter(String di, String t, String l) {
        this.dataIndex = di;
        this.type = t;
        this.label = l;
    }

    /**
     * @return The dataIndex
     */
    public String getDataIndex() {
        return dataIndex;
    }

    /**
     * @param dataIndex THe dataIndex to set.
     */
    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    /**
     * @return The filter type.
     */
    public String getType() {
        return type;
    }

    /**
     * @param type  The filter type to set.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return The label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the multiSelect
     */
    public boolean isMultiSelect() {
        return multiSelect;
    }

    /**
     * @param multiSelect the multiSelect to set
     */
    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
}
