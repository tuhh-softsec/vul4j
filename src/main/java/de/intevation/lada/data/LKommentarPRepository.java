package de.intevation.lada.data;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJBTransactionRolledbackException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.manage.Manager;
import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.rest.Response;


@Named("lkommentarRepository")
public class LKommentarPRepository
extends Repository
{

    @Inject
    @Named("datamanager")
    private Manager manager;

    @Inject
    private Logger logger;

    public Response create(Object object) {
        if (!(object instanceof LKommentarP)) {
            return new Response(false, 602, object);
        }
        LKommentarP kommentar = (LKommentarP)object;
        Response response = new Response(true, 200, kommentar);
        try {
            manager.create(kommentar);
            return response;
        }
        catch(EntityExistsException eee) {
            response.setSuccess(false);
            response.setMessage(601);
        }
        catch(IllegalArgumentException iae) {
            response.setSuccess(false);
            response.setMessage(602);
        }
        catch(TransactionRequiredException tre) {
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
        if (!(object instanceof LKommentarP)) {
            return new Response(false, 602, object);
        }
        LKommentarP kommentar = (LKommentarP)object;
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

    @Override
    public Response delete(Object object) {
        if (!(object instanceof LKommentarP)) {
            return new Response(false, 602, null);
        }
        LKommentarP kommentar = (LKommentarP)object;
        Response response = new Response(true, 200, null);
        try {
            manager.delete(kommentar);
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
