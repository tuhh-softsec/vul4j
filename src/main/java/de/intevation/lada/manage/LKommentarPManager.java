package de.intevation.lada.manage;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.TransactionRequiredException;

import de.intevation.lada.model.LKommentarP;
import de.intevation.lada.model.LKommentarPId;

@Stateless
public class LKommentarPManager
{
    @Inject
    private EntityManager em;

    @Inject
    private Logger logger;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void create(LKommentarP kommentar)
    throws EntityExistsException,
        IllegalArgumentException,
        TransactionRequiredException
    {
        LKommentarPId id = new LKommentarPId();
        id.setProbeId(kommentar.getProbeId());
        kommentar.setId(id);
        em.persist(kommentar);
    }
}
