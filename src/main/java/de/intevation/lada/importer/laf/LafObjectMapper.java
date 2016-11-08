package de.intevation.lada.importer.laf;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.management.modelmbean.InvalidTargetObjectTypeException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

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
import de.intevation.lada.model.stammdaten.KoordinatenArt;
import de.intevation.lada.model.stammdaten.MessEinheit;
import de.intevation.lada.model.stammdaten.MessStelle;
import de.intevation.lada.model.stammdaten.Messgroesse;
import de.intevation.lada.model.stammdaten.MessprogrammKategorie;
import de.intevation.lada.model.stammdaten.MessprogrammTransfer;
import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Ortszusatz;
import de.intevation.lada.model.stammdaten.ProbenZusatz;
import de.intevation.lada.model.stammdaten.Probenart;
import de.intevation.lada.model.stammdaten.Staat;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.util.annotation.AuthorizationConfig;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.auth.Authorization;
import de.intevation.lada.util.auth.AuthorizationType;
import de.intevation.lada.util.auth.UserInfo;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
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
    @IdentifierConfig(type="Probe")
    private Identifier probeIdentifier;

    @Inject
    @IdentifierConfig(type="Messung")
    private Identifier messungIdentifier;

    @Inject
    private ObjectMerger merger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RW)
    private Repository repository;

    @Inject
    private ProbeFactory factory;

    private Map<String, List<ReportItem>> errors;
    private Map<String, List<ReportItem>> warnings;
    private List<ReportItem> currentErrors;
    private List<ReportItem> currentWarnings;


    private UserInfo userInfo;

    public void mapObjects(LafRawData data) {
        errors = new HashMap<String, List<ReportItem>>();
        warnings = new HashMap<String, List<ReportItem>>();
        for (int i = 0; i < data.getProben().size(); i++) {
            create(data.getProben().get(i));
        }
    }

    private void create(LafRawData.Probe object) {
        currentWarnings = new ArrayList<ReportItem>();
        currentErrors = new ArrayList<ReportItem>();
        Probe probe = new Probe();

        // Fill the object with data
        for (Entry<String, String> attribute : object.getAttributes().entrySet()) {
            addProbeAttribute(attribute, probe);
        }
        if (probe.getLaborMstId() == null) {
            probe.setLaborMstId(probe.getMstId());
        }
        // Use the deskriptor string to find the medium
        probe = factory.findMediaDesk(probe);
        //logProbe(probe);

        // Check if the user is authorized to create the probe
        boolean isAuthorized = authorizer.isAuthorized(userInfo, probe, Probe.class);
        if (!isAuthorized) {
            ReportItem err = new ReportItem();
            err.setCode(699);
            err.setKey(userInfo.getName());
            err.setValue("Messstelle " + probe.getMstId());
            currentErrors.add(err);
            errors.put(object.getIdentifier(),
                new ArrayList<ReportItem>(currentErrors));

            if (currentWarnings.size() > 0) {
                warnings.put(object.getIdentifier(),
                    new ArrayList<ReportItem>(currentWarnings));
            }
            return;
        }

        // Check for errors and warnings

        // Compare the probe with objects in the db
        Probe newProbe = null;
        try {
            Identified i = probeIdentifier.find(probe);
            Probe old = (Probe)probeIdentifier.getExisting();
            // Matching probe was found in the db. Update it!
            if(i == Identified.UPDATE) {
                merger.merge(old, probe);
                newProbe = old;
            }
            // Probe was found but some data does not match
            else if(i == Identified.REJECT){
                ReportItem err = new ReportItem();
                err.setCode(631);
                err.setKey("duplicate");
                err.setValue("");
                currentErrors.add(err);
                if (currentErrors.size() > 0) {
                    errors.put(object.getIdentifier(),
                        new ArrayList<ReportItem>(currentErrors));
                }
                if (currentWarnings.size() > 0) {
                    warnings.put(object.getIdentifier(),
                        new ArrayList<ReportItem>(currentWarnings));
                }
                return;
            }
            // It is a brand new probe!
            else if(i == Identified.NEW){
                Violation violation = probeValidator.validate(probe);
                if (violation.hasErrors()) {
                    for (Entry<String, List<Integer>> err : violation.getErrors().entrySet()) {
                        for (Integer code : err.getValue()) {
                            currentErrors.add(new ReportItem("validation", err.getKey(), code));
                        }
                    }
                    return;
                }
                Response created = repository.create(probe, "land");
                newProbe = ((Probe)created.getData());
            }
        } catch (InvalidTargetObjectTypeException e) {
            ReportItem err = new ReportItem();
            err.setCode(604);
            err.setKey("not known");
            err.setValue("No valid Probe Object");
            currentErrors.add(err);
            if (currentErrors.size() > 0) {
                errors.put(object.getIdentifier(),
                    new ArrayList<ReportItem>(currentErrors));
            }
            if (currentWarnings.size() > 0) {
                warnings.put(object.getIdentifier(),
                    new ArrayList<ReportItem>(currentWarnings));
            }
            return;
        }
        if (newProbe == null) {
            // Only occurs if object type is not probe
            return;
        }
        // Create kommentar objects
        List<KommentarP> kommentare = new ArrayList<KommentarP>();
        for (int i = 0; i < object.getKommentare().size(); i++) {
            KommentarP tmp = createProbeKommentar(object.getKommentare().get(i), newProbe.getId());
            if (tmp != null) {
                kommentare.add(tmp);
            }
        }
        // Persist kommentar objects
        merger.mergeKommentare(newProbe, kommentare);

        // Create zusatzwert objects
        List<ZusatzWert> zusatzwerte = new ArrayList<ZusatzWert>();
        for (int i = 0; i < object.getZusatzwerte().size(); i++) {
            ZusatzWert tmp = createZusatzwert(object.getZusatzwerte().get(i), newProbe.getId());
            if (tmp != null) {
                zusatzwerte.add(tmp);
            }
        }
        // Persist zusatzwert objects
        merger.mergeZusatzwerte(newProbe, zusatzwerte);

        // Merge entnahmeOrt
        createEntnahmeOrt(object.getEntnahmeOrt(), newProbe.getId());

        // Create ursprungsOrte
        List<Ortszuordnung> uOrte = new ArrayList<Ortszuordnung>();
        for (int i = 0; i < object.getUrsprungsOrte().size(); i++) {
            Ortszuordnung tmp = createUrsprungsOrt(object.getUrsprungsOrte().get(i), newProbe.getId());
            if (tmp != null) {
                uOrte.add(tmp);
            }
        }
        // Persist ursprungsOrte
        merger.mergeUrsprungsOrte(newProbe.getId(), uOrte);

        // Create messung objects
        for (int i = 0; i < object.getMessungen().size(); i++) {
            create(object.getMessungen().get(i), newProbe.getId(), newProbe.getMstId());
        }
        Violation violation = probeValidator.validate(newProbe);
        for (Entry<String, List<Integer>> warn : violation.getWarnings().entrySet()) {
            for (Integer code : warn.getValue()) {
                currentWarnings.add(new ReportItem("validation", warn.getKey(), code));
            }
        }
        if (currentErrors.size() > 0) {
            errors.put(object.getIdentifier(),
                new ArrayList<ReportItem>(currentErrors));
        }
        if (currentWarnings.size() > 0) {
            warnings.put(object.getIdentifier(),
                new ArrayList<ReportItem>(currentWarnings));
        }
    }

    private void create(LafRawData.Messung object, int probeId, String mstId) {
        Messung messung = new Messung();
        messung.setProbeId(probeId);

        // Fill the new messung with data
        for (Entry<String, String> attribute : object.getAttributes().entrySet()) {
            addMessungAttribute(attribute, messung);
        }

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
        try {
            Identified i = messungIdentifier.find(messung);
            Messung old = (Messung)messungIdentifier.getExisting();
            if (i == Identified.UPDATE) {
                merger.mergeMessung(old, messung);
                newMessung = old;
                // We do not import the status. Doing this can cause an
                // inconsistent status protocol.
            }
            else if (i == Identified.REJECT) {
                ReportItem warn = new ReportItem();
                warn.setCode(631);
                warn.setKey("duplicate");
                warn.setValue("Messung: " + messung.getNebenprobenNr());
                currentWarnings.add(warn);
                return;
            }
            else if (i == Identified.NEW) {
                // Create a new messung and the first status
                Response created = repository.create(messung, "land");
                newMessung = ((Messung)created.getData());
                created = repository.getById(Messung.class, newMessung.getId(), "land");
                newMessung = ((Messung)created.getData());
                StatusProtokoll status = new StatusProtokoll();
                status.setDatum(new Timestamp(new Date().getTime()));
                status.setMessungsId(newMessung.getId());
                status.setMstId(mstId);
                status.setStatusKombi(1);
                Response st = repository.create(status, "land");
                newMessung.setStatus(((StatusProtokoll)st.getData()).getId());
                repository.update(newMessung, "land");
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
            KommentarM tmp = createMessungKommentar(object.getKommentare().get(i), newMessung.getId());
            if (tmp != null) {
                kommentare.add(tmp);
            }
        }
        merger.mergeMessungKommentare(newMessung, kommentare);
        List<Messwert> messwerte = new ArrayList<Messwert>();
        for (int i = 0; i < object.getMesswerte().size(); i++) {
            Messwert tmp = createMesswert(object.getMesswerte().get(i), newMessung.getId());
            if (tmp != null) {
                messwerte.add(tmp);
            }
        }
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

    private KommentarP createProbeKommentar(Map<String, String> attributes, int probeId) {
        KommentarP kommentar = new KommentarP();
        kommentar.setProbeId(probeId);
        kommentar.setMstId(attributes.get("MST_ID"));
        kommentar.setText(attributes.get("TEXT"));
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String date = attributes.get("DATE") + " " + attributes.get("TIME");
        Date d;
        try {
            d = format.parse(date);
            kommentar.setDatum(new Timestamp(d.getTime()));
        }
        catch (ParseException e) {
            ReportItem warn = new ReportItem();
            warn.setCode(674);
            warn.setKey("not valid");
            warn.setValue("Date: " + date);
            currentWarnings.add(warn);
        }
        if (!userInfo.getMessstellen().contains(kommentar.getMstId())) {
            ReportItem warn = new ReportItem();
            warn.setCode(699);
            warn.setKey(userInfo.getName());
            warn.setValue("Kommentar: " + kommentar.getMstId());
            currentWarnings.add(warn);
            return null;
        }
        return kommentar;
    }

    private ZusatzWert createZusatzwert(Map<String, String> attributes, int probeId) {
        ZusatzWert zusatzwert = new ZusatzWert();
        zusatzwert.setProbeId(probeId);
        zusatzwert.setMessfehler(Float.valueOf(attributes.get("MESSFEHLER")));
        zusatzwert.setMesswertPzs(Double.valueOf(attributes.get("MESSWERT_PZS")));
        QueryBuilder<ProbenZusatz> builder =
            new QueryBuilder<ProbenZusatz>(
                repository.entityManager("stamm"),
                ProbenZusatz.class);
        builder.and("zusatzwert", attributes.get("PZS"));
        List<ProbenZusatz> zusatz =
            (List<ProbenZusatz>)repository.filter(
                builder.getQuery(),
                "stamm").getData();

        if (zusatz == null || zusatz.isEmpty()) {
            ReportItem warn = new ReportItem();
            warn.setCode(673);
            warn.setKey("zusatzwert");
            warn.setValue(attributes.get("PZS"));
            currentWarnings.add(warn);
            return null;
        }
        zusatzwert.setPzsId(zusatz.get(0).getId());
        return zusatzwert;
    }

    private Messwert createMesswert(Map<String, String> attributes, int messungsId) {
        Messwert messwert = new Messwert();
        messwert.setMessungsId(messungsId);
        if (attributes.containsKey("MESSGROESSE_ID")) {
            messwert.setMessgroesseId(Integer.valueOf(attributes.get("MESSGROESSE_ID")));
        }
        else if (attributes.containsKey("MESSGROESSE")) {
            QueryBuilder<Messgroesse> builder =
                new QueryBuilder<Messgroesse>(
                    repository.entityManager("stamm"),
                    Messgroesse.class);
            builder.and("messgroesse", attributes.get("MESSGROESSE"));
            List<Messgroesse> groesse =
                (List<Messgroesse>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();

            if (groesse == null || groesse.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("messwert");
                warn.setValue(attributes.get("MESSGROESSE"));
                currentWarnings.add(warn);
                return null;
            }
            messwert.setMessgroesseId(groesse.get(0).getId());
        }
        if (attributes.containsKey("MEH_ID")) {
            messwert.setMehId(Integer.valueOf(attributes.get("MEH_ID")));
        }
        else if (attributes.containsKey("MEH")) {
            QueryBuilder<MessEinheit> builder =
                new QueryBuilder<MessEinheit>(
                    repository.entityManager("stamm"),
                    MessEinheit.class);
            builder.and("einheit", attributes.get("MEH"));
            List<MessEinheit> einheit =
                (List<MessEinheit>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();

            if (einheit == null || einheit.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("messwert");
                warn.setValue(attributes.get("MEH"));
                currentWarnings.add(warn);
                return null;
            }
            messwert.setMehId(einheit.get(0).getId());
        }

        String wert = attributes.get("MESSWERT");
        if (wert.startsWith("<")) {
            wert = wert.substring(1);
            messwert.setMesswertNwg("<");
        }
        messwert.setMesswert(Double.valueOf(wert));
        messwert.setMessfehler(Double.valueOf(attributes.get("MESSFEHLER")).floatValue());
        if (attributes.containsKey("NWG")) {
            messwert.setNwgZuMesswert(Double.valueOf(attributes.get("NWG")));
        }
        if (attributes.containsKey("GRENZWERT")) {
            messwert.setGrenzwertueberschreitung(attributes.get("GRENZWERT").toUpperCase() == "J" ? true : false);
        }
        return messwert;
    }

    private KommentarM createMessungKommentar(Map<String, String> attributes, int messungsId) {
        KommentarM kommentar = new KommentarM();
        kommentar.setMessungsId(messungsId);
        kommentar.setMstId(attributes.get("MST_ID"));
        kommentar.setText(attributes.get("TEXT"));
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String date = attributes.get("DATE") + " " + attributes.get("TIME");
        Date d;
        try {
            d = format.parse(date);
            kommentar.setDatum(new Timestamp(d.getTime()));
        }
        catch (ParseException e) {
            ReportItem warn = new ReportItem();
            warn.setCode(674);
            warn.setKey("kommentar");
            warn.setValue("Date: " + date);
            currentWarnings.add(warn);
        }
        if (!userInfo.getMessstellen().contains(kommentar.getMstId())) {
            return null;
        }
        return kommentar;
    }

    private void createStatusProtokoll(String status, Messung messung, String mstId) {
        int mst = Integer.valueOf(status.substring(0, 1));
        int land = Integer.valueOf(status.substring(1, 2));
        int lst = Integer.valueOf(status.substring(2, 3));

        boolean hasMst = false;
        boolean hasLand = false;

        StatusProtokoll last = null;
        if (userInfo.getFunktionenForMst(mstId).contains(1)) {
            QueryBuilder<StatusKombi> builder =
                new QueryBuilder<StatusKombi>(
                    repository.entityManager("stamm"),
                    StatusKombi.class);
            builder.and("statusWert", mst);
            builder.and("statusStufe", 1);
            List<StatusKombi> kombi =
                (List<StatusKombi>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (kombi != null && !kombi.isEmpty()) {
                StatusProtokoll statusMst = new StatusProtokoll();
                statusMst.setDatum(new Timestamp(new Date().getTime()));
                statusMst.setMessungsId(messung.getId());
                statusMst.setMstId(mstId);
                statusMst.setStatusKombi(kombi.get(0).getId());
                Response r = repository.create(statusMst, "land");
                last = (StatusProtokoll)r.getData();
            }
            hasMst = true;
        }
        MessStelle messStelle = repository.getByIdPlain(MessStelle.class, mstId, "stamm");
        if (userInfo.getNetzbetreiber().contains(messStelle.getNetzbetreiberId()) &&
            userInfo.getFunktionenForNetzbetreiber(messStelle.getNetzbetreiberId()).contains(2) &&
            hasMst) {
                // Set status for stufe land.
            QueryBuilder<StatusKombi> builder =
                new QueryBuilder<StatusKombi>(
                    repository.entityManager("stamm"),
                    StatusKombi.class);
            builder.and("statusWert", land);
            builder.and("statusStufe", 2);
            List<StatusKombi> kombi =
                (List<StatusKombi>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (kombi != null && !kombi.isEmpty()) {
                StatusProtokoll statusLand = new StatusProtokoll();
                statusLand.setDatum(new Timestamp(new Date().getTime()));
                statusLand.setMessungsId(messung.getId());
                statusLand.setMstId(mstId);
                statusLand.setStatusKombi(kombi.get(0).getId());
                Response r = repository.create(statusLand, "land");
                last = (StatusProtokoll)r.getData();
            }
        }
        if (userInfo.getFunktionen().contains(3) &&
            hasLand) {
            // Set status for stufe lst.
            QueryBuilder<StatusKombi> builder =
                new QueryBuilder<StatusKombi>(
                    repository.entityManager("stamm"),
                    StatusKombi.class);
            builder.and("statusWert", lst);
            builder.and("statusStufe", 3);
            List<StatusKombi> kombi =
                (List<StatusKombi>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (kombi != null && !kombi.isEmpty()) {
                StatusProtokoll statusLst = new StatusProtokoll();
                statusLst.setDatum(new Timestamp(new Date().getTime()));
                statusLst.setMessungsId(messung.getId());
                statusLst.setMstId(mstId);
                statusLst.setStatusKombi(kombi.get(0).getId());
                Response r = repository.create(statusLst, "land");
                last = (StatusProtokoll)r.getData();
            }
        }
        if (last != null) {
            messung.setStatus(last.getId());
            repository.update(messung, "land");
        }
    }

    private Ortszuordnung createUrsprungsOrt(
        Map<String, String> ursprungsOrt,
        Integer id
    ) {
        Ortszuordnung ort = new Ortszuordnung();
        ort.setOrtszuordnungTyp("U");
        ort.setProbeId(id);

        Ort o = findOrCreateOrt(ursprungsOrt, "U_");
        if (o == null) {
            return null;
        }
        ort.setOrtId(Long.valueOf(o.getId()));
        if (ursprungsOrt.containsKey("U_ORTS_ZUSATZTEXT")) {
            ort.setOrtszusatztext(ursprungsOrt.get("U_ORTS_ZUSATZTEXT"));
        }
        if (ursprungsOrt.containsKey("U_ORTS_ZUSATZCODE")) {
            Ortszusatz zusatz = repository.getByIdPlain(
                Ortszusatz.class,
                ursprungsOrt.get("U_ORTS_ZUSATZCODE"),
                "stamm");
            if (zusatz != null) {
                o.setOzId(zusatz.getOzsId());
                repository.update(o, "stamm");
            }
        }
        return ort;
    }

    private void createEntnahmeOrt(
        Map<String, String> entnahmeOrt,
        Integer id
    ) {
        Ortszuordnung ort = new Ortszuordnung();
        ort.setOrtszuordnungTyp("E");
        ort.setProbeId(id);

        Ort o = findOrCreateOrt(entnahmeOrt, "P_");
        if (o == null) {
            return;
        }
        ort.setOrtId(Long.valueOf(o.getId()));
        if (entnahmeOrt.containsKey("P_ORTS_ZUSATZTEXT")) {
            ort.setOrtszusatztext(entnahmeOrt.get("P_ORTS_ZUSATZTEXT"));
        }
        merger.mergeEntnahmeOrt(id, ort);
    }

    private Ort findOrCreateOrt(Map<String, String> attributes, String type) {
        // If laf contains coordinates, find a ort with matching coordinates or
        // create one.
        for (Entry<String, String> entry : attributes.entrySet()) {
            logger.debug(entry.getKey() + ": " + entry.getValue());
        }
        if ((attributes.get(type + "KOORDINATEN_ART") != null ||
             attributes.get(type + "KOORDINATEN_ART_S") != null) &&
            attributes.get(type + "KOORDINATEN_X") != null &&
            attributes.get(type + "KOORDINATEN_Y") != null
        ) {
            QueryBuilder<Ort> builder =
                new QueryBuilder<Ort>(
                    repository.entityManager("stamm"),
                    Ort.class);
            if (attributes.get(type + "KOORDINATEN_ART_S") != null) {
                builder.and("kdaId", Integer.valueOf(attributes.get(type + "KOORDINATEN_ART_S")));
            }
            else {
                QueryBuilder<KoordinatenArt> kdaBuilder =
                    new QueryBuilder<KoordinatenArt>(
                        repository.entityManager("stamm"),
                        KoordinatenArt.class);
                kdaBuilder.and("koordinatenart", attributes.get(type + "KOORDINATEN_ART"));
                List<KoordinatenArt> arten = repository.filterPlain(kdaBuilder.getQuery(), "stamm");
                if (arten == null || arten.isEmpty()) {
                    ReportItem err = new ReportItem();
                    err.setCode(632);
                    err.setKey("KoordinatenArt");
                    err.setValue("Not found");
                    currentErrors.add(err);
                    return null;
                }
                logger.debug("kda: " + arten.get(0).getId());
                builder.and("kdaId", arten.get(0).getId());
            }
            builder.and("koordXExtern", attributes.get(type + "KOORDINATEN_X"));
            builder.and("koordYExtern", attributes.get(type + "KOORDINATEN_Y"));
            List<Ort> orte = repository.filterPlain(builder.getQuery(), "stamm");
            logger.debug(attributes.get(type + "KOORDINATEN_ART_S"));
            logger.debug(attributes.get(type + "KOORDINATEN_X"));
            logger.debug(attributes.get(type + "KOORDINATEN_Y"));
            logger.debug(orte.size());
            if (orte != null && orte.size() > 0) {
                return orte.get(0);
            }
            else {
                return createNewOrt(attributes, type);
            }
        }
        // If laf contains gemeinde attributes, find a ort with matching gemId
        // or create one.
        String gemId = null;
        if (attributes.get(type + "GEMEINDENAME") != null) {
            QueryBuilder<Verwaltungseinheit> builder =
                new QueryBuilder<Verwaltungseinheit>(
                    repository.entityManager("stamm"),
                    Verwaltungseinheit.class);
            builder.and("bezeichnung", attributes.get(type + "GEMEINDENAME"));
            List<Verwaltungseinheit> ves =
                repository.filterPlain(builder.getQuery(), "stamm");
            if (ves != null && ves.size() > 0) {
                gemId = ves.get(0).getId();
            }
        }
        else if (attributes.get(type + "GEMEINDESCHLUESSEL") != null) {
            gemId = attributes.get(type + "GEMEINDESCHLUESSEL");
        }
        if (gemId != null) {
            QueryBuilder<Ort> builder =
                new QueryBuilder<Ort>(
                    repository.entityManager("stamm"),
                    Ort.class);
            builder.and("gemId", gemId);
            List<Ort> orte = repository.filterPlain(builder.getQuery(), "stamm");
            if (orte != null && orte.size() > 0) {
                return orte.get(0);
            }
            else {
                return createNewOrt(attributes, type);
            }
        }
        else {
            // Create a new ort.
        }
        return createNewOrt(attributes, type);
    }

    private Ort createNewOrt(Map<String, String> attributes, String type) {
        Ort ort = new Ort();
        ort.setOrtTyp(1);
        String hLand = "";
        String staatFilter = "";
        if (attributes.get(type + "HERKUNFTSLAND_S") != null) {
            staatFilter = "staatIso";
            hLand = attributes.get(type + "HERKUNFTSLAND_S");
        }
        else if (attributes.get(type + "HERKUNFTSLAND_KURZ") != null) {
            staatFilter = "staatKurz";
            hLand = attributes.get(type + "HERKUNFTSLAND_KURZ");
        }
        else if (attributes.get(type + "HERKUNFTSLAND_LANG") != null) {
            staatFilter = "staat";
            hLand = attributes.get(type + "HERKUNFTSLAND_LANG");
        }
        QueryBuilder<Staat> builderStaat =
            new QueryBuilder<Staat>(
                repository.entityManager("stamm"),
                Staat.class);
        if (staatFilter.length() > 0) {
            builderStaat.and(staatFilter, hLand);
            List<Staat> staat =
                repository.filterPlain(builderStaat.getQuery(), "stamm");
            if (staat != null && staat.size() > 0) {
                ort.setStaatId(staat.get(0).getId());
            }
        }

        String gemId = null;
        if (attributes.get(type + "GEMEINDENAME") != null) {
            QueryBuilder<Verwaltungseinheit> builder =
                new QueryBuilder<Verwaltungseinheit>(
                    repository.entityManager("stamm"),
                    Verwaltungseinheit.class);
            builder.and("bezeichnung", attributes.get(type + "GEMEINDENAME"));
            List<Verwaltungseinheit> ves =
                repository.filterPlain(builder.getQuery(), "stamm");
            if (ves != null && ves.size() > 0) {
                gemId = ves.get(0).getId();
            }
        }
        else if (attributes.get(type + "GEMEINDESCHLUESSEL") != null) {
            gemId = attributes.get(type + "GEMEINDESCHLUESSEL");
        }
        if (gemId != null) {
            ort.setGemId(gemId);
        }
        if ((attributes.get(type + "KOORDINATEN_ART") != null ||
             attributes.get(type + "KOORDINATEN_ART_S") != null) &&
            attributes.get(type + "KOORDINATEN_X") != null &&
            attributes.get(type + "KOORDINATEN_Y") != null
        ) {
            if (attributes.get(type + "KOORDINATEN_ART_S") != null) {
            }
        }
//        repository.create(ort, "stamm");
//        return ort;
        return null;
    }

    private void logProbe(Probe probe) {
        logger.debug("%PROBE%");
        logger.debug("datenbasis: " + probe.getDatenbasisId());
        logger.debug("betriebsart: " + probe.getBaId());
        logger.debug("erzeuger: " + probe.getErzeugerId());
        logger.debug("hauptprobennummer: " + probe.getHauptprobenNr());
        logger.debug("idalt: " + probe.getIdAlt());
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

        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        if ("DATENBASIS_S".equals(key) && probe.getDatenbasisId() == null) {
            Integer v = Integer.valueOf(value.toString());
            probe.setDatenbasisId(v);
        }
        else if ("DATENBASIS_S".equals(key) && probe.getDatenbasisId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }


        if ("DATENBASIS".equals(key) && probe.getDatenbasisId() == null) {
            QueryBuilder<Datenbasis> builder =
                new QueryBuilder<Datenbasis>(
                    repository.entityManager("stamm"),
                    Datenbasis.class);
            builder.and("datenbasis", value.toString());
            List<Datenbasis> datenbasis =
                (List<Datenbasis>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (datenbasis == null || datenbasis.isEmpty()) {
                ReportItem err = new ReportItem();
                err.setCode(673);
                err.setKey("datenbasis");
                err.setValue(key);
                currentErrors.add(err);
                return;
            }
            Integer v = datenbasis.get(0).getId();
            probe.setDatenbasisId(v);
        }
        else if ("DATENBASIS".equals(key) && probe.getDatenbasisId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }

        if ("PROBE_ID".equals(key)) {
            probe.setIdAlt(value);
        }

        if ("HAUPTPROBENNUMMER".equals(key)) {
            probe.setHauptprobenNr(value.toString());
        }

        if ("MPR_ID".equals(key)) {
            Integer v = Integer.valueOf(value.toString());
            probe.setMprId(v);
        }

        if ("MESSSTELLE".equals(key)) {
            probe.setMstId(value.toString());
        }

        if ("MESSLABOR".equals(key)) {
            probe.setLaborMstId(value.toString());
        }

        if ("MESSPROGRAMM_S".equals(key) && probe.getBaId() == null) {
            QueryBuilder<MessprogrammTransfer> builder =
                new QueryBuilder<MessprogrammTransfer>(
                    repository.entityManager("stamm"),
                    MessprogrammTransfer.class);
            builder.and("messprogrammS", value);
            List<MessprogrammTransfer> transfer=
                (List<MessprogrammTransfer>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (transfer == null || transfer.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("messprogramm");
                warn.setValue(key);
                currentWarnings.add(warn);
                return;
            }
            probe.setBaId(transfer.get(0).getBaId());
        }
        if ("MESSPROGRAMM_C".equals(key)){
            QueryBuilder<MessprogrammTransfer> builder =
                new QueryBuilder<MessprogrammTransfer>(
                    repository.entityManager("stamm"),
                    MessprogrammTransfer.class);
            builder.and("messprogrammC", value);
            List<MessprogrammTransfer> transfer=
                (List<MessprogrammTransfer>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (transfer == null || transfer.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("messprogramm");
                warn.setValue(key);
                currentWarnings.add(warn);
                return;
            }
            probe.setBaId(transfer.get(0).getBaId());
        }

        if ("MESSPROGRAMM_LAND".equals(key)) {
            QueryBuilder<MessprogrammKategorie> builder =
                new QueryBuilder<MessprogrammKategorie>(
                    repository.entityManager("stamm"),
                    MessprogrammKategorie.class);
            builder.or("netzbetreiberId", userInfo.getNetzbetreiber());
            builder.and("code", value);
            List<MessprogrammKategorie> kategorie =
                (List<MessprogrammKategorie>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (kategorie == null || kategorie.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("messprogramm_land");
                warn.setValue(key);
                currentWarnings.add(warn);
                return;
            }
            probe.setMplId(kategorie.get(0).getId());
        }

        if ("SOLL_DATUM_UHRZEIT_A".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setSolldatumBeginn(new Timestamp(d.getTime()));
            }
            catch (ParseException e) {
                currentWarnings.add(new ReportItem(key, value.toString(), 674));
            }
        }
        if ("SOLL_DATUM_UHRZEIT_E".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setSolldatumEnde(new Timestamp(d.getTime()));
            }
            catch (ParseException e) {
                currentWarnings.add(new ReportItem(key, value.toString(), 674));
            }
        }
        if ("PROBENAHME_DATUM_UHRZEIT_A".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setProbeentnahmeBeginn(new Timestamp(d.getTime()));
            }
            catch (ParseException e) {
                currentWarnings.add(new ReportItem(key, value.toString(), 674));
            }
        }
        if ("PROBENAHME_DATUM_UHRZEIT_E".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                probe.setProbeentnahmeEnde(new Timestamp(d.getTime()));
            }
            catch (ParseException e) {
                currentWarnings.add(new ReportItem(key, value.toString(), 674));
            }
        }

        if ("UMWELTBEREICH_S".equals(key) && probe.getUmwId() == null) {
            probe.setUmwId(value.toString());
        }
        else if ("UMWELTBEREICH_S".equals(key) && probe.getUmwId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }
        if ("UMWELTBEREICH_C".equals(key) && probe.getUmwId() == null) {
            QueryBuilder<Umwelt> builder =
                new QueryBuilder<Umwelt>(
                    repository.entityManager("stamm"),
                    Umwelt.class);
            int length = value.toString().length() > 80 ? 80 : value.toString().length();
            builder.and("umweltBereich", value.toString().substring(0, length));
            List<Umwelt> umwelt =
                (List<Umwelt>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (umwelt == null || umwelt.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("umwelt");
                warn.setValue(key);
                currentWarnings.add(warn);
                return;
            }
            probe.setUmwId(umwelt.get(0).getId());
        }
        else if ("UMWELTBEREICH_C".equals(key) && probe.getUmwId() != null){
            currentWarnings.add(new ReportItem(key, value.toString(), 672));
        }

        if ("DESKRIPTOREN".equals(key)) {
            value = value.replace(" ", "0");
            List<String> tmp = new ArrayList<String>();
            tmp.add("D:");
            for (int i =  0; i < value.length() - 2; i += 2) {
                tmp.add(value.substring(i, i+2));
            }
            probe.setMediaDesk(StringUtils.join(tmp.toArray(), " "));
        }

        if ("TESTDATEN".equals(key)) {
            if (!value.toString().equals("0")) {
                probe.setTest(true);
            }
            else {
                probe.setTest(false);
            }
        }

        if ("MEDIUM".equals(key)) {
            probe.setMedia(value.toString());
        }

        if ("PROBENART".equals(key)) {
            QueryBuilder<Probenart> builder =
                new QueryBuilder<Probenart>(
                    repository.entityManager("stamm"),
                    Probenart.class);
            builder.and("probenart", value.toString());
            List<Probenart> probenart =
                (List<Probenart>)repository.filter(
                    builder.getQuery(),
                    "stamm").getData();
            if (probenart == null || probenart.isEmpty()) {
                ReportItem warn = new ReportItem();
                warn.setCode(673);
                warn.setKey("probenart");
                warn.setValue(key);
                currentWarnings.add(warn);
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
        DateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        if ("MESSUNGS_ID".equals(key)) {
            messung.setIdAlt(Integer.valueOf(value));
        }
        if ("NEBENPROBENNUMMER".equals(key)) {
            messung.setNebenprobenNr(value.toString());
        }
        else if ("MESS_DATUM_UHRZEIT".equals(key)) {
            try {
                Date d = format.parse(value.toString());
                messung.setMesszeitpunkt(new Timestamp(d.getTime()));
            }
            catch (ParseException e) {
                currentWarnings.add(new ReportItem(key, value.toString(), 674));
            }
        }
        else if ("MESSZEIT_SEKUNDEN".equals(key)) {
            Integer i = Integer.valueOf(value.toString());
            messung.setMessdauer(i);
        }
        else if ("MESSMETHODE_S".equals(key)) {
            messung.setMmtId(value.toString());
        }
        else if ("ERFASSUNG_ABGESCHLOSSEN".equals(key)) {
            if(!value.toString().equals("0")) {
                messung.setFertig(true);
            }
            else {
                messung.setFertig(false);
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
}



