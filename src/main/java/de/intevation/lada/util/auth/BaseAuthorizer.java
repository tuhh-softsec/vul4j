package de.intevation.lada.util.auth;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.model.land.LStatusProtokoll;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.QueryBuilder;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

public abstract class BaseAuthorizer implements Authorizer {

    /**
     * The Repository used to read from Database.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    protected Repository repository;

    /**
     * Get the authorization of a single probe.
     *
     * @param userInfo  The user information.
     * @param probe     The probe to authorize.
     */
    protected boolean getAuthorization(UserInfo userInfo, LProbe probe) {
        if (userInfo.getMessstellen().contains(probe.getMstId())) {
            return true;
        }
        else {
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
        EntityManager manager = repository.entityManager("land");
        QueryBuilder<LMessung> builder =
            new QueryBuilder<LMessung>(
                manager,
                LMessung.class);
        builder.and("probeId", probeId);
        Response response = repository.filter(builder.getQuery(), "land");
        @SuppressWarnings("unchecked")
        List<LMessung> messungen = (List<LMessung>) response.getData();
        for (int i = 0; i < messungen.size(); i++) {
            if (messungen.get(i).getStatus() == null) {
                return false;
            }
            LStatusProtokoll status = repository.getByIdPlain(
                LStatusProtokoll.class, messungen.get(i).getStatus(), "land");
            if (status.getStatusWert() != 0 && status.getStatusWert() != 4) {
                return true;
            }
        }
        return false;
    }

    public boolean isMessungReadOnly(Integer messungsId) {
        LMessung messung =
            repository.getByIdPlain(LMessung.class, messungsId, "land");
        if (messung.getStatus() == null) {
            return false;
        }
        LStatusProtokoll status = repository.getByIdPlain(
            LStatusProtokoll.class,
            messung.getStatus(),
            "land");
        return (status.getStatusWert() != 0 && status.getStatusWert() != 4);
    }

}
