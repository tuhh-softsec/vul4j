package org.esigate.aggregator;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.ResourceContext;
import org.esigate.parser.Parser;


/**
 * Parses a page to find tags to be replaced by contents from other providers.
 * 
 * Sample syntax used for includes :
 * <ul>
 * <li>
 * &lt;!--$includeblock$provider$page$blockname$--&gt;&lt;!--$endincludeblock$
 * --&gt;</li>
 * <li>&lt;!--$includetemplate$provider$page$templatename$--&gt;&lt;!--
 * $endincludetemplate$--&gt;</li>
 * <li>&lt;!--$beginput$name$--&gt;&lt;!--$endput$--&gt;</li>
 * </ul>
 * 
 * Sample syntax used inside included contents for template and block
 * definition:
 * <ul>
 * <li>&lt;!--$beginblock$name$--&gt;</li>
 * <li>&lt;!--$begintemplate$name$--&gt;</li>
 * <li>&lt;!--$beginparam$name$--&gt;</li>
 * </ul>
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class AggregateRenderer implements Renderer, Appendable {
	/** Generic pattern for all the tags we want to look for. */
	private final static Pattern PATTERN = Pattern.compile("<!--\\$[^>]*\\$-->");

	private final Parser parser = new Parser(PATTERN,
			IncludeBlockElement.TYPE, IncludeTemplateElement.TYPE, PutElement.TYPE);
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private Writer out;

	public AggregateRenderer(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	/** {@inheritDoc} */
	public void render(ResourceContext requestContext, String content, Writer out) throws IOException, HttpErrorPage {
		this.out = out;
		if (content == null) {
			return;
		}
		parser.parse(content, this);
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

}
