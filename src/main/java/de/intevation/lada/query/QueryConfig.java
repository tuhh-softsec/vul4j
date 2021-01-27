/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.query;

import java.util.List;


/**
 * Container for SQL query configuration.
 *
 * The server can filter {@link LProbeInfo} objects by configurable SQL queries
 * as described in the project wiki
 * (https://bfs-intern.intevation.de/Server/Suche).
 * This container is used to store the config at runtime.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class QueryConfig {

    private String id;
    private String name;
    private String description;
    private String sql;
    private String type;
    private List<QueryFilter> filters;
    private List<ResultConfig> results;

    public QueryConfig() { }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @param sql the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the filter
     */
    public List<QueryFilter> getFilters() {
        return filters;
    }

    /**
     * @param filters the filter to set
     */
    public void setFilters(List<QueryFilter> filters) {
        this.filters = filters;
    }

    /**
     * @return the results
     */
    public List<ResultConfig> getResults() {
        return results;
    }

    /**
     * @param results the results to set
     */
    public void setResults(List<ResultConfig> results) {
        this.results = results;
    }
}
