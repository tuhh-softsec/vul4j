package de.intevation.lada.util.data;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.stammdaten.MassEinheitUmrechnung;

import de.intevation.lada.model.stammdaten.Umwelt;
import de.intevation.lada.util.annotation.RepositoryConfig;

/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */

public class MesswertNormalizer {

    /**
     * Get the list of conversion for the given meh ids.
     * @param mehIdTo MehId to convert to
     * @param mehIdFrom MehId to convert from
     * @param defaultRepo Repository to use
     * @return Conversions as list
     */
    private static List<MassEinheitUmrechnung> getConversions(Integer mehIdTo, Integer mehIdFrom, Repository defaultRepo) {
        QueryBuilder<MassEinheitUmrechnung> builder = new QueryBuilder<>(
            defaultRepo.entityManager(Strings.STAMM),
            MassEinheitUmrechnung.class
        );
        builder.and("mehIdZu", mehIdTo);
        builder.and("mehVon", mehIdFrom);
        return defaultRepo.filterPlain(builder.getQuery(), Strings.STAMM);
    }

    /**
     * Converts the given messwert list into the standard unit of the given UmweltId
     * @param messwerte Messwerte to convert
     * @param umwId UmweltId to get the standard unit from
     */
    public static List<Messwert> normalizeMesswerte(List<Messwert> messwerte, String umwId, Repository defaultRepo) {
        if (umwId == null || umwId.equals("")) {
            return messwerte;
        }
        Umwelt umwelt = defaultRepo.getByIdPlain(Umwelt.class, umwId, Strings.STAMM);
        Integer mehIdToConvertTo = umwelt.getMehId();
        Integer secMehIdToConvertTo = umwelt.getSecMehId();

        for (Messwert messwert: messwerte) {
            if (mehIdToConvertTo != null && mehIdToConvertTo.equals(messwert.getMehId()) ||
                secMehIdToConvertTo != null && secMehIdToConvertTo.equals(messwert.getMehId())) {
                // no conversion needed
                continue;
            }
            //Get the conversion factors
            List<MassEinheitUmrechnung> primaryMeu= getConversions(
                    mehIdToConvertTo, messwert.getMehId(), defaultRepo);
            List<MassEinheitUmrechnung> secondaryMeu = getConversions(
                    secMehIdToConvertTo, messwert.getMehId(), defaultRepo);
            if (primaryMeu.size() == 0 && secondaryMeu.size() == 0) {
                //No suitable conversion found: continue
                continue;
            }
            MassEinheitUmrechnung meu = primaryMeu.size() > 0 ?
                    primaryMeu.get(0): secondaryMeu.get(0);
            Double factor = meu.getFaktor();

            //Update einheit
            messwert.setMehId(primaryMeu.size() > 0 ? mehIdToConvertTo: secMehIdToConvertTo);
            //Update messwert
            if (messwert.getMesswert() != null) {
                messwert.setMesswert(messwert.getMesswert() * factor);
            }
            //update nwgZuMesswert
            if (messwert.getNwgZuMesswert() != null) {
                messwert.setNwgZuMesswert(messwert.getNwgZuMesswert() * factor);
            }
        }
        return messwerte;
    }
}
