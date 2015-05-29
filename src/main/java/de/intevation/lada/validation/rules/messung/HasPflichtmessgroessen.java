package de.intevation.lada.validation.rules.messung;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.stamm.PflichtMessgroesse;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Messung")
public class HasPflichtmessgroessen implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        LMessung messung = (LMessung)object;
        QueryBuilder<PflichtMessgroesse> builder =
            new QueryBuilder<PflichtMessgroesse>(
                repository.entityManager("stamm"),
                PflichtMessgroesse.class);
        builder.and("mmtId", messung.getMmtId());
        Response response = repository.filter(builder.getQuery(), "stamm");
        List<PflichtMessgroesse> pflicht =
            (List<PflichtMessgroesse>)response.getData();

        QueryBuilder<LMesswert> wertBuilder =
            new QueryBuilder<LMesswert>(
                repository.entityManager("land"), LMesswert.class);
        wertBuilder.and("messungsId", messung.getId());
        Response wertResponse =
            repository.filter(wertBuilder.getQuery(), "land");
        List<LMesswert> messwerte = (List<LMesswert>)wertResponse.getData();
        Violation violation = new Violation();
        boolean missing = false;
        for (PflichtMessgroesse p : pflicht) {
            for (LMesswert wert : messwerte) {
                if (!p.getMessgroesseId().equals(wert.getMessgroesseId())) {
                    missing = true;
                }
            }
        }
        if (missing) {
            violation.addWarning("pflichtmessgroesse", 631);
        }
        return violation.hasWarnings() ? violation : null;
    }
}
