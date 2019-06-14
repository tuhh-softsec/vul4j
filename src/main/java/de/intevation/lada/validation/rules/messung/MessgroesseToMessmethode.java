/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messung;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Messwert;
import de.intevation.lada.model.stammdaten.Messgroesse;
import de.intevation.lada.model.stammdaten.MmtMessgroesse;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for messungen.
 * Validates if the "messgroesse" fits the "messmethode".
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Messung")
public class MessgroesseToMessmethode implements Rule {

    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Messung messung = (Messung)object;
        String mmt = messung.getMmtId();
        QueryBuilder<Messwert> builder =
            new QueryBuilder<Messwert>(
                repository.entityManager(Strings.LAND), Messwert.class);
        builder.and("messungsId", messung.getId());
        Response response = repository.filter(builder.getQuery(), Strings.LAND);
        @SuppressWarnings("unchecked")
        List<Messwert> messwerte = (List<Messwert>)response.getData();

        QueryBuilder<MmtMessgroesse> mmtBuilder =
            new QueryBuilder<MmtMessgroesse>(
                    repository.entityManager(Strings.STAMM), MmtMessgroesse.class);

        Response results = repository.filter(mmtBuilder.getQuery(), Strings.STAMM);
        @SuppressWarnings("unchecked")
        List<MmtMessgroesse> messgroessen = (List<MmtMessgroesse>)results.getData();
        List<MmtMessgroesse> found = new ArrayList<MmtMessgroesse>();
        for (MmtMessgroesse mg: messgroessen) {
            if (mg.getMmtId().equals(mmt)) {
                found.add(mg);
            }
        }
        Violation violation = new Violation();
        for(Messwert messwert: messwerte) {
            boolean hit = false;
            for (MmtMessgroesse messgroesse: found) {
                if (messwert.getMessgroesseId().equals(
                        messgroesse.getMessgroesseId())) {
                    hit = true;
                }
            }
            if (!hit) {
                Messgroesse mg = repository.getByIdPlain(
                    Messgroesse.class,
                    messwert.getMessgroesseId(),
                    Strings.STAMM);
                violation.addError("messgroesse#" + mmt + " " + mg.getMessgroesse(), 632);
            }
        }
        if (violation.hasErrors()) {
            return violation;
        }
        return null;
    }
}
