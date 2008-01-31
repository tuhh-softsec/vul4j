package net.webassembletool;

import java.util.Locale;

/**
 * User context that can be used in the master application to define the user id
 * and the Locale. This context will be transmitted to the provider
 * applications.<br />
 * There is one instance of user context associated with each Thread.
 * 
 * @author François-Xavier Bonnet
 * 
 */
public class Context {
    private String user;
    private Locale locale;

    public Context() {
	// Nothing to do
    }

    public Locale getLocale() {
	return locale;
    }

    public void setLocale(Locale locale) {
	this.locale = locale;
    }

    public String getUser() {
	return user;
    }

    public void setUser(String user) {
	this.user = user;
    }
}
