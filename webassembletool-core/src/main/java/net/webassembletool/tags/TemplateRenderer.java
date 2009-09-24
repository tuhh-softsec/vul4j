package net.webassembletool.tags;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.regex.Pattern;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.Renderer;
import net.webassembletool.parser.Parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Template renderer.
 * <p>
 * Retrieves a template from the provider application and renders it to the
 * writer replacing the parameters with the given map. If <code>name</code>
 * param is <code>null</code>, the whole page will be used as the template.<br />
 * eg: The template <code>mytemplate</code> can be delimited in the provider
 * page by comments <code>&lt;!--$begintemplate$mytemplate$--&gt;</code> and
 * <code>&lt;!--$endtemplate$mytemplate$--&gt;</code>.<br />
 * Inside the template, the parameters can be defined by comments.<br />
 * eg: parameter named <code>myparam</code> should be delimited by comments
 * <code>&lt;!--$beginparam$myparam$--&gt;</code> and
 * <code>&lt;!--$endparam$myparam$--&gt;</code>
 * 
 * @author Stanislav Bernatskyi
 * @author Francois-Xavier Bonnet
 */
public class TemplateRenderer implements Renderer {
	private final static Log LOG = LogFactory.getLog(TemplateRenderer.class);
	private final static Pattern PATTERN = Pattern
			.compile("<!--\\$[^>]*\\$-->");

	private final String page;
	private final String name;
	private final Map<String, String> params;

	public TemplateRenderer(String name, Map<String, String> params, String page) {
		this.name = name;
		this.params = params;
		this.page = page;
	}

	/** {@inheritDoc} */
	public void render(String content, Writer out) throws IOException,
			HttpErrorPage {
		LOG.debug("Rendering block " + name + " in page " + page);
		if (content == null) {
			if (params != null) {
				for (String value : params.values()) {
					out.write(value);
				}
			}
		} else {
			Parser parser = new Parser(PATTERN, TemplateElement.TYPE,
					ParamElement.TYPE);
			parser.setAttribute("params", params);
			if (name != null) {
				parser.setAttribute("name", name);
				parser.parse(content, out, false);
			} else {
				parser.setAttribute("insideTemplate", Boolean.TRUE);
				parser.parse(content, out, true);
			}
		}
	}

}
