/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.util.data;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

/**
 * Implementation for a new data type in the postgresql/postgis jdbc driver.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public class JsonObjectType implements UserType {

    private final Logger logger = Logger.getLogger(JsonObjectType.class);

    /**
     * Reconstruct an object from the cacheable representation. At the very
     * least this method should perform a deep copy if the type is mutable.
     * (optional <span id="IL_AD5" class="IL_AD">operation</span>)
     *
     * @param cached
     *            the object to be cached
     * @param owner
     *            the owner of the cached object
     * @return a reconstructed object from the cachable representation
     * @throws HibernateException
     */
    @Override
    public Object assemble(
        Serializable cached,
        Object owner)
    throws HibernateException {
        return this.deepCopy(cached);
    }

    /**
     * Return a deep copy of the persistent state, stopping at entities and st
     * collections. It is not necessary to copy immutable objects, or null
     * values, in which case it is safe to simple return the argument.
     *
     * @param value
     *            the object to be cloned, which may be null
     *
     * @return object a copy
     * @throws HibernateException
     */
    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }

    /**
     * Transform the object into its cacheable representation. At the very least
     * this method should perform a deep copy if the type is mutable. That may
     * not be enough for some implementations, however; for example,
     * <span id="IL_AD11" class="IL_AD">associations</span> must be cached as
     * <span id="IL_AD9" class="IL_AD">identifier</span> values.
     * (optional operation)
     *
     * @param value
     *            the object to be cached
     * @return a cachable representation of the object
     * @throws HibernateException
     */
    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (String) this.deepCopy(value);
    }

    /**
     * Compare two instances of the class mapped by this type for persistence
     * "equality". Equality of the persistence state.
     *
     * @param x
     * @param y
     * @return <span id="IL_AD12" class="IL_AD">boolean</span>
     * @throws HibernateException
     */
    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    /**
     * Get a hashcode for the instance, consistent with persistence "equality".
     */
    @Override
    public int hashCode(Object x) throws HibernateException {
        return x.hashCode();
    }

    /**
     * Are objects of this type mutable?
     *
     * @return boolean
     */
    @Override
    public boolean isMutable() {
        return true;
    }

    /**
     * During merge, <span id="IL_AD7" class="IL_AD">replace</span>
     * the existing (target) values in the entity we are
     * merging to with a new (original) value from the detched entity we are
     * merging. For immutable objects, or null values, it is safe to return a
     * copy of the first parameter. For the objects with component values, it
     * might make sense to recursively replace component values
     *
     * @param original
     *            the value from the detched entity being merged
     * @param target
     *            the value in the managed entity
     * @param owner
     * @return the value to be merged
     * @throws HibernateException
     */
    @Override
    public Object replace(
        Object original,
        Object target,
        Object owner)
    throws HibernateException {
        return original;
    }

    /**
     * The class returned by <tt>nullSafeGet()</tt>
     *
     * @return Class
     */
    @Override
    public Class returnedClass() {
        return String.class;
    }

    /**
     * Returns the SQL type <span id="IL_AD3" class="IL_AD">codes</span>
     * for the columns mapped by this type. The codes
     * are defined on <tt>java.sql.Types</tt>
     *
     * @return int[] the typecodes
     * @see java.sql.Types
     */
    @Override
    public int[] sqlTypes() {
        return new int[] {Types.JAVA_OBJECT};
    }

    @Override
    public Object nullSafeGet(
        ResultSet rs,
        String[] names,
        SharedSessionContractImplementor session,
        Object owner)
    throws HibernateException, SQLException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;
        try {
            node = mapper.readTree(rs.getString(names[0]));
            return node;
        } catch (IOException e) {
            logger.debug("Exception while reading Tree", e);
        }
        return mapper.createObjectNode();
    }

    @Override
    public void nullSafeSet(
        PreparedStatement st,
        Object value,
        int index,
        SharedSessionContractImplementor session)
    throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
            return;
        }
        st.setObject(index, value, Types.OTHER);
    }
}
