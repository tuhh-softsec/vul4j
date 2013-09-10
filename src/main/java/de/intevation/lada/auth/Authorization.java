package de.intevation.lada.auth;

/**
 * Defines the interface for data access.
 *
 * @author <a href="mailto:rrenkert@intevation.de">Raimund Renkert</a>
 */
public interface Authorization
{
    public boolean isReadOnly(String id);
}
