package de.intevation.lada.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.intevation.lada.model.SUmwelt;

@ApplicationScoped
public class SUmweltRepository {

    @Inject
    EntityManager em;

    public List<SUmwelt> findAll() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<SUmwelt> criteria = builder.createQuery(SUmwelt.class);
        Root<SUmwelt> member = criteria.from(SUmwelt.class);
        criteria.select(member);
        return em.createQuery(criteria).getResultList();
    }

    public SUmwelt findById(String id) {
        return em.find(SUmwelt.class, id);
    }
}
