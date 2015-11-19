/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.validator;

import java.util.List;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.model.land.LStatusProtokoll;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;

public class Status {

    private Validator validator;

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public final void checkKombiNegative(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("StatusValidator");
        prot.setType("check status kombi");
        prot.setPassed(false);
        protocol.add(prot);
        LStatusProtokoll status = new LStatusProtokoll();
        status.setStatusStufe(2);
        status.setStatusWert(7);
        Violation violation = validator.validate(status);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("kombi"));
        Assert.assertTrue(violation.getErrors().get("kombi").contains(632));
        prot.setPassed(true);
    }

    public final void checkKombiPositive(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("StatusValidator");
        prot.setType("check status kombi");
        prot.setPassed(false);
        protocol.add(prot);
        LStatusProtokoll status = new LStatusProtokoll();
        status.setStatusStufe(1);
        status.setStatusWert(1);
        Violation violation = validator.validate(status);
        if (violation.hasErrors()) {
            Assert.assertFalse(violation.getErrors().containsKey("kombi"));
        }
        prot.setPassed(true);
    }
}
