/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer;

import javax.management.modelmbean.InvalidTargetObjectTypeException;

/**
 * Interface for object identifier.
 */
public interface Identifier {

    /**
     * Find and identify the object.
     * @param object the object.
     * @return Enum idicating the identification type.
     * @throws InvalidTargetObjectTypeException if the given object has an
     *                                          invalid type.
     */
    Identified find(Object object)
        throws InvalidTargetObjectTypeException;

    /**
     * Get the object identified in "find", if any.
     * @return the found object
     */
    Object getExisting();
}
