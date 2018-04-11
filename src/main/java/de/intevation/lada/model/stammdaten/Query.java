package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;


/**
 * The persistent class for the query database table.
 * 
 */
@Entity
public class Query implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String description;

    private String name;

    private Integer owner;

    private String sql;

    //bi-directional many-to-one association to GridColumn
    @OneToMany(mappedBy="query")
    private List<GridColumn> gridColumns;

    //bi-directional many-to-one association to QueryNetzbetreiber
    @OneToMany(mappedBy="query")
    private List<QueryNetzbetreiber> queryNetzbetreibers;

    public Query() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOwner() {
        return this.owner;
    }

    public void setOwner(Integer owner) {
        this.owner = owner;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<GridColumn> getGridColumns() {
        return this.gridColumns;
    }

    public void setGridColumns(List<GridColumn> gridColumns) {
        this.gridColumns = gridColumns;
    }

    public GridColumn addGridColumn(GridColumn gridColumn) {
        getGridColumns().add(gridColumn);
        gridColumn.setQuery(this);

        return gridColumn;
    }

    public GridColumn removeGridColumn(GridColumn gridColumn) {
        getGridColumns().remove(gridColumn);
        gridColumn.setQuery(null);

        return gridColumn;
    }

    public List<QueryNetzbetreiber> getQueryNetzbetreibers() {
        return this.queryNetzbetreibers;
    }

    public void setQueryNetzbetreibers(List<QueryNetzbetreiber> queryNetzbetreibers) {
        this.queryNetzbetreibers = queryNetzbetreibers;
    }
}
