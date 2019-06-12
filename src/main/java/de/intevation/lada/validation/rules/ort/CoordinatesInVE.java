/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.ort;

import java.util.List;

import javax.inject.Inject;

import com.vividsolutions.jts.geom.Point;

import org.apache.log4j.Logger;

import de.intevation.lada.model.stammdaten.Ort;
import de.intevation.lada.model.stammdaten.Verwaltungseinheit;
import de.intevation.lada.model.stammdaten.Verwaltungsgrenze;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for ort.
 * Validates if the coordinates are in the specified "Verwaltungseinheit".
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Ort")
public class CoordinatesInVE implements Rule {

    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @SuppressWarnings("unchecked")
    @Override
    public Violation execute(Object object) {
        Ort ort = (Ort)object;
        String gemId = "".equals(ort.getGemId())
            ? null
            : ort.getGemId();

        if (gemId != null && ort.getGeom() != null) {

            QueryBuilder<Verwaltungsgrenze> vg =
                new QueryBuilder<Verwaltungsgrenze>(
                    repository.entityManager(Strings.STAMM),
                    Verwaltungsgrenze.class);
            vg.and("gemId", gemId);
            List<Verwaltungsgrenze> vgs = repository.filterPlain(
                vg.getQuery(), Strings.STAMM);
            if (vgs == null || vgs.isEmpty()) {
                Violation violation = new Violation();
                violation.addWarning("gemId", 650);
                return violation;
            }

            Point p = ort.getGeom();
            if (p == null) {
                logger.error("geom is null. "
                    + "Probably OrtFactory.transformCoordinates() has not "
                    + "been called on this ort.");
            }
            Boolean unscharf = ort.getUnscharf();
            Violation violation = new Violation();
            for (Verwaltungsgrenze singlevg : vgs) {
                if (singlevg.getShape().contains(p)) {
                    if (unscharf != null && !unscharf) {
                        return null;
                    } else {
                        ort.setUnscharf(false);
                        return null;
                    }
                } else {
                    double dist = singlevg.getShape().distance(p) ;
                    dist = dist * (3.1415926/180) * 6378137;
                    if (dist < 1000) {
                        ort.setUnscharf(true);
                        return null;
                    } else {
                        ort.setUnscharf(false);
                        violation.addWarning("koordXExtern", 651);
                        violation.addWarning("koordYExtern", 651);
                        return violation;
                    }
                }
           }

           violation.addWarning("koordXExtern", 652);
           violation.addWarning("koordYExtern", 652);
           return violation;
        }
        return null;
    }

}
