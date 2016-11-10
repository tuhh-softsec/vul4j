/* Copyright (C) 2013 by Bundesamt fuer Strahlenschutz
 * Software engineering by Intevation GmbH
 *
 * This file is Free Software under the GNU GPL (v>=3)
 * and comes with ABSOLUTELY NO WARRANTY! Check out
 * the documentation coming with IMIS-Labordaten-Application for details.
 */
package de.intevation.lada.lock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.Messung;
import de.intevation.lada.model.land.Probe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

/**
 * Data object locker using a timestamp to lock data access.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
@LockConfig(type=LockType.TIMESTAMP)
public class TimestampLocker implements ObjectLocker {

    /**
     * The logger used in this class.
     */
    @Inject
    private Logger logger;

    /**
     * The repository used to read data.
     */
    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    Repository repository;

    /**
     * Test whether a data object is locked or not.
     *
     * @param o The object to test.
     * @return True if the object is locked else false.
     */
    @Override
    public boolean isLocked(Object o) {
        if (o instanceof Probe) {
            Probe newProbe = (Probe)o;
            Probe oldProbe = (Probe)repository.getById(
                Probe.class,
                newProbe.getId(),
                "land").getData();
            logger.debug("old: " + oldProbe.getTreeModified().getTime());
            logger.debug("new: " + newProbe.getTreeModified().getTime());
            if (oldProbe.getTreeModified().getTime() >
                    newProbe.getTreeModified().getTime()) {
                return true;
            }
        }
        else {
            Method[] methods = o.getClass().getMethods();
            for (Method m: methods) {
                if (m.getName().equals("getProbeId")) {
                    Integer id;
                    try {
                        id = (Integer) m.invoke(o);
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        return true;
                    }
                    Response response =
                        repository.getById(Probe.class, id, "land");
                    Probe probe = (Probe)response.getData();
                    return isNewer(o, probe.getTreeModified());
                }
                if (m.getName().equals("getMessungsId")) {
                    Integer id;
                    try {
                        id = (Integer) m.invoke(o);
                    } catch (IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException e) {
                        return true;
                    }
                    Response mResponse =
                        repository.getById(Messung.class, id, "land");
                    Messung messung = (Messung)mResponse.getData();
                    boolean newerMessung = isNewer(o, messung.getTreeModified());
                    return newerMessung;
                }
            }
        }
        return false;
    }

    /**
     * Test whether an object is newer tha the given timestamp.
     *
     * @param o     The object to test.
     * @param t     The timestamp.
     * @return True if the object is newer.
     */
    private boolean isNewer(Object o, Timestamp t) {
        Method m;
        try {
            m = o.getClass().getMethod("getParentModified");
            Timestamp ot = (Timestamp)m.invoke(o);
            if (ot == null) {
                return true;
            }
            return t.getTime() > ot.getTime();
        } catch (NoSuchMethodException | SecurityException |
                 IllegalAccessException | IllegalArgumentException|
                 InvocationTargetException e) {
            return true;
        }
    }
}
