package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The persistent class for the grid_column_values database table.
 *
 */
@Entity
@Table(name="grid_column_values")
public class GridColumnValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Integer id;

    @Column(name="column_index")
    private Integer columnIndex;

    @Column(name="filter_active")
    private Boolean filterActive;

    @Column(name="filter_value")
    private String filterValue;

    @Column(name="filter_negate")
    private Boolean filterNegate;

    @Column(name="filter_regex")
    private Boolean filterRegex;

    @Column(name="filter_is_null")
    private Boolean filterIsNull;

    private String sort;

    @Column(name="sort_index")
    private Integer sortIndex;

    @Column(name="user_id")
    private Integer userId;

    private Boolean visible;

    private Integer width;

    //bi-directional one-to-one association to GridColumn
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="grid_column")
    private GridColumn gridColumn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="query_user")
    private QueryUser queryUser;

    //Connected grid column's id, used for creating/updating grid_column_values
    @Transient
    private int gridColumnId;

    @Transient
    private int queryUserId;

    public GridColumnValue() {
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

    public Boolean getFilterActive() {
        return this.filterActive;
    }

    public void setFilterActive(Boolean filterActive) {
        this.filterActive = filterActive;
    }

    public String getFilterValue() {
        return this.filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public String getSort() {
        return this.sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getSortIndex() {
        return this.sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public int getGridColumnId() {
        return gridColumnId;
    }

    public void setgridColumnId(int gridColumnId) {
        this.gridColumnId = gridColumnId;
    }

    @JsonIgnore
    public GridColumn getGridColumn() {
        return this.gridColumn;
    }

    public void setGridColumn(GridColumn gridColumn) {
        this.gridColumn = gridColumn;
    }

    @JsonIgnore
    public QueryUser getQueryUser() {
        return this.queryUser;
    }

    public void setQueryUser(QueryUser queryUser) {
        this.queryUser = queryUser;
    }

    public int getQueryUserId() {
        return this.queryUserId;
    }

    public void setQueryUserId(int queryUserId) {
        this.queryUserId = queryUserId;
    }

    public Boolean getFilterNegate() {
        return filterNegate;
    }

    public void setFilterNegate(Boolean filterNegate) {
        this.filterNegate = filterNegate;
    }

    public Boolean getFilterRegex() {
        return filterRegex;
    }

    public void setFilterRegex(Boolean filterRegex) {
        this.filterRegex = filterRegex;
    }

    public Boolean getFilterIsNull() {
        return filterIsNull;
    }

    public void setFilterIsNull(Boolean filterIsNull) {
        this.filterIsNull = filterIsNull;
    }
}
