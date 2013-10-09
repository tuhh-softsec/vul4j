package de.intevation.lada.model.query;

/**
 * Container for data filter.
 * Stores filter defined in the SQL query configuration.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class QueryFilter
{
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
     * @param dataIndex The dataIndex.
     * @param type      The filter type.
     * @param label     The label.
     */
    public QueryFilter(String dataIndex, String type, String label) {
        this.dataIndex = dataIndex;
        this.type = type;
        this.label = label;
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
