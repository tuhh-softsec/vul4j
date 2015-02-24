package de.intevation.lada.test.validator;

import java.sql.Timestamp;
import java.util.List;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;

public class Probe {

    private Validator validator;

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public final void hasHauptprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has hauptprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setHauptprobenNr("1234567890");
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Assert.assertFalse(violation.getErrors().containsKey("hauptprobenNr"));
            prot.setPassed(true);
            return;
        }
        prot.setPassed(true);
    }

    public final void hasNoHauptprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has no hauptprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("hauptprobenNr"));
        Assert.assertTrue(violation.getErrors().get("hauptprobenNr").contains(631));
        prot.setPassed(true);
    }

    public final void existingHauptprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("existing hauptprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setHauptprobenNr("120510002");
        prot.addInfo("hauptprobenNr", "120510002");
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("hauptprobenNr"));
        Assert.assertTrue(violation.getErrors().get("hauptprobenNr").contains(611));
        prot.setPassed(true);
    }

    public final void uniqueHauptprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("unique hauptprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setHauptprobenNr("1234567890");
        prot.addInfo("hauptprobenNr", "1234567890");
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Assert.assertFalse(violation.getErrors().containsKey("hauptprobenNr"));
            return;
        }
        prot.setPassed(true);
    }

    public final void uniqueHauptprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("unique hauptprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setId(1);
        probe.setHauptprobenNr("1234567890");
        prot.addInfo("hauptprobenNr", "1234567890");
        Violation violation = validator.validate(probe);
        if (violation.hasErrors()) {
            Assert.assertFalse(violation.getErrors().containsKey("hauptprobenNr"));
            return;
        }
        prot.setPassed(true);
    }

    public final void existingHauptprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("existing hauptprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setId(1);
        probe.setHauptprobenNr("120224003");
        prot.addInfo("hauptprobenNr", "120224003");
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("hauptprobenNr"));
        Assert.assertTrue(violation.getErrors().get("hauptprobenNr").contains(611));
        prot.setPassed(true);
    }

    public final void hasEntnahmeOrt(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has entnahmeOrt");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setId(1);
        Violation violation = validator.validate(probe);
        if (violation.hasWarnings()) {
            Assert.assertFalse(violation.getWarnings().containsKey("entnahmeOrt"));
        }
        prot.setPassed(true);
    }

    public final void hasNoEntnahmeOrt(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has no entnahmeOrt");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setId(710);
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("entnahmeOrt"));
        Assert.assertTrue(violation.getWarnings().get("entnahmeOrt").contains(631));
        prot.setPassed(true);
    }

    public final void hasProbeentnahmeBegin(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        probe.setProbeentnahmeBeginn(new Timestamp(1376287046510l));
        probe.setProbeentnahmeEnde(new Timestamp(1376287046511l));
        Violation violation = validator.validate(probe);
        if (violation.hasWarnings()) {
            Assert.assertFalse(violation.getWarnings().containsKey("probeentnahmeBegin"));
        }
        prot.setPassed(true);
    }

    public final void hasNoProbeentnahmeBegin(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("ProbeValidator");
        prot.setType("has no probeentnahmeBegin");
        prot.setPassed(false);
        protocol.add(prot);
        LProbe probe = new LProbe();
        Violation violation = validator.validate(probe);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("probeentnahmeBegin"));
        Assert.assertTrue(violation.getWarnings().get("probeentnahmeBegin").contains(631));
        prot.setPassed(true);
    }
        prot.setPassed(true);
    }
}
