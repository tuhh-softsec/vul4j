/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.model.stamm;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the filter_value database table.
 * 
 */
@Entity
@Table(name="filter_value")
public class FilterValue implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String value;

    //bi-directional many-to-one association to Filter
    @ManyToOne
    private Filter filter;

    //bi-directional many-to-one association to LadaUser
    @ManyToOne
    @JoinColumn(name="user_id")
    private LadaUser ladaUser;

    @Column(name="query_id")
    private Integer query;

    public FilterValue() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Filter getFilter() {
        return this.filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    public LadaUser getLadaUser() {
        return this.ladaUser;
    }

    public void setLadaUser(LadaUser ladaUser) {
        this.ladaUser = ladaUser;
    }

    public Integer getQuery() {
        return this.query;
    }

    public void setQuery(Integer query) {
        this.query = query;
    }

}
