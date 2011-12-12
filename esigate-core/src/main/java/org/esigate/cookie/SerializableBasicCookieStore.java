package org.esigate.cookie;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;

import org.esigate.api.Cookie;


/**
 * Serializable implementation of {@link CookieStore}.
 * 
 * @author Nicolas Richeton
 */
public class SerializableBasicCookieStore implements CustomCookieStore {
	/** Serial Id. */
	private static final long serialVersionUID = 5884817839252416275L;

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.esigate.extension.Extension#init(java.util.Properties)
	 */
	public void init(Properties properties) {
	}

	private final TreeSet<Cookie> cookies;

    public SerializableBasicCookieStore() {
        super();
        this.cookies = new TreeSet<Cookie>(new CookieIdentityComparator());
    }

    /**
     * Adds an {@link Cookie cookie}, replacing any existing equivalent cookies.
     * If the given cookie has already expired it will not be added, but existing
     * values will still be removed.
     *
     * @param cookie the {@link Cookie cookie} to be added
     *
     * @see #addCookies(Cookie[])
     *
     */
    public synchronized void addCookie(Cookie cookie) {
        if (cookie != null) {
            // first remove any old cookie that is equivalent
            cookies.remove(cookie);
            if (!cookie.isExpired(new Date())) {
                cookies.add(cookie);
            }
        }
    }

    /**
     * Returns an immutable array of {@link Cookie cookies} that this
     * state currently contains.
     *
     * @return an array of {@link Cookie cookies}.
     */
    public synchronized List<Cookie> getCookies() {
        //create defensive copy so it won't be concurrently modified
        return new ArrayList<Cookie>(cookies);
    }

    /**
     * Removes all of {@link Cookie cookies} in this state
     * that have expired by the specified {@link java.util.Date date}.
     *
     * @return true if any cookies were purged.
     *
     * @see Cookie#isExpired(Date)
     */
    public synchronized boolean clearExpired(final Date date) {
        if (date == null) {
            return false;
        }
        boolean removed = false;
        for (Iterator<Cookie> it = cookies.iterator(); it.hasNext();) {
            if (it.next().isExpired(date)) {
                it.remove();
                removed = true;
            }
        }
        return removed;
    }

    /**
     * Clears all cookies.
     */
    public synchronized void clear() {
        cookies.clear();
    }

    @Override
    public synchronized String toString() {
        return cookies.toString();
    }


}
