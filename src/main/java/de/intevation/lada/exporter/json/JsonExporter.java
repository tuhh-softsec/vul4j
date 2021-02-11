/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.exporter.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.log4j.Logger;

import de.intevation.lada.exporter.ExportConfig;
import de.intevation.lada.exporter.ExportFormat;
import de.intevation.lada.exporter.Exporter;
import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.model.stammdaten.Betriebsart;
import de.intevation.lada.model.stammdaten.Datenbasis;
import de.intevation.lada.model.stammdaten.Deskriptoren;
import de.intevation.lada.model.stammdaten.MessEinheit;
import de.intevation.lada.model.stammdaten.MessMethode;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.Messgroesse;
import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.ProbenZusatz;
import de.intevation.lada.model.stammdaten.Probenart;
import de.intevation.lada.model.stammdaten.Probenehmer;
import de.intevation.lada.model.stammdaten.Staat;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

/**
 * Exporter class for writing query results to JSON.
 *
 * @author <a href="mailto:raimund.renkert@intevation.de">Raimund Renkert</a>
 */
@ExportConfig(format = ExportFormat.JSON)
public class JsonExporter implements Exporter {

    private static final int ZEBS_COUNTER = 3;

    @Inject private Logger logger;

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    /**
     * Export a query result.
     * @param queryResult Result to export as list of maps. Every list item
     *                    represents a row,
     *                    while every map key represents a column
     * @param encoding Ignored. Result is always UTF_8.
     * @param options Export options as JSON Object. Options are: <p>
     *        <ul>
     *          <li> id: Name of the id column, mandatory </li>
     *          <li> subData: key of the subData json object, optional </li>
     *          <li> timezone: Target timezone for timestamp conversion </li>
     *        </ul>
     *
     * @param columnsToInclude List of column names to include in the export.
     *                         If not set, all columns will be exported
     * @return Export result as input stream or null if the export failed
     */
    @Override
    @SuppressWarnings("unchecked")
    public InputStream export(
        List<Map<String, Object>> queryResult,
        String encoding,
        JsonObject options,
        ArrayList<String> columnsToInclude
    ) {
        if (!options.containsKey("id")) {
            logger.error("No id column given");
            return null;
        }
        String subDataKey = options.getString("subData", "");

        final JsonObjectBuilder builder = Json.createObjectBuilder();
        final String timezone =
            options.containsKey("timezone")
            ? options.getString("timezone") : "UTC";
        String idColumn = options.getString("id");

        //For each result
        queryResult.forEach(item -> {
            JsonObjectBuilder rowBuilder = Json.createObjectBuilder();
            //Add value for each column
            columnsToInclude.forEach(key -> {
                Object value = item.getOrDefault(key, null);
                if (value == null) {
                    rowBuilder.add(key, JsonValue.NULL);
                    return;
                }
                if (value instanceof Integer) {
                    rowBuilder.add(key, (Integer) value);
                } else if (value instanceof Double) {
                    rowBuilder.add(key, (Double) value);
                } else if (value instanceof Timestamp) {
                    //Convert to target timezone
                    Timestamp time = (Timestamp) value;
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date(time.getTime()));
                    SimpleDateFormat sdf =
                        new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    sdf.setTimeZone(TimeZone.getTimeZone(timezone));
                    rowBuilder.add(key, sdf.format(calendar.getTime()));
                } else {
                    rowBuilder.add(key, value.toString());
                }
            });
            //Append id
            if (!subDataKey.isEmpty()
                && item.containsKey(subDataKey)
                && item.get(subDataKey) instanceof List<?>
            ) {
                List<Map<String, Object>> subData =
                    (List<Map<String, Object>>) item.get(subDataKey);
                rowBuilder.add(subDataKey, createSubdataArray(subData));
            }
            builder.add(item.get(idColumn).toString(), rowBuilder);
        });
        return new ByteArrayInputStream(
            builder.build().toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Create a json array from a list of sub data maps.
     * @param subData Sub data as list of maps
     * @return Json array
     */
    private JsonArray createSubdataArray(List<Map<String, Object>> subData) {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        subData.forEach(map -> {
            JsonObjectBuilder itemBuilder = Json.createObjectBuilder();
            map.forEach((key, value) -> {
                if (value == null) {
                    itemBuilder.add(key, JsonValue.NULL);
                    return;
                }
                if (value instanceof Integer) {
                    itemBuilder.add(key, (Integer) value);
                } else if (value instanceof Double) {
                    itemBuilder.add(key, (Double) value);
                } else {
                    itemBuilder.add(key, value.toString());
                }
            });
            arrayBuilder.add(itemBuilder.build());
        });
        return arrayBuilder.build();
    }

    /**
     * Export Probe objects as JSON.
     * @param proben List of Probe IDs to export.
     * @param messungen Ignored. All associated Messung objects are exported.
     * @param encoding Ignored. Result is always UTF_8.
     * @param userInfo UserInfo
     * @return Export result as InputStream or null if the export failed
     */
    @Override
    public InputStream exportProben(
        List<Integer> proben,
        List<Integer> messungen,
        Charset encoding,
        UserInfo userInfo
    ) {
        //Create json.
        String json = createJsonString(proben, userInfo);
        if (json == null) {
            return null;
        }

        InputStream in = new ByteArrayInputStream(
            json.getBytes(StandardCharsets.UTF_8));
        try {
            in.close();
        } catch (IOException e) {
            logger.debug("Error while closing Stream.", e);
            return null;
        }
        return in;
    }

    private String createJsonString(List<Integer> probeIds, UserInfo userInfo) {
        QueryBuilder<Probe> builder = new QueryBuilder<Probe>(
                repository.entityManager(Strings.LAND),
                Probe.class
            );
        for (Integer id : probeIds) {
            builder.or("id", id);
        }
        List<Probe> proben =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(proben);
            JsonNode nodes = mapper.readTree(tmp);
            addSubObjects(nodes);
            return mapper.writeValueAsString(nodes);
        } catch (IOException e) {
            logger.debug("Error parsing object structure.", e);
            return null;
        }
    }

    private JsonNode addSubObjects(JsonNode proben) {
        for (int i = 0; i < proben.size(); i++) {
            ObjectNode probe = (ObjectNode) proben.get(i);
            Probenart art = repository.getByIdPlain(
                Probenart.class,
                probe.get("probenartId").asInt(),
                Strings.STAMM);
            Datenbasis datenbasis = repository.getByIdPlain(
                Datenbasis.class,
                probe.get("datenbasisId").asInt(),
                Strings.STAMM);
            Umwelt umw = repository.getByIdPlain(
                Umwelt.class,
                probe.get("umwId").asText(),
                Strings.STAMM);
            probe.put("probenart",
                art == null ? "" : art.getProbenart());
            probe.put("datenbasis",
                datenbasis == null ? "" : datenbasis.getDatenbasis());
            probe.put("umw", umw == null ? "" : umw.getUmweltBereich());
            if (probe.get("baId").asInt() != 0) {
                Betriebsart ba = repository.getByIdPlain(
                    Betriebsart.class,
                    probe.get("baId").asInt(),
                    Strings.STAMM);
                probe.put("messRegime", ba.getName());
            }
            if (probe.get("mplId").asInt() != 0) {
                MessprogrammKategorie mpl = repository.getByIdPlain(
                    MessprogrammKategorie.class,
                    probe.get("mplId").asInt(),
                    Strings.STAMM);
                probe.put("mplCode", mpl.getCode());
                probe.put("mpl", mpl.getBezeichnung());
            }
            if (probe.get("probeNehmerId").asInt() != 0) {
                Probenehmer probenehmer = repository.getByIdPlain(
                    Probenehmer.class,
                    probe.get("probeNehmerId").asInt(),
                    Strings.STAMM);
                probe.put("prnId", probenehmer.getPrnId());
                probe.put("prnBezeichnung", probenehmer.getBezeichnung());
                probe.put(
                    "prnKurzBezeichnung", probenehmer.getKurzBezeichnung());
            }

            addMessungen(proben.get(i));
            addKommentare(proben.get(i));
            addZusatzwerte(proben.get(i));
            addDeskriptoren(proben.get(i));
            addOrtszuordung(proben.get(i));
            addMessstelle(proben.get(i));
        }
        return proben;
    }

    private void addMessstelle(JsonNode node) {
        MessStelle messstelle = repository.getByIdPlain(
            MessStelle.class,
            node.get("mstId").asText(),
            Strings.STAMM);
        MessStelle laborMessstelle = repository.getByIdPlain(
            MessStelle.class,
            node.get("laborMstId").asText(),
            Strings.STAMM);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(messstelle);
            String tmp2 = mapper.writeValueAsString(laborMessstelle);
            JsonNode nodes = mapper.readTree(tmp);
            JsonNode nodes2 = mapper.readTree(tmp2);
            ((ObjectNode) node).set("messstelle", nodes);
            ((ObjectNode) node).set("labormessstelle", nodes2);
        } catch (IOException e) {
            logger.debug("Could not export Messstelle for Probe "
                + node.get("externeProbeId").asText());
        }
    }

    private void addMessungen(JsonNode probe) {
        QueryBuilder<Messung> builder = new QueryBuilder<Messung>(
                repository.entityManager(Strings.LAND),
                Messung.class
            );
        builder.and("probeId", probe.get("id").asInt());
        List<Messung> messungen =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(messungen);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                MessMethode mmt = repository.getByIdPlain(
                    MessMethode.class,
                    nodes.get(i).get("mmtId").asText(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put("mmt",
                    mmt == null ? "" : mmt.getMessmethode());
                addMesswerte(nodes.get(i));
                addMessungsKommentare(nodes.get(i));
                addStatusProtokoll(nodes.get(i));
            }
            ((ObjectNode) probe).set("messungen", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Messungen for Probe "
                + probe.get("externeProbeId").asText());
        }
    }

    private void addKommentare(JsonNode probe) {
        QueryBuilder<KommentarP> builder = new QueryBuilder<KommentarP>(
                repository.entityManager(Strings.LAND),
                KommentarP.class
            );
        builder.and("probeId", probe.get("id").asInt());
        List<KommentarP> kommentare =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(kommentare);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                MessStelle mst = repository.getByIdPlain(
                    MessStelle.class,
                    nodes.get(i).get("mstId").asText(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put(
                    "mst",
                    mst.getMessStelle());
            }
            ((ObjectNode) probe).set("kommentare", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Kommentare for Probe "
                + probe.get("externeProbeId").asText());
        }
    }

    private void addZusatzwerte(JsonNode probe) {
        QueryBuilder<ZusatzWert> builder = new QueryBuilder<ZusatzWert>(
                repository.entityManager(Strings.LAND),
                ZusatzWert.class
            );
        builder.and("probeId", probe.get("id").asInt());
        List<ZusatzWert> zusatzwerte =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(zusatzwerte);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                ProbenZusatz pz = repository.getByIdPlain(
                    ProbenZusatz.class,
                    nodes.get(i).get("pzsId").asText(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put(
                    "pzwGroesse", pz.getBeschreibung());
                Integer mehId = pz.getMessEinheitId();
                MessEinheit meh = repository.getByIdPlain(
                    MessEinheit.class,
                    mehId,
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put(
                    "meh", meh.getEinheit());
            }
            ((ObjectNode) probe).set("zusatzwerte", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Zusatzwerte for Probe "
                + probe.get("externeProbeId").asText());
        }
    }

    private void addDeskriptoren(JsonNode probe) {
        String desk = probe.get("mediaDesk").asText();
        String[] parts = desk.split(" ");
        if (parts.length <= 1) {
            return;
        }

        QueryBuilder<Deskriptoren> builder = new QueryBuilder<Deskriptoren>(
                repository.entityManager(Strings.STAMM),
                Deskriptoren.class
            );
        int vorgaenger = 0;
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        boolean isZebs = Integer.parseInt(parts[1]) == 1;
        int hdV = 0;
        int ndV = 0;
        for (int i = 0; i < parts.length - 1; i++) {
            String beschreibung = "";
            if (Integer.parseInt(parts[i + 1]) != 0) {
                builder.and("ebene", i);
                builder.and("sn", Integer.parseInt(parts[i + 1]));
                if (i != 0) {
                    builder.and("vorgaenger", vorgaenger);
                }
                List<Deskriptoren> found =
                    repository.filterPlain(builder.getQuery(), Strings.STAMM);
                if (!found.isEmpty()) {
                    beschreibung = found.get(0).getBeschreibung();
                    if ((isZebs && i < ZEBS_COUNTER)
                        || (!isZebs && i < 1)
                    ) {
                        if (i < ZEBS_COUNTER) {
                            hdV = found.get(0).getId();
                        }
                        if (isZebs && i == 1) {
                            ndV = found.get(0).getId();
                        }
                        vorgaenger = hdV;
                    } else {
                        if (!isZebs && i == 1) {
                            ndV = found.get(0).getId();
                        }
                        vorgaenger = ndV;
                    }
                }
            }
            node.put("S" + i, beschreibung);
            builder = builder.getEmptyBuilder();
        }
        ((ObjectNode) probe).set("deskriptoren", node);
    }

    private void addMesswerte(JsonNode node) {
        QueryBuilder<Messwert> builder = new QueryBuilder<Messwert>(
                repository.entityManager(Strings.LAND),
                Messwert.class
            );
        builder.and("messungsId", node.get("id").asInt());
        List<Messwert> messwerte =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(messwerte);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                MessEinheit meh = repository.getByIdPlain(
                    MessEinheit.class,
                    nodes.get(i).get("mehId").asInt(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put("meh",
                    meh == null ? "" : meh.getEinheit());
                Messgroesse mg = repository.getByIdPlain(
                    Messgroesse.class,
                    nodes.get(i).get("messgroesseId").asInt(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put("messgroesse",
                    mg == null ? "" : mg.getMessgroesse());
            }
            ((ObjectNode) node).set("messwerte", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Messwerte for Messung "
                + node.get("nebenprobenNr").asText());
        }
    }

    private void addMessungsKommentare(JsonNode node) {
        QueryBuilder<KommentarM> builder = new QueryBuilder<KommentarM>(
                repository.entityManager(Strings.LAND),
                KommentarM.class
            );
        builder.and("messungsId", node.get("id").asInt());
        List<KommentarM> kommentare =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(kommentare);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                MessStelle mst = repository.getByIdPlain(
                    MessStelle.class,
                    nodes.get(i).get("mstId").asText(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put(
                    "mst",
                    mst.getMessStelle());
            }
            ((ObjectNode) node).set("kommentare", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Kommentare for Messung "
                + node.get("nebenprobenNr").asText());
        }
    }

    private void addStatusProtokoll(JsonNode node) {
        QueryBuilder<StatusProtokoll> builder =
            new QueryBuilder<StatusProtokoll>(
                repository.entityManager(Strings.LAND),
                StatusProtokoll.class
            );
        builder.and("messungsId", node.get("id").asInt());
        List<StatusProtokoll> status =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(status);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                StatusKombi kombi = repository.getByIdPlain(
                    StatusKombi.class,
                    nodes.get(i).get("statusKombi").asInt(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put(
                    "statusStufe",
                    kombi.getStatusStufe().getStufe());
                ((ObjectNode) nodes.get(i)).put(
                    "statusWert",
                    kombi.getStatusWert().getWert());
                MessStelle mst = repository.getByIdPlain(
                    MessStelle.class,
                    nodes.get(i).get("mstId").asText(),
                    Strings.STAMM);
                ((ObjectNode) nodes.get(i)).put(
                    "mst",
                    mst.getMessStelle());
            }
            ((ObjectNode) node).set("statusprotokoll", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Statusprotokoll for Messung "
                + node.get("nebenprobenNr").asText());
        }
    }

    private void addOrtszuordung(JsonNode node) {
        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
                repository.entityManager(Strings.LAND),
                Ortszuordnung.class
            );
        builder.and("probeId", node.get("id").asInt());
        List<Ortszuordnung> ortszuordnung =
            repository.filterPlain(builder.getQuery(), Strings.LAND);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(ortszuordnung);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                addOrt(nodes.get(i));
            }
            ((ObjectNode) node).set("ortszuordnung", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Ortszuordnugen for Probe "
                + node.get("externeProbeId").asText());
        }
    }

    private void addOrt(JsonNode node) {
        Ort ort = repository.getByIdPlain(
            Ort.class,
            node.get("ortId").asInt(),
            Strings.STAMM);
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(ort);
            JsonNode oNode = mapper.readTree(tmp);
            Verwaltungseinheit ve = repository.getByIdPlain(
                Verwaltungseinheit.class,
                oNode.get("gemId").asText(),
                Strings.STAMM);
            ((ObjectNode) oNode).put("gem",
                ve == null ? "" : ve.getBezeichnung());
            if (oNode.get("staatId").isNull()) {
                ((ObjectNode) oNode).put("staat", "");
            } else {
                Staat staat = repository.getByIdPlain(
                        Staat.class,
                        oNode.get("staatId").asInt(),
                        Strings.STAMM);
                ((ObjectNode) oNode).put("staat", staat.getStaat());
            }
            ((ObjectNode) node).set("ort", oNode);
        } catch (IOException e) {
            logger.debug("Could not export Ort for Ortszuordnung "
                + node.get("id").asText());
        }
    }
}
