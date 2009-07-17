package net.webassembletool.parse;

import java.io.IOException;
import java.io.Writer;

import net.webassembletool.HttpErrorPage;

/**
 * Content rendering strategy.
 * 
 * @author Stanislav Bernatskyi
 */
public interface Renderer {

	/**
	 * Renders provided source and writes results to the output
	 * 
	 * @param src
	 *            source to be rendered
	 * @param out
	 *            output destination
	 * @param replaceRules
	 *            replace rules
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	void render(String src, Writer out) throws IOException, HttpErrorPage;
}
