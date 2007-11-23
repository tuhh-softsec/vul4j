package net.webassembletool;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * User context that can be used in the master application to define the user id and the Locale. This context will be transmitted to the provider applications.<br />
 * There is one instance of user context associated with each Thread.
 * 
 * @author François-Xavier Bonnet
 *
 */
public class Context {
	private final static ThreadLocal<Context> current = new ThreadLocal<Context>();
	private String user;
	private Locale locale;
	private Context() {
		// Nothing to do
	}
	/**
	 * Shortcut for getCurrent(true)
	 * @return The current context
	 */
	public final static Context getCurrent() {
		return getCurrent(true);
	}
	public final static void retrieveFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			Context context = (Context) session.getAttribute(Context.class.getName());
			Context.setCurrent(context);
		} else {
			Context.setCurrent(null);
		}
	}
	public final static void saveToSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute(Context.class.getName(), Context.getCurrent(false));
	}
	/**
	 * @param create Specifies if the context must me created if it does not exists already
	 * @return
	 */
	public final static Context getCurrent(boolean create) {
		Context currentContext = current.get();
		if (currentContext == null && create) {
			currentContext = new Context();
			current.set(currentContext);
		}
		return currentContext;
	}
	public final static void setCurrent(Context context) {
		current.set(context);
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
