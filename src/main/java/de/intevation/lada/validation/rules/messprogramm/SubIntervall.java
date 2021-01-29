/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.messprogramm;

import java.util.Hashtable;
import java.util.Set;

import de.intevation.lada.model.land.Messprogramm;
import de.intevation.lada.util.data.StatusCodes;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for Messprogramm.
 * Validates if the subintervall period is meaningful.
 */
@ValidationRule("Messprogramm")
public class SubIntervall implements Rule {

    private Hashtable<String, Integer> intervallMax;

    public SubIntervall() {
        this.intervallMax = new Hashtable<String, Integer>();

        this.intervallMax.put("J",  365);
        this.intervallMax.put("H",  184);
        this.intervallMax.put("Q",  92);
        this.intervallMax.put("M",  31);
        this.intervallMax.put("W4", 28);
        this.intervallMax.put("W2", 14);
        this.intervallMax.put("W",  7);
        this.intervallMax.put("T",  1);
    }

    @Override
    public Violation execute(Object object) {
        Messprogramm messprogramm = (Messprogramm) object;
        Violation violation = new Violation();

        String probenintervall = messprogramm.getProbenintervall();
        Integer teilVon = messprogramm.getTeilintervallVon();
        Integer teilBis = messprogramm.getTeilintervallBis();
        Integer offset = messprogramm.getIntervallOffset();
        Integer gueltigVon = messprogramm.getGueltigVon();
        Integer gueltigBis = messprogramm.getGueltigBis();

        // skip this validation if relevant mandatory fields not given
        if (probenintervall != null
            && teilVon != null
            && teilBis != null
        ) {
            if ("J".equals(probenintervall)) {
                if (gueltigVon != null && gueltigBis != null) {
                    if (teilVon < gueltigVon || teilVon > gueltigBis) {
                        violation.addError(
                            "teilintervallVon",
                            StatusCodes.VALUE_OUTSIDE_RANGE);
                    }
                    if (teilBis < gueltigVon || teilBis > gueltigBis) {
                        violation.addError(
                            "teilintervallBis",
                            StatusCodes.VALUE_OUTSIDE_RANGE);
                    }
                    if (offset != null
                        && offset > intervallMax.get("J") - 1) {
                        violation.addError(
                            "intervallOffset", StatusCodes.VALUE_OUTSIDE_RANGE);
                    }
                }
            } else {
                // lower limits are independent of intervall type
                if (teilVon < 1) {
                    violation.addError(
                        "teilintervallVon", StatusCodes.VALUE_OUTSIDE_RANGE);
                }
                if (teilBis < 1) {
                    violation.addError(
                        "teilintervallBis", StatusCodes.VALUE_OUTSIDE_RANGE);
                }
                if (offset != null && offset < 0) {
                    violation.addError(
                        "intervallOffset", StatusCodes.VALUE_OUTSIDE_RANGE);
                }

                // upper limits depend on (valid) intervall type
                Set<String> probenintervallSet = intervallMax.keySet();
                if (!probenintervallSet.contains(probenintervall)) {
                    violation.addError(
                        "probenintervall", StatusCodes.VALUE_OUTSIDE_RANGE);
                } else {
                    for (String intervallKey : probenintervallSet) {
                        if (intervallKey.equals(probenintervall)) {
                            if (teilVon > intervallMax.get(intervallKey)) {
                                violation.addError(
                                    "teilintervallVon",
                                    StatusCodes.VALUE_OUTSIDE_RANGE);
                            }
                            if (teilBis > intervallMax.get(intervallKey)) {
                                violation.addError(
                                    "teilintervallBis",
                                    StatusCodes.VALUE_OUTSIDE_RANGE);
                            }
                            if (offset != null
                                && offset
                                > intervallMax.get(intervallKey) - 1) {
                                violation.addError(
                                    "intervallOffset",
                                    StatusCodes.VALUE_OUTSIDE_RANGE);
                            }
                        }
                    }
                }
            }

            // lower limit has to be less than or equal to upper limit
            if (teilVon > teilBis) {
                violation.addError(
                    "teilintervallVon", StatusCodes.DATE_BEGIN_AFTER_END);
                violation.addError(
                    "teilintervallBis", StatusCodes.DATE_BEGIN_AFTER_END);
            }
        }

        return violation.hasErrors()
            ? violation
            : null;
    }
}
