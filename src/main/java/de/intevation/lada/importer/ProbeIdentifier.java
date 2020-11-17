/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer;

import java.util.List;

import javax.inject.Inject;
import javax.management.modelmbean.InvalidTargetObjectTypeException;

import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;

/**
 * Class to identify a probe object.
 */
@IdentifierConfig(type = "Probe")
public class ProbeIdentifier implements Identifier {

    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    private Repository repository;

    private Probe found;

    @Override
    public Identified find(Object object)
    throws InvalidTargetObjectTypeException {
        found = null;
        if (!(object instanceof Probe)) {
            throw new InvalidTargetObjectTypeException(
                "Object is not of type Probe");
        }
        Probe probe = (Probe) object;
        QueryBuilder<Probe> builder = new QueryBuilder<Probe>(
            repository.entityManager(Strings.LAND),
            Probe.class
        );

        // externeProbeId null and hauptprobenNr not null and mstId not null.
        if (probe.getExterneProbeId() == null
            && probe.getHauptprobenNr() != null
            && probe.getMstId() != null
        ) {
            builder.and("mstId", probe.getMstId());
            builder.and("hauptprobenNr", probe.getHauptprobenNr());
            List<Probe> proben =
                repository.filterPlain(builder.getQuery(), Strings.LAND);
            if (proben.size() > 1) {
                // Should never happen. DB has unique constraint for
                // "hauptprobenNr"
                return Identified.REJECT;
            }
            if (proben.isEmpty()) {
                return Identified.NEW;
            }
            found = proben.get(0);
            return Identified.UPDATE;
        } else if (probe.getExterneProbeId() != null
            && (probe.getHauptprobenNr() == null
                || probe.getMstId() == null)
        ) {
            builder.and("externeProbeId", probe.getExterneProbeId());
            List<Probe> proben =
                repository.filterPlain(builder.getQuery(), Strings.LAND);
            if (proben.size() > 1) {
                // Should never happen. DB has unique constraint for
                // "externeProbeId"
                return Identified.REJECT;
            }
            if (proben.isEmpty()) {
                return Identified.NEW;
            }
            found = proben.get(0);
            return Identified.UPDATE;
        } else {
            builder.and("externeProbeId", probe.getExterneProbeId());
            List<Probe> proben =
                repository.filterPlain(builder.getQuery(), Strings.LAND);
            if (proben.size() > 1) {
                // Should never happen. DB has unique constraint for
                // "externeProbeId"
                return Identified.REJECT;
            }
            if (proben.isEmpty()) {
                return Identified.NEW;
            }
            if (proben.get(0).getHauptprobenNr() == null
                || proben.get(0).getHauptprobenNr().equals(
                    probe.getHauptprobenNr())
                || probe.getHauptprobenNr().isEmpty()
                || proben.get(0).getHauptprobenNr().isEmpty()
            ) {
                found = proben.get(0);
                return Identified.UPDATE;
            } else {
                return Identified.REJECT;
            }
        }
    }

    /**
     * @return the found probe
     */
    public Object getExisting() {
        return found;
    }
}
