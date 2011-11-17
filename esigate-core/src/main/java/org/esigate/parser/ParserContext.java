package org.esigate.parser;

import javax.servlet.http.HttpServletRequest;

public interface ParserContext {

	/** @return {@linkplain HttpServletRequest} associated with current processing. */
	HttpServletRequest getRequest();

	/** @return <code>true</code> if error has been handled by this element and it should not be propagated further. */
	boolean reportError(Exception e);
	Element getCurrent();
	<T> T findAncestor(Class<T> type);
}
