/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.validation.rules.probe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.stammdaten.DeskriptorUmwelt;
import de.intevation.lada.model.stammdaten.Deskriptoren;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

//import org.apache.log4j.Logger;

/**
 * Validation rule for probe.
 * Validates if the umwelt id fits the deskriptor string.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Probe")
public class DeskriptorToUmwelt implements Rule {

//    @Inject
//    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        Probe probe = (Probe)object;
        if (probe.getMediaDesk() == null || probe.getMediaDesk().equals("")) {
            return null;
        }
        if (probe.getUmwId() == null) {
            return null;
        }
        String[] mediaDesk = probe.getMediaDesk().split(" ");
        if (mediaDesk.length <= 1 || "00".equals(mediaDesk[1])) {
            return null;
        }
        List<Integer> mediaIds = new ArrayList<Integer>();
        boolean zebs = false;
        Integer parent = null;
        Integer hdParent = null;
        Integer ndParent = null;
        if ("01".equals(mediaDesk[1])) {
            zebs = true;
        }
        for (int i = 1; i < mediaDesk.length; i++) {
            if ("00".equals(mediaDesk[i])) {
                mediaIds.add(-1);
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
                repository.entityManager(Strings.STAMM), Deskriptoren.class);
            if (parent != null) {
                builder.and("vorgaenger", parent);
            }
            builder.and("sn", mediaDesk[i]);
            builder.and("ebene", i - 1);
            Response response = repository.filter(builder.getQuery(), Strings.STAMM);
            @SuppressWarnings("unchecked")
            List<Deskriptoren> data = (List<Deskriptoren>)response.getData();
            if (data.isEmpty()) {
                Violation violation = new Violation();
                violation.addWarning("mediaDesk", 633);
                return violation;
            }
            hdParent = data.get(0).getId();
            mediaIds.add(data.get(0).getId());
            if (i == 2) {
                ndParent = data.get(0).getId();
            }
        }
        Violation violation = validateUmwelt(mediaIds, probe.getUmwId());
        return violation;
    }

    private Violation validateUmwelt(List<Integer> media, String umwId) {
        if (media.size() == 0) {
            Violation violation = new Violation();
            violation.addWarning("umwId#" + umwId, 632);
            return violation;
        }

        QueryBuilder<DeskriptorUmwelt> builder =
            new QueryBuilder<DeskriptorUmwelt>(
                repository.entityManager(Strings.STAMM), DeskriptorUmwelt.class);

        for (int i = 0; i < media.size(); i++) {
            String field = "s" + (i > 9 ? i : "0" + i);
            QueryBuilder<DeskriptorUmwelt> tmp = builder.getEmptyBuilder();
            if (media.get(i) != -1) {
                tmp.and(field, media.get(i));
                tmp.or(field, null);
                builder.and(tmp);
            }
            else {
                builder.and(field, null);
            }
        }
        Response response = repository.filter(builder.getQuery(), Strings.STAMM);
        @SuppressWarnings("unchecked")
        List<DeskriptorUmwelt> data = (List<DeskriptorUmwelt>)response.getData();
        if (data.isEmpty()) {
            Violation violation = new Violation();
            violation.addWarning("umwId#" + umwId, 632);
            return violation;
        }

        boolean unique = isUnique(data);
        if (unique && umwId.equals(data.get(0).getUmwId())) {
            return null;
        }
        else if (unique && !umwId.equals(data.get(0).getUmwId())) {
            Violation violation = new Violation();
            violation.addWarning("umwId#" + umwId, 632);
            return violation;
        }
        else {
            Violation violation = new Violation();
            violation.addWarning("umwId#" + umwId, 632);
            return violation;
        }
    }

    private boolean isUnique(List<DeskriptorUmwelt> list) {
        if (list.isEmpty()) {
            return false;
        }
        String element = list.get(0).getUmwId();
        for (int i = 1; i < list.size(); i++) {
            if (!element.equals(list.get(i).getUmwId())) {
                return false;
            }
        }
        return true;
    }
}
