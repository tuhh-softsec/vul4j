package de.intevation.lada.test.validator;

import java.util.List;

import org.junit.Assert;

import de.intevation.lada.Protocol;
import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.validation.Validator;
import de.intevation.lada.validation.Violation;

public class Messung {

    private Validator validator;

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public final void hasNebenprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has nebenprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setNebenprobenNr("10R1");
        messung.setProbeId(4);
        Violation violation = validator.validate(messung);
        if (violation.hasWarnings()) {
            Assert.assertFalse(violation.getWarnings().containsKey("nebenprobenNr"));
        }
        prot.setPassed(true);
    }

    public final void hasNoNebenprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has no nebenprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setProbeId(4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("nebenprobenNr"));
        Assert.assertTrue(violation.getWarnings().get("nebenprobenNr").contains(631));
        prot.setPassed(true);
    }

    public final void hasEmptyNebenprobenNr(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has empty nebenprobenNr");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setNebenprobenNr("");
        messung.setProbeId(4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("nebenprobenNr"));
        Assert.assertTrue(violation.getWarnings().get("nebenprobenNr").contains(631));
        prot.setPassed(true);
    }

    public final void existingNebenprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("existing nebenprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setNebenprobenNr("00G1");
        messung.setProbeId(4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("nebenprobenNr"));
        Assert.assertTrue(violation.getErrors().get("nebenprobenNr").contains(611));
        prot.setPassed(true);
    }

    public final void uniqueNebenprobenNrNew(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("unique nebenprobenNr (new)");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setNebenprobenNr("00G2");
        messung.setProbeId(4);
        Violation violation = validator.validate(messung);
        if (violation.hasErrors()) {
            Assert.assertFalse(violation.getErrors().containsKey("nebenprobenNr"));
        }
        prot.setPassed(true);
    }

    public final void uniqueNebenprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("unique nebenprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setId(45);
        messung.setProbeId(4);
        messung.setNebenprobenNr("00G2");
        Violation violation = validator.validate(messung);
        if (violation.hasErrors()) {
            Assert.assertFalse(violation.getErrors().containsKey("hauptprobenNr"));
            return;
        }
        prot.setPassed(true);
    }

    public final void existingHauptprobenNrUpdate(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("existing nebenprobenNr (update)");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setId(776);
        messung.setProbeId(1);
        messung.setNebenprobenNr("0003");
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasErrors());
        Assert.assertTrue(violation.getErrors().containsKey("nebenprobenNr"));
        Assert.assertTrue(violation.getErrors().get("nebenprobenNr").contains(611));
        prot.setPassed(true);
    }

    public final void hasMesswert(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has messwert");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setId(1);
        messung.setProbeId(4);
        Violation violation = validator.validate(messung);
        if (violation.hasWarnings()) {
            Assert.assertFalse(violation.getWarnings().containsKey("messwert"));
        }
        prot.setPassed(true);
    }

    public final void hasNoMesswert(List<Protocol> protocol) {
        Protocol prot = new Protocol();
        prot.setName("MessungValidator");
        prot.setType("has no messwert");
        prot.setPassed(false);
        protocol.add(prot);
        LMessung messung = new LMessung();
        messung.setId(990);
        messung.setProbeId(4);
        Violation violation = validator.validate(messung);
        Assert.assertTrue(violation.hasWarnings());
        Assert.assertTrue(violation.getWarnings().containsKey("messwert"));
        Assert.assertTrue(violation.getWarnings().get("messwert").contains(631));
        prot.setPassed(true);
    }
}
