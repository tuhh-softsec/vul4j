package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
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
    @OneToMany(mappedBy="query", fetch=FetchType.EAGER)
    private List<GridColumn> gridColumns;

    //uni-directional many-to-many association to NetzBetreiber
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="query_netzbetreiber"
        , joinColumns={
            @JoinColumn(name="query")
            }
        , inverseJoinColumns={
            @JoinColumn(name="netzbetreiber", referencedColumnName="id")
            }
        )
    private List<NetzBetreiber> netzBetreibers;

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

        return gridColumn;
    }

    public GridColumn removeGridColumn(GridColumn gridColumn) {
        getGridColumns().remove(gridColumn);

        return gridColumn;
    }

    public List<NetzBetreiber> getNetzBetreibers() {
        return this.netzBetreibers;
    }

    public void setNetzBetreibers(List<NetzBetreiber> netzBetreibers) {
        this.netzBetreibers = netzBetreibers;
    }

}
