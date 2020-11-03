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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


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

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="data_type")
    private ResultType dataType;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="filter")
    private Filter filter;

    private String name;

    private Integer position;

    @Column(name="base_query")
    private Integer baseQuery;

    //bi-directional one-to-one association to GridColumnValue
    @OneToMany(mappedBy="gridColumn", fetch=FetchType.EAGER)
    @JsonIgnore
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

    public ResultType getDataType() {
        return this.dataType;
    }

    public void setDataType(ResultType dataType) {
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

    public Integer getBaseQuery() {
        return this.baseQuery;
    }

    public void setBaseQuery(Integer query) {
        this.baseQuery = query;
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
