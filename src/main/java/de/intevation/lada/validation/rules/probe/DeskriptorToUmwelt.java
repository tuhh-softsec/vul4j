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
            for (int i = 0; i < data.size(); i++) {
            }
            int found = -1;
            int lastMatch = -12;
            for (int i = 0; i < data.size(); i++) {
                int matches = -12;
                for (int j = 0; j < 12; j++) {
                    switch(j) {
                        case 0: if (media.get(0).equals(data.get(i).getS00()) ||
                                    media.get(0).equals(-1) && data.get(i).getS00() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 1: if (media.get(1).equals(data.get(i).getS01()) ||
                                    media.get(1).equals(-1) && data.get(i).getS01() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 2: if (media.get(2).equals(data.get(i).getS02()) ||
                                    media.get(2).equals(-1) && data.get(i).getS02() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 3: if (media.get(3).equals(data.get(i).getS03()) ||
                                    media.get(3).equals(-1) && data.get(i).getS03() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 4: if (media.get(4).equals(data.get(i).getS04()) ||
                                    media.get(4).equals(-1) && data.get(i).getS04() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 5: if (media.get(5).equals(data.get(i).getS05()) ||
                                    media.get(5).equals(-1) && data.get(i).getS05() == null
                                ) {
                                    matches +=1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 6: if (media.get(6).equals(data.get(i).getS06()) ||
                                    media.get(6).equals(-1) && data.get(i).getS06() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 7: if (media.get(7).equals(data.get(i).getS07()) ||
                                    media.get(7).equals(-1) && data.get(i).getS07() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 8: if (media.get(8).equals(data.get(i).getS08()) ||
                                    media.get(8).equals(-1) && data.get(i).getS08() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 9: if (media.get(9).equals(data.get(i).getS09()) ||
                                    media.get(9).equals(-1) && data.get(i).getS09() == null
                                ) {
                                    matches += 1;
                                }
                                else  {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 10: if (media.get(10).equals(data.get(i).getS10()) ||
                                    media.get(10).equals(-1) && data.get(i).getS10() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                        case 11: if (media.get(11).equals(data.get(i).getS11()) ||
                                    media.get(11).equals(-1) && data.get(i).getS11() == null
                                ) {
                                    matches += 1;
                                }
                                else {
                                    j = 12; matches = -12;
                                }
                                break;
                    }
                }
                if (matches > lastMatch) {
                    lastMatch = matches;
                    found = i;
                }
            }
            if (found >= 0 && data.get(found).getUmwId().equals(umwId)) {
                return null;
            }
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
