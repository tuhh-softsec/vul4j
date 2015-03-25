package de.intevation.lada.lock;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;

import javax.inject.Inject;

import org.apache.log4j.Logger;

import de.intevation.lada.model.land.LMessung;
import de.intevation.lada.model.land.LProbe;
import de.intevation.lada.util.annotation.RepositoryConfig;
import de.intevation.lada.util.data.Repository;
import de.intevation.lada.util.data.RepositoryType;
import de.intevation.lada.util.rest.Response;

@LockConfig(type=LockType.TIMESTAMP)
public class TimestampLocker implements ObjectLocker {

    @Inject
    private Logger logger;

    @Inject
    @RepositoryConfig(type=RepositoryType.RO)
    Repository repository;

    @Override
    public boolean isLocked(Object o) {
        if (o instanceof LProbe) {
            LProbe newProbe = (LProbe)o;
            LProbe oldProbe = (LProbe)repository.getById(
                LProbe.class,
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
                        repository.getById(LProbe.class, id, "land");
                    LProbe probe = (LProbe)response.getData();
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
                        repository.getById(LMessung.class, id, "land");
                    LMessung messung = (LMessung)mResponse.getData();
                    Response pResponse =
                        repository.getById(LProbe.class, messung.getProbeId(), "land");
                    LProbe probe = (LProbe)pResponse.getData();
                    boolean newerMessung = isNewer(o, messung.getTreeModified());
                    boolean newerProbe = isNewer(o, probe.getTreeModified());
                    return newerMessung || newerProbe;
                }
            }
        }
        return false;
    }

    private boolean isNewer(Object o, Timestamp t) {
        Method m;
        try {
            m = o.getClass().getMethod("getTreeModified");
            Timestamp ot = (Timestamp)m.invoke(o);
            return t.getTime() > ot.getTime();
        } catch (NoSuchMethodException | SecurityException |
                 IllegalAccessException | IllegalArgumentException|
                 InvocationTargetException e) {
            return true;
        }
    }
}
