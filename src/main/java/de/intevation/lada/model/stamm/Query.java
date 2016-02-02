/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * The persistent class for the query database table.
 * 
 */
@Entity
public class Query implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id", unique=true, nullable=false)
    private Integer id;

    private String description;

    private String name;

    private String sql;

    private String type;

    @Transient
    private Boolean favorite;

    //bi-directional many-to-one association to Filter
    @OneToMany(fetch=FetchType.EAGER, mappedBy="query")
    private List<Filter> filters;

    //bi-directional many-to-one association to Result
    @OneToMany(fetch=FetchType.EAGER, mappedBy="query")
    private List<Result> results;

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

    public String getSql() {
        return this.sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean isFavorite() {
        if (favorite == null) {
            return false;
        }
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public List<Filter> getFilters() {
        return this.filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public Filter addFilter(Filter filter) {
        getFilters().add(filter);
        filter.setQuery(this);

        return filter;
    }

    public Filter removeFilter(Filter filter) {
        getFilters().remove(filter);
        filter.setQuery(null);

        return filter;
    }

    public List<Result> getResults() {
        return this.results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Result addResult(Result result) {
        getResults().add(result);
        result.setQuery(this);

        return result;
    }

    public Result removeResult(Result result) {
        getResults().remove(result);
        result.setQuery(null);

        return result;
    }

}
