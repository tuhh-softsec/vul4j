/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer.laf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import de.intevation.lada.factory.OrtFactory;
import de.intevation.lada.factory.ProbeFactory;
import de.intevation.lada.importer.Identified;
import de.intevation.lada.importer.Identifier;
import de.intevation.lada.importer.IdentifierConfig;
import de.intevation.lada.importer.ObjectMerger;
import de.intevation.lada.importer.ReportItem;
import de.intevation.lada.model.land.KommentarM;
import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.land.ZusatzWert;
import de.intevation.lada.model.stammdaten.Datenbasis;
import de.intevation.lada.model.stammdaten.ImporterConfig;
import de.intevation.lada.model.stammdaten.KoordinatenArt;
import de.intevation.lada.model.stammdaten.MessEinheit;
import de.intevation.lada.model.stammdaten.MessMethode;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.Messgroesse;
import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
import de.intevation.lada.model.stammdaten.MessprogrammTransfer;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Ortszusatz;
import de.intevation.lada.model.stammdaten.ProbenZusatz;
import de.intevation.lada.model.stammdaten.Probenart;
import de.intevation.lada.model.stammdaten.Probenehmer;
import de.intevation.lada.model.stammdaten.ReiProgpunktGruppe;
import de.intevation.lada.model.stammdaten.Staat;
import de.intevation.lada.model.stammdaten.StatusErreichbar;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.model.stammdaten.Zeitbasis;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.MesswertNormalizer;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationConfig;

public class LafObjectMapper {

    @Inject
    private Logger logger;

    @Inject
    @AuthorizationConfig(type=AuthorizationType.HEADER)
    private Authorization authorizer;

    @Inject
    @ValidationConfig(type="Probe")
    private Validator probeValidator;

    @Inject
    @ValidationConfig(type="Messung")
    private Validator messungValidator;

    @Inject
    @ValidationConfig(type="Ort")
    private Validator ortValidator;

    @Inject
    @IdentifierConfig(type="Probe")
    private Identifier probeIdentifier;

    @Inject
    @IdentifierConfig(type="Messung")
    private Identifier messungIdentifier;

    @Inject
    @ValidationConfig(type="Messwert")
    private Validator messwertValidator;

    @Inject
    private ObjectMerger merger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    @Inject
    private ProbeFactory factory;

    @Inject OrtFactory ortFactory;

    private Map<String, List<ReportItem>> errors;
    private Map<String, List<ReportItem>> warnings;
    private List<ReportItem> currentErrors;
    private List<ReportItem> currentWarnings;

    private int currentZeitbasis;


    private UserInfo userInfo;

    private List<ImporterConfig> config;

    public void mapObjects(LafRawData data) {
        errors = new HashMap<>();
        warnings = new HashMap<>();
        for (int i = 0; i < data.getProben().size(); i++) {
            create(data.getProben().get(i));
        }
    }

    private void create(LafRawData.Probe object) {
        currentWarnings = new ArrayList<>();
        currentErrors = new ArrayList<>();
        Probe probe = new Probe();
        Iterator<ImporterConfig> importerConfig = config.iterator();
        while (importerConfig.hasNext()) {
            ImporterConfig current = importerConfig.next();
            if ("zeitbasis".equals(current.getName())) {
                currentZeitbasis = Integer.valueOf(current.getToValue());
            }
        }
        if (object.getAttributes().containsKey("ZEITBASIS")) {
            List<ImporterConfig> cfg = getImporterConfigByAttributeUpper("ZEITBASIS");
            String attribute = object.getAttributes().get("ZEITBASIS");
            if (!cfg.isEmpty() && attribute.equals(cfg.get(0).getFromValue())) {
                attribute = cfg.get(0).getToValue();
            }
            QueryBuilder<Zeitbasis> builder =
                new QueryBuilder<>(
                    repository.entityManager(Strings.STAMM),
                    Zeitbasis.class);
            builder.and("bezeichnung", attribute);
            List<Zeitbasis> zb = repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (zb == null || zb.isEmpty()) {
                currentWarnings.add(new ReportItem("ZEITBASIS", object.getAttributes().get("ZEITBASIS"), 675));
            }
            else {
                currentZeitbasis = zb.get(0).getId();
            }
        }
        else if (object.getAttributes().containsKey("ZEITBASIS_S")) {
            currentZeitbasis = Integer.valueOf(object.getAttributes().get("ZEITBASIS_S"));
            Zeitbasis zeitbasis = repository.getByIdPlain(
                Zeitbasis.class,
                currentZeitbasis,
                Strings.STAMM);
            if ( zeitbasis == null) {
                currentWarnings.add(new ReportItem("ZEITBASIS_S", object.getAttributes().get("ZEITBASIS_S"), 675));
            }
        }

        // Fill the object with data
        for (Entry<String, String> attribute : object.getAttributes().entrySet()) {
            addProbeAttribute(attribute, probe);
        }
        doDefaults(probe);
        doConverts(probe);
        doTransforms(probe);
        if (probe.getLaborMstId() == null) {
            probe.setLaborMstId(probe.getMstId());
        }
        // Use the deskriptor string to find the medium
        probe = factory.findMediaDesk(probe);
        if (probe.getUmwId() == null) {
            factory.findUmweltId(probe);
        }

        // Check if the user is authorized to create the probe
        boolean isAuthorized = authorizer.isAuthorized(userInfo, probe, Probe.class);
        if (!isAuthorized) {
            ReportItem err = new ReportItem();
            err.setCode(699);
            err.setKey(userInfo.getName());
            err.setValue("Messstelle " + probe.getMstId());
            currentWarnings.clear();
            currentErrors.add(err);
            errors.put(object.getIdentifier(), new ArrayList<ReportItem>(currentErrors));
            return;
        }
        // logProbe(probe);

        // Check for errors and warnings

        // Compare the probe with objects in the db
        Probe newProbe = null;
        boolean oldProbeIsReadonly = false;
        try {
            Identified i = probeIdentifier.find(probe);
            Probe old = (Probe)probeIdentifier.getExisting();
            // Matching probe was found in the db. Update it!
            if(i == Identified.UPDATE) {
                oldProbeIsReadonly = authorizer.isReadOnly(old.getId());
                if(oldProbeIsReadonly) {
                    newProbe = old;
                    currentWarnings.add(new ReportItem("probe", old.getExterneProbeId(), 676));
                } 
                else {
                    if(merger.merge(old, probe)) {
                        newProbe = old;
                    } else {
                        ReportItem err = new ReportItem();
                        err.setCode(604);
                        err.setKey("Database error");
                        err.setValue("");
                        currentErrors.add(err);
                        if (!currentErrors.isEmpty()) {
                            errors.put(object.getIdentifier(),
                                new ArrayList<ReportItem>(currentErrors));
                        }
                        if (!currentWarnings.isEmpty()) {
                            warnings.put(object.getIdentifier(),
                                new ArrayList<ReportItem>(currentWarnings));
                        }
                        return;
                    }
                }
            }
            // Probe was found but some data does not match
            else if(i == Identified.REJECT){
                ReportItem err = new ReportItem();
                err.setCode(671);
                err.setKey("duplicate");
                err.setValue("");
                currentErrors.add(err);
                if (!currentErrors.isEmpty()) {
                    errors.put(object.getIdentifier(),
                        new ArrayList<ReportItem>(currentErrors));
                }
                if (!currentWarnings.isEmpty()) {
                    warnings.put(object.getIdentifier(),
                        new ArrayList<ReportItem>(currentWarnings));
                }
                return;
            }
            // It is a brand new probe!
            else if(i == Identified.NEW){
                Violation violation = probeValidator.validate(probe);
                if (!violation.hasErrors()) {
                    Response created = repository.create(probe, Strings.LAND);
                    newProbe = ((Probe)created.getData());
                }
                else {
                    for (Entry<String, List<Integer>> err : violation.getErrors().entrySet()) {
                        for (Integer code : err.getValue()) {
                            currentErrors.add(new ReportItem("validation", err.getKey(), code));
                        }
                    }
                    for (Entry<String, List<Integer>> warn : violation.getWarnings().entrySet()) {
                        for (Integer code : warn.getValue()) {
                            currentWarnings.add(new ReportItem("validation", warn.getKey(), code));
                        }
                    }
                }
            }
        } catch (InvalidTargetObjectTypeException e) {
            ReportItem err = new ReportItem();
            err.setCode(604);
            err.setKey("not known");
            err.setValue("No valid Probe Object");
            currentErrors.add(err);
            if (!currentErrors.isEmpty()) {
                errors.put(object.getIdentifier(),
                    new ArrayList<ReportItem>(currentErrors));
            }
            if (!currentWarnings.isEmpty()) {
                warnings.put(object.getIdentifier(),
                    new ArrayList<ReportItem>(currentWarnings));
            }
            return;
        }

        if (newProbe != null) {
            if(!oldProbeIsReadonly) {
                // Create kommentar objects
                List<KommentarP> kommentare = new ArrayList<>();
                for (int i = 0; i < object.getKommentare().size(); i++) {
                    KommentarP tmp = createProbeKommentar(object.getKommentare().get(i), newProbe);
                    if (tmp != null) {
                        kommentare.add(tmp);
                    }
                }
                // Persist kommentar objects
                merger.mergeKommentare(newProbe, kommentare);

                // Create zusatzwert objects
                List<ZusatzWert> zusatzwerte = new ArrayList<>();
                for (int i = 0; i < object.getZusatzwerte().size(); i++) {
                    ZusatzWert tmp = createZusatzwert(object.getZusatzwerte().get(i), newProbe.getId());
                    if (tmp != null) {
                        zusatzwerte.add(tmp);
                    }
                }
                // Persist zusatzwert objects
                merger.mergeZusatzwerte(newProbe, zusatzwerte);

                // Special things for REI-Messpunkt
                if (probe.getReiProgpunktGrpId() != null ||
                    Integer.valueOf(3).equals(probe.getDatenbasisId()) ||
                    Integer.valueOf(4).equals(probe.getDatenbasisId())) {
                    createReiMesspunkt(object, newProbe);
                }
                else {
                    // Merge entnahmeOrt
                    createEntnahmeOrt(object.getEntnahmeOrt(), newProbe);

                    // Create ursprungsOrte
                    List<Ortszuordnung> uOrte = new ArrayList<>();
                    for (int i = 0; i < object.getUrsprungsOrte().size(); i++) {
                        Ortszuordnung tmp = createUrsprungsOrt(object.getUrsprungsOrte().get(i), newProbe);
                        if (tmp != null) {
                            uOrte.add(tmp);
                        }
                    }
                    // Persist ursprungsOrte
                    merger.mergeUrsprungsOrte(newProbe.getId(), uOrte);
                }
            }

            // Create messung objects
            for (int i = 0; i < object.getMessungen().size(); i++) {
                create(object.getMessungen().get(i), newProbe, newProbe.getMstId());
            }
            Violation violation = probeValidator.validate(newProbe);
            for (Entry<String, List<Integer>> err : violation.getErrors().entrySet()) {
                for (Integer code : err.getValue()) {
                    currentErrors.add(new ReportItem("validation", err.getKey(), code));
                }
            }
            for (Entry<String, List<Integer>> warn : violation.getWarnings().entrySet()) {
                for (Integer code : warn.getValue()) {
                    currentWarnings.add(new ReportItem("validation", warn.getKey(), code));
                }
            }
        }
        if (!currentErrors.isEmpty()) {
            if (errors.containsKey(object.getIdentifier())) {
                errors.get(object.getIdentifier()).addAll(currentErrors);
            }
            else {
                errors.put(object.getIdentifier(),
                    new ArrayList<ReportItem>(currentErrors));
            }
        }
        if (!currentWarnings.isEmpty()) {
            if (warnings.containsKey(object.getIdentifier())) {
                warnings.get(object.getIdentifier()).addAll(currentWarnings);
            }
            else {
                warnings.put(object.getIdentifier(),
                    new ArrayList<ReportItem>(currentWarnings));
            }
        }
    }

    private void doDefaults(Probe probe) {
        doDefaults(probe, Probe.class, "probe");
    }

    private void doConverts(Probe probe) {
        doConverts(probe, Probe.class, "probe");
    }

    private void doTransforms(Probe probe) {
        doTransformations(probe, Probe.class, "probe");
    }

    private void doDefaults(Messung messung) {
        doDefaults(messung, Messung.class, "messung");
    }

    private void doConverts(Messung messung) {
        doConverts(messung, Messung.class, "messung");
    }

    private void doTransforms(Messung messung) {
        doTransformations(messung, Messung.class, "messung");
    }

    private void doDefaults(Messwert messwert) {
        doDefaults(messwert, Messwert.class, "messwert");
    }

    private void doConverts(Messwert messwert) {
        doConverts(messwert, Messwert.class, "messwert");
    }

    private void doTransforms(Messwert messwert) {
        doTransformations(messwert, Messwert.class, "messwert");
    }

    private void doDefaults(ZusatzWert zusatzwert) {
        doDefaults(zusatzwert, ZusatzWert.class, "zusatwert");
    }

    private void doConverts(ZusatzWert zusatzwert) {
        doConverts(zusatzwert, ZusatzWert.class, "zusatzwert");
    }

    private void doTransforms(ZusatzWert zusatzwert) {
        doTransformations(zusatzwert, ZusatzWert.class, "zusatwert");
    }

    private void doDefaults(KommentarM kommentar) {
        doDefaults(kommentar, KommentarM.class, "kommentarm");
    }

    private void doConverts(KommentarM kommentar) {
        doConverts(kommentar, KommentarM.class, "kommentarm");
    }

    private void doTransforms(KommentarM kommentar) {
        doTransformations(kommentar, KommentarM.class, "kommentarm");
    }

    private void doDefaults(KommentarP kommentar) {
        doDefaults(kommentar, KommentarP.class, "kommentarp");
    }

    private void doConverts(KommentarP kommentar) {
        doConverts(kommentar, KommentarP.class, "kommentarp");
    }

    private void doTransforms(KommentarP kommentar) {
        doTransformations(kommentar, KommentarP.class, "kommentarp");
    }

    private void doDefaults(Ortszuordnung ort) {
        doDefaults(ort, Ortszuordnung.class, "ortszuordnung");
    }

    private void doConverts(Ortszuordnung ort) {
        doDefaults(ort, Ortszuordnung.class, "ortszuordnung");
    }

    private void doTransforms(Ortszuordnung ort) {
        doTransformations(ort, Ortszuordnung.class, "ortszuordnung");
    }

    private <T> void doDefaults(Object object, Class<T> clazz, String table) {
        Iterator<ImporterConfig> i = config.iterator();
        while (i.hasNext()) {
            ImporterConfig current = i.next();
            if (table.equals(current.getName()) &&
                "default".equals(current.getAction())
                ) {
                String attribute = current.getAttribute();
                Method getter;
                Method setter = null;
                try {
                    getter = clazz.getMethod("get" +
                        attribute.substring(0, 1).toUpperCase() +
                        attribute.substring(1));
                    String methodName = "set" +
                        attribute.substring(0, 1).toUpperCase() +
                        attribute.substring(1);
                    for (Method method : clazz.getMethods()) {
                        String name = method.getName();
                        if (!methodName.equals(name)) {
                            continue;
                        }
                        setter = method;
                        break;
                    }
                }
                catch(NoSuchMethodException | SecurityException e) {
                    logger.debug("attribute " + attribute + " does not exist");
                    return;
                }
                try {
                    Object value = getter.invoke(object);
                    if (value == null && setter != null) {
                        setter.invoke(object, current.getToValue());
                    }
                }
                catch(IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e
                ) {
                    logger.debug("Could not set attribute " + attribute);
                    return;
                }
            }
        }
    }

    private List<ImporterConfig> getImporterConfigByAttributeUpper(String attribute) {
        Iterator<ImporterConfig> i = config.iterator();
        List<ImporterConfig> result = new ArrayList<ImporterConfig>();
        while (i.hasNext()) {
            ImporterConfig current = i.next();
            if (current.getAttribute().toUpperCase().equals(attribute)) {
                result.add(current);
            }
        }
        return result;
    }

    private <T> void doConverts(Object object, Class<T> clazz, String table) {
        Iterator<ImporterConfig> i = config.iterator();
        while (i.hasNext()) {
            ImporterConfig current = i.next();
            if (table.equals(current.getName()) &&
                "convert".equals(current.getAction())
                ) {
                String attribute = current.getAttribute();
                Method getter;
                Method setter = null;
                try {
                    getter = clazz.getMethod("get" +
                        attribute.substring(0, 1).toUpperCase() +
                        attribute.substring(1));
                    String methodName = "set" +
                        attribute.substring(0, 1).toUpperCase() +
                        attribute.substring(1);
                    for (Method method : clazz.getMethods()) {
                        String name = method.getName();
                        if (!methodName.equals(name)) {
                            continue;
                        }
                        setter = method;
                        break;
                    }
                }
                catch(NoSuchMethodException | SecurityException e) {
                    logger.warn("attribute " + attribute + " does not exist");
                    return;
                }
                try {
                    Object value = getter.invoke(object);
                    if (value.equals(current.getFromValue()) &&
                        setter != null
                    ) {
                        setter.invoke(object, current.getToValue());
                    }
                }
                catch(IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e
                ) {
                    logger.warn("Could not convert attribute " + attribute);
                    return;
                }
            }
        }
    }

    private <T> void doTransformations(Object object, Class<T> clazz, String table) {
        Iterator<ImporterConfig> i = config.iterator();
        while (i.hasNext()) {
            ImporterConfig current = i.next();
            if (table.equals(current.getName()) &&
                "transform".equals(current.getAction())
                ) {
                String attribute = current.getAttribute();
                Method getter;
                Method setter = null;
                try {
                    getter = clazz.getMethod("get" +
                        attribute.substring(0, 1).toUpperCase() +
                        attribute.substring(1));
                    String methodName = "set" +
                        attribute.substring(0, 1).toUpperCase() +
                        attribute.substring(1);
                    for (Method method : clazz.getMethods()) {
                        String name = method.getName();
                        if (methodName.equals(name)) {
                            setter = method;
                            break;
                        }
                    }
                    if (setter == null) {
                        logger.warn("Could not transform attribute " + attribute);
                        return;
                    }
                }
                catch(NoSuchMethodException | SecurityException e) {
                    logger.warn("attribute " + attribute + " does not exist");
                    return;
                }
                try {
                    Object value = getter.invoke(object);
                    char from = (char) Integer.parseInt(current.getFromValue(), 16);
                    char to = (char) Integer.parseInt(current.getToValue(), 16);
                    value = value.toString().replaceAll("[" + String.valueOf(from) + "]", String.valueOf(to));
                    setter.invoke(object, value);
                }
                catch(IllegalAccessException |
                    IllegalArgumentException |
                    InvocationTargetException e
                ) {
                    logger.warn("Could not transform attribute " + attribute);
                    return;
                }
            }
        }
    }

    private void create(LafRawData.Messung object, Probe probe, String mstId) {
        Messung messung = new Messung();
        messung.setProbeId(probe.getId());

        // Fill the new messung with data
        for (Entry<String, String> attribute : object.getAttributes().entrySet()) {
            addMessungAttribute(attribute, messung);
        }
        doDefaults(messung);
        doConverts(messung);
        doTransforms(messung);
        // Check if the user is authorized to create the object
        if (!authorizer.isAuthorizedOnNew(userInfo, messung, Messung.class)) {
            ReportItem warn = new ReportItem();
            warn.setCode(699);
            warn.setKey(userInfo.getName());
            warn.setValue("Messung: " + messung.getNebenprobenNr());
            currentErrors.add(warn);
            return;
        }

        // Compare with messung objects in the db
        Messung newMessung = null;
        boolean oldMessungIsReadonly = false;
        try {
            Identified i = messungIdentifier.find(messung);
            Messung old = (Messung)messungIdentifier.getExisting();
            if (i == Identified.UPDATE) {
                oldMessungIsReadonly = authorizer.isMessungReadOnly(old.getId());
                if (oldMessungIsReadonly) {
                    currentErrors.add(new ReportItem("messung", old.getExterneMessungsId(), 676));
                    return;
                } else {
                    merger.mergeMessung(old, messung);
                    newMessung = old;
                    if (object.getAttributes().containsKey("BEARBEITUNGSSTATUS")) {
                        createStatusProtokoll(object.getAttributes().get("BEARBEITUNGSSTATUS"), newMessung, mstId);
                    }
                }
            }
            else if (i == Identified.REJECT) {
                ReportItem err = new ReportItem();
                err.setCode(631);
                err.setKey("identification");
                err.setValue("Messung");
                currentErrors.add(err);
                return;
            }
            else if (i == Identified.NEW) {
                // Check if Messung has all fields that have db constraints (validation rule?)
                if (messung.getMmtId() == null) {
                    ReportItem err = new ReportItem();
                    err.setCode(631);
                    err.setKey("not valid (missing Messmethode)");
                    err.setValue("Messung: " + messung.getNebenprobenNr());
                    currentErrors.add(err);
                    return;
                }

                // Create a new messung and the first status
                Response created = repository.create(messung, Strings.LAND);
                newMessung = ((Messung)created.getData());
                created = repository.getById(Messung.class, newMessung.getId(), Strings.LAND);
                newMessung = ((Messung)created.getData());
                if (object.getAttributes().containsKey("BEARBEITUNGSSTATUS")) {
                    createStatusProtokoll(object.getAttributes().get("BEARBEITUNGSSTATUS"), newMessung, mstId);
                }
            }
        }
        catch(InvalidTargetObjectTypeException e) {
            ReportItem err = new ReportItem();
            err.setCode(604);
            err.setKey("not valid");
            err.setValue("Messung: " + messung.getNebenprobenNr());
            currentErrors.add(err);
            return;
        }
        List<KommentarM> kommentare = new ArrayList<KommentarM>();
        for (int i = 0; i < object.getKommentare().size(); i++) {
            KommentarM tmp = createMessungKommentar(object.getKommentare().get(i), newMessung.getId(), probe);
            if (tmp != null) {
                kommentare.add(tmp);
            }
        }
        merger.mergeMessungKommentare(newMessung, kommentare);
        List<Messwert> messwerte = new ArrayList<Messwert>();
        List<Integer> messgroessenListe = new ArrayList<Integer>();
        for (int i = 0; i < object.getMesswerte().size(); i++) {
            Messwert tmp = createMesswert(object.getMesswerte().get(i), newMessung.getId());
            if (tmp != null) {
                if (messgroessenListe.contains(tmp.getMessgroesseId())) {
                    currentWarnings.add(new ReportItem(
                        (object.getMesswerte().get(i).get("MESSGROESSE_ID") == null) ?
                            "MESSWERT - MESSGROESSE" :
                            "MESSWERT - MESSGROESSE_ID", 
                        (object.getMesswerte().get(i).get("MESSGROESSE_ID") == null) ?
                            object.getMesswerte().get(i).get("MESSGROESSE").toString():
                            object.getMesswerte().get(i).get("MESSGROESSE_ID").toString(), 
                        672));
                }
                else {
                    messwerte.add(tmp);
                    messgroessenListe.add(tmp.getMessgroesseId());
                    Violation violation = messwertValidator.validate(tmp);
                    for (Entry<String, List<Integer>> err : violation.getErrors().entrySet()) {
                        for (Integer code : err.getValue()) {
                            currentErrors.add(new ReportItem(
                                "validation",
                                err.getKey() + "#" +
                                ((object.getMesswerte().get(i).get("MESSGROESSE_ID") == null) ?
                                    object.getMesswerte().get(i).get("MESSGROESSE").toString() :
                                    object.getMesswerte().get(i).get("MESSGROESSE_ID").toString()),
                                code));
                        }
                    }
                    for (Entry<String, List<Integer>> warn : violation.getWarnings().entrySet()) {
                        for (Integer code : warn.getValue()) {
                            currentWarnings.add(new ReportItem(
                                "validation",
                                warn.getKey() + "#" +
                                ((object.getMesswerte().get(i).get("MESSGROESSE_ID") == null) ?
                                    object.getMesswerte().get(i).get("MESSGROESSE").toString() :
                                    object.getMesswerte().get(i).get("MESSGROESSE_ID").toString()),
                                code));
                        }
                    }
                }
            }
        }
        messwerte = MesswertNormalizer.normalizeMesswerte(messwerte, probe.getUmwId(), repository);
        merger.mergeMesswerte(newMessung, messwerte);
        // Check for warnings and errors
        Violation violation = messungValidator.validate(newMessung);
        for (Entry<String, List<Integer>> err : violation.getErrors().entrySet()) {
            for (Integer code : err.getValue()) {
                currentErrors.add(new ReportItem("validation", err.getKey(), code));
            }
        }
        for (Entry<String, List<Integer>> warn : violation.getWarnings().entrySet()) {
            for (Integer code : warn.getValue()) {
                currentWarnings.add(new ReportItem("validation", warn.getKey(), code));
            }
        }
    }

    private KommentarP createProbeKommentar(Map<String, String> attributes, Probe probe) {
        if (attributes.get("TEXT").equals("")) {
            currentWarnings.add(new ReportItem("PROBENKOMMENTAR", "Text", 631));
            return null;
        };
        KommentarP kommentar = new KommentarP();
        kommentar.setProbeId(probe.getId());
        kommentar.setText(attributes.get("TEXT"));
        if (attributes.containsKey("MST_ID")) {
            kommentar.setMstId(attributes.get("MST_ID"));
        }
        else {
            kommentar.setMstId(probe.getMstId());
        }
        if (attributes.containsKey("DATE")) {
            String date = attributes.get("DATE") + " " + attributes.get("TIME");
            kommentar.setDatum(getDate(date));
        }
        else {
            kommentar.setDatum(Timestamp.from(Instant.now().atZone(ZoneOffset.UTC).toInstant()));
        }
        doDefaults(kommentar);
        doConverts(kommentar);
        doTransforms(kommentar);
        if (!userInfo.getMessstellen().contains(kommentar.getMstId())) {
            currentWarnings.add(new ReportItem(userInfo.getName(), "Kommentar: " + kommentar.getMstId(), 699));
            return null;
        }
        return kommentar;
    }

    private ZusatzWert createZusatzwert(Map<String, String> attributes, int probeId) {
        ZusatzWert zusatzwert = new ZusatzWert();
        zusatzwert.setProbeId(probeId);
        if (attributes.containsKey("MESSFEHLER")) {
            zusatzwert.setMessfehler(Float.valueOf(attributes.get("MESSFEHLER").replaceAll(",", ".")));
        }
        String wert = attributes.get("MESSWERT_PZS");
        if (wert.startsWith("<")) {
            wert = wert.substring(1);
            zusatzwert.setKleinerAls("<");
        }
        zusatzwert.setMesswertPzs(Double.valueOf(wert.replaceAll(",", ".")));
        List<ImporterConfig> cfgs = getImporterConfigByAttributeUpper("ZUSATZWERT");
        String attribute = attributes.get("PZS");
        boolean isId = false;
        if (attribute == null) {
            attribute = attributes.get("PZS_ID");
            isId = true;
        }
        for (int i = 0; i < cfgs.size(); i++) {
            ImporterConfig cfg = cfgs.get(i);
            if (cfg.getAction().equals("convert") &&
                cfg.getFromValue().equals(attribute)
            ) {
                attribute = cfg.getToValue();
            }
            if (cfg.getAction().equals("transform")) {
                char from = (char) Integer.parseInt(cfg.getFromValue(), 16);
                char to = (char) Integer.parseInt(cfg.getToValue(), 16);
                attribute = attribute.replaceAll("[" + String.valueOf(from) + "]", String.valueOf(to));
            }
        }
        QueryBuilder<ProbenZusatz> builder =
            new QueryBuilder<ProbenZusatz>(
                repository.entityManager(Strings.STAMM),
                ProbenZusatz.class);
        if (isId) {
            builder.and("id", attribute);
        }
        else {
            builder.and("zusatzwert", attribute);
        }
        List<ProbenZusatz> zusatz =
            (List<ProbenZusatz>)repository.filterPlain(
                builder.getQuery(),
                Strings.STAMM);

        doDefaults(zusatzwert);
        doConverts(zusatzwert);
        doTransforms(zusatzwert);
        if (zusatz == null || zusatz.isEmpty()) {
            currentWarnings.add(new ReportItem(
                (isId) ? "PROBENZUSATZBESCHREIBUNG" : "PZB_S", 
                attribute, 
                675));
            return null;
        }
        zusatzwert.setPzsId(zusatz.get(0).getId());
        return zusatzwert;
    }

    private Messwert createMesswert(Map<String, String> attributes, int messungsId) {
        Messwert messwert = new Messwert();
        messwert.setMessungsId(messungsId);
        if (attributes.containsKey("MESSGROESSE_ID")) {
                Messgroesse messgreosse = repository.getByIdPlain(
                    Messgroesse.class,
                    Integer.valueOf(attributes.get("MESSGROESSE_ID")),
                    Strings.STAMM);
            if ( messgreosse == null) {
                currentWarnings.add(new ReportItem("MESSWERT - MESSGROESSE_ID", attributes.get("MESSGROESSE_ID"), 675));
                return null;
            }
            messwert.setMessgroesseId(Integer.valueOf(attributes.get("MESSGROESSE_ID")));
        }
        else if (attributes.containsKey("MESSGROESSE")) {
            List<ImporterConfig> cfgs = getImporterConfigByAttributeUpper("MESSGROESSE");
            String attribute = attributes.get("MESSGROESSE");
            for (int i = 0; i< cfgs.size(); i++) {
                ImporterConfig cfg = cfgs.get(i);
                if (cfg != null &&
                    cfg.getAction().equals("convert") &&
                    cfg.getFromValue().equals(attribute)
                ) {
                    attribute = cfg.getToValue();
                }
                if (cfg != null && cfg.getAction().equals("transform")) {
                    char from = (char) Integer.parseInt(cfg.getFromValue(), 16);
                    char to = (char) Integer.parseInt(cfg.getToValue(), 16);
                    attribute = attribute.replaceAll("[" + String.valueOf(from) + "]", String.valueOf(to));
                }
            }
            QueryBuilder<Messgroesse> builder =
                new QueryBuilder<Messgroesse>(
                    repository.entityManager(Strings.STAMM),
                    Messgroesse.class);
            // accept various nuclide notations (e.g. "Cs-134", "CS 134", "Cs134", "SC134", ...)
            String messgroesseString = attribute;
            if (attribute.matches("^[A-Za-z]+( |-)?[0-9].*")) {
                messgroesseString = attribute.substring(0,1).toUpperCase() +
                                    attribute.replaceAll("(-| )?[0-9].*","").substring(1).toLowerCase() +
                                    '-' +
                                    attribute.replaceFirst("^[A-Za-z]*(-| )?","").toLowerCase();
            }

            builder.and("messgroesse", messgroesseString);
            List<Messgroesse> groesse =
                (List<Messgroesse>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (groesse == null || groesse.isEmpty()) {
                currentWarnings.add(new ReportItem("MESSWERT - MESSGROESSE", attributes.get("MESSGROESSE"), 675));
                return null;
            }
            messwert.setMessgroesseId(groesse.get(0).getId());
        }
        if (attributes.containsKey("MESSEINHEIT_ID")) {
                MessEinheit messEinheit = repository.getByIdPlain(
                    MessEinheit.class,
                    Integer.valueOf(attributes.get("MESSEINHEIT_ID")),
                    Strings.STAMM);
            if ( messEinheit == null) {
                currentWarnings.add(new ReportItem("MESSWERT - MESSEINHEIT_ID", attributes.get("MESSEINHEIT_ID"), 675));
                return null;
            }
            messwert.setMehId(Integer.valueOf(attributes.get("MESSEINHEIT_ID")));
        }
        else if (attributes.containsKey("MESSEINHEIT")) {
            List<ImporterConfig> cfgs = getImporterConfigByAttributeUpper("MESSEINHEIT");
            String attribute = attributes.get("MESSEINHEIT");
            for (int i = 0; i < cfgs.size(); i++) {
                ImporterConfig cfg = cfgs.get(i);
                if (cfg != null &&
                    cfg.getAction().equals("convert") &&
                    cfg.getFromValue().equals(attribute)
                ) {
                    attribute = cfg.getToValue();
                }
                if (cfg != null && cfg.getAction().equals("transform")) {
                    char from = (char) Integer.parseInt(cfg.getFromValue(), 16);
                    char to = (char) Integer.parseInt(cfg.getToValue(), 16);
                    attribute = attribute.replaceAll("[" + String.valueOf(from) + "]", String.valueOf(to));
                }
            }
            QueryBuilder<MessEinheit> builder =
                new QueryBuilder<MessEinheit>(
                    repository.entityManager(Strings.STAMM),
                    MessEinheit.class);
            builder.and("einheit", attribute);
            List<MessEinheit> einheit =
                (List<MessEinheit>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (einheit == null || einheit.isEmpty()) {
                currentWarnings.add(new ReportItem("MESSWERT - MESSEINHEIT", attribute, 675));
                return null;
            }
            messwert.setMehId(einheit.get(0).getId());
        }

        String wert = attributes.get("MESSWERT");
        if (wert.startsWith("<")) {
            wert = wert.substring(1);
            messwert.setMesswertNwg("<");
        }
        messwert.setMesswert(Double.valueOf(wert.replaceAll(",", ".")));
        if (attributes.containsKey("MESSFEHLER")) {
            messwert.setMessfehler(Double.valueOf(attributes.get("MESSFEHLER").replaceAll(",", ".")).floatValue());
        }
        if (attributes.containsKey("NWG")) {
            messwert.setNwgZuMesswert(Double.valueOf(attributes.get("NWG").replaceAll(",", ".")));
        }
        if (attributes.containsKey("GRENZWERT")) {
            messwert.setGrenzwertueberschreitung(attributes.get("GRENZWERT").toUpperCase() == "J" ? true : false);
        }
        doDefaults(messwert);
        doConverts(messwert);
        doTransforms(messwert);
        if (messwert.getMesswertNwg() != null && messwert.getNwgZuMesswert() == null) {
            messwert.setNwgZuMesswert(messwert.getMesswert());
            messwert.setMesswert(null);
        }
        else if (messwert.getMesswertNwg() != null && messwert.getMesswert().equals(messwert.getNwgZuMesswert()) ||
                 messwert.getMesswertNwg() != null && messwert.getMesswert() == 0.0) {
            messwert.setMesswert(null);
        }
        if (messwert.getMesswertNwg() != null && messwert.getMessfehler() == 0) {
            messwert.setMessfehler(null);
        }
        return messwert;
    }

    private KommentarM createMessungKommentar(Map<String, String> attributes, int messungsId, Probe probe) {
        if (attributes.get("TEXT").equals("")) {
            currentWarnings.add(new ReportItem("KOMMENTAR", "Text", 631));
            return null;
        };
        KommentarM kommentar = new KommentarM();
        kommentar.setMessungsId(messungsId);
        if (attributes.containsKey("MST_ID")) {
            kommentar.setMstId(attributes.get("MST_ID"));
        }
        else {
            kommentar.setMstId(probe.getMstId());
        }
        if (attributes.containsKey("DATE")) {
            String date = attributes.get("DATE") + " " + attributes.get("TIME");
            kommentar.setDatum(getDate(date));
        }
        else {
            kommentar.setDatum(Timestamp.from(Instant.now().atZone(ZoneOffset.UTC).toInstant()));
        }
        kommentar.setText(attributes.get("TEXT"));
        doDefaults(kommentar);
        doConverts(kommentar);
        doTransforms(kommentar);
        if (!userInfo.getMessstellen().contains(kommentar.getMstId())) {
            currentWarnings.add(new ReportItem(userInfo.getName(), "Messungs Kommentar: " + kommentar.getMstId(), 699));
            return null;
        }
        return kommentar;
    }

    private void createStatusProtokoll(String status, Messung messung, String mstId) {
        for (int i = 1; i <= 3; i++) {
            if (status.substring(i-1, i).equals("0")) {
                // no further status settings
                return;
            }            
            else if (!addStatusProtokollEntry(i, Integer.valueOf(status.substring(i-1, i)), messung, mstId)) {
                return;
            }
        }
    }

    private boolean addStatusProtokollEntry(int statusStufe, int statusWert, Messung messung, String mstId) {
        // validation check of new status entries
        int newKombi = 0;
        QueryBuilder<StatusKombi> builder =
            new QueryBuilder<StatusKombi>(
                repository.entityManager(Strings.STAMM),
                StatusKombi.class);
        builder.and("statusWert", statusWert);
        builder.and("statusStufe", statusStufe);
        List<StatusKombi> kombi =
            (List<StatusKombi>)repository.filterPlain(
                builder.getQuery(),
                Strings.STAMM);
        if (kombi != null && !kombi.isEmpty()) {
            newKombi = kombi.get(0).getId();
        } else {
            currentWarnings.add(new ReportItem("status#" + statusStufe, statusWert, 675));
            return false;
        }
        // get current status kombi
        StatusProtokoll currentStatus = repository.getByIdPlain(
            StatusProtokoll.class, messung.getStatus(), Strings.LAND);
        StatusKombi currentKombi = repository.getByIdPlain(
            StatusKombi.class, currentStatus.getStatusKombi(), Strings.STAMM);
        // check if erreichbar
        QueryBuilder<StatusErreichbar> errFilter =
            new QueryBuilder<StatusErreichbar>(
                repository.entityManager(Strings.STAMM),
                StatusErreichbar.class);
        errFilter.and("stufeId", statusStufe);
        errFilter.and("wertId", statusWert);
        errFilter.and("curStufe", currentKombi.getStatusStufe().getId());
        errFilter.and("curWert", currentKombi.getStatusWert().getId());
        List<StatusErreichbar> erreichbar = repository.filterPlain(errFilter.getQuery(), Strings.STAMM);
        if (erreichbar.isEmpty()) {
            currentWarnings.add(new ReportItem("status#" + statusStufe, statusWert, 675));
            return false;
        }
        // check auth
        MessStelle messStelle = repository.getByIdPlain(MessStelle.class, mstId, Strings.STAMM);
        if ((statusStufe == 1 && userInfo.getFunktionenForMst(mstId).contains(1)) ||
            (statusStufe == 2 && 
                userInfo.getNetzbetreiber().contains(messStelle.getNetzbetreiberId()) &&
                userInfo.getFunktionenForNetzbetreiber(messStelle.getNetzbetreiberId()).contains(2)) ||
            (statusStufe == 3 && 
                userInfo.getFunktionen().contains(3))) {
            StatusProtokoll newStatus = new StatusProtokoll();
            newStatus.setDatum(new Timestamp(new Date().getTime()));
            newStatus.setMessungsId(messung.getId());
            newStatus.setMstId(mstId);
            newStatus.setStatusKombi(newKombi);
            Response r = repository.create(newStatus, Strings.LAND);
            messung.setStatus(newStatus.getId());
            repository.update(messung, Strings.LAND);
            return true;
        } else {
            currentWarnings.add(new ReportItem("status#" + statusStufe, statusWert, 699));
            return false;
        }
    }

    private void createReiMesspunkt(LafRawData.Probe object, Probe probe) {

        QueryBuilder<Ortszuordnung> builder = new QueryBuilder<Ortszuordnung>(
            repository.entityManager("stamm"),
            Ortszuordnung.class);
        builder.and("probeId", probe.getId());
        List<Ortszuordnung> zuordnungen =
            repository.filterPlain(builder.getQuery(), "land");
        if (!zuordnungen.isEmpty()) {
            // Probe already has an ort.
            return;
        }

        List<Map<String, String>> uort = object.getUrsprungsOrte();
        if (uort.size() > 0 &&
            uort.get(0).containsKey("U_ORTS_ZUSATZCODE")
        ) {
            // WE HAVE A REI-MESSPUNKT!
            // Search for the ort in db
            Map<String, String> uo = uort.get(0);
            QueryBuilder<Ort> builder1 = new QueryBuilder<Ort>(
                repository.entityManager("stamm"),
                Ort.class);
            builder1.and("ortId", uo.get("U_ORTS_ZUSATZCODE"));
            List<Ort> messpunkte =
                repository.filterPlain(builder1.getQuery(), "stamm");
            if (!messpunkte.isEmpty()) {
                Ortszuordnung ort = new Ortszuordnung();
                ort.setOrtszuordnungTyp("R");
                ort.setProbeId(probe.getId());
                ort.setOrtId(messpunkte.get(0).getId());
                if (uo.containsKey("U_ORTS_ZUSATZTEXT")) {
                    ort.setOrtszusatztext(uo.get("U_ORTS_ZUSATZTEXT"));
                }
                repository.create(ort, "land");
                probe.setKtaGruppeId(messpunkte.get(0).getKtaGruppeId());
                repository.update(probe, "land");
            }
            else {
                ReportItem warn = new ReportItem();
                warn.setCode(632);
                warn.setKey("Ort");
                warn.setValue(uo.get("U_ORTS_ZUSATZCODE"));
                currentWarnings.add(warn);
            }
        }
        else {
            Ort o = null;
            if (uort.size() > 0) {
                o = findOrCreateOrt(uort.get(0), "U_", probe);
            }
            if (o == null) {
                o = findOrCreateOrt(object.getEntnahmeOrt(), "P_", probe);
            }
            if (o == null) {
                return;
            }
            o.setOrtTyp(3);
            repository.update(o, "stamm");
            Ortszuordnung ort = new Ortszuordnung();
            ort.setOrtId(o.getId());
            ort.setOrtszuordnungTyp("R");
            ort.setProbeId(probe.getId());
            if (uort.size() > 0 &&
                uort.get(0).containsKey("U_ORTS_ZUSATZCODE")
            ) {
                Map<String, String> uo = uort.get(0);
                o.setOrtId(uo.get("U_ORTS_ZUSATZCODE"));
                if (uo.containsKey("U_ORTS_ZUSATZTEXT")) {
                    ort.setOrtszusatztext(uo.get("U_ORTS_ZUSATZTEXT"));
                }
            }
            repository.create(ort, "land");
        }
        return;
    }

    private Ortszuordnung createUrsprungsOrt(
        Map<String, String> ursprungsOrt,
        Probe probe
    ) {
        if (ursprungsOrt.isEmpty()) {
            return null;
        }
        Ortszuordnung ort = new Ortszuordnung();
        ort.setOrtszuordnungTyp("U");
        ort.setProbeId(probe.getId());

        Ort o = findOrCreateOrt(ursprungsOrt, "U_", probe);
        if (o == null) {
            return null;
        }
        ort.setOrtId(o.getId());
        if (ursprungsOrt.containsKey("U_ORTS_ZUSATZTEXT")) {
            ort.setOrtszusatztext(ursprungsOrt.get("U_ORTS_ZUSATZTEXT"));
        }
        doDefaults(ort);
        doConverts(ort);
        doTransforms(ort);
        return ort;
    }

    private void createEntnahmeOrt(
        Map<String, String> entnahmeOrt,
        Probe probe
    ) {
        if (entnahmeOrt.isEmpty()) {
            return;
        }
        Ortszuordnung ort = new Ortszuordnung();
        ort.setOrtszuordnungTyp("E");
        ort.setProbeId(probe.getId());

        Ort o = findOrCreateOrt(entnahmeOrt, "P_", probe);
        if (o == null) {
            return;
        }
        ort.setOrtId(o.getId());
        if (entnahmeOrt.containsKey("P_ORTS_ZUSATZTEXT")) {
            ort.setOrtszusatztext(entnahmeOrt.get("P_ORTS_ZUSATZTEXT"));
        }
        doDefaults(ort);
        doConverts(ort);
        doTransforms(ort);
        merger.mergeEntnahmeOrt(probe.getId(), ort);
    }

    private Ort findOrCreateOrt(Map<String, String> attributes, String type, Probe probe) {
        Ort o = new Ort();
        // If laf contains coordinates, find a ort with matching coordinates or
        // create one.
        if ((attributes.get(type + "KOORDINATEN_ART") != null ||
             attributes.get(type + "KOORDINATEN_ART_S") != null) &&
            !attributes.get(type + "KOORDINATEN_X").equals("") &&
            attributes.get(type + "KOORDINATEN_X") != null &&
            !attributes.get(type + "KOORDINATEN_X").equals("") &&
            attributes.get(type + "KOORDINATEN_Y") != null
        ) {
            if (attributes.get(type + "KOORDINATEN_ART_S") != null) {
                o.setKdaId(Integer.valueOf(attributes.get(type + "KOORDINATEN_ART_S")));
                KoordinatenArt koordinatenArt = repository.getByIdPlain(
                    KoordinatenArt.class,
                    o.getKdaId(),
                    Strings.STAMM);
                if ( koordinatenArt == null) {
                    currentWarnings.add(new ReportItem(type + "KOORDINATEN_ART_S", attributes.get(type + "KOORDINATEN_ART_S"), 675));
                    o.setKdaId(null);
                }
            }
            else {
                QueryBuilder<KoordinatenArt> kdaBuilder =
                    new QueryBuilder<KoordinatenArt>(
                        repository.entityManager(Strings.STAMM),
                        KoordinatenArt.class);
                kdaBuilder.and("koordinatenart", attributes.get(type + "KOORDINATEN_ART"));
                List<KoordinatenArt> arten = repository.filterPlain(kdaBuilder.getQuery(), Strings.STAMM);
                if (arten == null || arten.isEmpty()) {
                    currentWarnings.add(new ReportItem(type + "KOORDINATEN_ART", attributes.get(type + "KOORDINATEN_ART"), 675));
                    o.setKdaId(null);
                }
                else {
                    o.setKdaId(arten.get(0).getId());
                }
            }
            o.setKoordXExtern(attributes.get(type + "KOORDINATEN_X"));
            o.setKoordYExtern(attributes.get(type + "KOORDINATEN_Y"));
        }
        // If laf contains gemeinde attributes, find a ort with matching gemId
        // or create one.
        if (attributes.get(type + "GEMEINDENAME") != null && !attributes.get(type + "GEMEINDENAME").equals("")) {
            QueryBuilder<Verwaltungseinheit> builder =
                new QueryBuilder<Verwaltungseinheit>(
                    repository.entityManager(Strings.STAMM),
                    Verwaltungseinheit.class);
            builder.and("bezeichnung", attributes.get(type + "GEMEINDENAME"));
            List<Verwaltungseinheit> ves =
                repository.filterPlain(builder.getQuery(), Strings.STAMM);
            if (ves == null || ves.size() == 0) {
                currentWarnings.add(new ReportItem("GEMEINDENAME", attributes.get(type + "GEMEINDENAME"), 675));
            }
            else {
                o.setGemId(ves.get(0).getId());
            }
        }
        else if (attributes.get(type + "GEMEINDESCHLUESSEL") != null && !attributes.get(type + "GEMEINDESCHLUESSEL").equals("")) {
            o.setGemId(attributes.get(type + "GEMEINDESCHLUESSEL"));
            Verwaltungseinheit v = repository.getByIdPlain(Verwaltungseinheit.class, o.getGemId(), Strings.STAMM);
            if (v == null) {
                currentWarnings.add(new ReportItem(type + "GEMEINDESCHLUESSEL", o.getGemId(), 675));
                o.setGemId(null);
            }
        }
        String key = "";
        String hLand = "";
        String staatFilter = "";
        if (attributes.get(type + "HERKUNFTSLAND_S") != null && !attributes.get(type + "HERKUNFTSLAND_S").equals("")) {
            staatFilter = "id";
            key = "HERKUNFTSLAND_S";
            hLand = attributes.get(type + "HERKUNFTSLAND_S");
        }
        else if (attributes.get(type + "HERKUNFTSLAND_KURZ") != null && !attributes.get(type + "HERKUNFTSLAND_KURZ").equals("")) {
            staatFilter = "staatKurz";
            key = "HERKUNFTSLAND_KURZ";
            hLand = attributes.get(type + "HERKUNFTSLAND_KURZ");
        }
        else if (attributes.get(type + "HERKUNFTSLAND_LANG") != null && !attributes.get(type + "HERKUNFTSLAND_LANG").equals("")) {
            staatFilter = "staat";
            key = "HERKUNFTSLAND_LANG";
            hLand = attributes.get(type + "HERKUNFTSLAND_LANG");
        }

        if (staatFilter.length() > 0) {
            QueryBuilder<Staat> builderStaat =
                new QueryBuilder<Staat>(
                    repository.entityManager(Strings.STAMM),
                    Staat.class);
            builderStaat.and(staatFilter, hLand);
            List<Staat> staat =
                repository.filterPlain(builderStaat.getQuery(), Strings.STAMM);
            if (staat == null || staat.size() == 0) {
                currentWarnings.add(new ReportItem(key, hLand, 675));
            }
            else if (staat != null && staat.size() > 0) {
                o.setStaatId(staat.get(0).getId());
            }
        }
        if (attributes.containsKey(type + "HOEHE_NN")) {
            o.setHoeheUeberNn(Float.valueOf(attributes.get(type + "HOEHE_NN")));
        }
        if (attributes.containsKey(type + "ORTS_ZUSATZCODE") && !attributes.get(type + "ORTS_ZUSATZCODE").equals("")) {
            Ortszusatz zusatz = repository.getByIdPlain(
                Ortszusatz.class,
                attributes.get(type + "ORTS_ZUSATZCODE"),
                Strings.STAMM);
            if ( zusatz == null) {
                currentWarnings.add(new ReportItem(type + "ORTS_ZUSATZCODE", attributes.get(type + "ORTS_ZUSATZCODE"), 675));
            }
            else {
                o.setOzId(zusatz.getOzsId());
            }
        }

        // checkk if all attributes are empty
        if (o.getKdaId() == null && o.getGemId() == null && o.getStaatId() == null && o.getOzId() == null) {
            return null;
        }

        MessStelle mst = repository.getByIdPlain(MessStelle.class, probe.getMstId(), Strings.STAMM);
        o.setNetzbetreiberId(mst.getNetzbetreiberId());
        o = ortFactory.completeOrt(o);
        if (o == null || o.getGeom() == null) {
            currentWarnings.addAll(ortFactory.getErrors());
            return null;
        }
        Violation violation = ortValidator.validate(o);
        for (Entry<String, List<Integer>> warn :
                 violation.getWarnings().entrySet()) {
            for (Integer code : warn.getValue()) {
                currentWarnings.add(
                    new ReportItem("validation", warn.getKey(), code));
            }
        }
        if (violation.hasErrors()) {
            for (Entry<String, List<Integer>> err :
                     violation.getErrors().entrySet()) {
                for (Integer code : err.getValue()) {
                    // Add to warnings because Probe object might be imported
                    currentWarnings.add(
                        new ReportItem("validation", err.getKey(), code));
                }
            }
            return null;
        }
        if (o.getId() != null) {
            return o;
        }
        repository.create(o, Strings.STAMM);
        return o;
    }

    private Timestamp getDate(String date) {
        ZoneId fromLaf = ZoneId.of("UTC");
        switch (currentZeitbasis) {
            case 1: fromLaf = ZoneId.of("UTC+2");
                    break;
            case 3: fromLaf = ZoneId.of("UTC+1");
                    break;
            case 4: fromLaf = ZoneId.of("CET");
                    break;
            default: break;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HHmm").withZone(fromLaf);
        ZonedDateTime orig = ZonedDateTime.parse(date, formatter);
        ZonedDateTime utc = orig.withZoneSameInstant(ZoneOffset.UTC);
        return Timestamp.from(utc.toInstant());
    }

    private void logProbe(Probe probe) {
        logger.debug("%PROBE%");
        logger.debug("datenbasis: " + probe.getDatenbasisId());
        logger.debug("betriebsart: " + probe.getBaId());
        logger.debug("erzeuger: " + probe.getErzeugerId());
        logger.debug("hauptprobennummer: " + probe.getHauptprobenNr());
        logger.debug("externeprobeid: " + probe.getExterneProbeId());
        logger.debug("labor: " + probe.getLaborMstId());
        logger.debug("deskriptoren: " + probe.getMediaDesk());
        logger.debug("media: " + probe.getMedia());
        logger.debug("mittelung: " + probe.getMittelungsdauer());
        logger.debug("mpl: " + probe.getMplId());
        logger.debug("mpr: " + probe.getMprId());
        logger.debug("mst: " + probe.getMstId());
        logger.debug("pnbeginn: " + probe.getProbeentnahmeBeginn());
        logger.debug("pnende: " + probe.getProbeentnahmeEnde());
        logger.debug("probenart: " + probe.getProbenartId());
        logger.debug("probenehmer: " + probe.getProbeNehmerId());
        logger.debug("sbeginn: " + probe.getSolldatumBeginn());
        logger.debug("sende: " + probe.getSolldatumEnde());
        logger.debug("test: " + probe.getTest());
        logger.debug("umw: " + probe.getUmwId());
    }

    private void addProbeAttribute(Entry<String, String> attribute, Probe probe) {
        String key = attribute.getKey();
        String value = attribute.getValue();

        if ("DATENBASIS_S".equals(key) && !value.equals("") && probe.getDatenbasisId() == null) {
            Datenbasis datenbasis = repository.getByIdPlain(
                Datenbasis.class,
                Integer.valueOf(value.toString()),
                Strings.STAMM);
            if ( datenbasis == null) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            Integer v = Integer.valueOf(value.toString());
            probe.setDatenbasisId(v);
        }
        else if ("DATENBASIS_S".equals(key) && probe.getDatenbasisId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }


        if ("DATENBASIS".equals(key)&& !value.equals("") && probe.getDatenbasisId() == null) {
            List<ImporterConfig> cfgs = getImporterConfigByAttributeUpper("DATENBASIS");
            String attr = value.toString();
            for (int i = 0; i < cfgs.size(); i++) {
                ImporterConfig cfg = cfgs.get(i);
                if (cfg != null &&
                    cfg.getAction().equals("convert") &&
                    cfg.getFromValue().equals(attr)
                ) {
                    attr = cfg.getToValue();
                }
                if (cfg != null && cfg.getAction().equals("transform")) {
                    char from = (char) Integer.parseInt(cfg.getFromValue(), 16);
                    char to = (char) Integer.parseInt(cfg.getToValue(), 16);
                    attr = attr.replaceAll("[" + String.valueOf(from) + "]", String.valueOf(to));
                }
            }
            QueryBuilder<Datenbasis> builder =
                new QueryBuilder<Datenbasis>(
                    repository.entityManager(Strings.STAMM),
                    Datenbasis.class);
            builder.and("datenbasis", attr);
            List<Datenbasis> datenbasis =
                (List<Datenbasis>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (datenbasis == null || datenbasis.isEmpty()) {
                currentErrors.add(new ReportItem(key, attr, 675));
                return;
            }
            Integer v = datenbasis.get(0).getId();
            probe.setDatenbasisId(v);
        }
        else if ("DATENBASIS".equals(key) && !value.equals("") && probe.getDatenbasisId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }

        if ("PROBE_ID".equals(key)) {
            probe.setExterneProbeId(value);
        }

        if ("HAUPTPROBENNUMMER".equals(key)) {
            probe.setHauptprobenNr(value.toString());
        }

        if ("MPR_ID".equals(key)) {
            Integer v = Integer.valueOf(value.toString());
            probe.setMprId(v);
        }

        if ("MESSSTELLE".equals(key) && !value.equals("")) {
            MessStelle mst = repository.getByIdPlain(
                MessStelle.class,
                value.toString(),
                Strings.STAMM);
            if ( mst == null) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setMstId(value.toString());
        }

        if ("MESSLABOR".equals(key) && !value.equals("")) {
            MessStelle mst = repository.getByIdPlain(
                MessStelle.class,
                value.toString(),
                Strings.STAMM);
            if ( mst == null) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setLaborMstId(value.toString());
        }

        if ("MESSPROGRAMM_S".equals(key) && !value.equals("") && probe.getBaId() == null) {
            QueryBuilder<MessprogrammTransfer> builder =
                new QueryBuilder<MessprogrammTransfer>(
                    repository.entityManager(Strings.STAMM),
                    MessprogrammTransfer.class);
            builder.and("messprogrammS", value);
            List<MessprogrammTransfer> transfer=
                (List<MessprogrammTransfer>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (transfer == null || transfer.isEmpty()) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setBaId(transfer.get(0).getBaId());
            if (probe.getDatenbasisId() == null) {
                probe.setDatenbasisId(transfer.get(0).getDatenbasisId());
            }
        }
        if ("MESSPROGRAMM_C".equals(key) && !value.equals("")){
            QueryBuilder<MessprogrammTransfer> builder =
                new QueryBuilder<MessprogrammTransfer>(
                    repository.entityManager(Strings.STAMM),
                    MessprogrammTransfer.class);
            builder.and("messprogrammC", value);
            List<MessprogrammTransfer> transfer=
                (List<MessprogrammTransfer>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (transfer == null || transfer.isEmpty()) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setBaId(transfer.get(0).getBaId());
            if (probe.getDatenbasisId() == null) {
                probe.setDatenbasisId(transfer.get(0).getDatenbasisId());
            }
        }

        if ("MESSPROGRAMM_LAND".equals(key) && !value.equals("")) {
            QueryBuilder<MessprogrammKategorie> builder =
                new QueryBuilder<MessprogrammKategorie>(
                    repository.entityManager(Strings.STAMM),
                    MessprogrammKategorie.class);
            builder.or("netzbetreiberId", userInfo.getNetzbetreiber());
            builder.and("code", value);
            List<MessprogrammKategorie> kategorie =
                (List<MessprogrammKategorie>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (kategorie == null || kategorie.isEmpty()) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setMplId(kategorie.get(0).getId());
        }

        if ("PROBENAHMEINSTITUTION".equals(key) && !value.equals("")) {
            QueryBuilder<Probenehmer> builder =
                new QueryBuilder<Probenehmer>(
                    repository.entityManager(Strings.STAMM),
                    Probenehmer.class);
            builder.or("netzbetreiberId", userInfo.getNetzbetreiber());
            builder.and("prnId", value);
            List<Probenehmer> prn =
                (List<Probenehmer>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (prn == null || prn.isEmpty()) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setProbeNehmerId(prn.get(0).getId());
        }

        if ("SOLL_DATUM_UHRZEIT_A".equals(key) && !value.equals("")) {
            probe.setSolldatumBeginn(getDate(value.toString()));
        }
        if ("SOLL_DATUM_UHRZEIT_E".equals(key) && !value.equals("")) {
            probe.setSolldatumEnde(getDate(value.toString()));
        }
        if ("PROBENAHME_DATUM_UHRZEIT_A".equals(key) && !value.equals("")) {
            probe.setProbeentnahmeBeginn(getDate(value.toString()));
        }
        if ("PROBENAHME_DATUM_UHRZEIT_E".equals(key) && !value.equals("")) {
            probe.setProbeentnahmeEnde(getDate(value.toString()));
        }

        if ("UMWELTBEREICH_S".equals(key) &&
            probe.getUmwId() == null &&
            !value.equals("")
        ) {
            Umwelt umw = repository.getByIdPlain(
                Umwelt.class,
                value.toString(),
                Strings.STAMM);
            if ( umw == null) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setUmwId(value.toString());
        }
        else if ("UMWELTBEREICH_S".equals(key) && probe.getUmwId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }
        if ("UMWELTBEREICH_C".equals(key) &&
            probe.getUmwId() == null &&
            !value.equals("")
        ) {
            QueryBuilder<Umwelt> builder =
                new QueryBuilder<Umwelt>(
                    repository.entityManager(Strings.STAMM),
                    Umwelt.class);
            int length = value.toString().length() > 80 ? 80 : value.toString().length();
            builder.and("umweltBereich", value.toString().substring(0, length));
            List<Umwelt> umwelt =
                (List<Umwelt>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (umwelt == null || umwelt.isEmpty()) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setUmwId(umwelt.get(0).getId());
        }
        else if ("UMWELTBEREICH_C".equals(key) && probe.getUmwId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }

        if ("DESKRIPTOREN".equals(key)) {
            if (value.length() > 24) value = value.substring(0,24); // ignore deskriptor S12 at the laf import
            if (value.length() < 26) {
                for (int i = value.length(); i <= 26 ; i++) {
                    value += " ";
                }
            }
            value = value.replace(" ", "0");
            List<String> tmp = new ArrayList<String>();
            tmp.add("D:");
            for (int i =  0; i < value.length() - 4; i += 2) {
                tmp.add(value.substring(i, i+2));
            }
            probe.setMediaDesk(StringUtils.join(tmp.toArray(), " "));
        }

        if ("TESTDATEN".equals(key)) {
            if (value.toString().equals("1")) {
                probe.setTest(true);
            }
            else if (value.toString().equals("0")) {
                probe.setTest(false);
            }
            else if (!value.toString().equals("")) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
            }
        }

        if ("REI_PROGRAMMPUNKTGRUPPE".equals(key) && !value.equals("")) {
            QueryBuilder<ReiProgpunktGruppe> builder =
                new QueryBuilder<ReiProgpunktGruppe>(
                    repository.entityManager("stamm"),
                    ReiProgpunktGruppe.class);
            builder.and("reiProgPunktGruppe", value.toString());
            List<ReiProgpunktGruppe> list =
                repository.filterPlain(builder.getQuery(), "stamm");
            if (!list.isEmpty()) {
                probe.setReiProgpunktGrpId(list.get(0).getId());
            }
            else {
                currentWarnings.add(new ReportItem(key, value.toString(), 632));
            }
        }

        if ("MEDIUM".equals(key)) {
            probe.setMedia(value.toString());
        }

        if ("PROBENART".equals(key) && !value.equals("")) {
            List<ImporterConfig> cfgs = getImporterConfigByAttributeUpper("PROBENART");
            String attr = value.toString();
            for (int i = 0; i < cfgs.size(); i++) {
                ImporterConfig cfg = cfgs.get(i);
                if (cfg != null &&
                    cfg.getAction().equals("convert") &&
                    cfg.getFromValue().equals(attr)
                ) {
                    attr = cfg.getToValue();
                }
                if (cfg != null && cfg.getAction().equals("transform")) {
                    char from = (char) Integer.parseInt(cfg.getFromValue(), 16);
                    char to = (char) Integer.parseInt(cfg.getToValue(), 16);
                    attr = attr.replaceAll("[" + String.valueOf(from) + "]", String.valueOf(to));
                }
            }
            QueryBuilder<Probenart> builder =
                new QueryBuilder<Probenart>(
                    repository.entityManager(Strings.STAMM),
                    Probenart.class);
            builder.and("probenart", attr);
            List<Probenart> probenart =
                (List<Probenart>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (probenart == null || probenart.isEmpty()) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
                return;
            }
            probe.setProbenartId(probenart.get(0).getId());
        }
    }
    /**
     * Add an attribute to the given LMessung object.
     *
     * @param key       The key mapping to a object member.
     * @param value     The value to set.
     * @param messung   The entity object.
     * @return The updated entity object.
     */
    public Messung addMessungAttribute(
        Entry<String, String> attribute,
        Messung messung
    ) {
        String key = attribute.getKey();
        String value = attribute.getValue();
        if ("MESSUNGS_ID".equals(key) && !value.equals("")) {
            messung.setExterneMessungsId(Integer.valueOf(value));
        }
        if ("NEBENPROBENNUMMER".equals(key) && !value.equals("")) {
            messung.setNebenprobenNr(value.toString());
        }
        else if ("MESS_DATUM_UHRZEIT".equals(key) && !value.equals("")) {
            messung.setMesszeitpunkt(getDate(value.toString()));
        }
        else if ("MESSZEIT_SEKUNDEN".equals(key) && !value.equals("")) {
            Integer i = Integer.valueOf(value.toString());
            messung.setMessdauer(i);
        }
        else if ("MESSMETHODE_S".equals(key) && !value.equals("")) {
            MessMethode mmt = repository.getByIdPlain(
                MessMethode.class,
                value.toString(),
                Strings.STAMM);
            if ( mmt == null) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
            }
            else {
                messung.setMmtId(value.toString());
            }
        }
        else if ("MESSMETHODE_C".equals(key) && !value.equals("")) {
            QueryBuilder<MessMethode> builder =
                new QueryBuilder<MessMethode>(
                    repository.entityManager(Strings.STAMM),
                    MessMethode.class);
            builder.and("messmethode", value.toString());
            List<MessMethode> mm =
                (List<MessMethode>)repository.filterPlain(
                    builder.getQuery(),
                    Strings.STAMM);
            if (mm == null || mm.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("messmethode");
                warn.setValue(key);
                currentWarnings.add(warn);
            }
            else {
                messung.setMmtId(mm.get(0).getId());
            }
        }
        else if ("ERFASSUNG_ABGESCHLOSSEN".equals(key)) {
            if (value.toString().equals("1")) {
                messung.setFertig(true);
            }
            else if (value.toString().equals("0")) {
                messung.setFertig(false);
            }
            else if (!value.toString().equals("")) {
                currentWarnings.add(new ReportItem(key, value.toString(), 675));
            }
        }
        return messung;
    }

    /**
     * @return the errors
     */
    public Map<String, List<ReportItem>> getErrors() {
        return errors;
    }

    /**
     * @return the errors
     */
    public Map<String, List<ReportItem>> getWarnings() {
        return warnings;
    }

    /**
     * @return the userInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * @param userInfo the userInfo to set
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /**
     * @return the config
     */
    public List<ImporterConfig> getConfig() {
        return config;
    }

    /**
     * @param config the config to set
     */
    public void setConfig(List<ImporterConfig> config) {
        this.config = config;
    }
}
