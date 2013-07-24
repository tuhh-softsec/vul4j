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
    String dataIndex;
    String header;
    Integer flex;
    Integer width;

    public ResultConfig(String dataIndex, String header, Integer flex, Integer width) {
        this.dataIndex= dataIndex;
        this.header= header;
        this.flex = flex;
        this.width = width;
    }

    public ResultConfig(String dataIndex, String header, Integer flex) {
        this.dataIndex= dataIndex;
        this.header= header;
        this.flex = flex;
        this.width = null;
    }

    public ResultConfig(String dataIndex, String header) {
        this.dataIndex= dataIndex;
        this.header= header;
        this.flex = 0;
        this.width = null;
    }

    /**
     * @return the dataIndex
     */
    public String getDataIndex() {
        return dataIndex;
    }

    /**
     * @param dataIndex the dataIndex to set
     */
    public void setDataIndex(String dataIndex) {
        this.dataIndex = dataIndex;
    }

    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the width
     */
    public Integer getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(Integer width) {
        this.width = width;
    }

    /**
     * @return the flex
     */
    public Integer getFlex() {
        return flex;
    }

    /**
     * @param flex the flex to set
     */
    public void setFlex(Integer flex) {
        this.flex = flex;
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
            List<QueryConfig> queries = this.loadQueryConfig();
            Response response = new Response(true, 200, queries);
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LOrt>());
        }
    }

    private List<QueryConfig> loadQueryConfig() {
        List<QueryConfig> configs = new ArrayList<QueryConfig>();

        /* Typicall available fields
        {header: 'Datenbasis',  dataIndex: 'datenbasisId', width: 70},
        {header: 'MPL',  dataIndex: 'mplId', width: 50},
        {header: 'UWB',  dataIndex: 'umwId', width: 50},
        {header: 'MMT',  dataIndex: 'messmethode'},
        {header: 'HPNR',  dataIndex: 'hauptprobenNr'},
        {header: 'NPNR',  dataIndex: 'nebenprobenNr'},
        {header: 'E.Gemeinde',  dataIndex: 'bezeichnung', flex: 1},
        {header: 'Ursprungsgemeinde',  dataIndex: 'kreis', flex: 1},
        {header: 'ProbeID', dataIndex: 'probeId'},
        {header: 'MST', dataIndex: 'mstId', width: 50}
        */

        /* Query 1 */
        QueryConfig qc1 = new QueryConfig();
        qc1.setId(1);
        qc1.setName("MST, UWB");
        qc1.setDescription("Das ist die Beschreibung von Abfrage 1");
        qc1.setSql("Select * from l_probe");
        List<String> filters = new ArrayList<String>();
        filters.add("mst");
        qc1.setFilter(filters);
        List<ResultConfig> results = new ArrayList<ResultConfig>();
        results.add(new ResultConfig("datenbasisId","Datenbases"));
        results.add(new ResultConfig("mplId","MPL"));
        results.add(new ResultConfig("umwId","UWB"));
        results.add(new ResultConfig("messmethode","MMT"));
        results.add(new ResultConfig("hauptprobenNr","HPNR"));
        results.add(new ResultConfig("nebenprobenNr","NPNR"));
        results.add(new ResultConfig("bezeichnung","E.Gemeinde"));
        results.add(new ResultConfig("kreis","Ursprungsgemeinde"));
        results.add(new ResultConfig("probeId","ProbeID"));
        results.add(new ResultConfig("mstId","MS"));
        qc1.setResults(results);
        configs.add(qc1);
        /* Query 2 */
        QueryConfig qc2 = new QueryConfig();
        qc2.setId(2);
        qc2.setName("Test");
        qc2.setDescription("Das ist die Beschreibung von Abfrage 2");
        qc2.setSql("Select * from l_probe");
        List<String> qcf2= new ArrayList<String>();
        qcf2.add("mst");
        qc2.setFilter(qcf2);
        List<ResultConfig> qcr2= new ArrayList<ResultConfig>();
        qcr2.add(new ResultConfig("mst","Messstelle"));
        qc2.setResults(qcr2);
        configs.add(qc2);
        return configs;
    }
}
