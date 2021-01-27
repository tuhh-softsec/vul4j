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
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;

/**
 * Test Status entities.
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class StatusTest {

    private static final int KOMBI632 = 632;
    private static final int ID1 = 1;
    private static final int ID2 = 2;
    private static final int ID7 = 7;
    private Validator validator;

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Test if status kombi is not existing.
     * @param protocol The test protocol.
     */
    public final void checkKombiNegative(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("StatusValidator");
        prot.setType("check status kombi");
        prot.setPassed(false);
        protocol.add(prot);
        StatusProtokoll status = new StatusProtokoll();
        status.setStatusStufe(ID2);
        status.setStatusWert(ID7);
        Violation violation = validator.validate(status);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("kombi"));
        Assert.assertTrue(
            violation.getErrors().get("kombi").contains(KOMBI632));
        prot.setPassed(true);
    }

    /**
     * Test if status kombi is existing.
     * @param protocol The test protocol.
     */
    public final void checkKombiPositive(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("StatusValidator");
        prot.setType("check status kombi");
        prot.setPassed(false);
        protocol.add(prot);
        StatusProtokoll status = new StatusProtokoll();
        status.setStatusStufe(ID1);
        status.setStatusWert(ID1);
        Violation violation = validator.validate(status);
        if (violation.hasErrors()) {
            Assert.assertFalse(violation.getErrors().containsKey("kombi"));
        }
        prot.setPassed(true);
    }
}
