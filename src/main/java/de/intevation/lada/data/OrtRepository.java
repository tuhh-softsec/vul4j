package de.intevation.lada.data;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.Ort;
import de.intevation.lada.rest.Response;

@Named("ortrepository")
public class OrtRepository
extends Repository
{
    @Inject
    @Named("datamanager")
    private Manager manager;

    @Override
    public Response create(Object object) {
        if (!(object instanceof Ort)) {
            return new Response(false, 602, object);
        }
        Ort ort = (Ort)object;
        try {
            manager.create(ort);
            return new Response(true, 200, ort);
        }
        catch(EntityExistsException eee) {
            return new Response(false, 601, ort);
        }
        catch(IllegalArgumentException iae) {
            return new Response(false, 602, ort);
        }
        catch(TransactionRequiredException tre) {
            return new Response(false, 603, ort);
        }
    }

    @Override
    public Response update(Object object) {
        if (!(object instanceof Ort)) {
            return new Response(false, 602, object);
        }
        Ort ort = (Ort)object;
        Response response = new Response(true, 200, ort);
        try {
            manager.update(ort);
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
    public Response delete(Object object) {
        if (!(object instanceof Ort)) {
            return new Response(false, 602, null);
        }
        Ort ort = (Ort)object;
        Response response = new Response(true, 200, null);
        try {
            manager.delete(ort);
        }
        catch (IllegalArgumentException iae) {
            response.setSuccess(false);
            response.setMessage(602);
        }
        catch (TransactionRequiredException tre) {
            response.setSuccess(false);
            response.setMessage(603);
        }
        return response;
    }

}
