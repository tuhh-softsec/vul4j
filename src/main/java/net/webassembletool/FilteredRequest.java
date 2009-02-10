package net.webassembletool;

import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Wrapper to the response used inside the servlet filter to override some of
 * the methods of the request.
 * 
 * @author François-Xavier Bonnet
 */
public class FilteredRequest extends HttpServletRequestWrapper {
    public FilteredRequest(HttpServletRequest request) {
        super(request);
    }

    /**
     * Returns the user defined as parameter "user" if present
     */
    @Override
    public String getRemoteUser() {
        String user = getParameter("user");
        if (user != null)
            return user;
        else
            return super.getRemoteUser();
    }

    /**
     * Returns the locale defined as parameter "locale" if present
     */
    @Override
    public Locale getLocale() {
        String localeString = getParameter("locale");
        if (localeString != null)
            return new Locale(localeString);
        else
            return super.getLocale();
    }

    /**
     * Returns the locale defined as parameter "locale" if present
     */
    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<Locale> getLocales() {
        String localeString = getParameter("locale");
        if (localeString != null) {
            final Locale locale = new Locale(localeString);
            return new Enumeration<Locale>() {
                private boolean moreElements = true;

                public boolean hasMoreElements() {
                    return moreElements;
                }

                public Locale nextElement() {
                    moreElements = false;
                    return locale;
                }
            };
        } else {
            return super.getLocales();
        }
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            public String getName() {
                return getRemoteUser();
            }
        };
    }
}
