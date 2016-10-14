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
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
import de.intevation.lada.model.stammdaten.Deskriptoren;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;

@ExportConfig(format=ExportFormat.JSON)
public class JsonExporter implements Exporter {

    @Inject private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public InputStream export(List<Integer> proben, UserInfo userInfo) {
        logger.debug("export json");
        String json = "";

        //Create json.
        json = createJsonString(proben, userInfo);
        if (json == null) {
            return null;
        }

        InputStream in = new ByteArrayInputStream(json.getBytes());
        try {
            in.close();
        }
        catch (IOException e) {
            logger.debug("Error while closing Stream.");
            return null;
        }
        return in;
    }

    private String createJsonString(List<Integer> probeIds, UserInfo userInfo) {
        QueryBuilder<Probe> builder = new QueryBuilder<Probe>(
                repository.entityManager("land"),
                Probe.class
            );
        for (Integer id : probeIds) {
            builder.or("id", id);
        }
        List<Probe> proben = repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(proben);
            JsonNode nodes = mapper.readTree(tmp);
            addSubObjects(nodes);
            return mapper.writeValueAsString(nodes);
        } catch (IOException e) {
            logger.debug("Error parsing object structure.");
            return null;
        }
    }

    private JsonNode addSubObjects(JsonNode proben) {
        for (int i = 0; i < proben.size(); i++) {
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
            "stamm");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(messstelle);
            JsonNode nodes = mapper.readTree(tmp);
            ((ObjectNode)node).set("messstelle", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Messstelle for Probe "
                + node.get("idAlt").asText());
        }
    }

    private void addMessungen(JsonNode probe) {
        QueryBuilder<Messung> builder = new QueryBuilder<Messung>(
                repository.entityManager("land"),
                Messung.class
            );
        builder.and("probeId", probe.get("id").asInt());
        List<Messung> messungen = repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(messungen);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                addMesswerte(nodes.get(i));
                addMessungsKommentare(nodes.get(i));
                addStatusProtokoll(nodes.get(i));
            }
            ((ObjectNode)probe).set("messungen", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Messungen for Probe "
                + probe.get("idAlt").asText());
        }
    }

    private void addKommentare(JsonNode probe) {
        QueryBuilder<KommentarP> builder = new QueryBuilder<KommentarP>(
                repository.entityManager("land"),
                KommentarP.class
            );
        builder.and("probeId", probe.get("id").asInt());
        List<KommentarP> kommentare =
            repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(kommentare);
            JsonNode nodes = mapper.readTree(tmp);
            ((ObjectNode)probe).set("kommentare", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Kommentare for Probe "
                + probe.get("idAlt").asText());
        }
    }

    private void addZusatzwerte(JsonNode probe) {
        QueryBuilder<ZusatzWert> builder = new QueryBuilder<ZusatzWert>(
                repository.entityManager("land"),
                ZusatzWert.class
            );
        builder.and("probeId", probe.get("id").asInt());
        List<ZusatzWert> zusatzwerte=
            repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(zusatzwerte);
            JsonNode nodes = mapper.readTree(tmp);
            ((ObjectNode)probe).set("zusatzwerte", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Zusatzwerte for Probe "
                + probe.get("idAlt").asText());
        }
    }

    private void addDeskriptoren(JsonNode probe) {
        String desk = probe.get("mediaDesk").asText();
        String[] parts = desk.split(" ");

        QueryBuilder<Deskriptoren> builder = new QueryBuilder<Deskriptoren>(
                repository.entityManager("stamm"),
                Deskriptoren.class
            );
        int vorgaenger = 0;
        ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
        for (int i = 0; i < parts.length - 1; i++) {
            logger.debug("ebene: "  + i);
            logger.debug("sn: " + Integer.parseInt(parts[i+1]));
            String beschreibung = "";
            if (Integer.parseInt(parts[i+1]) != 0) {
                builder.and("ebene", i);
                builder.and("sn", Integer.parseInt(parts[i+1]));
                logger.debug(vorgaenger);
                if (vorgaenger != 0) {
                    builder.and("vorgaenger", vorgaenger);
                }
                List<Deskriptoren> found = repository.filterPlain(builder.getQuery(), "stamm");
                if (found.size() > 0) {
                    beschreibung = found.get(0).getBeschreibung();
                    vorgaenger = found.get(0).getId();
                }
            }
            node.put("S" + i, beschreibung);
            builder = builder.getEmptyBuilder();
        }
        ((ObjectNode)probe).set("deskriptoren", node);
    }

    private void addMesswerte(JsonNode node) {
        QueryBuilder<Messwert> builder = new QueryBuilder<Messwert>(
                repository.entityManager("land"),
                Messwert.class
            );
        builder.and("messungsId", node.get("id").asInt());
        List<Messwert> messwerte =
            repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(messwerte);
            JsonNode nodes = mapper.readTree(tmp);
            ((ObjectNode)node).set("messwerte", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Messwerte for Messung "
                + node.get("nebenprobenNr").asText());
        }
    }

    private void addMessungsKommentare(JsonNode node) {
        QueryBuilder<KommentarM> builder = new QueryBuilder<KommentarM>(
                repository.entityManager("land"),
                KommentarM.class
            );
        builder.and("messungsId", node.get("id").asInt());
        List<KommentarM> kommentare =
            repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(kommentare);
            JsonNode nodes = mapper.readTree(tmp);
            ((ObjectNode)node).set("kommentare", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Kommentare for Messung "
                + node.get("nebenprobenNr").asText());
        }
    }

    private void addStatusProtokoll(JsonNode node) {
        QueryBuilder<StatusProtokoll> builder = new QueryBuilder<StatusProtokoll>(
                repository.entityManager("land"),
                StatusProtokoll.class
            );
        builder.and("messungsId", node.get("id").asInt());
        List<StatusProtokoll> status =
            repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(status);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                StatusKombi kombi = repository.getByIdPlain(
                    StatusKombi.class,
                    nodes.get(i).get("statusKombi").asInt(),
                    "stamm");
                ((ObjectNode)nodes.get(i)).put(
                    "statusStufe",
                    kombi.getStatusStufe().getStufe());
                ((ObjectNode)nodes.get(i)).put(
                    "statusWert",
                    kombi.getStatusWert().getWert());
            }
            ((ObjectNode)node).set("statusprotokoll", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Statusprotokoll for Messung "
                + node.get("nebenprobenNr").asText());
        }
    }

    private void addOrtszuordung(JsonNode node) {
        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
                repository.entityManager("land"),
                Ortszuordnung.class
            );
        builder.and("probeId", node.get("id").asInt());
        List<Ortszuordnung> ortszuordnung =
            repository.filterPlain(builder.getQuery(), "land");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(ortszuordnung);
            JsonNode nodes = mapper.readTree(tmp);
            for (int i = 0; i < nodes.size(); i++) {
                addOrt(nodes.get(i));
            }
            ((ObjectNode)node).set("ortszuordnung", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Ortszuordnugen for Probe "
                + node.get("idAlt").asText());
        }
    }

    private void addOrt(JsonNode node) {
        QueryBuilder<Ort> builder = new QueryBuilder<Ort>(
                repository.entityManager("stamm"),
                Ort.class
            );
        builder.and("id", node.get("ortId").asInt());
        List<Ort> ort=
            repository.filterPlain(builder.getQuery(), "stamm");
        final ObjectMapper mapper = new ObjectMapper();
        try {
            String tmp = mapper.writeValueAsString(ort);
            JsonNode nodes = mapper.readTree(tmp);
            ((ObjectNode)node).set("ort", nodes);
        } catch (IOException e) {
            logger.debug("Could not export Ort for Ortszuordnung "
                + node.get("id").asText());
        }
    }
}
