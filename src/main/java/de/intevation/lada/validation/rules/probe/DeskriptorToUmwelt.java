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

import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.stamm.DeskriptorUmwelt;
import de.intevation.lada.model.stamm.Deskriptoren;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;
import de.intevation.lada.validation.Violation;
import de.intevation.lada.validation.annotation.ValidationRule;
import de.intevation.lada.validation.rules.Rule;

/**
 * Validation rule for probe.
 * Validates if the umwelt id fits the deskriptor string.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@ValidationRule("Probe")
public class DeskriptorToUmwelt implements Rule {

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    private Repository repository;

    @Override
    public Violation execute(Object object) {
        LProbe probe = (LProbe)object;
        if (probe.getMediaDesk() == null || probe.getMediaDesk().equals("")) {
            return null;
        }
        if (probe.getUmwId() == null) {
            return null;
        }
        String[] mediaDesk = probe.getMediaDesk().split(" ");
        if (mediaDesk.length <= 1) {
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
                repository.entityManager("stamm"), Deskriptoren.class);
            if (parent != null) {
                builder.and("vorgaenger", parent);
            }
            builder.and("sn", mediaDesk[i]);
            builder.and("ebene", i - 1);
            Response response = repository.filter(builder.getQuery(), "stamm");
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
        Violation violation = validateUmwelt(mediaIds, probe.getUmwId(), zebs, 0);
        return violation;
    }

    private Violation validateUmwelt(List<Integer> media, String umwId, boolean isZebs, int ndx) {
        QueryBuilder<DeskriptorUmwelt> builder =
            new QueryBuilder<DeskriptorUmwelt>(
                repository.entityManager("stamm"), DeskriptorUmwelt.class);

        if (media.size() == 0) {
            Violation violation = new Violation();
            violation.addWarning("umwId", 632);
            return violation;
        }

        int size = 1;
        for (int i = size; i >= 0; i--) {
            if (media.get(i) == -1) {
                continue;
            }
            String field = "s" + (i > 9 ? i : "0" + i);
            builder.and(field, media.get(i));
        }
        Response response = repository.filter(builder.getQuery(), "stamm");
        @SuppressWarnings("unchecked")
        List<DeskriptorUmwelt> data = (List<DeskriptorUmwelt>)response.getData();
        if (data.isEmpty()) {
            Violation violation = new Violation();
            violation.addWarning("umwId", 632);
            return violation;
        }

        boolean unique = isUnique(data);
        if (unique && umwId.equals(data.get(0).getUmwId())) {
            return null;
        }
        else if (unique && !umwId.equals(data.get(0).getUmwId())) {
            Violation violation = new Violation();
            violation.addWarning("umwId", 632);
            return violation;
        }
        else {
            Violation violation = new Violation();
            violation.addWarning("umwId", 632);
            boolean found = false;
            for (int i = 0; i < data.size(); i++) {
                if (!data.get(i).getUmwId().equals(umwId)) {
                    continue;
                }
                for (int j = size + 1; j < 13; j++) {
                    switch(j) {
                        case 2: if (data.get(i).getS02() == null ||
                                    data.get(i).getS02().equals(media.get(2)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 3: if (data.get(i).getS03() == null ||
                                    data.get(i).getS03().equals(media.get(3)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 4: if (data.get(i).getS04() == null ||
                                    data.get(i).getS04().equals(media.get(4)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 5: if (data.get(i).getS05() == null ||
                                    data.get(i).getS05().equals(media.get(5)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 6: if (data.get(i).getS06() == null ||
                                    data.get(i).getS06().equals(media.get(6)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 7: if (data.get(i).getS07() == null ||
                                    data.get(i).getS07().equals(media.get(7)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 8: if (data.get(i).getS08() == null ||
                                    data.get(i).getS08().equals(media.get(8)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 9: if (data.get(i).getS09() == null ||
                                    data.get(i).getS09().equals(media.get(9)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 10: if (data.get(i).getS10() == null ||
                                     data.get(i).getS10().equals(media.get(10)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 11: if (data.get(i).getS11() == null ||
                                     data.get(i).getS11().equals(media.get(11)))
                                    found = true;
                                else
                                    found = false;
                                break;
                        case 12: if (data.get(i).getS12() == null ||
                                     data.get(i).getS12().equals(media.get(12)))
                                    found = true;
                                else
                                    found = false;
                                break;
                    }
                    if (found) {
                        return null;
                    }
                }
            }
            return violation;
        }
    }

    private boolean isUnique(List<DeskriptorUmwelt> list) {
        if (list.isEmpty()) {
            return false;
        }
        String element = list.get(0).getUmwId();
        for (int i = 1; i < list.size(); i++) {
            if (!element.equals(list.get(i))) {
                return false;
            }
        }
        return true;
    }
}
