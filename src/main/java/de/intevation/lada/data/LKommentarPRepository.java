package de.intevation.lada.data;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        try {
            manager.create(kommentar);
            return new Response(true, 200, kommentar);
        }
        catch(EntityExistsException eee) {
            return new Response(false, 601, kommentar);
        }
        catch(IllegalArgumentException iae) {
            return new Response(false, 602, kommentar);
        }
        catch(TransactionRequiredException tre) {
            logger.log(Level.INFO, "exception: " + tre);
            return new Response(false, 603, kommentar);
        }
    }

    @Override
    public Response update(Object object) {
        return new Response(false, 698, new ArrayList<LKommentarP>());
    }

    @Override
    public Response delete(Object object) {
        // TODO Auto-generated method stub
        return null;
    }
}
