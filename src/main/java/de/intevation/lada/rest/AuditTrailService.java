/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.rest;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

//import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.intevation.lada.model.land.AuditTrailMessung;
import de.intevation.lada.model.land.AuditTrailProbe;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

/**
 * REST service for AuditTrail.
 * <p>
 * The services produce data in the application/json media type.
 * All HTTP methods use the authorization module to determine if the user is
 * allowed to perform the requested action.
 * A typical response holds information about the action performed and the data.
 * <pre>
 * <code>
 * {
 *  "success": [boolean];
 *  "message": [string],
 *  "data":[{
 *      "id": [number],
 *      "identifier: [string]
 *      "audit": [array]
 *  }],
 * }
 * </code>
 * </pre>
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Path("rest/audit")
@RequestScoped
public class AuditTrailService {

    /**
     * Class to store tablename and value field for foreign key mappings.
     */
    private class TableMapper {
        private String mappingTable;
        private String valueField;

        public TableMapper(
            String mappingTable,
            String valueField
        ) {
            this.mappingTable = mappingTable;
            this.valueField = valueField;
        }

        public String getMappingTable() {
            return mappingTable;
        }

        public String getValueField() {
            return valueField;
        }
    }

//    @Inject Logger logger;
    /**
     * The data repository granting read/write access.
     */
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
     * Map foreign key to their associated table and the display value.
     */
    private Map<String, TableMapper> mappings;

    /**
     * Initialize the object with key <-> table mappings.
     */
    @PostConstruct
    public void initialize() {
        mappings = new HashMap<String, TableMapper>();
        mappings.put("messgroesse_id",
            new TableMapper("messgroesse", "messgroesse"));
        mappings.put("meh_id",
            new TableMapper("mess_einheit", "einheit"));
        mappings.put("ort_id",
            new TableMapper("ort", "ort_id"));
        mappings.put("datenbasis_id",
            new TableMapper("datenbasis", "datenbasis"));
        mappings.put("ba_id",
            new TableMapper("betriebsart", "name"));
        mappings.put("mpl_id",
            new TableMapper("messprogramm_kategorie", "code"));
        mappings.put("probenart_id",
            new TableMapper("probenart", "probenart"));
        mappings.put("probe_nehmer_id",
            new TableMapper("probenehmer", "prn_id"));
        mappings.put("probeentnahme_beginn",
            new TableMapper("date", "dd.MM.yy HH:mm"));
        mappings.put("probeentnahme_ende",
            new TableMapper("date", "dd.MM.yy HH:mm"));
        mappings.put("solldatum_beginn",
                new TableMapper("date", "dd.MM.yy HH:mm"));
        mappings.put("solldatum_ende",
                new TableMapper("date", "dd.MM.yy HH:mm"));
        mappings.put("messzeitpunkt",
                new TableMapper("date", "dd.MM.yy HH:mm"));
        mappings.put("kta_gruppe_id",
            new TableMapper("kta_gruppe", "kta_gruppe"));
        mappings.put("rei_progpunkt_grp_id",
            new TableMapper("rei_progpunkt_gruppe", "rei_prog_punkt_gruppe"));
    }

    /**
     * Service to generate audit trail for probe objects.
     */
    @GET
    @Path("/probe/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getProbe(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        if (id == null || "".equals(id)) {
            String ret = "{\"success\": false," +
                "\"message\":698,\"data\":null}";
            return ret;
        }

        Integer pId = null;
        String ret = "{\"success\": false," +
            "\"message\":600,\"data\":null}";
        try {
            pId = Integer.valueOf(id);
        }
        catch(NumberFormatException nfe) {
            return ret;
        }
        // Get the plain probe object to have the hauptproben_nr.
        Probe probe = repository.getByIdPlain(Probe.class, pId, Strings.LAND);
        if (probe == null) {
            return ret;
        }
        UserInfo userInfo = authorization.getInfo(request);


        // Get all entries for the probe and its sub objects.
        QueryBuilder<AuditTrailProbe> builder =
            new QueryBuilder<AuditTrailProbe>(
                repository.entityManager(Strings.LAND),
                AuditTrailProbe.class);
        builder.and("objectId", id);
        builder.and("tableName", "probe");
        builder.or("probeId", id);
        builder.orderBy("tstamp", true);
        List<AuditTrailProbe> audit =
            repository.filterPlain(builder.getQuery(), Strings.LAND);

        // Create an empty JsonObject
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("success", true);
        responseNode.put("message", 200);
        ObjectNode auditJson = responseNode.putObject("data");
        ArrayNode entries = auditJson.putArray("audit");
        auditJson.put("id", probe.getId());
        auditJson.put(
            "identifier",
            (probe.getHauptprobenNr() == null) ? probe.getExterneProbeId() : probe.getHauptprobenNr()
            );
        for (AuditTrailProbe a : audit) {
            //If audit entry shows a messwert, do not show if:
            // - StatusKombi is 1 (MST - nicht vergeben)
            // - User is not owner of the messung
            if (a.getTableName().equals("messwert")) {
                Messung messung = repository.getByIdPlain(Messung.class, a.getMessungsId(), Strings.LAND);
                StatusProtokoll status = repository.getByIdPlain(StatusProtokoll.class, messung.getStatus(), Strings.LAND);
                if(status.getStatusKombi() == 1
                        && !userInfo.getMessstellen().contains(probe.getMstId())) {
                    continue;
                }       
        
            }
            entries.add(createEntry(a, mapper));
        }
        return responseNode.toString();
    }

    /**
     * Create a JSON object for an AuditTrailProbe entry.
     *
     * @param audit The table entry
     * @param mapper JSON object mapper
     */
    private ObjectNode createEntry(AuditTrailProbe audit, ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", audit.getTstamp().getTime());
        node.put("type", audit.getTableName());
        node.put("action", audit.getAction());
        ObjectNode data = translateValues((ObjectNode)audit.getChangedFields());
        node.putPOJO("changedFields", data);
        if ("kommentar_p".equals(audit.getTableName())) {
            node.put("identifier", audit.getRowData().get("datum").toString());
        }
        if ("zusatz_wert".equals(audit.getTableName())) {
            node.put("identifier", audit.getRowData().get("pzs_id").toString());
        }
        if ("ortszuordnung".equals(audit.getTableName())) {
            String value = translateId(
                "ort",
                "ort_id",
                audit.getRowData().get("ort_id").toString(),
                "id",
                Strings.STAMM);
            node.put("identifier", value);
        }
        if ("messung".equals(audit.getTableName())) {
            Messung m = repository.getByIdPlain(
                Messung.class, audit.getObjectId(), Strings.LAND);
            node.put("identifier",
                (m == null) ? 
                    "(deleted)" : 
                    (m.getNebenprobenNr() == null) ? 
                        m.getExterneMessungsId().toString() : 
                        m.getNebenprobenNr()
                );
        }
        if (audit.getMessungsId() != null) {
            Messung m = repository.getByIdPlain(
                Messung.class, audit.getMessungsId(), Strings.LAND);
            ObjectNode identifier = node.putObject("identifier");
            identifier.put("messung",
                (m.getNebenprobenNr() == null) ? m.getExterneMessungsId().toString() : m.getNebenprobenNr());
            if ("kommentar_m".equals(audit.getTableName())) {
                identifier.put("identifier",
                    audit.getRowData().get("datum").toString());
            }
            if ("messwert".equals(audit.getTableName())) {
                String value = translateId(
                    "messgroesse",
                    "messgroesse",
                    audit.getRowData().get("messgroesse_id").toString(),
                    "id",
                    Strings.STAMM);
                identifier.put("identifier", value);
            }
        }
        return node;
    }

    /**
     * Service to generate audit trail for messung objects.
     */
    @GET
    @Path("/messung/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessung(
        @Context HttpServletRequest request,
        @PathParam("id") String id
    ) {
        if (id == null || "".equals(id)) {
            String ret = "{\"success\": false," +
                "\"message\":698,\"data\":null}";
            return ret;
        }

        Integer mId = null;
        String ret = "{\"success\": false," +
            "\"message\":600,\"data\":null}";
        try {
            mId = Integer.valueOf(id);
        }
        catch(NumberFormatException nfe) {
            return ret;
        }
        Messung messung = repository.getByIdPlain(Messung.class, mId, Strings.LAND);
        if (messung == null) {
            return ret;
        }
        StatusProtokoll status = repository.getByIdPlain(StatusProtokoll.class, messung.getStatus(), Strings.LAND);
        Probe probe = repository.getByIdPlain(Probe.class, messung.getProbeId(), Strings.LAND);
        UserInfo userInfo = authorization.getInfo(request);
        QueryBuilder<AuditTrailMessung> builder =
            new QueryBuilder<AuditTrailMessung>(
                repository.entityManager(Strings.LAND),
                AuditTrailMessung.class);
        builder.and("objectId", mId);
        builder.and("tableName", "messung");
        builder.or("messungsId", mId);
        builder.orderBy("tstamp", true);
        List<AuditTrailMessung> audit =
            repository.filterPlain(builder.getQuery(), Strings.LAND);

        // Create an empty JsonObject
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("success", true);
        responseNode.put("message", 200);
        ObjectNode auditJson = responseNode.putObject("data");
        ArrayNode entries = auditJson.putArray("audit");
        auditJson.put("id", messung.getId());
        auditJson.put(
            "identifier",
            (messung.getNebenprobenNr() == null) ? messung.getExterneMessungsId().toString() : messung.getNebenprobenNr()
            );
        for (AuditTrailMessung a : audit) {
            //If audit entry shows a messwert, do not show if:
            // - StatusKombi is 1 (MST - nicht vergeben)
            // - User is not owner of the messung
            if (a.getTableName().equals("messwert")
                    && status.getStatusKombi() == 1
                    && !userInfo.getMessstellen().contains(probe.getMstId())) {
                continue;
            }
            entries.add(createEntry(a, mapper));

        }
        return responseNode.toString();
    }

    /**
     * Create a JSON object for an AuditTrailMessung entry.
     *
     * @param audit The table entry
     * @param mapper JSON object mapper
     */
    private ObjectNode createEntry(AuditTrailMessung audit, ObjectMapper mapper) {
        ObjectNode node = mapper.createObjectNode();
        node.put("timestamp", audit.getTstamp().getTime());
        node.put("type", audit.getTableName());
        node.put("action", audit.getAction());
        ObjectNode data = (ObjectNode)audit.getChangedFields();
        node.putPOJO("changedFields", data);
        if ("kommentar_m".equals(audit.getTableName())) {
            node.put("identifier", audit.getRowData().get("datum").toString());
        }
        if ("messwert".equals(audit.getTableName())) {
            String value = translateId(
                "messgroesse",
                "messgroesse",
                audit.getRowData().get("messgroesse_id").toString(),
                "id",
                Strings.STAMM);
            node.put("identifier", value);
        }
        return node;
    }

    /**
     * Translate a foreign key into the associated value.
     */
    private String translateId(
        String table,
        String field,
        String id,
        String idField,
        String source
    ) {
        EntityManager manager = repository.entityManager(source);
        String sql = "SELECT " + field + " FROM " + table + " WHERE " + idField +" = :id ;";
        javax.persistence.Query query = manager.createNativeQuery(sql);
        if (id == null) {
            return "";
        }
        try {
            int value = Integer.parseInt(id);
            query.setParameter("id", value);
        }
        catch (NumberFormatException nfe) {
            query.setParameter("id", id);
        }
        List<String> result = query.getResultList();
        return result.get(0);
    }

    private Long formatDate(String format, String date) {
        DateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            return inFormat.parse(date).getTime();
        } catch (ParseException e) {
            return 0L;
        }
    }

    /**
     * Translate all known foreign keys
     */
    private ObjectNode translateValues(ObjectNode node) {
        for (Iterator<String> i = node.fieldNames(); i.hasNext();) {
            String key = i.next();
            if (mappings.containsKey(key)) {
                TableMapper m = mappings.get(key);
                if (m.getMappingTable().equals("date")) {
                    Long value = formatDate(m.getValueField(), node.get(key).asText());
                    node.put(key, value);
                }
                else {
                    String value = translateId(
                        m.getMappingTable(),
                        m.getValueField(),
                        !node.get(key).isNull() ? node.get(key).asText() : null,
                        "id",
                        Strings.STAMM);
                    node.put(key, value);
                }
            }
        }
        return node;
    }
}
