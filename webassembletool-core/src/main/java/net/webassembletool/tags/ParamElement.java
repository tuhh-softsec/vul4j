package net.webassembletool.tags;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;

public class ParamElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$beginparam$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endparam$");
		}

		public Element newInstance() {
			return new ParamElement();
		}

	};

	private boolean valueFound = false;

	public void doEndTag(String tag, Writer out, Parser parser) {
		// Nothing to do
	}

	public void doStartTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		Boolean insideTemplate = (Boolean) parser
				.getAttribute("insideTemplate");
		if (Boolean.TRUE.equals(insideTemplate)) {
			String value = null;
			Map<String, String> params = (Map<String, String>) parser
					.getAttribute("params");
			if (params != null)
				value = params.get(name);
			if (value != null) {
				out.write(value);
				valueFound = true;
			}
		}
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) throws IOException {
		if (!valueFound)
			out.append(content, begin, end);
	}

	public boolean isClosed() {
		return false;
	}

}
