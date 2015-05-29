package de.intevation.lada.factory;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.LKommentarP;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.MessprogrammMmt;
import de.intevation.lada.model.land.MessungTranslation;
import de.intevation.lada.model.land.ProbeTranslation;
import de.intevation.lada.model.stamm.SOrt;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

public class ProbeFactory {

    @Inject
    @RepositoryConfig(type = RepositoryType.RW)
    private Repository repository;

    public List<LProbe> create(String id, Long from, Long to) {
        QueryBuilder<Messprogramm> builder =
            new QueryBuilder<Messprogramm>(
                    repository.entityManager("land"),
                    Messprogramm.class);
        builder.and("id", id);
        Response response = repository.filter(builder.getQuery(), "land");
        List<Messprogramm> messprogramme =
            (List<Messprogramm>)response.getData();
        if (messprogramme == null || messprogramme.isEmpty()) {
            return null;
        }
        Messprogramm messprogramm = messprogramme.get(0);
        Calendar start = Calendar.getInstance();
        start.setTime(new Date(from));
        Calendar end = Calendar.getInstance();
        end.setTime(new Date (to));
        // benutzereingabe + gültigVon/Bis
        // kann mehrere Intervalle enthalten, wenn nutzereingabe über mehrere
        // Jahre.
        Date[][] intervals = calculateIntervals(start, end, messprogramm);
        List<LProbe> proben = new ArrayList<LProbe>();
        for (Date[] interval : intervals) {
            //erzeuge proben für einen intervall(gültigkeitsbereich)
            createProben(interval, messprogramm, proben);
        }
        return proben;
    }

    private void createProben(
        Date[] interval,
        Messprogramm messprogramm,
        List<LProbe> proben
    ) {
        Calendar start = Calendar.getInstance();
        start.setTime(interval[0]);
        int startDay = start.get(Calendar.DAY_OF_YEAR);
        Calendar end = Calendar.getInstance();
        end.setTime(interval[1]);
        int endDay = end.get(Calendar.DAY_OF_YEAR);
        if ("J".equals(messprogramm.getProbenintervall()) &&
            messprogramm.getTeilintervallVon() >= startDay + messprogramm.getIntervallOffset() &&
            messprogramm.getTeilintervallBis() <= endDay + messprogramm.getIntervallOffset()) {
            start.add(Calendar.DATE, messprogramm.getTeilintervallVon() - startDay + messprogramm.getIntervallOffset());
            Date startDate = start.getTime();
            end.add(Calendar.DATE, messprogramm.getTeilintervallBis() - endDay + messprogramm.getIntervallOffset());
            Date endDate = end.getTime();
            LProbe probe = createObjects(messprogramm, startDate, endDate);
            proben.add(probe);
            return;
        }
        int intervalDays = parseInterval(messprogramm.getProbenintervall());
        int teilStart = messprogramm.getTeilintervallVon() + startDay;
        int teilEnd = messprogramm.getTeilintervallBis() + startDay;
        int offset = messprogramm.getIntervallOffset() == null ? 0 : messprogramm.getIntervallOffset();

        for (;
            teilStart >= startDay + offset && teilEnd <= endDay + offset;) {
            start.add(Calendar.DATE, teilStart - startDay + offset);
            Date startDate = start.getTime();
            end.add(Calendar.DATE, teilEnd - endDay + offset);
            Date endDate = end.getTime();
            LProbe probe = createObjects(messprogramm, startDate, endDate);
            proben.add(probe);
            teilStart += intervalDays;
            teilEnd += intervalDays;
            end.setTime(interval[1]);
            start.setTime(interval[0]);
        }
        return;
    }

    private LProbe createObjects(
        Messprogramm messprogramm,
        Date startDate,
        Date endDate
    ) {
        LProbe probe = new LProbe();
        probe.setBaId(messprogramm.getBaId());
        probe.setDatenbasisId(messprogramm.getDatenbasisId());
        probe.setMediaDesk(messprogramm.getMediaDesk());
        probe.setMstId(messprogramm.getMstId());
        probe.setNetzbetreiberId(messprogramm.getNetzbetreiberId());
        probe.setProbenartId(messprogramm.getProbenartId());
        probe.setProbeNehmerId(messprogramm.getProbeNehmerId());
        probe.setSolldatumBeginn(new Timestamp(startDate.getTime()));
        probe.setSolldatumEnde(new Timestamp(endDate.getTime()));
        probe.setTest(messprogramm.getTest());
        probe.setUmwId(messprogramm.getUmwId());
        repository.create(probe, "land");
        ProbeTranslation translation = new ProbeTranslation();
        translation.setProbeId(probe);
        repository.create(translation, "land");

        if (messprogramm.getProbeKommentar() != null &&
            !messprogramm.getProbeKommentar().equals("")) {
            LKommentarP kommentar = new LKommentarP();
            kommentar.setDatum(new Timestamp(new Date().getTime()));
            kommentar.setProbeId(probe.getId());
            kommentar.setText(messprogramm.getProbeKommentar());
            kommentar.setErzeuger(messprogramm.getMstId());

            repository.create(kommentar, "land");
        }

        QueryBuilder<MessprogrammMmt> builder =
            new QueryBuilder<MessprogrammMmt>(
                    repository.entityManager("land"),
                    MessprogrammMmt.class);
        builder.and("messprogrammId", messprogramm.getId());
        Response response = repository.filter(builder.getQuery(), "land");
        List<MessprogrammMmt> mmts = (List<MessprogrammMmt>)response.getData();
        for (MessprogrammMmt mmt : mmts) {
            LMessung messung = new LMessung();
            messung.setFertig(false);
            messung.setGeplant(true);
            messung.setMmtId(mmt.getMmtId());
            messung.setNebenprobenNr(
                messprogramm.getNetzbetreiberId() + mmt.getMmtId());
            messung.setProbeId(probe.getId());
            repository.create(messung, "land");
            MessungTranslation mTranslation = new MessungTranslation();
            mTranslation.setMessungsId(messung);
            repository.create(mTranslation, "land");
            for (int mw : mmt.getMessgroessen()) {
                LMesswert wert = new LMesswert();
                wert.setMessgroesseId(mw);
                wert.setMessungsId(messung.getId());
                wert.setMesswert(0f);
                wert.setMehId(1);
                repository.create(wert, "land");
            }
        }
        if (messprogramm.getGemId() != null &&
            !messprogramm.getGemId().equals("")) {
            LOrt ort = new LOrt();
            ort.setOrtsTyp("E");
            ort.setProbeId(probe.getId());
            QueryBuilder<SOrt> ortBuilder = new QueryBuilder<SOrt>(
                repository.entityManager("stamm"), SOrt.class);
            ortBuilder.and("verwaltungseinheitId", messprogramm.getGemId());
            Response ortResponse = repository.filter(ortBuilder.getQuery(), "stamm");
            List<SOrt> orte = (List<SOrt>) ortResponse.getData();
            if (orte != null && !orte.isEmpty()) {
                ort.setOrt(BigInteger.valueOf(orte.get(0).getId()));
            }
            repository.create(ort, "land");
        }
        // Reolad the probe to have the old id
        probe =
            (LProbe)repository.getById(LProbe.class, probe.getId(), "land").getData();
        return probe;
    }

    private Date[][] calculateIntervals(
        Calendar start,
        Calendar end,
        Messprogramm messprogramm
    ) {
        int realStart = start.get(Calendar.DAY_OF_YEAR);
        int realEnd = end.get(Calendar.DAY_OF_YEAR);
        int startYear = start.get(Calendar.YEAR);
        int endYear = end.get(Calendar.YEAR);
        int years = (endYear - startYear) + 1;
        Date[][] intervals = new Date[years][2];
        for (int i = 0; i < years; i++) {
            Calendar cStart = Calendar.getInstance();
            cStart.set(startYear + i, start.get(Calendar.MONTH), start.get(Calendar.DAY_OF_MONTH));
            if (messprogramm.getGueltigVon() == null ||
                messprogramm.getGueltigVon() <= 0 ||
                (realStart > messprogramm.getGueltigVon() &&
                 i == 0)
            ) {
                intervals[0][0] = start.getTime();
            }
            else {
                start.add(Calendar.DATE, messprogramm.getGueltigVon() - realStart);
                Date startDate = start.getTime();
                intervals[i][0] = startDate;
            }

            Calendar cEnd = Calendar.getInstance();
            cEnd.set(startYear + i, end.get(Calendar.MONTH), end.get(Calendar.DAY_OF_MONTH));
            if (messprogramm.getGueltigBis() == null ||
                messprogramm.getGueltigBis() <= 0 ||
                (realEnd < messprogramm.getGueltigBis() &&
                 i == years - 1)
            ) {
                intervals[i][1] = cEnd.getTime();
            }
            else {
                cEnd.add(Calendar.DATE, messprogramm.getGueltigBis() - realEnd);
                Date endDate = cEnd.getTime();
                intervals[i][1] = endDate;
            }
        }

        return intervals;
    }

    private int parseInterval(String interval) {
        if ("J".equals(interval)) {
            return 365;
        }
        else if ("H".equals(interval)) {
            return 183;
        }
        else if ("Q".equals(interval)) {
            return 91;
        }
        else if ("M".equals(interval)) {
            return 30;
        }
        else if ("W4".equals(interval)) {
            return 28;
        }
        else if ("W2".equals(interval)) {
            return 14;
        }
        else if ("W".equals(interval)) {
            return 7;
        }
        else if ("T".equals(interval)) {
            return 1;
        }
        else {
            return 0;
        }
    }
}
