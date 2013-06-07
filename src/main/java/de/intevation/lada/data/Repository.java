package de.intevation.lada.data;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.inject.Named;

/**
 * This generic Container is an interface to request and select Data
 * obejcts from the connected database.
 * 
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@Named
@ApplicationScoped
public class Repository
{
    /**
     * The entitymanager managing the data.
     */
    @Inject
    private EntityManager em;

    /**
     * Errors/Warnings occured in repository operations.
     */
    private boolean success;
    private int generalError;
    private Map<String, Integer> errors;
    private Map<String, Integer> warnings;

    /**
     * Get all objects of type <link>clazz</link>from database.
     *
     * @param clazz The class type.
     * @return List of objects.
     */
    public <T> List<T> findAll(Class<T> clazz) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(clazz);
        Root<T> member = criteria.from(clazz);
        criteria.select(member);
        return em.createQuery(criteria).getResultList();
    }

    /**
     * Find a single object identified by its id.
     * 
     * @param clazz The class type.
     * @param id The object id.
     * @return The requested object of type clazz
     */
    public <T> T findById(Class<T> clazz, String id) {
        T item = em.find(clazz, id);
        if (item == null) {
            this.setGeneralError(600);
        }
        return item;
    }

    /**
     * Getter for the success boolean which indicates whether the request
     * succeeds.
     *
     * @return The true or false.
     */
    public boolean getSuccess() {
        return this.success;
    }

    /**
     * Protected setter for the success boolean which indicates whether the
     * request succeeds.
     *
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * Getter for the error code returned by the validator.
     *
     * @return The error code returned by the validator.
     */
    public int getGeneralError() {
        return generalError;
    }

    /**
     * Protected setter for the general error code.
     *
     * @param generalError
     */
    protected void setGeneralError(int generalError) {
        this.generalError = generalError;
    }

    /**
     * Getter for all errors occured while validating a LProbe object.
     *
     * @return Map of field - error code pairs.
     */
    public Map<String, Integer> getErrors() {
        return errors;
    }

    /**
     * Protected setter for validation errors.
     * 
     * @param errors
     */
    protected void setErrors(Map<String, Integer> errors) {
        this.errors = errors;
    }

    /**
     * Getter for all warnings occured while validating a LProbe object.
     *
     * @return Map of field - error code pairs.
     */
    public Map<String, Integer> getWarnings() {
        return warnings;
    }

    /**
     * Protected setter for validation warnings.
     *
     * @param warnings
     */
    protected void setWarnings(Map<String, Integer> warnings) {
        this.warnings = warnings;
    }
}
