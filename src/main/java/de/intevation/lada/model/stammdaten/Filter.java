package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


/**
 * The persistent class for the filter database table.
 * 
 */
@Entity
public class Filter implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String name;

    private String parameter;

    private String sql;

    //bi-directional many-to-one association to FilterType
    @ManyToOne
    @JoinColumn(name="type")
    private FilterType filterType;

    public Filter() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParameter() {
        return this.parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public FilterType getFilterType() {
        return this.filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }
}
