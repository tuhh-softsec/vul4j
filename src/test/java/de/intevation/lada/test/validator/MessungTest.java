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
import de.intevation.lada.model.land.Messung;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;

/**
 * Test messung entities.
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class MessungTest {

    private static final int ID990 = 990;
    private static final int ID776 = 776;
    private static final int ID45 = 45;
    private static final int ID611 = 611;
    private static final int ID631 = 631;
    private static final int ID4 = 4;
    private Validator validator;

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Test nebenproben nr.
     * @param protocol the test protocol.
     */
    public final void hasNebenprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has nebenprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setNebenprobenNr("10R1");
        messung.setProbeId(ID4);
        Violation violation = validator.validate(messung);
        if (violation.hasWarnings()) {
            Assert.assertFalse(
                violation.getWarnings().containsKey("nebenprobenNr"));
        }
        prot.setPassed(true);
    }

    /**
     * Test without nebenproben nr.
     * @param protocol the test protocol.
     */
    public final void hasNoNebenprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has no nebenprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setProbeId(ID4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("nebenprobenNr"));
        Assert.assertTrue(
            violation.getWarnings().get("nebenprobenNr").contains(ID631));
        prot.setPassed(true);
    }

    /**
     * Test empty nebenproben nr.
     * @param protocol the test protocol.
     */
    public final void hasEmptyNebenprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has empty nebenprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setNebenprobenNr("");
        messung.setProbeId(ID4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("nebenprobenNr"));
        Assert.assertTrue(
            violation.getWarnings().get("nebenprobenNr").contains(ID631));
        prot.setPassed(true);
    }

    /**
     * Test new existing nebenproben nr.
     * @param protocol the test protocol.
     */
    public final void existingNebenprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("existing nebenprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setNebenprobenNr("00G1");
        messung.setProbeId(ID4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("nebenprobenNr"));
        Assert.assertTrue(
            violation.getErrors().get("nebenprobenNr").contains(ID611));
        prot.setPassed(true);
    }

    /**
     * Test new unique nebenproben nr.
     * @param protocol the test protocol.
     */
    public final void uniqueNebenprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("unique nebenprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setNebenprobenNr("00G2");
        messung.setProbeId(ID4);
        Violation violation = validator.validate(messung);
        if (violation.hasErrors()) {
            Assert.assertFalse(
                violation.getErrors().containsKey("nebenprobenNr"));
        }
        prot.setPassed(true);
    }

    /**
     * Test update unique nebenproben nr.
     * @param protocol the test protocol.
     */
    public final void uniqueNebenprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("unique nebenprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setId(ID45);
        messung.setProbeId(ID4);
        messung.setNebenprobenNr("00G2");
        Violation violation = validator.validate(messung);
        if (violation.hasErrors()) {
            Assert.assertFalse(
                violation.getErrors().containsKey("hauptprobenNr"));
            return;
        }
        prot.setPassed(true);
    }

    /**
     * Test update existing nebenproben nr.
     * @param protocol the test protocol.
     */
    public final void existingNebenprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("existing nebenprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setId(ID776);
        messung.setProbeId(1);
        messung.setNebenprobenNr("0003");
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("nebenprobenNr"));
        Assert.assertTrue(
            violation.getErrors().get("nebenprobenNr").contains(ID611));
        prot.setPassed(true);
    }

    /**
     * Test messwert.
     * @param protocol the test protocol.
     */
    public final void hasMesswert(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has messwert");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setId(1);
        messung.setProbeId(ID4);
        Violation violation = validator.validate(messung);
        if (violation.hasWarnings()) {
            Assert.assertFalse(violation.getWarnings().containsKey("messwert"));
        }
        prot.setPassed(true);
    }

    /**
     * Test no messwert.
     * @param protocol the test protocol.
     */
    public final void hasNoMesswert(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has no messwert");
        prot.setPassed(false);
        protocol.add(prot);
        Messung messung = new Messung();
        messung.setId(ID990);
        messung.setProbeId(ID4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("messwert"));
        Assert.assertTrue(
            violation.getWarnings().get("messwert").contains(ID631));
        prot.setPassed(true);
    }
}
