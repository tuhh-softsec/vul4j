package de.intevation.lada.validation.rules.probe;

import java.util.List;

import javax.inject.Inject;

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
public class UniqueHauptprobenNr implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repo;

    @SuppressWarnings("unchecked")
    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        QueryBuilder<LProbe> builder = new QueryBuilder<LProbe>(
            repo.entityManager("land"),
            LProbe.class);
        builder.and("hauptprobenNr", probe.getHauptprobenNr());
        Response response = repo.filter(builder.getQuery(), "land");
        if (!((List<LProbe>)response.getData()).isEmpty()) {
            LProbe found = ((List<LProbe>)response.getData()).get(0);
            // The probe found in the db equals the new probe. (Update)
            if (probe.getId() != null && probe.getId() == found.getId()) {
                return null;
            }
            Violation violation = new Violation();
            violation.addError("hauptprobenNr", 611);
            return violation;
        }
        return null;
    }

}
