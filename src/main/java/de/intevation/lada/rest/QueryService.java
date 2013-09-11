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
import de.intevation.lada.model.query.QueryConfig;
import de.intevation.lada.utils.QueryTools;

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
    public Response get(
        @Context UriInfo info,
        @Context HttpHeaders headers
    ) {
        try {
            if (!authentication.isAuthorizedUser(headers)) {
                return new Response(false, 699, new ArrayList<LOrt>());
            }
            Response response = new Response(true, 200, this.loadQueryConfig());
            return response;
        }
        catch(AuthenticationException ae) {
            return new Response(false, 699, new ArrayList<LOrt>());
        }
    }

    private List<QueryConfig> loadQueryConfig() {
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
        return QueryTools.getConfig();
    }
}
