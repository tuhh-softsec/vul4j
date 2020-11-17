/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.auth;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.model.land.StatusProtokoll;
import de.intevation.lada.model.stammdaten.StatusKombi;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.data.Strings;
import de.intevation.lada.util.rest.Response;

public abstract class BaseAuthorizer implements Authorizer {

    /**
     * The Repository used to read from Database.
     */
    @Inject
    @RepositoryConfig(type = RepositoryType.RO)
    protected Repository repository;

    /**
     * Get the authorization of a single probe.
     *
     * @param userInfo  The user information.
     * @param probe     The probe to authorize.
     */
    protected boolean getAuthorization(UserInfo userInfo, Probe probe) {
        if (probe.getMstId() != null
            && userInfo.getMessstellen().contains(probe.getMstId())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Test whether a probe is readonly.
     *
     * @param probeId   The probe Id.
     * @return True if the probe is readonly.
     */
    public boolean isProbeReadOnly(Integer probeId) {
        EntityManager manager = repository.entityManager(Strings.LAND);
        QueryBuilder<Messung> builder =
            new QueryBuilder<Messung>(
                manager,
                Messung.class);
        builder.and("probeId", probeId);
        Response response = repository.filter(builder.getQuery(), Strings.LAND);
        @SuppressWarnings("unchecked")
        List<Messung> messungen = (List<Messung>) response.getData();
        for (int i = 0; i < messungen.size(); i++) {
            if (messungen.get(i).getStatus() == null) {
                continue;
            }
            StatusProtokoll status =
                repository.getByIdPlain(
                    StatusProtokoll.class,
                    messungen.get(i).getStatus(),
                    Strings.LAND);
            StatusKombi kombi = repository.getByIdPlain(
                StatusKombi.class, status.getStatusKombi(), Strings.STAMM);
            if (kombi.getStatusWert().getId() != 0
                && kombi.getStatusWert().getId() != 4
            ) {
                return true;
            }
        }
        return false;
    }

    public boolean isMessungReadOnly(Integer messungsId) {
        Messung messung =
            repository.getByIdPlain(Messung.class, messungsId, Strings.LAND);
        if (messung.getStatus() == null) {
            return false;
        }
        StatusProtokoll status = repository.getByIdPlain(
            StatusProtokoll.class,
            messung.getStatus(),
            Strings.LAND);
        StatusKombi kombi = repository.getByIdPlain(
            StatusKombi.class, status.getStatusKombi(), Strings.STAMM);
        return (kombi.getStatusWert().getId() != 0
                && kombi.getStatusWert().getId() != 4);
    }

}
