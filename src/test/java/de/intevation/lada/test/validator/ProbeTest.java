/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.test.validator;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;

/**
 * Test probe validations.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class ProbeTest {

    private static final long TS1 = 1376287046510L;
    private static final long TS2 = 1376287046511L;
    private static final long TS3 = 2376287046511L;
    private static final int ID710 = 710;
    private static final int ID611 = 611;
    private static final int ID631 = 631;
    private static final int ID661 = 661;
    private static final int ID662 = 662;

    private Validator validator;

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * Test hauptprobennr.
     * @param protocol the test protocol.
     */
    public final void hasHauptprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has hauptprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setHauptprobenNr("4554567890");
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Assert.assertFalse(
                violation.getErrors().containsKey("hauptprobenNr"));
        }
        prot.setPassed(true);
    }

    /**
     * Test no hauptprobennr.
     * @param protocol the test protocol.
     */
    public final void hasNoHauptprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has no hauptprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("hauptprobenNr"));
        Assert.assertTrue(
            violation.getErrors().get("hauptprobenNr").contains(ID631));
        prot.setPassed(true);
    }

    /**
     * Test new existing hpnr.
     * @param protocol the test protocol.
     */
    public final void existingHauptprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("existing hauptprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setHauptprobenNr("120510002");
        prot.addInfo("hauptprobenNr", "120510002");
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("hauptprobenNr"));
        Assert.assertTrue(
            violation.getErrors().get("hauptprobenNr").contains(ID611));
        prot.setPassed(true);
    }

    /**
     * Test new unique hpnr.
     * @param protocol the test protocol.
     */
    public final void uniqueHauptprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("unique hauptprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setHauptprobenNr("4564567890");
        prot.addInfo("hauptprobenNr", "4564567890");
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Assert.assertFalse(
                violation.getErrors().containsKey("hauptprobenNr"));
        }
        prot.setPassed(true);
    }

    /**
     * Test update unique hpnr.
     * @param protocol the test protocol.
     */
    public final void uniqueHauptprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("unique hauptprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setId(1);
        probe.setHauptprobenNr("4564567890");
        prot.addInfo("hauptprobenNr", "4564567890");
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Assert.assertFalse(
                violation.getErrors().containsKey("hauptprobenNr"));
        }
        prot.setPassed(true);
    }

    /**
     * Test update of existing hpnr..
     * @param protocol the test protocol.
     */
    public final void existingHauptprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("existing hauptprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setId(1);
        probe.setHauptprobenNr("120224003");
        prot.addInfo("hauptprobenNr", "120224003");
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("hauptprobenNr"));
        Assert.assertTrue(
            violation.getErrors().get("hauptprobenNr").contains(ID611));
        prot.setPassed(true);
    }

    /**
     * Test entnahmeort.
     * @param protocol the test protocol.
     */
    public final void hasEntnahmeOrt(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has entnahmeOrt");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setId(1);
        Violation violation = validator.validate(probe);
        if (violation.hasWarnings()) {
            Assert.assertFalse(
                violation.getWarnings().containsKey("entnahmeOrt"));
        }
        prot.setPassed(true);
    }

    /**
     * Test no entnahmeort.
     * @param protocol the test protocol.
     */
    public final void hasNoEntnahmeOrt(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has no entnahmeOrt");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setId(ID710);
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("entnahmeOrt"));
        Assert.assertTrue(
            violation.getWarnings().get("entnahmeOrt").contains(ID631));
        prot.setPassed(true);
    }

    /**
     * Test probenahmebegin.
     * @param protocol the test protocol.
     */
    public final void hasProbeentnahmeBegin(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setProbeentnahmeBeginn(new Timestamp(TS1));
        probe.setProbeentnahmeEnde(new Timestamp(TS2));
        Violation violation = validator.validate(probe);
        if (violation.hasWarnings()) {
            Assert.assertFalse(
                violation.getWarnings().containsKey("probeentnahmeBeginn"));
        }
        prot.setPassed(true);
    }

    /**
     * Test no probenahme begin.
     * @param protocol the test protocol.
     */
    public final void hasNoProbeentnahmeBegin(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has no probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(
            violation.getWarnings().containsKey("probeentnahmeBeginn"));
        Assert.assertTrue(
            violation.getWarnings().get("probeentnahmeBeginn").contains(ID631));
        prot.setPassed(true);
    }

    /**
     * Test probenahme begin without end.
     * @param protocol the test protocol.
     */
    public final void timeNoEndProbeentnahmeBegin(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("time no end probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setProbeentnahmeBeginn(new Timestamp(TS1));
        Violation violation = validator.validate(probe);
        if (violation.hasWarnings()) {
            Assert.assertFalse(
                violation.getWarnings().containsKey("probeentnahmeBeginn"));
        }
        prot.setPassed(true);
    }

    /**
     * Test probenahme begin without begin.
     * @param protocol the test protocol.
     */
    public final void timeNoBeginProbeentnahmeBegin(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("time no begin probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setProbeentnahmeEnde(new Timestamp(TS1));
        Violation violation = validator.validate(probe);
        Assert.assertTrue(
            violation.getWarnings().get("probeentnahmeBeginn").contains(ID631));
        Assert.assertTrue(
            violation.getWarnings().get("probeentnahmeBeginn").contains(ID662));
        prot.setPassed(true);
    }

    /**
     * Test probenahme begin after end.
     * @param protocol the test protocol.
     */
    public final void timeBeginAfterEndProbeentnahmeBegin(
        List<Protocol> protocol
    ) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("time begin after end probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setProbeentnahmeBeginn(new Timestamp(TS2));
        probe.setProbeentnahmeEnde(new Timestamp(TS1));
        Violation violation = validator.validate(probe);
        Assert.assertTrue(
            violation.getWarnings().get("probeentnahmeBeginn").contains(ID662));
        prot.setPassed(true);
    }

    /**
     * Test probenahmebegin in future.
     * @param protocol the test protocol.
     */
    public final void timeBeginFutureProbeentnahmeBegin(
        List<Protocol> protocol
    ) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("time begin in future probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setProbeentnahmeBeginn(new Timestamp(TS3));
        Violation violation = validator.validate(probe);
        Assert.assertTrue(
            violation.getWarnings().get("probeentnahmeBeginn").contains(ID661));
        prot.setPassed(true);
    }

    /**
     * Test umwelt.
     * @param protocol the test protocol.
     */
    public final void hasUmwelt(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has Umwelt");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setUmwId("A4");
        Violation violation = validator.validate(probe);
        if (violation.hasWarnings()) {
            Assert.assertFalse(violation.getWarnings().containsKey("umwId"));
        }
        prot.setPassed(true);
    }

    /**
     * Test no umwelt.
     * @param protocol the test protocol.
     */
    public final void hasNoUmwelt(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has no Umwelt");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("umwId"));
        Assert.assertTrue(violation.getWarnings().get("umwId").contains(ID631));
        prot.setPassed(true);
    }

    /**
     * Test empty umwelt.
     * @param protocol the test protocol.
     */
    public final void hasEmptyUmwelt(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has empty Umwelt");
        prot.setPassed(false);
        protocol.add(prot);
        Probe probe = new Probe();
        probe.setUmwId("");
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("umwId"));
        Assert.assertTrue(violation.getWarnings().get("umwId").contains(ID631));
        prot.setPassed(true);
    }
}
