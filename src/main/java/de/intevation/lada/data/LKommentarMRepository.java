package de.intevation.lada.data;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.LKommentarM;
import de.intevation.lada.rest.Response;

/**
 * This Container is an interface to request, filter and select LKommentarM
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named("lkommentarmrepository")
public class LKommentarMRepository
extends Repository
{

    /**
     * Manager class for LPRobe. Used to manipulate data objects.
     */
    @Inject
    @Named("datamanager")
    private Manager manager;

    @Override
    public Response create(Object object) {
        if (!(object instanceof LKommentarM)) {
            return new Response(false, 602, object);
        }
        LKommentarM kommentar = (LKommentarM)object;
        Response response = new Response(true, 200, kommentar);
        try {
            manager.create(kommentar);
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
        catch (EJBTransactionRolledbackException te) {
            response.setSuccess(false);
            response.setMessage(604);
        }
        return response;
    }

    @Override
    public Response update(Object object) {
        if (!(object instanceof LKommentarM)) {
            return new Response(false, 602, object);
        }
        LKommentarM kommentar = (LKommentarM)object;
        Response response = new Response(true, 200, kommentar);
        try {
            manager.update(kommentar);
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
        catch (EJBTransactionRolledbackException te) {
            response.setSuccess(false);
            response.setMessage(604);
        }
        return response;
    }
}
