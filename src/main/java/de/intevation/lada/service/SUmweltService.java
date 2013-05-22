package de.intevation.lada.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.intevation.lada.model.SUmwelt;

@Stateless
public class SUmweltService {
    @Inject
    private Logger log;

    @Inject
    private EntityManager em;
}
