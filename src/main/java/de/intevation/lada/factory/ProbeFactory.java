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
import java.util.Hashtable;

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

    private static Hashtable<String, int[]> fieldsTable;

    public ProbeFactory() {
        int[] T = { Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR, 1 };

        int[] M = { Calendar.MONTH, Calendar.DAY_OF_MONTH, 1 };
        int[] Q = { Calendar.MONTH, Calendar.DAY_OF_MONTH, 3 };
        int[] H = { Calendar.MONTH, Calendar.DAY_OF_MONTH, 6 };

        this.fieldsTable = new Hashtable<String, int[]>();

        this.fieldsTable.put("T", T);
        this.fieldsTable.put("M", M);
        this.fieldsTable.put("Q", Q);
        this.fieldsTable.put("H", H);
    }

    private class Intervall {
        private int teilVon;
        private int teilBis;
        private int offset;

        private int intervallField;
        private int subIntField;
        private int intervallFactor;

        private Calendar from;

        public Intervall(
            Messprogramm messprogramm,
            Calendar start
        ) {
            this.teilVon = messprogramm.getTeilintervallVon();
            this.teilBis = messprogramm.getTeilintervallBis();
            this.offset = messprogramm.getIntervallOffset();

            this.intervallField = fieldsTable
                .get(messprogramm.getProbenintervall())[0];
            this.subIntField = fieldsTable
                .get(messprogramm.getProbenintervall())[1];
            this.intervallFactor = fieldsTable
                .get(messprogramm.getProbenintervall())[2];

            /* Align with beginning of next interval
             * like first day of next quarter.*/
            int startIntField = start.get(intervallField);
            this.from = (Calendar)start.clone();
            from.set(
                intervallField,
                startIntField + startIntField % intervallFactor
            );
            from = adjustSubIntField(from, teilVon);
            if (start.after(from)) {
                // to next intervall if start not at first day of intervall
                this.roll();
            }
        }

       /**
        * Return given calendar adjusted to start of intervall (e.g. first
        * day in quarter) plus offset and given amount of days.
        *
        * @param cal Calendar to be adjusted
        * @param int amount of days to be added (plus offset)
        *
        * @return the adjusted Calendar object.
        */
        private Calendar adjustSubIntField(Calendar cal, int teil) {
            int intValue = cal.get(intervallField);
            intValue = intValue - intValue % intervallFactor;
            cal.set(intervallField, intValue);

            int subIntValue = intervallField == subIntField ? intValue : 0
                + offset + Math.min(teil, getDuration());
            cal.set(subIntField, subIntValue);

            return cal;
        }

        /**
         * @return sum of actual maxima for subIntField from beginning of
         * actual intervall for the next intervallFactor values intervallField
         * or just intervallFactor, if subIntField == intervallField.
         */
        private int getDuration() {
            if (subIntField == intervallField) {
                return intervallFactor;
            }
            logger.debug("## calculate maximum ##");
            int duration = 0;
            Calendar tmp = (Calendar)from.clone();
            /* reset to beginning of intervall, e.g. first day of quarter
             * to compensate possible overflow if
             * teilVon > maximum of intervallField: */
            int intValue = from.get(intervallField);
            tmp.set(
                intervallField,
                intValue - intValue % intervallFactor
            );
            tmp.set(subIntField, tmp.getActualMinimum(subIntField));
            logger.debug(tmp);
            for (int i = 0; i < intervallFactor; i++) {
                logger.debug(tmp.getActualMaximum(subIntField));
                duration += tmp.getActualMaximum(subIntField);
                tmp.add(intervallField, 1);
            }
            logger.debug(duration);
            return duration;
        }

        public Date getFrom() {
            logger.debug("getFrom() from: " + from);
            return from.getTime();
        }

        public Date getTo() {
            logger.debug("getTo() from: " + from);
            Calendar to = (Calendar)from.clone();
            to = adjustSubIntField(to, teilBis);
            return to.getTime();
        }

        public boolean startInLeapYear() {
            return from.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
        }

        public int getStartDOY() {
            return from.get(Calendar.DAY_OF_YEAR);
        }

        public void roll() {
            from.add(intervallField, intervallFactor);
            from = adjustSubIntField(from, teilVon);
        }

    }
    // end Intervall class


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
    public List<LProbe> create(Messprogramm messprogramm, Long from, Long to) {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(from);

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(to);
        /* Adjust to end of the day as we want to generate Probe objects
         * before or at this day. */
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        List<LProbe> proben = new ArrayList<LProbe>();

        if (fieldsTable.keySet().contains(messprogramm.getProbenintervall())) {
            proben.addAll(generateMonthly(messprogramm, start, end));
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
     * @param   proben          An (empty) list for probe objects
     *                          filled by this method
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

        // If fixed interval (W, W2, W4)
        if ("W".equals(messprogramm.getProbenintervall())) {
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
        else if ("J".equals(messprogramm.getProbenintervall())) {
            int offset = messprogramm.getIntervallOffset();
            int teilVon = messprogramm.getTeilintervallVon();
            int teilBis = messprogramm.getTeilintervallBis();

            if (teilVon >= startDay + offset
                && teilBis <= endDay + offset
            ) {
                start.add(Calendar.DATE, teilVon - startDay + offset);
                Date startDate = start.getTime();
                end.add(Calendar.DATE, teilBis - endDay + offset);
                Date endDate = end.getTime();
                LProbe probe = createObjects(messprogramm, startDate, endDate);
                if (probe != null) {
                    proben.add(probe);
                }
            }
        }
    }

    private List<LProbe> generateMonthly(
        Messprogramm messprogramm,
        Calendar start,
        Calendar end
    ) {
        logger.debug("start: " + start);

        int gueltigVon = messprogramm.getGueltigVon();
        int gueltigBis = messprogramm.getGueltigBis();

        List<LProbe> proben = new ArrayList<LProbe>();

        for (Intervall intervall = new Intervall(messprogramm, start);
             intervall.getFrom().before(end.getTime());
             intervall.roll()
        ) {
            /* Leap year adaption of validity period.
             * It is assumed here (and should be enforced by the data model)
             * that gueltigVon and gueltigBis are always given relative to
             * a non-leap year. E.g. a value of 59 is assumed to denote
             * March 1 and thus has to be adapted in a leap year. */
            int leapDay = intervall.startInLeapYear() ? 1 : 0;
            int actualGueltigVon =
                gueltigVon > 58
                ? gueltigVon + leapDay
                : gueltigVon;
            int actualGueltigBis =
                gueltigBis > 58
                ? gueltigBis + leapDay
                : gueltigBis;

            int solldatumBeginnDOY = intervall.getStartDOY();

            if ((
                    // Validity within one year
                    actualGueltigVon < actualGueltigBis
                    && solldatumBeginnDOY >= actualGueltigVon
                    && solldatumBeginnDOY <= actualGueltigBis
                ) || (
                    // Validity over turn of the year
                    actualGueltigVon > actualGueltigBis
                    && (solldatumBeginnDOY >= actualGueltigVon
                        || solldatumBeginnDOY <= actualGueltigBis)
                )
            ) {
                LProbe probe = createObjects(
                    messprogramm,
                    intervall.getFrom(),
                    intervall.getTo()
                );
                if (probe != null) {
                    proben.add(probe);
                }
            } else {
                logger.debug(solldatumBeginnDOY + " not within validity "
                + actualGueltigVon + " to " + actualGueltigBis);
            }
        }

        return proben;
    }

    private List<LProbe> generate(
        Messprogramm messprogramm,
        Calendar start,
        Calendar end,
        int days
    ) {
        int offset = messprogramm.getIntervallOffset();
        int startDay = start.get(Calendar.DAY_OF_YEAR) +
            messprogramm.getTeilintervallVon() - 1 + offset;
        int endDay = end.get(Calendar.DAY_OF_YEAR);

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
            LProbe probe = createObjects(
                messprogramm, start.getTime(), end.getTime());
            if (probe != null) {
                proben.add(probe);
            }
        }
        return proben;
    }

    private Calendar getMonday(Calendar week) {
        if (week.get(Calendar.DAY_OF_WEEK) > 1) {
            week.set(Calendar.WEEK_OF_YEAR,
                week.get(Calendar.WEEK_OF_YEAR) + 1);
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
            Response ortResponse = repository.filter(
                ortBuilder.getQuery(), "stamm");
            @SuppressWarnings("unchecked")
            List<Ort> orte = (List<Ort>) ortResponse.getData();
            if (orte != null && !orte.isEmpty()) {
                ort.setOrtId(Long.valueOf(orte.get(0).getId()));
            }
            repository.create(ort, "land");
        }
        // Reolad the probe to have the old id
        probe = (LProbe)repository.getById(
            LProbe.class, probe.getId(), "land").getData();
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
            cStart.set(
                startYear + i,
                start.get(Calendar.MONTH),
                start.get(Calendar.DAY_OF_MONTH)
            );
            if (messprogramm.getGueltigVon() <= 0
                || (realStart > messprogramm.getGueltigVon() && i == 0)
            ) {
                intervals[0][0] = start.getTime();
            }
            else {
                start.add(Calendar.DATE,
                    messprogramm.getGueltigVon() - realStart);
                Date startDate = start.getTime();
                intervals[i][0] = startDate;
            }

            Calendar cEnd = Calendar.getInstance();
            cEnd.set(
                startYear + i,
                end.get(Calendar.MONTH),
                end.get(Calendar.DAY_OF_MONTH)
            );
            if (messprogramm.getGueltigBis() <= 0
                || (realEnd < messprogramm.getGueltigBis() && i == years - 1)
            ) {
                intervals[i][1] = cEnd.getTime();
            }
            else {
                cEnd.add(Calendar.DATE,
                    messprogramm.getGueltigBis() - realEnd);
                Date endDate = cEnd.getTime();
                intervals[i][1] = endDate;
            }
        }

        return intervals;
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
