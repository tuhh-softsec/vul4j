package de.intevation.lada.validation.rules.messung;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Messung")
public class Date implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        LMessung messung = (LMessung)object;
        Integer probeId = messung.getProbeId();
        Response response = repository.getById(LProbe.class, probeId, "land");
        LProbe probe = (LProbe) response.getData();
        if (probe == null) {
            Map<String, Integer> errors = new HashMap<String, Integer>();
            errors.put("lprobe", 604);
        }
        if (probe.getProbeentnahmeEnde() == null ||
            probe.getProbeentnahmeEnde().after(messung.getMesszeitpunkt())) {
            Violation violation = new Violation();
            violation.addWarning("messzeitpunkt", 632);
            return violation;
        }
        return null;
    }
}
