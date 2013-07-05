package de.intevation.lada.data;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class QueryBuilder<T>
{
    private EntityManager manager;
    private CriteriaBuilder builder;
    private CriteriaQuery<T> query;
    private Root<T> root;
    private Class<T> clazz;
    private Predicate filter;

    /**
     * Create a new QueryBuilder for the specified class.
     *
     * @param manager
     * @param clazz
     */
    public QueryBuilder(EntityManager manager, Class<T> clazz) {
        this.manager = manager;
        this.clazz = clazz;
        this.builder = this.manager.getCriteriaBuilder();
        this.query = this.builder.createQuery(this.clazz);
        this.root = this.query.from(this.clazz);
    }

    /**
     * Get the criteria query build with this class.
     *
     * @return The query.
     */
    public CriteriaQuery<T> getQuery() {
        this.query.where(this.filter);
        return this.query;
    }

    /**
     * Logical AND operation.
     *
     * @param id    The database column name.
     * @param value The filter value
     * @return The builder itself.
     */
    public QueryBuilder<T> and(String id, Object value) {
        Predicate p = this.builder.equal(this.root.get(id), value);
        if (this.filter != null) {
            this.filter = this.builder.and(this.filter, p);
        }
        else {
            this.filter = this.builder.and(p);
        }
        return this;
    }

    /**
     * Logical OR operation.
     *
     * @param id    The database column name
     * @param value The filter value.
     * @return The builder itself.
     */
    public QueryBuilder<T> or(String id, Object value) {
        Predicate p = this.builder.equal(this.root.get(id), value);
        if (this.filter != null) {
            this.filter = this.builder.or(this.filter, p);
        }
        else {
            this.filter = this.builder.or(p);
        }
        return this;
    }

    /**
     * Logical AND operation.
     * All elements in <i>values</i> will be concatenated with AND operator.
     *
     * @param id        The database column name.
     * @param values    List of values.
     * @return The builder itself.
     */
    public QueryBuilder<T> and(String id, List<String> values) {
        for(String v: values) {
            this.and(id, v);
        }
        return this;
    }

    /**
     * Logical OR operation.
     * All elements in <i>values</i> will be concatenated with OR operator.
     *
     * @param id        The database column name.
     * @param values    List of values.
     * @return The builder itself.
     */
    public QueryBuilder<T> or(String id, List<String> values) {
        for (String v: values) {
            this.or(id, v);
        }
        return this;
    }

    /**
     * Logical AND operation.
     * The actually defined query will be concatenated with the query defined
     * in the builder <i>b</i>.
     *
     * @param b     A builder.
     * @return The builder itself.
     */
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

    /**
     * Logical OR operation.
     * The actually defined query will be concatenated with the query defined
     * in the builder <i>b</i>.
     *
     * @param b     A builder.
     * @return The builder itself.
     */
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

    /**
     * Use 'distinct' in the query.
     */
    public void distinct() {
        this.query.distinct(true);
    }

    /**
     * Order result by the specified column name
     *
     * @param id    The column name.
     * @param asc   Ascending(true), Descending(false).
     */
    public void orderBy(String id, boolean asc) {
        if (asc) {
            this.query.orderBy(this.builder.asc(this.root.get(id)));
        }
        else {
            this.query.orderBy(this.builder.desc(this.root.get(id)));
        }
    }

    /**
     * Get an empty instance of this builder to create subfilters.
     *
     * @return An empty instance of this builder.
     */
    public QueryBuilder<T> getEmptyBuilder(){
        QueryBuilder<T> copy = new QueryBuilder<T>(manager, clazz);
        copy.builder = this.builder;
        copy.root = this.root;
        return copy;
    }
}