package de.intevation.lada.validation.rules.probe;

import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.stamm.Deskriptoren;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

@ValidationRule("Probe")
public class Deskriptor implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        String[] mediaDesk = probe.getMediaDesk().split(" ");
        if (mediaDesk.length <= 1) {
            Violation violation = new Violation();
            violation.addWarning("mediaDesk", 631);
            return violation;
        }
        boolean zebs = false;
        Integer parent = null;
        Integer hdParent = null;
        Integer ndParent = null;
        if ("01".equals(mediaDesk[1])) {
            zebs = true;
        }
        for (int i = 1; i < mediaDesk.length; i++) {
            if ("00".equals(mediaDesk[i])) {
                continue;
            }
            if (zebs && i < 5) {
                parent = hdParent;
            }
            else if (!zebs && i < 3) {
                parent = hdParent;
            }
            else {
                parent = ndParent;
            }
            QueryBuilder<Deskriptoren> builder = new QueryBuilder<Deskriptoren>(
                repository.entityManager("stamm"), Deskriptoren.class);
            if (parent != null) {
                builder.and("vorgaenger", parent);
            }
            builder.and("sn", mediaDesk[i]);
            builder.and("ebene", i - 1);
            Response response = repository.filter(builder.getQuery(), "stamm");
            List<Deskriptoren> data = (List<Deskriptoren>)response.getData();
            if (data.isEmpty()) {
                Violation violation = new Violation();
                violation.addWarning("mediaDesk", 633);
                return violation;
            }
            hdParent = data.get(0).getId();
            if (i == 2) {
                ndParent = data.get(0).getId();
            }
        }
        return null;
    }
}
