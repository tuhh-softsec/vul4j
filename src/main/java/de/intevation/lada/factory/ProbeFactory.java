/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.factory;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LKommentarP;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.land.LOrtszuordnung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LStatusProtokoll;
import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.MessprogrammMmt;
import de.intevation.lada.model.land.MessungTranslation;
import de.intevation.lada.model.land.ProbeTranslation;
import de.intevation.lada.model.stamm.DeskriptorUmwelt;
import de.intevation.lada.model.stamm.Deskriptoren;
import de.intevation.lada.model.stamm.Ort;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

/**
 * This factory creates probe objects and its children using a messprogramm
 * as template.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ProbeFactory {

    @Inject Logger logger;

    /**
     * The data repository
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RW)
    private Repository repository;

    /**
     * Create a list of probe objects
     *
     * @param id    Messprogramm id
     * @param from  The start date
     * @param to    The end date
     *
     * @return List of probe objects.
     */
    public List<LProbe> create(String id, Long from, Long to) {
        QueryBuilder<Messprogramm> builder =
            new QueryBuilder<Messprogramm>(
                    repository.entityManager("land"),
                    Messprogramm.class);
        builder.and("id", id);
        Response response = repository.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
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
        List<LProbe> proben = new ArrayList<LProbe>();


        if ("M".equals(messprogramm.getProbenintervall())) {
            Calendar realStart = getStart ("M", start);
            proben.addAll(generateMonth(messprogramm, realStart, end, "M"));
        }
        else if ("Q".equals(messprogramm.getProbenintervall())) {
            Calendar realStart = getStart ("Q", start);
            proben.addAll(generateQuarter(messprogramm, realStart, end, "Q"));
        }
        else if ("H".equals(messprogramm.getProbenintervall())) {
            Calendar realStart = getStart ("H", start);
            proben.addAll(generateHalf(messprogramm, realStart, end, "H"));
        }
        else {
            Date[][] intervals = calculateIntervals(start, end, messprogramm);
            for (Date[] interval : intervals) {
                createProben(interval, messprogramm, proben);
            }
        }
        return proben;
    }

    /**
     * Create LProbe objects using the interval and messprogramm details.
     *
     * @param   interval        The time interval for probe objects.
     * @param   messprogramm    The messprogramm containing details.
     * @param   proben          An (empty) list for probe objects filled by this
     *                          method
     * @return
     */
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

        // If fixed interval (T, W, W2, W4)
        if ("T".equals(messprogramm.getProbenintervall())) {
            proben.addAll(generate(messprogramm, start, end, 1));
        }
        else if ("W".equals(messprogramm.getProbenintervall())) {
            Calendar realStart = getMonday(start);
            proben.addAll(generate(messprogramm, realStart, end, 7));
        }
        else if ("W2".equals(messprogramm.getProbenintervall())) {
            Calendar realStart = getMonday(start);
            proben.addAll(generate(messprogramm, realStart, end, 14));
        }
        else if ("W4".equals(messprogramm.getProbenintervall())) {
            Calendar realStart = getMonday(start);
            proben.addAll(generate(messprogramm, realStart, end, 28));
        }
        else if ("J".equals(messprogramm.getProbenintervall()) &&
            messprogramm.getTeilintervallVon() >= startDay + messprogramm.getIntervallOffset() &&
            messprogramm.getTeilintervallBis() <= endDay + messprogramm.getIntervallOffset()) {
            start.add(Calendar.DATE, messprogramm.getTeilintervallVon() - startDay + messprogramm.getIntervallOffset());
            Date startDate = start.getTime();
            end.add(Calendar.DATE, messprogramm.getTeilintervallBis() - endDay + messprogramm.getIntervallOffset());
            Date endDate = end.getTime();
            LProbe probe = createObjects(messprogramm, startDate, endDate);
            if (probe != null) {
                proben.add(probe);
            }
            return;
        }
    }

    private List<LProbe> generateMonth(
        Messprogramm messprogramm,
        Calendar start,
        Calendar end,
        String interval
    ) {
        int offset = messprogramm.getIntervallOffset() == null ?
            0 : messprogramm.getIntervallOffset();
        int teilVon = messprogramm.getTeilintervallVon() == null ?
            0 : messprogramm.getTeilintervallVon() - 1;
        int teilBis = messprogramm.getTeilintervallBis() == null ?
            0 : messprogramm.getTeilintervallBis();
        int manualDuration = teilBis - teilVon;

        List<LProbe> proben = new ArrayList<LProbe>();

        int currentLength = getDurationMonth(start);
        if (manualDuration > 0) {
            currentLength = manualDuration;
        }
        Calendar monthStart = (Calendar)start.clone();
        Calendar monthEnd = Calendar.getInstance();
        monthStart.add(Calendar.DAY_OF_YEAR, offset + teilVon);
        monthEnd.setTime(start.getTime());
        monthEnd.add(Calendar.DAY_OF_YEAR, currentLength - 1);
        for (;monthStart.before(end);) {
            if (monthStart.get(Calendar.DAY_OF_YEAR) > messprogramm.getGueltigVon() &&
                monthStart.get(Calendar.DAY_OF_YEAR) < messprogramm.getGueltigBis()) {
                LProbe probe = createObjects(messprogramm, monthStart.getTime(), monthEnd.getTime());
                if (probe != null) {
                    proben.add(probe);
                }
            }
            monthStart.set(Calendar.DAY_OF_MONTH, 2);
            monthStart = getStart("M", monthStart);
            monthStart.add(Calendar.DAY_OF_YEAR, offset + teilVon);
            if (manualDuration <= 0) {
                currentLength = getDurationMonth(monthStart);
            }
            monthEnd.setTime(monthStart.getTime());
            monthEnd.add(Calendar.DAY_OF_YEAR, currentLength - 1);
        }

        return proben;
    }

    private List<LProbe> generateQuarter(
        Messprogramm messprogramm,
        Calendar start,
        Calendar end,
        String interval
    ) {
        int offset = messprogramm.getIntervallOffset() == null ?
            0 : messprogramm.getIntervallOffset();
        int teilVon = messprogramm.getTeilintervallVon() == null ?
            0 : messprogramm.getTeilintervallVon() - 1;
        int teilBis = messprogramm.getTeilintervallBis() == null ?
            0 : messprogramm.getTeilintervallBis();
        int manualDuration = teilBis - teilVon;

        List<LProbe> proben = new ArrayList<LProbe>();

        int currentLength = getDurationQuarter(start);
        if (manualDuration > 0) {
            currentLength = manualDuration;
        }
        Calendar quarterStart = (Calendar)start.clone();
        Calendar quarterEnd = Calendar.getInstance();
        quarterStart.add(Calendar.DAY_OF_YEAR, offset + teilVon);
        quarterEnd.setTime(start.getTime());
        quarterEnd.add(Calendar.DAY_OF_YEAR, currentLength);
        for (;quarterStart.before(end);) {
            if (quarterStart.get(Calendar.DAY_OF_YEAR) > messprogramm.getGueltigVon() &&
                quarterStart.get(Calendar.DAY_OF_YEAR) < messprogramm.getGueltigBis()) {
                LProbe probe = createObjects(messprogramm, quarterStart.getTime(), quarterEnd.getTime());
                if (probe != null) {
                    proben.add(probe);
                }
            }
            quarterStart.set(Calendar.DAY_OF_MONTH, 2);
            quarterStart = getStart("Q", quarterStart);
            quarterStart.add(Calendar.DAY_OF_YEAR, offset + teilVon);
            if (manualDuration <= 0) {
                currentLength = getDurationQuarter(quarterStart);
            }
            quarterEnd.setTime(quarterStart.getTime());
            quarterEnd.add(Calendar.DAY_OF_YEAR, currentLength);
        }

        return proben;
    }

    private List<LProbe> generateHalf(
        Messprogramm messprogramm,
        Calendar start,
        Calendar end,
        String interval
    ) {
        int offset = messprogramm.getIntervallOffset() == null ?
            0 : messprogramm.getIntervallOffset();
        int teilVon = messprogramm.getTeilintervallVon() == null ?
            0 : messprogramm.getTeilintervallVon() - 1;
        int teilBis = messprogramm.getTeilintervallBis() == null ?
            0 : messprogramm.getTeilintervallBis();
        int manualDuration = teilBis - teilVon;

        List<LProbe> proben = new ArrayList<LProbe>();

        int currentLength = getDurationHalf(start);
        if (manualDuration > 0) {
            currentLength = manualDuration;
        }
        Calendar halfStart = (Calendar)start.clone();
        Calendar halfEnd = Calendar.getInstance();
        halfStart.add(Calendar.DAY_OF_YEAR, offset + teilVon);
        halfEnd.setTime(halfStart.getTime());
        halfEnd.add(Calendar.DAY_OF_YEAR, currentLength);
        for (;halfStart.before(end);) {
            if (halfStart.get(Calendar.DAY_OF_YEAR) > messprogramm.getGueltigVon() &&
                halfStart.get(Calendar.DAY_OF_YEAR) < messprogramm.getGueltigBis()) {
                LProbe probe = createObjects(messprogramm, halfStart.getTime(), halfEnd.getTime());
                if (probe != null) {
                    proben.add(probe);
                }
            }
            halfStart.set(Calendar.DAY_OF_MONTH, 2);
            halfStart = getStart("H", halfStart);
            halfStart.add(Calendar.DAY_OF_YEAR, offset + teilVon);
            if (manualDuration <= 0) {
                currentLength = getDurationHalf(halfStart);
            }
            halfEnd.setTime(halfStart.getTime());
            halfEnd.add(Calendar.DAY_OF_YEAR, currentLength);
        }

        return proben;
    }

    private List<LProbe> generate(
        Messprogramm messprogramm,
        Calendar start,
        Calendar end,
        int days
    ) {
        int offset = messprogramm.getIntervallOffset() == null ?
            0 : messprogramm.getIntervallOffset();
        int startDay = start.get(Calendar.DAY_OF_YEAR) +
            messprogramm.getTeilintervallVon() - 1 + offset;
        int endDay = end.get(Calendar.DAY_OF_YEAR);


        //int teilStart = messprogramm.getTeilintervallVon() + startDay;
        //int teilEnd = messprogramm.getTeilintervallBis() + startDay;
        List<LProbe> proben = new ArrayList<LProbe>();
        int duration = messprogramm.getTeilintervallBis() -
            messprogramm.getTeilintervallVon();

        logger.debug("real start day: " + startDay);
        logger.debug("real end day" + endDay);
        for (;startDay <= endDay;) {
            logger.debug("generate from " + startDay);
            start.set(Calendar.DAY_OF_YEAR, startDay);
            end.set(Calendar.DAY_OF_YEAR, startDay + duration);
            logger.debug("from: " + start.getTime() + " to " + end.getTime());
            startDay += days;
            LProbe probe = createObjects(messprogramm, start.getTime(), end.getTime());
            if (probe != null) {
                proben.add(probe);
            }
        }
        return proben;
    }

    private int getDurationMonth(Calendar month) {
        return month.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private int getDurationQuarter(Calendar month) {
        if ((month.get(Calendar.MONTH)) % 3 != 0) {
            logger.debug("not a valid month!");
            return 91; // Fallback to not generate to much probe objects.
        }
        int duration = 0;
        Calendar tmp = (Calendar)month.clone();
        for (int i = 0; i < 3; i++) {
            duration += tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
            tmp.set(Calendar.MONTH, tmp.get(Calendar.MONTH) + 1);
        }
        return duration;
    }

    private int getDurationHalf(Calendar month) {
        if ((month.get(Calendar.MONTH)) % 6 != 0) {
            logger.debug("not a valid month!");
            return 183; // Fallback to not generate to much probe objects.
        }
        int duration = 0;
        Calendar tmp = (Calendar)month.clone();
        for (int i = 0; i < 6; i++) {
            duration += tmp.getActualMaximum(Calendar.DAY_OF_MONTH);
            tmp.set(Calendar.MONTH, tmp.get(Calendar.MONTH) + 1);
        }
        return duration - 1;
    }

    private Calendar getStart(String interval, Calendar date) {
        Calendar start = Calendar.getInstance();
        start.setTime(date.getTime());
        if ("M".equals(interval)) {
            if (start.get(Calendar.DAY_OF_MONTH) > 1) {
                int month = start.get(Calendar.MONTH) + 1;
                start.set(Calendar.MONTH, month);
                start.set(Calendar.DAY_OF_MONTH, 1);
            }
        }
        else if ("Q".equals(interval)) {
            int month = start.get(Calendar.MONTH); /* 0 through 11 */
            int quarter = (month / 3) + 1;
            Calendar tmp = (Calendar)start.clone();
            logger.debug("in " + quarter + ". quarter");

            tmp.set(Calendar.MONTH, (quarter * 3));
            logger.debug("its the " + tmp.get(Calendar.MONTH) + ". month");
            tmp.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfQuarter = tmp.get(Calendar.DAY_OF_YEAR);
            if (start.get(Calendar.DAY_OF_YEAR) > firstDayOfQuarter) {
                if (quarter < 4) {
                    start.set(Calendar.MONTH, (quarter + 1) * 3 - 2);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                }
                else {
                    // Next year...
                    start.set(Calendar.MONTH, 0);
                    start.set(Calendar.YEAR, start.get(Calendar.YEAR) + 1);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                }
            }
            else {
                start = (Calendar)tmp.clone();
            }
        }
        else if ("H".equals(interval)) {
            int month = start.get(Calendar.MONTH);
            int half = (month / 6) + 1;
            Calendar tmp = (Calendar)start.clone();
            tmp.set(Calendar.MONTH, half * 6 - 6);
            tmp.set(Calendar.DAY_OF_MONTH, 1);
            int firstDayOfHalf = tmp.get(Calendar.DAY_OF_YEAR);
            if (start.get(Calendar.DAY_OF_YEAR) > firstDayOfHalf) {
                if (half == 1) {
                    start.set(Calendar.MONTH, (half + 1) * 6 - 6);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                }
                else {
                    // Next year...
                    start.set(Calendar.MONTH, 0);
                    start.set(Calendar.YEAR, start.get(Calendar.YEAR) + 1);
                    start.set(Calendar.DAY_OF_MONTH, 1);
                }
            }
        }
        return start;
    }

    private Calendar getMonday(Calendar week) {
        if (week.get(Calendar.DAY_OF_WEEK) > 1) {
            week.set(Calendar.WEEK_OF_YEAR, week.get(Calendar.WEEK_OF_YEAR) + 1);
        }
        week.set(Calendar.DAY_OF_WEEK, week.getFirstDayOfWeek());
        return week;
    }

    /**
     * Create a single probe object.
     *
     * @param   messprogramm    The messprogramm containing probe details
     * @param   startDate       The date for 'solldatumbeginn'
     * @param   endDate         The date for 'solldatumende'
     *
     * @return The new probe object.
     */
    private LProbe createObjects(
        Messprogramm messprogramm,
        Date startDate,
        Date endDate
    ) {
        QueryBuilder<LProbe> builderProbe =
            new QueryBuilder<LProbe>(
                repository.entityManager("land"),
                LProbe.class);
        builderProbe.and("mprId", messprogramm.getId());
        builderProbe.and("solldatumBeginn", startDate);
        builderProbe.and("solldatumEnde", endDate);

        List<LProbe> proben =
            repository.filterPlain(builderProbe.getQuery(), "land");

        if (!proben.isEmpty()) {
            return null;
        }
        LProbe probe = new LProbe();
        probe.setBaId(messprogramm.getBaId());
        probe.setDatenbasisId(messprogramm.getDatenbasisId());
        probe.setMediaDesk(messprogramm.getMediaDesk());
        probe.setMstId(messprogramm.getMstId());
        probe.setLaborMstId(messprogramm.getLaborMstId());
        probe.setNetzbetreiberId(messprogramm.getNetzbetreiberId());
        probe.setProbenartId(messprogramm.getProbenartId());
        probe.setProbeNehmerId(messprogramm.getProbeNehmerId());
        probe.setSolldatumBeginn(new Timestamp(startDate.getTime()));
        probe.setSolldatumEnde(new Timestamp(endDate.getTime()));
        probe.setTest(messprogramm.getTest());
        probe.setUmwId(messprogramm.getUmwId());
        probe.setMprId(messprogramm.getId());
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
        @SuppressWarnings("unchecked")
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

            LStatusProtokoll status = new LStatusProtokoll();
            status.setDatum(new Timestamp(new Date().getTime()));
            status.setMessungsId(messung.getId());
            status.setErzeuger(probe.getMstId());
            status.setStatusStufe(1);
            status.setStatusWert(0);
            repository.create(status, "land");
            messung.setStatus(status.getId());
            repository.update(messung, "land");

            for (int mw : mmt.getMessgroessen()) {
                LMesswert wert = new LMesswert();
                wert.setMessgroesseId(mw);
                wert.setMessungsId(messung.getId());
                wert.setMesswert(0d);
                wert.setMehId(1);
                repository.create(wert, "land");
            }
        }
        if (messprogramm.getOrtId() != null &&
            !messprogramm.getOrtId().equals("")) {
            LOrtszuordnung ort = new LOrtszuordnung();
            ort.setOrtszuordnungTyp("E");
            ort.setProbeId(probe.getId());
            QueryBuilder<Ort> ortBuilder = new QueryBuilder<Ort>(
                repository.entityManager("stamm"), Ort.class);
            ortBuilder.and("id", messprogramm.getOrtId());
            Response ortResponse = repository.filter(ortBuilder.getQuery(), "stamm");
            @SuppressWarnings("unchecked")
            List<Ort> orte = (List<Ort>) ortResponse.getData();
            if (orte != null && !orte.isEmpty()) {
                ort.setOrtId(Long.valueOf(orte.get(0).getId()));
            }
            repository.create(ort, "land");
        }
        // Reolad the probe to have the old id
        probe =
            (LProbe)repository.getById(LProbe.class, probe.getId(), "land").getData();
        return probe;
    }

    /**
     * Determine the interval for probe generation using a start date, end date
     * and the messprogramm.
     *
     * @param   start   Calendar object defining the start of the first interval
     * @param   end     Calendar object defining the end of the last interval.
     * @param   messprogramm    The messprogramm
     *
     * @return An array of start/end pairs.
     */
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

    /**
     * Parse an interval string.
     * Posible values are: J, H, Q, M, W4, W2, W, T
     *
     * @param   interval    the interval string.
     *
     * @return the amount of days for the given interval.
     */
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

    /**
     * Search for the umwelt id using the 'deskriptor'.
     *
     * @param   probe   The probe object.
     *
     * @return The updated probe object.
     */
    public LProbe findUmweltId(LProbe probe) {
        String[] mediaDesk = probe.getMediaDesk().split(" ");
        if (mediaDesk.length <= 1) {
            return probe;
        }
        probe.setUmwId(findUmwelt(mediaDesk));
        return probe;
    }

    /**
     * Search for the media description using the 'deskriptor'.
     *
     * @param   probe   The probe object
     *
     * @return The updated probe object.
     */
    public LProbe findMediaDesk(LProbe probe) {
        Object result = repository.queryFromString(
                "SELECT get_media_from_media_desk( :mediaDesk );", "stamm")
            .setParameter("mediaDesk", probe.getMediaDesk())
            .getSingleResult();
        probe.setMedia(result != null ? result.toString() : "");
        return probe;
    }

    /**
     * Search for the umwelt id using the 'deskriptor'.
     *
     * @param   messprogramm    The messprogramm
     *
     * @return The updated messprogramm.
     */
    public Messprogramm findUmweltId(Messprogramm messprogramm) {
        String[] mediaDesk = messprogramm.getMediaDesk().split(" ");
        if (mediaDesk.length <= 1) {
            return messprogramm;
        }
        messprogramm.setUmwId(findUmwelt(mediaDesk));
        return messprogramm;
    }

    /**
     * Find the umwelt id for a given deskriptor.
     *
     * @param   mediaDesk   The deskriptor string
     *
     * @return The umwelt id or an empty string.
     */
    private String findUmwelt(String[] mediaDesk) {
        List<Integer> mediaIds = new ArrayList<Integer>();
        boolean zebs = false;
        Integer parent = null;
        Integer hdParent = null;
        Integer ndParent = null;
        if ("01".equals(mediaDesk[1])) {
            zebs = true;
        }
        for (int i = 1; i < mediaDesk.length; i++) {
            if ("00".equals(mediaDesk[i])) {
                mediaIds.add(-1);
                continue;
            }
            if (zebs && i < 5) {
                parent = hdParent;
            }
            else if (!zebs && i < 3) {
                parent = hdParent;
            }
            else {
                parent = ndParent;
            }
            QueryBuilder<Deskriptoren> builder = new QueryBuilder<Deskriptoren>(
                repository.entityManager("stamm"), Deskriptoren.class);
            if (parent != null) {
                builder.and("vorgaenger", parent);
            }
            builder.and("sn", mediaDesk[i]);
            builder.and("ebene", i - 1);
            Response response = repository.filter(builder.getQuery(), "stamm");
            @SuppressWarnings("unchecked")
            List<Deskriptoren> data = (List<Deskriptoren>)response.getData();
            if (data.isEmpty()) {
                return "";
            }
            hdParent = data.get(0).getId();
            mediaIds.add(data.get(0).getId());
            if (i == 2) {
                ndParent = data.get(0).getId();
            }
        }
        return getUmwelt(mediaIds, zebs);
    }

    /**
     * Find the umwelt id in the database using media deskriptor ids.
     *
     * @param   media   The list of media ids.
     * @param   isZebs  Flag for type of the deskriptor.
     *
     * @return The umwelt id or an empty string.
     */
    private String getUmwelt(List<Integer> media, boolean isZebs) {
        QueryBuilder<DeskriptorUmwelt> builder =
            new QueryBuilder<DeskriptorUmwelt>(
                repository.entityManager("stamm"), DeskriptorUmwelt.class);

        if (media.size() == 0) {
            return "";
        }

        int size = 1;
        for (int i = size; i >= 0; i--) {
            if (media.get(i) == -1) {
                continue;
            }
            String field = "s" + (i > 9 ? i : "0" + i);
            builder.and(field, media.get(i));
        }
        Response response = repository.filter(builder.getQuery(), "stamm");
        @SuppressWarnings("unchecked")
        List<DeskriptorUmwelt> data = (List<DeskriptorUmwelt>)response.getData();
        if (data.isEmpty()) {
            return null;
        }

        boolean unique = isUnique(data);
        if (unique) {
            return data.get(0).getUmwId();
        }
        else {
            int found = -1;
            for (int i = 0; i < data.size(); i++) {
                int matches = 0;
                int lastMatch = 0;
                for (int j = size + 1; j < 12; j++) {
                    switch(j) {
                        case 2: if (media.get(2).equals(data.get(i).getS02()))
                                    matches += 1;
                                break;
                        case 3: if (media.get(3).equals(data.get(i).getS03()))
                                    matches += 1;
                                break;
                        case 4: if (media.get(4).equals(data.get(i).getS04()))
                                    matches += 1;
                                break;
                        case 5: if (media.get(5).equals(data.get(i).getS05()))
                                    matches +=1;
                                break;
                        case 6: if (media.get(6).equals(data.get(i).getS06()))
                                    matches += 1;
                                break;
                        case 7: if (media.get(7).equals(data.get(i).getS07()))
                                    matches += 1;
                                break;
                        case 8: if (media.get(8).equals(data.get(i).getS08()))
                                    matches += 1;
                                break;
                        case 9: if (media.get(9).equals(data.get(i).getS09()))
                                    matches += 1;
                                break;
                        case 10: if (media.get(10).equals(data.get(i).getS10()))
                                    matches += 1;
                                break;
                        case 11: if (media.get(11).equals(data.get(i).getS11()))
                                    matches += 1;
                                break;
                        case 12: if (media.get(12).equals(data.get(i).getS12()))
                                    matches += 1;
                                break;
                    }
                    if (matches > lastMatch) {
                        lastMatch = matches;
                        found = i;
                    }
                }
                if (found >= 0) {
                    return data.get(found).getUmwId();
                }
            }
            return null;
        }
    }

    /**
     * Determine if the entries in the list have the same umwelt id.
     *
     * @param   list    A list of DescriptorUmwelt objects.
     *
     * @return true if the objects have the same umwelt id else false.
     */
    private boolean isUnique(List<DeskriptorUmwelt> list) {
        if (list.isEmpty()) {
            return false;
        }
        String element = list.get(0).getUmwId();
        for (int i = 1; i < list.size(); i++) {
            if (!element.equals(list.get(i))) {
                return false;
            }
        }
        return true;
    }

}
