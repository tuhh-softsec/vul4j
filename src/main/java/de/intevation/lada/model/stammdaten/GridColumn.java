package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;


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

    @Column(name="column_index")
    private Integer columnIndex;

    @Column(name="data_index")
    private String dataIndex;

    @Column(name="data_type")
    private Integer dataType;

    @Column(name="filter_active")
    private Boolean filterActive;

    private String name;

    private String sort;

    private Boolean visible;

    private Integer width;

    //uni-directional many-to-one association to Filter
    @ManyToOne
    @JoinColumn(name="filter")
    private Filter filter;

    //uni-directional many-to-one association to FilterValue
    @ManyToOne
    @JoinColumn(name="filter_value")
    private FilterValue filterValue;

    //bi-directional many-to-one association to Query
    @ManyToOne
    @JoinColumn(name="query")
    private Query query;

    public GridColumn() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getColumnIndex() {
        return this.columnIndex;
    }

    public void setColumnIndex(Integer columnIndex) {
        this.columnIndex = columnIndex;
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

    public Boolean getFilterActive() {
        return this.filterActive;
    }

    public void setFilterActive(Boolean filterActive) {
        this.filterActive = filterActive;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Boolean getVisible() {
        return this.visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Integer getWidth() {
        return this.width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public FilterValue getFilterValue() {
        return this.filterValue;
    }

    public void setFilterValue(FilterValue filterValue) {
        this.filterValue = filterValue;
    }

    @JsonIgnore
    public Query getQuery() {
        return this.query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

}
