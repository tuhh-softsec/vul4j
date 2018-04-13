package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

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
    private Integer id;

    @Column(name="column_index")
    private Integer columnIndex;

    @Column(name="filter_active")
    private Boolean filterActive;

    @Column(name="filter_value")
    private String filterValue;

    private String sort;

    @Column(name="user_id")
    private Integer userId;

    private Boolean visible;

    private Integer width;

    //bi-directional one-to-one association to GridColumn
    @OneToOne
    @JoinColumn(name="grid_column")
    private GridColumn gridColumn;

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

    @JsonIgnore
    public GridColumn getGridColumn() {
        return this.gridColumn;
    }

    public void setGridColumn(GridColumn gridColumn) {
        this.gridColumn = gridColumn;
    }

}
