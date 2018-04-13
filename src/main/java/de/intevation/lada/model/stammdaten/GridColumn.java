package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;


/**
 * The persistent class for the grid_column database table.
 * 
 */
@Entity
@Table(name="grid_column")
public class GridColumn implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Column(name="data_index")
    private String dataIndex;

    @Column(name="data_type")
    private Integer dataType;

    private Integer filter;

    private String name;

    private Integer query;

    //bi-directional one-to-one association to GridColumnValue
    @OneToOne(mappedBy="gridColumn")
    private GridColumnValue gridColumnValue;

    public GridColumn() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDataIndex() {
        return this.dataIndex;
    }

    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    public Integer getDataType() {
        return this.dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public Integer getFilter() {
        return this.filter;
    }

    public void setFilter(Integer filter) {
        this.filter = filter;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuery() {
        return this.query;
    }

    public void setQuery(Integer query) {
        this.query = query;
    }

    public GridColumnValue getGridColumnValue() {
        return this.gridColumnValue;
    }

    public void setGridColumnValue(GridColumnValue gridColumnValue) {
        this.gridColumnValue = gridColumnValue;
    }

}
