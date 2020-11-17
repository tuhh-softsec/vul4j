/* Copyright (C) 2016 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.importer;

/**
 * Enum for object identification.
 */
public enum Identified {
    /**
     * Object is not exisiting.
     */
    NEW,

    /**
     * Object exists.
     */
    UPDATE,

    /**
     * Object can not be created or updated.
     */
    REJECT
}
