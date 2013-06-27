package de.intevation.lada.data;

import java.util.Map;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.LProbe;
import de.intevation.lada.model.LProbeInfo;
import de.intevation.lada.rest.Response;
import de.intevation.lada.validation.ValidationException;
import de.intevation.lada.validation.Validator;

/**
 * This Container is an interface to request, filter and select LProbe
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lproberepository")
public class LProbeRepository extends Repository{

    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * Manager class for LPRobe. Used to manipulate data objects.
     */
    @Inject
    @Named("datamanager")
    private Manager manager;

    @Inject
    @Named("lprobevalidator")
    private Validator validator;

    @Override
    public <T> Response findAll(Class<T> clazz) {
        QueryBuilder<LProbeInfo> builder =
            new QueryBuilder<LProbeInfo>(em, LProbeInfo.class);
        builder.distinct();
        return filter(builder.getQuery());
    }

    @Override
    public <T> Response findById(Class<T> clazz, String id) {
        QueryBuilder<LProbeInfo> builder =
            new QueryBuilder<LProbeInfo>(em, LProbeInfo.class);
        builder.and("probeId", id);
        builder.distinct();
        return filter(builder.getQuery());
    }

    /**
     * Validate and persist a new LProbe object.
     *
     * @param probe The new LProbe object
     * @return Response.
     */
    public Response create(Object object) {
        if (!(object instanceof LProbe)) {
            return new Response(false, 602, object);
        }
        LProbe probe = (LProbe)object;
        Response response = new Response(true, 200, probe);
        // Try to save the new LProbe.
        try {
            Map<String, Integer> warnings = validator.validate(probe);
            manager.create(probe);
            response.setWarnings(warnings);
            return response;
        }
        catch (EntityExistsException eee) {
            response.setSuccess(false);
            response.setMessage(601);
        }
        catch (IllegalArgumentException iae) {
            response.setSuccess(false);
            response.setMessage(602);
        }
        catch (TransactionRequiredException tre) {
            response.setSuccess(false);
            response.setMessage(603);
        }
        catch (ValidationException ve) {
            response.setSuccess(false);
            response.setMessage(604);
            response.setErrors(ve.getErrors());
            response.setWarnings(ve.getWarnings());
        }
        catch (EJBTransactionRolledbackException te) {
            response.setSuccess(false);
            response.setMessage(604);
        }
        return response;
    }

    public Response update(Object object) {
        if (!(object instanceof LProbe)) {
            return new Response(false, 602, object);
        }
        LProbe probe = (LProbe)object;
        Response response = new Response(true, 200, probe);
        // Try to save the new LProbe.
        try {
            Map<String, Integer> warnings = validator.validate(probe);
            manager.update(probe);
            response.setWarnings(warnings);
            return response;
        }
        catch (EntityExistsException eee) {
            response.setSuccess(false);
            response.setMessage(601);
        }
        catch (IllegalArgumentException iae) {
            response.setSuccess(false);
            response.setMessage(602);
        }
        catch (TransactionRequiredException tre) {
            response.setSuccess(false);
            response.setMessage(603);
        }
        catch (ValidationException ve) {
            response.setSuccess(false);
            response.setMessage(604);
            response.setErrors(ve.getErrors());
            response.setWarnings(ve.getWarnings());
        }
        catch (EJBTransactionRolledbackException te) {
            response.setSuccess(false);
            response.setMessage(604);
        }
        return response;
    }
}
