/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest.importer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;

import de.intevation.lada.importer.ImportConfig;
import de.intevation.lada.importer.ImportFormat;
import de.intevation.lada.importer.Importer;
import de.intevation.lada.importer.laf.LafImporter;
import de.intevation.lada.model.stammdaten.ImporterConfig;
import de.intevation.lada.model.stammdaten.Tag;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.data.TagUtil;
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
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    /**
     * The authorization module.
     */
    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorization;

    /**
     * Import a given list of files, generate a tag and set it to all imported records.
     * Expected input format:
     * <pre>
     * <code>
     * {
     *   "encoding": "UTF-8",
     *   "files": {
     *     "firstFileName.laf": "base64 encoded content",
     *     "secondFilename.laf": "base64 encoded content",
     *     //...
     *   }
     * }
     * </code>
     * </pre>
     */
    @POST
    @Path("/laf/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response multiUpload(
        JsonObject jsonInput,
        @Context HttpServletRequest request
    ) {
        UserInfo userInfo = authorization.getInfo(request);
        //Get file content strings from input object
        JsonObject filesObject = jsonInput.getJsonObject("files");

        String encoding = jsonInput.getString("encoding");

        //Contains: fileName: fileContent as String
        Map<String, String> files = new HashMap<String, String>();
        //Ids of alle imported probe records
        List<Integer> importedProbeids = new ArrayList<Integer>();
        //Contains a response data object for every import
        Map<String, Map<String, Object>> importResponseData = new HashMap<String, Map<String, Object>>();

        String mstId = request.getHeader("X-LADA-MST");
        if (mstId == null) {
            return new Response(false, 699, "Missing header for messtelle.");
        }

        filesObject.forEach((fileName, fileContent) -> {
            String encodedString = fileContent.toString();
            byte[] decodedBytes = Base64.decodeBase64(encodedString);
            String decodedContent = new String(decodedBytes, Charset.forName(encoding));
            files.put(fileName, decodedContent);
        });

        //Import each file
        files.forEach((fileName, content) -> {
            logLAFFile(mstId, content);
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
            Map<String, Object> fileResponseData = new HashMap<String,Object>();
            if (!importer.getErrors().isEmpty()) {
                fileResponseData.put("errors", importer.getErrors());
            }
            if (!importer.getWarnings().isEmpty()) {
                fileResponseData.put("warnings", importer.getWarnings());
            }
            fileResponseData.put("success", true);
            fileResponseData.put("probeIds", ((LafImporter) importer).getImportedIds());
            importResponseData.put(fileName, fileResponseData);
            importedProbeids.addAll(((LafImporter) importer).getImportedIds());
        });

        boolean success = false;
        // If import created at least a new record
        if (importedProbeids.size() > 0) {
            success = true;
            //Generate a tag for the imported probe records
            Response tagCreation = TagUtil.generateTag("IMP", mstId, repository);
            if (!tagCreation.getSuccess()) {
                //TODO: Tag creation failed -> import success?
            }
            Tag newTag = (Tag) tagCreation.getData();
            TagUtil.setTagForProbeRecords(importedProbeids, newTag.getId(), repository);

            //Put new tag in import response
            importResponseData.forEach((file, responseData) -> {
                responseData.put("tag", newTag.getTag());
            });
        }
        return new Response(success, 200, importResponseData);
    }

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
        logLAFFile(mstId, content);
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

        List<Integer> importedProbeids = ((LafImporter) importer).getImportedIds();
        respData.put("probeIds", importedProbeids);

        // If import created at least a new record
        if (importedProbeids.size() > 0) {
            //Generate a tag for the imported probe records
            Response tagCreation = TagUtil.generateTag("IMP", mstId, repository);
            if (!tagCreation.getSuccess()) {
                //TODO: Tag creation failed -> import success?
            }
            Tag newTag = (Tag) tagCreation.getData();
            TagUtil.setTagForProbeRecords(importedProbeids, newTag.getId(), repository);

            respData.put("tag", newTag.getTag());
        }

        return new Response(true, 200, respData);
    }

    /**
     * Log the imported file for debugging purposes.
     *
     * @param mstId Id from Header
     * @param content The laf file content
     */
    private void logLAFFile(String mstId, String content) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssS");
        Date now = new Date();
        // Create filename for imported laf
        String fileName = df.format(now) + "-" + mstId + ".laf";
        // Set default log path as fallback
        String filePath = "/var/log/wildfly/";
        // Get logger and appender for import logger
        Logger lafLogger = Logger.getLogger("import");
        Appender lafAppender = Logger.getRootLogger().getAppender("laf");
        // Retrive path set for import logger
        if (lafAppender instanceof FileAppender) {
            File appenderFile = new File(((FileAppender)lafAppender).getFile());
            filePath = appenderFile.getParent();
        }
        // Write laf file if debug enabled
        if (lafLogger.isDebugEnabled()) {
            lafLogger.debug("X-LADA-MST: " + mstId);
            lafLogger.debug("Imported file logged to: " + filePath + "/" + fileName);
            try {
                FileWriter f = new FileWriter(filePath + "/" + fileName);
                f.write(content);
                f.close();
            } catch (IOException e) {
                lafLogger.debug("Could not write import file to " + filePath);
            }
        }
    }
}
