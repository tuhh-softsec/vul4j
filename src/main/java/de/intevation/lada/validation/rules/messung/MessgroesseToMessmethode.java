package de.intevation.lada.validation.rules.messung;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LMesswert;
import de.intevation.lada.model.stamm.MmtMessgroesse;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Messung")
public class MessgroesseToMessmethode implements Rule {

    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        LMessung messung = (LMessung)object;
        String mmt = messung.getMmtId();
        QueryBuilder<LMesswert> builder =
            new QueryBuilder<LMesswert>(
                repository.entityManager("land"), LMesswert.class);
        builder.and("messungsId", messung.getId());
        Response response = repository.filter(builder.getQuery(), "land");
        List<LMesswert> messwerte = (List<LMesswert>)response.getData();

        QueryBuilder<MmtMessgroesse> mmtBuilder =
            new QueryBuilder<MmtMessgroesse>(
                    repository.entityManager("stamm"), MmtMessgroesse.class);

        Response results =
            repository.filter(mmtBuilder.getQuery(), "stamm");
        List<MmtMessgroesse> messgroessen =
            (List<MmtMessgroesse>)results.getData();
        List<MmtMessgroesse> found = new ArrayList<MmtMessgroesse>();
        for (MmtMessgroesse mg: messgroessen) {
            if (mmt.equals(mg.getMmtMessgroessePK().getMmtId())) {
                logger.debug("found: " + mg.getMmtMessgroessePK().getMmtId() + " for " + mmt);
                found.add(mg);
            }
        }
        Violation violation = new Violation();
        for(LMesswert messwert: messwerte) {
            boolean hit = false;
            for (MmtMessgroesse messgroesse: found) {
                logger.debug("###### mmt: " + messwert.getMessgroesseId() + " mmtmg: " + messgroesse.getMmtMessgroessePK().getMessgroessengruppeId());
                if (messwert.getMessgroesseId().equals(
                        messgroesse.getMmtMessgroessePK().getMessgroessengruppeId())) {
                    hit = true;
                }
            }
            if (!hit) {
                violation.addWarning("messgroesse", 632);
            }
        }
        return violation.hasWarnings() ? violation : null;
    }
}
