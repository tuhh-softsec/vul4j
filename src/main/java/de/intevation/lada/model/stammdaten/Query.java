package de.intevation.lada.model.stammdaten;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

    //uni-directional many-to-many association to NetzBetreiber
    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(
        name="query_messstelle"
        , joinColumns={
            @JoinColumn(name="query")
            }
        , inverseJoinColumns={
            @JoinColumn(name="mess_stelle", referencedColumnName="id")
            }
        )
    private List<MessStelle> messStelles;

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

    public List<MessStelle> getMessStelles() {
        return this.messStelles;
    }

    public void setMessStelles(List<MessStelle> messStelles) {
        this.messStelles = messStelles;
    }

}
