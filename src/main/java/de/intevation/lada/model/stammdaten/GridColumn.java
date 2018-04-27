package de.intevation.lada.model.stammdaten;

import java.util.List;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="filter")
    private Filter filter;

    private String name;

    private Integer position;

    private Integer query;

    //bi-directional one-to-one association to GridColumnValue
    @OneToMany(mappedBy="gridColumn", fetch=FetchType.EAGER)
    private List<GridColumnValue> gridColumnValues;

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

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPosition() {
        return this.position;
    }
    
    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getQuery() {
        return this.query;
    }

    public void setQuery(Integer query) {
        this.query = query;
    }

    public List<GridColumnValue> getGridColumnValues() {
        return this.gridColumnValues;
    }

    public void addGridColumnValue(GridColumnValue gridColumnValue) {
        this.gridColumnValues.add(gridColumnValue);
    }

    public void setGridColumnValue(List<GridColumnValue> gridColumnValues) {
        this.gridColumnValues = gridColumnValues;
    }

}
