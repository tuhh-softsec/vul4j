package de.intevation.lada.util.data;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.stammdaten.MassEinheitUmrechnung;
import de.intevation.lada.model.stammdaten.MessEinheit;
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

        for (Messwert messwert: messwerte) {
            MessEinheit einheit = defaultRepo.getByIdPlain(MessEinheit.class, messwert.getMehId(), Strings.STAMM);

            //Get the conversion factor
            QueryBuilder<MassEinheitUmrechnung> builder = new QueryBuilder<>(
                defaultRepo.entityManager(Strings.STAMM),
                MassEinheitUmrechnung.class
            );
            builder.and("mehIdZu", mehIdToConvertTo);
            builder.and("mehVon", einheit);
            List<MassEinheitUmrechnung> meu = defaultRepo.filterPlain(builder.getQuery(), Strings.STAMM);
            if (meu.size() == 0) {
                //No suitable conversion found: continue
                continue;
            }
            Float factor = meu.get(0).getFaktor();

            //Update messwert
            messwert.setMehId(mehIdToConvertTo);
            messwert.setMesswert(messwert.getMesswert() * factor);
        }
        return messwerte;
    }
}