package net.webassembletool.tags;

import java.io.IOException;
import java.io.Writer;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;

public class TemplateElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$begintemplate$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endtemplate$");
		}

		public Element newInstance() {
			return new TemplateElement();
		}

	};

	private boolean matches = false;

	public void doEndTag(String tag, Writer out, Parser parser)
			throws IOException {
		if (matches)
			parser.setAttribute("insideTemplate", Boolean.FALSE);
	}

	public void doStartTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		matches = name.equals(parser.getAttribute("name"));
		if (matches)
			parser.setAttribute("insideTemplate", Boolean.TRUE);
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) throws IOException {
		if (matches)
			out.append(content, begin, end);
	}

	public boolean isClosed() {
		return false;
	}

}
