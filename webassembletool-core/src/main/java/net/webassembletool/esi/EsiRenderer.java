package net.webassembletool.esi;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.webassembletool.Driver;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;
import net.webassembletool.ResourceContext;
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
public class EsiRenderer implements Renderer, Appendable {
	private final static Parser PARSER = new Parser(Pattern
			.compile("(<esi:[^>]*>)|(</esi:[^>]*>)|(<!--esi)|(-->)"),
			IncludeElement.TYPE, Comment.TYPE, CommentElement.TYPE,
			RemoveElement.TYPE);
	private Writer out;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final Driver driver;

	public EsiRenderer(HttpServletRequest request,
			HttpServletResponse response, Driver driver) {
		this.request = request;
		this.response = response;
		this.driver = driver;
	}

	/** {@inheritDoc} */
	public void render(ResourceContext requestContext, String content,
			Writer out) throws IOException, HttpErrorPage {
		this.out = out;
		if (content == null) {
			return;
		}
		PARSER.parse(content, this);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public Appendable append(CharSequence csq) throws IOException {
		out.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		out.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		out.append(csq, start, end);
		return this;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Driver getDriver() {
		return driver;
	}
}