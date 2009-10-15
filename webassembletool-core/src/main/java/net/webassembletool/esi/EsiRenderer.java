package net.webassembletool.esi;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;
import net.webassembletool.parser.Parser;

/**
 * Retrieves a resource from the provider application and parses it to find ESI
 * tags to be replaced by contents from other applications.
 * 
 * For more information about ESI language specification, see <a
 * href="http://www.w3.org/TR/esi-lang">Edge Side Include</a>
 * 
 * @author Francois-Xavier Bonnet
 */
public class EsiRenderer implements Renderer {
	private final static Pattern PATTERN = Pattern
			.compile("(<esi:[^>]*>)|(<!--esi)|(-->)");

	private final HttpServletRequest request;

	public EsiRenderer(HttpServletRequest request) {
		this.request = request;
	}

	/** {@inheritDoc} */
	public void render(String content, Writer out) throws IOException,
			HttpErrorPage {
		if (content == null)
			return;
		Parser parser = new Parser(PATTERN, IncludeElement.TYPE, Comment.TYPE);
		parser.setAttribute("request", request);
		parser.parse(content, out, true);
	}
}