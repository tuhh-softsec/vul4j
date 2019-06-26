/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.importer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import de.intevation.lada.importer.ImportConfig;
import de.intevation.lada.importer.ImportFormat;
import de.intevation.lada.importer.Importer;
import de.intevation.lada.model.stammdaten.ImporterConfig;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

/**
 * This class produces a RESTful service to interact with probe objects.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("data/import")
@RequestScoped
public class LafImportService {

    @Inject
    private Logger logger;

    /**
     * The importer
     */
    @Inject
    @ImportConfig(format=ImportFormat.LAF)
    private Importer importer;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Import a LAF formatted file.
     *
     * @param input     String containing file content.
     * @param header    The HTTP header containing authorization information.
     * @return Response object.
     */
    @POST
    @Path("/laf")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public Response upload(
        String content,
        @Context HttpServletRequest request
    ) {
        UserInfo userInfo = authorization.getInfo(request);

        String mstId = request.getHeader("X-LADA-MST");
        if (mstId == null) {
            return new Response(false, 699, "Missing header for messtelle.");
        }
        List<ImporterConfig> config = new ArrayList<ImporterConfig>();
        if (!"".equals(mstId)) {
            QueryBuilder<ImporterConfig> builder =
                new QueryBuilder<ImporterConfig>(
                    repository.entityManager(Strings.STAMM),
                    ImporterConfig.class);
            builder.and("mstId", mstId);
            config = (List<ImporterConfig>) repository.filterPlain(builder.getQuery(), Strings.STAMM);
        }
        importer.doImport(content, userInfo, config);
        Map<String, Object> respData = new HashMap<String,Object>();
        if (!importer.getErrors().isEmpty()) {
            respData.put("errors", importer.getErrors());
        }
        if (!importer.getWarnings().isEmpty()) {
            respData.put("warnings", importer.getWarnings());
        }

        return new Response(true, 200, respData);
    }
}
