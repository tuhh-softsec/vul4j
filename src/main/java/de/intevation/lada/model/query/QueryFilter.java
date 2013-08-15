package de.intevation.lada.model.query;


public class QueryFilter
{
    private String dataIndex;
    private String type;

    public QueryFilter() {
    }

    public QueryFilter(String dataIndex, String type) {
        this.dataIndex = dataIndex;
        this.type = type;
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
}
