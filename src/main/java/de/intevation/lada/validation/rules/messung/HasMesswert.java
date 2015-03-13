package de.intevation.lada.validation.rules.messung;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Messung")
public class HasMesswert implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repo;

    @Override
    public Violation execute(Object object) {
        LMessung messung = (LMessung)object;
        QueryBuilder<LMesswert> builder =
            new QueryBuilder<LMesswert>(
                repo.entityManager("land"), LMesswert.class);
        builder.and("messungsId", messung.getId());
        Response response = repo.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
        List<LMesswert> messwerte = (List<LMesswert>)response.getData();
        if (messwerte == null || messwerte.isEmpty()) {
            Violation violation = new Violation();
            violation.addWarning("messwert", 631);
            return violation;
        }
        return null;
    }
}
