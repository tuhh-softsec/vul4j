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
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.KommentarP;
import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.MessprogrammMmt;
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.land.Ortszuordnung;
import de.intevation.lada.model.land.OrtszuordnungMp;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.stammdaten.DeskriptorUmwelt;
import de.intevation.lada.model.stammdaten.Deskriptoren;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

/**
 * This factory creates probe objects and its children using a messprogramm
 * as template.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ProbeFactory {

    @Inject Logger logger;

    // Number of days in one week
    private static final int N_WEEK_DAYS = 7;

    // Day of year representing February 28
    private static final int FEBRUARY_28 = 58;

    private static Hashtable<String, int[]> fieldsTable;

    public ProbeFactory() {
        int[] T  = { Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR, 1 };
        int[] W  = { Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR,
                     N_WEEK_DAYS };
        int[] W2 = { Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR,
                     N_WEEK_DAYS * 2 };
        int[] W4 = { Calendar.DAY_OF_YEAR, Calendar.DAY_OF_YEAR,
                     N_WEEK_DAYS * 4 };

        int[] M = { Calendar.MONTH, Calendar.DAY_OF_MONTH, 1 };
        int[] Q = { Calendar.MONTH, Calendar.DAY_OF_MONTH, 3 };
        int[] H = { Calendar.MONTH, Calendar.DAY_OF_MONTH, 6 };

        int[] J = { Calendar.YEAR, Calendar.DAY_OF_YEAR, 1 };

        fieldsTable = new Hashtable<String, int[]>();

        fieldsTable.put("T", T);
        fieldsTable.put("W", W);
        fieldsTable.put("W2", W2);
        fieldsTable.put("W4", W4);
        fieldsTable.put("M", M);
        fieldsTable.put("Q", Q);
        fieldsTable.put("H", H);
        fieldsTable.put("J", J);
    }

    private class Intervall {
        /**
         * Start of sub-intervall relative to intervall start in days (1-based)
         */
        private final int teilVon;

        /**
         * End of sub-intervall relative to intervall start in days (1-based)
         */
        private final int teilBis;

        /**
         * Field number in Calendar object representing this intervall's unit
         */
        private final int intervallField;

        /**
         * Field number in Calendar object representing this intervall's
         * sub-intervall unit
         */
        private final int subIntField;

        /**
         * Number of units of intervallField representing this intervall's size
         */
        private final int intervallFactor;

        /**
         * Calendar object representing the this intervall's start
         */
        private Calendar from;

        /**
         * Constructs an Intervall from a given Messprogramm and initial
         * start date, which will be adjusted to the next possible start
         * of an intervall of the type given by the Messprogramm.
         *
         * @param Messprogramm the Messprogramm to use
         * @param Calendar initial start date
         */
        public Intervall(
            Messprogramm messprogramm,
            Calendar start
        ) {
            this.teilVon = messprogramm.getTeilintervallVon();
            this.teilBis = messprogramm.getTeilintervallBis();

            this.intervallField = fieldsTable
                .get(messprogramm.getProbenintervall())[0];
            this.subIntField = fieldsTable
                .get(messprogramm.getProbenintervall())[1];
            this.intervallFactor = fieldsTable
                .get(messprogramm.getProbenintervall())[2];

            this.from = (Calendar)start.clone();

            // Align with beginning of next interval
            if (intervallField == Calendar.DAY_OF_YEAR
                && intervallFactor % N_WEEK_DAYS == 0
                && from.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY
            ) {
                /* Intervalls representing multiples of weeks should start
                 * at Monday following the given start */
                from.add(Calendar.WEEK_OF_YEAR, 1);
                from.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            } else {
                /* Other intervalls should start at the beginning of the
                 * natural intervall at or following the given start (e.g.
                 * first day of next quarter) */
                int startIntField = start.get(intervallField);
                if (startIntField % intervallFactor != 0) {
                    from.add(
                        intervallField,
                        intervallFactor - startIntField % intervallFactor
                    );
                }
            }
        }

       /**
        * Return given calendar adjusted to start of intervall (e.g. first
        * day in quarter) plus given amount of days. If the given amount of
        * days is bigger than the number of days in the intervall, the number
        * of days in the intervall is added instead (i.e. the result is
        * adjusted to the end of the intervall).
        *
        * This is used to compute the actual start and end date
        * of the actual sub-intervall.
        *
        * @param cal Calendar to be adjusted
        * @param teil amount of days to be added
        *
        * @return the adjusted Calendar object.
        */
        private Calendar adjustSubIntField(Calendar cal, int teil) {
            int adjust = 0;
            if (intervallField != subIntField) {
                if (subIntField == Calendar.DAY_OF_YEAR) {
                    // Adjust in leap year
                    teil += startInLeapYear() && teil > FEBRUARY_28
                        ? 1
                        : 0;
                }
            } else {
                /* If intervallField == subIntField, we need to actually
                 * add to the value of intervallField */
                adjust += cal.get(intervallField) - 1;
            }

            /* If the given amount of days is bigger than the number of days
             * in the intervall, the number of days in the intervall is added
             * instead (i.e. the result is adjusted to the end of the
             * intervall).*/
            int subIntValue = adjust + Math.min(teil, getDuration());
            cal.set(subIntField, subIntValue);

            return cal;
        }

        /**
         * @return int Duration in days of the actual intervall
         *
         * Sum of actual maxima for subIntField from beginning of
         * actual intervall for the next intervallFactor values intervallField
         * or just intervallFactor, if subIntField == intervallField.
         */
        private int getDuration() {
            if (subIntField == intervallField) {
                return intervallFactor;
            }
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

            for (int i = 0; i < intervallFactor; i++) {
                duration += tmp.getActualMaximum(subIntField);
                tmp.add(intervallField, 1);
            }
            return duration;
        }

        /**
         * @return Calendar Returns a Date object representing the time value
         * (millisecond offset from the Epoch) of this intervall's
         * sub-intervall start
         */
        public Calendar getFrom() {
            return adjustSubIntField((Calendar)from.clone(), teilVon);
        }

        /**
         * @return Calendar Returns a Date object representing the time value
         * (millisecond offset from the Epoch) of this intervall's
         * sub-intervall end
         */
        public Calendar getTo() {
            return adjustSubIntField((Calendar)from.clone(), teilBis);
        }

        /**
         * @return boolean Does the actual intervall start in a leap year?
         */
        public boolean startInLeapYear() {
            return from.getActualMaximum(Calendar.DAY_OF_YEAR) > 365;
        }

        /**
         * @return int Returns the day number within the year of
         * this intervall's start
         */
        public int getStartDOY() {
            return getFrom().get(Calendar.DAY_OF_YEAR);
        }

        /**
         * Move intervall start to start of following intervall
         */
        public void roll() {
            from.add(intervallField, intervallFactor);
        }

    }
    // end Intervall class


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
    public List<Probe> create(Messprogramm messprogramm, Long from, Long to) {
        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(from);

        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(to);
        /* Adjust to end of the day as we want to generate Probe objects
         * before or at this day. */
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);

        int gueltigVon = messprogramm.getGueltigVon();
        int gueltigBis = messprogramm.getGueltigBis();
        int offset = messprogramm.getIntervallOffset();

        List<Probe> proben = new ArrayList<Probe>();

        for (Intervall intervall = new Intervall(messprogramm, start);
             intervall.getFrom().before(end);
             intervall.roll()
        ) {
            /* Leap year adaption of validity period.
             * It is assumed here (and should be enforced by the data model)
             * that gueltigVon and gueltigBis are always given relative to
             * a non-leap year. E.g. a value of 59 is assumed to denote
             * March 1 and thus has to be adapted in a leap year. */
            int leapDay = intervall.startInLeapYear() ? 1 : 0;
            int actualGueltigVon =
                gueltigVon  - 1 > FEBRUARY_28
                ? gueltigVon + leapDay
                : gueltigVon;
            int actualGueltigBis =
                gueltigBis - 1 > FEBRUARY_28
                ? gueltigBis + leapDay
                : gueltigBis;

            int solldatumBeginnDOY = intervall.getStartDOY() + offset;
            Calendar sollFrom = intervall.getFrom();
            sollFrom.add(Calendar.DATE, offset);
            Calendar sollTo = intervall.getTo();
            sollTo.add(Calendar.DATE, offset);

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
                Probe probe = createObjects(
                    messprogramm,
                    sollFrom.getTime(),
                    sollTo.getTime()
                );
                if (probe != null) {
                    proben.add(probe);
                }
            }
        }

        return proben;
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
    private Probe createObjects(
        Messprogramm messprogramm,
        Date startDate,
        Date endDate
    ) {
        QueryBuilder<Probe> builderProbe =
            new QueryBuilder<Probe>(
                repository.entityManager(Strings.LAND),
                Probe.class);
        builderProbe.and("mprId", messprogramm.getId());
        builderProbe.and("solldatumBeginn", startDate);
        builderProbe.and("solldatumEnde", endDate);

        List<Probe> proben =
            repository.filterPlain(builderProbe.getQuery(), Strings.LAND);

        if (!proben.isEmpty()) {
            return null;
        }
        Probe probe = new Probe();
        probe.setBaId(messprogramm.getBaId());
        probe.setDatenbasisId(messprogramm.getDatenbasisId());
        probe.setMediaDesk(messprogramm.getMediaDesk());
        probe = findMediaDesk(probe);
        probe.setMstId(messprogramm.getMstId());
        probe.setLaborMstId(messprogramm.getLaborMstId());
        probe.setProbenartId(messprogramm.getProbenartId());
        probe.setProbeNehmerId(messprogramm.getProbeNehmerId());
        probe.setSolldatumBeginn(new Timestamp(startDate.getTime()));
        probe.setSolldatumEnde(new Timestamp(endDate.getTime()));
        probe.setTest(messprogramm.getTest());
        probe.setUmwId(messprogramm.getUmwId());
        probe.setMprId(messprogramm.getId());
        probe.setMplId(messprogramm.getMplId());
        probe.setReiProgpunktGrpId(messprogramm.getReiProgpunktGrpId());
        probe.setKtaGruppeId(messprogramm.getKtaGruppeId());
        repository.create(probe, Strings.LAND);

        if (messprogramm.getProbeKommentar() != null &&
            !messprogramm.getProbeKommentar().equals("")) {
            KommentarP kommentar = new KommentarP();
            kommentar.setDatum(new Timestamp(new Date().getTime()));
            kommentar.setProbeId(probe.getId());
            kommentar.setText(messprogramm.getProbeKommentar());
            kommentar.setMstId(messprogramm.getMstId());

            repository.create(kommentar, Strings.LAND);
        }

        QueryBuilder<MessprogrammMmt> builder =
            new QueryBuilder<MessprogrammMmt>(
                    repository.entityManager(Strings.LAND),
                    MessprogrammMmt.class);
        builder.and("messprogrammId", messprogramm.getId());
        Response response = repository.filter(builder.getQuery(), Strings.LAND);
        @SuppressWarnings("unchecked")
        List<MessprogrammMmt> mmts = (List<MessprogrammMmt>)response.getData();
        for (int i = 0; i < mmts.size(); i++) {
            MessprogrammMmt mmt = mmts.get(i);
            Messung messung = new Messung();
            messung.setFertig(false);
            messung.setGeplant(true);
            messung.setMmtId(mmt.getMmtId());
            messung.setProbeId(probe.getId());
            repository.create(messung, Strings.LAND);

            for (int mw : mmt.getMessgroessen()) {
                Messwert wert = new Messwert();
                wert.setMessgroesseId(mw);
                wert.setMessungsId(messung.getId());
                wert.setMesswert(0d);
                if (messprogramm.getMehId() != null) {
                    wert.setMehId(messprogramm.getMehId());
                } else {
                    wert.setMehId(0);
                }
                repository.create(wert, Strings.LAND);
            }
        }
        QueryBuilder<OrtszuordnungMp> builderOrt =
            new QueryBuilder<OrtszuordnungMp>(
                repository.entityManager(Strings.LAND),
                OrtszuordnungMp.class);
        builderOrt.and("messprogrammId", messprogramm.getId());
        List<OrtszuordnungMp> orte =
            repository.filterPlain(builderOrt.getQuery(), Strings.LAND);
        for (OrtszuordnungMp ort : orte) {
            Ortszuordnung ortP = new Ortszuordnung();
            ortP.setOrtszuordnungTyp(ort.getOrtszuordnungTyp());
            ortP.setProbeId(probe.getId());
            ortP.setOrtId(ort.getOrtId());
            ortP.setOrtszusatztext(ort.getOrtszusatztext());
            repository.create(ortP, Strings.LAND);
        }
        // Reolad the probe to have the old id
        probe = (Probe)repository.getById(
            Probe.class, probe.getId(), Strings.LAND).getData();
        return probe;
    }

    /**
     * Search for the umwelt id using the 'deskriptor'.
     *
     * @param   probe   The probe object.
     *
     * @return The updated probe object.
     */
    public Probe findUmweltId(Probe probe) {
        String mediaDesk = probe.getMediaDesk();
        if (mediaDesk != null) {
            String[] mediaDeskParts = mediaDesk.split(" ");
            if (mediaDeskParts.length <= 1) {
                return probe;
            }
            probe.setUmwId(findUmwelt(mediaDeskParts));
        }
        return probe;
    }

    /**
     * Search for the media description using the 'deskriptor'.
     *
     * @param   probe   The probe object
     *
     * @return The updated probe object.
     */
    public Probe findMediaDesk(Probe probe) {
        String mediaDesk = probe.getMediaDesk();
        if (mediaDesk != null) {
            Object result = repository.queryFromString(
                "SELECT get_media_from_media_desk( :mediaDesk );", Strings.STAMM)
                .setParameter("mediaDesk", mediaDesk)
                .getSingleResult();
            probe.setMedia(result != null ? result.toString() : "");
        }
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
        String mediaDesk = messprogramm.getMediaDesk();
        if (mediaDesk != null) {
            String[] mediaDeskParts = mediaDesk.split(" ");
            if (mediaDeskParts.length <= 1) {
                return messprogramm;
            }
            messprogramm.setUmwId(findUmwelt(mediaDeskParts));
        }
        return messprogramm;
    }


    /**
     * Find the umwelt id for a given deskriptor.
     *
     * @param   mediaDesk   The deskriptor string
     *
     * @return The umwelt id or an empty string.
     */
    public String findUmwelt(String[] mediaDesk) {
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
                repository.entityManager(Strings.STAMM), Deskriptoren.class);
            if (parent != null) {
                builder.and("vorgaenger", parent);
            }
            builder.and("sn", mediaDesk[i]);
            builder.and("ebene", i - 1);
            Response response = repository.filter(builder.getQuery(), Strings.STAMM);
            @SuppressWarnings("unchecked")
            List<Deskriptoren> data = (List<Deskriptoren>)response.getData();
            if (data.isEmpty()) {
                return null;
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
                repository.entityManager(Strings.STAMM), DeskriptorUmwelt.class);

        if (media.size() == 0) {
            return null;
        }

        int size = 1;
        for (int i = 0; i < media.size(); i++) {
            String field = "s" + (i > 9 ? i : "0" + i);
            QueryBuilder<DeskriptorUmwelt> tmp = builder.getEmptyBuilder();
            if (media.get(i) != -1) {
                tmp.and(field, media.get(i));
                tmp.or(field, null);
                builder.and(tmp);
            }
            else {
                builder.and(field, null);
            }
        }
        Response response = repository.filter(builder.getQuery(), Strings.STAMM);
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
            int lastMatch = -12;
            for (int i = 0; i < data.size(); i++) {
                int matches = -12;
                for (int j = size; j < 12; j++) {
                    switch(j) {
                        case 1: if (media.get(1).equals(data.get(i).getS01()) ||
                                    media.get(1).equals(-1) && data.get(i).getS01() == null
                                )
                                    matches += 1;
                                break;
                        case 2: if (media.get(2).equals(data.get(i).getS02()) ||
                                    media.get(2).equals(-1) && data.get(i).getS02() == null
                                )
                                    matches += 1;
                                break;
                        case 3: if (media.get(3).equals(data.get(i).getS03()) ||
                                    media.get(3).equals(-1) && data.get(i).getS03() == null
                                )
                                    matches += 1;
                                break;
                        case 4: if (media.get(4).equals(data.get(i).getS04()) ||
                                    media.get(4).equals(-1) && data.get(i).getS04() == null
                                )
                                    matches += 1;
                                break;
                        case 5: if (media.get(5).equals(data.get(i).getS05()) ||
                                    media.get(5).equals(-1) && data.get(i).getS05() == null
                                )
                                    matches +=1;
                                break;
                        case 6: if (media.get(6).equals(data.get(i).getS06()) ||
                                    media.get(6).equals(-1) && data.get(i).getS06() == null
                                )
                                    matches += 1;
                                break;
                        case 7: if (media.get(7).equals(data.get(i).getS07()) ||
                                    media.get(7).equals(-1) && data.get(i).getS07() == null
                                )
                                    matches += 1;
                                break;
                        case 8: if (media.get(8).equals(data.get(i).getS08()) ||
                                    media.get(8).equals(-1) && data.get(i).getS08() == null
                                )
                                    matches += 1;
                                break;
                        case 9: if (media.get(9).equals(data.get(i).getS09()) ||
                                    media.get(9).equals(-1) && data.get(i).getS09() == null
                                )
                                    matches += 1;
                                break;
                        case 10: if (media.get(10).equals(data.get(i).getS10()) ||
                                    media.get(10).equals(-1) && data.get(i).getS10() == null
                                )
                                    matches += 1;
                                break;
                        case 11: if (media.get(11).equals(data.get(i).getS11()) ||
                                    media.get(11).equals(-1) && data.get(i).getS11() == null
                                )
                                    matches += 1;
                                break;
                    }
                    if (matches > lastMatch) {
                        lastMatch = matches;
                        found = i;
                    }
                }
            }
            if (found >= 0) {
                return data.get(found).getUmwId();
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
            if (!element.equals(list.get(i).getUmwId())) {
                return false;
            }
        }
        return true;
    }

}
