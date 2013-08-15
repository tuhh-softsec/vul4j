package de.intevation.lada.model.query;

import java.util.List;


public class QueryConfig
{
    int id;
    String name;
    String description;
    String sql;
    List<QueryFilter> filters;
    List<ResultConfig> results;

    public QueryConfig()
    {
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
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
     * @return the filter
     */
    public List<QueryFilter> getFilters() {
        return filters;
    }

    /**
     * @param filter the filter to set
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
