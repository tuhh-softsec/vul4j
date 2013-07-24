package de.intevation.lada.rest;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import de.intevation.lada.auth.Authentication;
import de.intevation.lada.auth.AuthenticationException;
import de.intevation.lada.model.LOrt;

class ResultConfig {
    String field;
    String label;
    String type;

    public ResultConfig(String field, String label, String type) {
        this.field = field;
        this.label = label;
        this.type = type;
    }

    /**
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * @param field the field to set
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
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
}

class QueryConfig {
    int id;
    String name;
    String description;
    String sql;
    List<String> filter;
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
    public List<String> getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(List<String> filter) {
        this.filter = filter;
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

/**
 * This class produces a RESTful service to read, write and update
 * LOrt objects.
 *
 * @author <a href="mailto:torsten.irlaender@intevation.de">Torsten Irländer</a>
 */
@Path("/query")
@RequestScoped
public class QueryService
{
    /**
     * The authorization module.
     */
    @Inject
    @Named("ldapauth")
    private Authentication authentication;

    /**
     * Request SQL-Queries
     *
     * Query parameters are used for the filter in form of key-value pairs.
     *
     * @param info      The URL query parameters.
     * @param headers   The HTTP header containing authorization information.
     * @return Response object.
     */
    @GET
    @Produces("text/json")
    public Response filter(
        @Context UriInfo info,
        @Context HttpHeaders headers
    ) {
        try {
            if (!authentication.isAuthorizedUser(headers)) {
                return new Response(false, 699, new ArrayList<LOrt>());
            }
            QueryConfig queries = this.loadQueryConfig();
            Response response = new Response(true, 200, queries);
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LOrt>());
        }
    }

    private QueryConfig loadQueryConfig() {
        QueryConfig queryConfig = new QueryConfig();
        /* Query 1 */
        queryConfig.setId(1);
        queryConfig.setName("MST, UWB");
        queryConfig.setDescription("Das ist die Beschreibung von Abfrage 1");
        queryConfig.setSql("Select * from l_probe");
        List<String> filters = new ArrayList<String>();
        filters.add("mst");
        queryConfig.setFilter(filters);
        List<ResultConfig> results = new ArrayList<ResultConfig>();
        results.add(new ResultConfig("mst","Messstelle","string"));
        queryConfig.setResults(results);
        return queryConfig;
    }
}
