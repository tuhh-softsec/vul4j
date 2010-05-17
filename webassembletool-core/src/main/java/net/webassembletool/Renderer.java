package net.webassembletool;

import java.io.IOException;
import java.io.Writer;

/**
 * Content rendering strategy.
 * 
 * @author Stanislav Bernatskyi
 */
public interface Renderer {

	/**
	 * Renders provided source and writes results to the output
	 * 
	 * @param requestContext
	 * 
	 * @param src
	 *            source to be rendered
	 * @param out
	 *            output destination
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	void render(ResourceContext requestContext, String src, Writer out)
			throws IOException, HttpErrorPage;
}
