package org.esigate.parser;

import org.esigate.ResourceContext;

public interface ParserContext {

	/** @return {@linkplain ResourceContext} associated with current processing. */
	ResourceContext getResourceContext();

	/** @return <code>true</code> if error has been handled by this element and it should not be propagated further. */
	boolean reportError(Exception e);
	Element getCurrent();
	<T> T findAncestor(Class<T> type);
}
