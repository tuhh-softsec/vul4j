package de.intevation.lada.model.stammdaten;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * The persistent class for the query_netzbetreiber database table.
 * 
 */
@Entity
@Table(name="query_netzbetreiber")
public class QueryNetzbetreiber implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private String netzbetreiber;

    //bi-directional many-to-one association to Query
    @ManyToOne
    @JoinColumn(name="query")
    private Query query;

    public QueryNetzbetreiber() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNetzbetreiber() {
        return this.netzbetreiber;
    }

    public void setNetzbetreiber(String netzbetreiber) {
        this.netzbetreiber = netzbetreiber;
    }

    public Query getQuery() {
        return this.query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

}
