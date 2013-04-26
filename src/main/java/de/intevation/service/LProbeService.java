package de.intevation.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.intevation.model.LProbe;

@Stateless
public class LProbeService {

    @Inject
    private Logger log;

    @Inject
    private EntityManager em;

//    @Inject
//    private Event<Member> memberEventSrc;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void delete(String id) throws Exception {
    	LProbe probe = em.find(LProbe.class, id);
        log.info("Deleting " + probe.getProbeId());
        em.remove(probe);
        //memberEventSrc.fire(member);
    }
}