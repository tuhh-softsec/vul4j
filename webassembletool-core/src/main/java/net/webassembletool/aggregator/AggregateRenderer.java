package net.webassembletool.aggregator;

import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;
import net.webassembletool.parser.Parser;

/**
 * Retrieves a resource from the provider application and parses it to find tags
 * to be replaced by contents from other providers.
 * 
 * Sample syntax used for includes :
 * <ul>
 * <li>&lt;!--$includeblock$provider$page$blockname$--&gt;</li>
 * <li>&lt;!--$includetemplate$provider$page$templatename$--&gt;</li>
 * <li>&lt;!--$beginput$name$--&gt;</li>
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
 * Aggregation is always in "proxy mode" that means cookies or parameters from
 * the original request are transmitted to the target server. <br/>
 * <b>NB: Cookies and parameters are not transmitted to templates or blocks
 * invoked by the page</b>.
 * 
 * @author Stanislav Bernatskyi
 */
public class AggregateRenderer implements Renderer {
	private final static Pattern PATTERN = Pattern
			.compile("<!--\\$[^>]*\\$-->");

	private final HttpServletRequest request;
	private final boolean propagateJsessionId;

	public AggregateRenderer(HttpServletRequest request,
			boolean propagateJsessionId) {
		this.request = request;
		this.propagateJsessionId = propagateJsessionId;
	}

	/** {@inheritDoc} */
	public void render(String content, Writer out) throws IOException,
			HttpErrorPage {
		if (content == null)
			return;
		Parser parser = new Parser(PATTERN, IncludeBlockElement.TYPE,
				IncludeTemplateElement.TYPE, PutElement.TYPE);
		parser.setAttribute("request", request);
		parser.parse(content, out, true);
	}
}
