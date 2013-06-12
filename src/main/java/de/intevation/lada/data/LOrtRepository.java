package de.intevation.lada.data;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;

import de.intevation.lada.model.LOrt;


public class LOrtRepository
extends Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * Filter LProbe object list by the given criteria.
     *
     * @param criteria
     * @return List of LProbe objects.
     */
    public List<LOrt> filter(CriteriaQuery<LOrt> criteria) {
        return em.createQuery(criteria).getResultList();
    }
}
