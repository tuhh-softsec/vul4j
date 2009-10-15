package net.webassembletool.aggregator;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.webassembletool.Driver;
import net.webassembletool.DriverFactory;
import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;
import net.webassembletool.tags.TemplateRenderer;

public class IncludeTemplateElement implements Element {
	public final static ElementType TYPE = new ElementType() {
		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$includetemplate$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endincludetemplate$");
		}

		public Element newInstance() {
			return new IncludeTemplateElement();
		}

	};
	private Driver driver;
	private String page = "";
	private String name = null;
	private Map<String, String> params = new HashMap<String, String>();

	public void doStartTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length > 3)
			driver = DriverFactory.getInstance(parameters[2]);
		else
			driver = DriverFactory.getInstance();
		if (parameters.length > 4)
			page = parameters[3];
		if (parameters.length > 5)
			name = parameters[4];
	}

	public void doEndTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage {
		driver.render(page, null, out, (HttpServletRequest) parser
				.getAttribute("request"), new TemplateRenderer(name, params,
				page));
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) {
		// Just ignore tag body
	}

	public void addParam(String name, String value) {
		params.put(name, value);
	}

	public boolean isClosed() {
		return false;
	}

}
