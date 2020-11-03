package de.intevation.lada.model;

import de.intevation.lada.model.stammdaten.GridColumnValue;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.FetchType;

/**
 * Persistent class containing user column definitions, used for executing Queries
 */
public class QueryColumns{

    @Basic(fetch=FetchType.EAGER)
    private List<GridColumnValue> columns;

    public QueryColumns(){}

    public void setColumns(List<GridColumnValue> columns) {
        this.columns = columns;
    }

    public List<GridColumnValue> getColumns() {
        return this.columns;
    }
}
