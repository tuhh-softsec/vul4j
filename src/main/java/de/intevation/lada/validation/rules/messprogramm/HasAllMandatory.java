/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messprogramm;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for Messprogramm.
 * Validates if the Messprogramm has Datenbasis.
 */
@ValidationRule("Messprogramm")
public class HasAllMandatory implements Rule {

    @Override
    public Violation execute(Object object) {
        Messprogramm messprogramm = (Messprogramm)object;
        Violation violation = new Violation();

        if (messprogramm.getNetzbetreiberId() == null
            | "".equals(messprogramm.getNetzbetreiberId())) {
            violation.addError("netzbetreiberId", 631);
        }
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
        if (messprogramm.getOrtId() == null) {
            violation.addError("ortId", 631);
        }

        return violation.hasErrors()
            ? violation
            :null;
    }
}
