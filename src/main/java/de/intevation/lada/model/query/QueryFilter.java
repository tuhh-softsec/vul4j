package de.intevation.lada.model.query;


public class QueryFilter
{
    private String dataIndex;
    private String type;
    private String label;

    public QueryFilter() {
    }

    public QueryFilter(String dataIndex, String type, String label) {
        this.dataIndex = dataIndex;
        this.type = type;
        this.label = label;
    }

    public String getDataIndex() {
        return dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
