package de.intevation.lada.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class QueryBuilder<T>
{
    private EntityManager manager;
    private CriteriaBuilder builder;
    private CriteriaQuery<T> query;
    private Root<T> root;
    private Class<T> clazz;
    private Predicate filter;

    public QueryBuilder(EntityManager manager, Class<T> clazz) {
        this.manager = manager;
        this.clazz = clazz;
        this.builder = this.manager.getCriteriaBuilder();
        this.query = this.builder.createQuery(this.clazz);
        this.root = this.query.from(this.clazz);
    }

    public CriteriaQuery<T> getQuery() {
        this.query.where(this.filter);
        return this.query;
    }

    public QueryBuilder<T> and(String id, String value) {
        Predicate p = this.builder.equal(this.root.get(id), value);
        if (this.filter != null) {
            this.filter = this.builder.and(this.filter, p);
        }
        else {
            this.filter = this.builder.and(p);
        }
        return this;
    }

    public QueryBuilder<T> or(String id, String value) {
        Predicate p = this.builder.equal(this.root.get(id), value);
        if (this.filter != null) {
            this.filter = this.builder.or(this.filter, p);
        }
        else {
            this.filter = this.builder.or(p);
        }
        return this;
    }

    public QueryBuilder<T> and(String id, List<String> values) {
        for(String v: values) {
            this.and(id, v);
        }
        return this;
    }

    public QueryBuilder<T> or(String id, List<String> values) {
        for (String v: values) {
            this.or(id, v);
        }
        return this;
    }

    public QueryBuilder<T> and(QueryBuilder<T> b) {
        if (b == null || b.filter == null) {
            return this;
        }
        if (this.filter != null) {
            this.filter = this.builder.and(this.filter, b.filter);
        }
        else {
            this.filter = this.builder.and(b.filter);
        }
        return this;
    }

    public QueryBuilder<T> or(QueryBuilder<T> b) {
        if (b == null || b.filter == null) {
            return this;
        }
        if (this.filter != null) {
            this.filter = this.builder.or(this.filter, b.filter);
        }
        else {
            this.filter = this.builder.or(b.filter);
        }
        return this;
    }

    public void distinct() {
        this.query.distinct(true);
    }

    public void orderBy(String id, boolean asc) {
        if (asc) {
            this.query.orderBy(this.builder.asc(this.root.get(id)));
        }
        else {
            this.query.orderBy(this.builder.desc(this.root.get(id)));
        }
    }

    public QueryBuilder<T> getEmptyBuilder(){
        QueryBuilder<T> copy = new QueryBuilder<T>(manager, clazz);
        copy.builder = this.builder;
        copy.root = this.root;
        return copy;
    }
}
