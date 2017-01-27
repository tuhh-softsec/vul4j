/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messprogramm;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.model.land.OrtszuordnungMp;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for Messprogramm.
 * Validates if the Messprogramm has all mandatory fields set.
 */
@ValidationRule("Messprogramm")
public class HasAllMandatory implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    Repository repository;

    @Override
    public Violation execute(Object object) {
        Messprogramm messprogramm = (Messprogramm)object;
        Violation violation = new Violation();

        if (messprogramm.getMstId() == null
            | "".equals(messprogramm.getMstId())) {
            violation.addError("mstlabor", 631);
        }
        if (messprogramm.getLaborMstId() == null
            | "".equals(messprogramm.getLaborMstId())) {
            violation.addError("mstlabor", 631);
        }
        if (messprogramm.getDatenbasisId() == null) {
            violation.addError("datenbasisId", 631);
        }
        if (messprogramm.getProbenartId() == null) {
            violation.addError("probenartId", 631);
        }
        if (messprogramm.getProbenintervall() == null
            | "".equals(messprogramm.getProbenintervall())) {
            violation.addError("probenintervall", 631);
        }
        if (messprogramm.getTeilintervallVon() == null) {
            violation.addError("teilintervallVon", 631);
        }
        if (messprogramm.getTeilintervallBis() == null) {
            violation.addError("teilintervallBis", 631);
        }
        if (messprogramm.getGueltigVon() == null) {
            violation.addError("gueltigVon", 631);
        }
        if (messprogramm.getGueltigBis() == null) {
            violation.addError("gueltigBis", 631);
        }
        QueryBuilder<OrtszuordnungMp> builder =
            new QueryBuilder<OrtszuordnungMp>(
                repository.entityManager("land"),
                OrtszuordnungMp.class);
        List<OrtszuordnungMp> orte = repository.filterPlain(
            builder.getQuery(), "land");
        boolean found = false;
        for (OrtszuordnungMp ort : orte) {
            if ("E".equals(ort.getOrtszuordnungTyp())) {
                found = true;
            }
        }
        if (!found) {
            violation.addError("entnahmeOrt", 631);
        }

        return violation.hasErrors()
            ? violation
            :null;
    }
}
