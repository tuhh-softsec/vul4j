package de.intevation.lada.lock;

public interface ObjectLocker {
    boolean isLocked(Object o);
}
