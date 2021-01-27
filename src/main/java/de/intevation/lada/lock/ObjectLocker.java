/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.lock;

/**
 * Interface for data object locker.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface ObjectLocker {
    /**
     * Check of object is locked.
     * @param o the object to check
     * @return true if the object is currently locked
     */
    boolean isLocked(Object o);
}
