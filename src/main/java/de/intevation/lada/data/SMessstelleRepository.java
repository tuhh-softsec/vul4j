package de.intevation.lada.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.intevation.lada.model.SMessStelle;


@ApplicationScoped
public class SMessstelleRepository
{
    @Inject
    EntityManager em;

    public List<SMessStelle> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SMessStelle> criteria = builder.createQuery(SMessStelle.class);
        Root<SMessStelle> member = criteria.from(SMessStelle.class);
        criteria.select(member);
        return em.createQuery(criteria).getResultList();
    }

    public SMessStelle findById(String id) {
        return em.find(SMessStelle.class, id);
    }
}
