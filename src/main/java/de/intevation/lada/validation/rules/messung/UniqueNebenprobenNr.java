package de.intevation.lada.validation.rules.messung;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Messung")
public class UniqueNebenprobenNr implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repo;

    @SuppressWarnings("unchecked")
    @Override
    public Violation execute(Object object) {
        LMessung messung= (LMessung)object;
        QueryBuilder<LMessung> builder = new QueryBuilder<LMessung>(
            repo.entityManager("land"),
            LMessung.class);
        builder.and("nebenprobenNr", messung.getNebenprobenNr());
        builder.and("probeId", messung.getProbeId());
        Response response = repo.filter(builder.getQuery(), "land");
        if (!((List<LMessung>)response.getData()).isEmpty()) {
            LMessung found = ((List<LMessung>)response.getData()).get(0);
            // The messung found in the db equals the new messung. (Update)
            if (messung.getId() != null &&
                messung.getId().equals(found.getId())) {
                return null;
            }
            Violation violation = new Violation();
            violation.addError("nebenprobenNr", 611);
            return violation;
        }
        return null;
    }

}
