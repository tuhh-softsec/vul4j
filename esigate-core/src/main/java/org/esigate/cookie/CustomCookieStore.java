package org.esigate.cookie;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.esigate.api.Cookie;
import org.esigate.extension.Extension;

/**
 * Interface for Cookie Store.
 * 
 * @see CookieStore
 * @see Extension
 * 
 * @author Nicolas Richeton
 * 
 */
public interface CustomCookieStore extends Serializable, Extension {
	/**
     * Adds an {@link Cookie}, replacing any existing equivalent cookies.
     * If the given cookie has already expired it will not be added, but existing
     * values will still be removed.
     *
     * @param cookie the {@link Cookie cookie} to be added
     */
    void addCookie(Cookie cookie);

    /**
     * Returns all cookies contained in this store.
     *
     * @return all cookies
     */
    List<Cookie> getCookies();

    /**
     * Removes all of {@link Cookie}s in this store that have expired by
     * the specified {@link java.util.Date}.
     *
     * @return true if any cookies were purged.
     */
    boolean clearExpired(Date date);

    /**
     * Clears all cookies.
     */
    void clear();
}
