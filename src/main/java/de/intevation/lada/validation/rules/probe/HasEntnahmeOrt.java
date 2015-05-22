package de.intevation.lada.validation.rules.probe;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.LOrt;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Probe")
public class HasEntnahmeOrt implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repo;

    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        Integer id = probe.getId();
        if (id == null) {
            Violation violation = new Violation();
            violation.addWarning("entnahmeOrt", 631);
            return violation;
        }
        QueryBuilder<LOrt> builder =
            new QueryBuilder<LOrt>(repo.entityManager("land"), LOrt.class);
        builder.and("probeId", id);
        Response response = repo.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
        List<LOrt> orte = (List<LOrt>)response.getData();
        for (LOrt ort: orte) {
            if ("E".equals(ort.getOrtsTyp())) {
                return null;
            }
        }
        Violation violation = new Violation();
        violation.addWarning("entnahmeOrt", 631);
        return violation;
    }

}
