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
public class TemplateRenderer implements Renderer, Appendable {
	private final static Log LOG = LogFactory.getLog(TemplateRenderer.class);
	private final static Parser PARSER = new Parser(Pattern
			.compile("<!--\\$[^>]*\\$-->"), TemplateElement.TYPE,
			ParamElement.TYPE);

	private final String page;
	private final String name;
	private final Map<String, String> params;
	private boolean write;
	private Writer out;

	public TemplateRenderer(String name, Map<String, String> params, String page) {
		this.name = name;
		this.params = params;
		this.page = page;
		if (name == null)
			write = true;
		else
			write = false;
	}

	/** {@inheritDoc} */
	public void render(String content, Writer out) throws IOException,
			HttpErrorPage {
		LOG.debug("Rendering block " + name + " in page " + page);
		this.out = out;
		if (content == null) {
			if (params != null) {
				for (String value : params.values()) {
					out.write(value);
				}
			}
		} else {
			PARSER.parse(content, this);
		}
	}

	public String getName() {
		return name;
	}

	public void setWrite(boolean write) {
		this.write = write;
	}

	public String getParam(String name) {
		return params.get(name);
	}

	public Appendable append(CharSequence csq) throws IOException {
		if (write)
			out.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		if (write)
			out.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		if (write)
			out.append(csq, start, end);
		return this;
	}

}
