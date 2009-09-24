package net.webassembletool.tags;

import java.io.IOException;
import java.io.Writer;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;

public class BlockElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$beginblock$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endblock$");
		}

		public Element newInstance() {
			return new BlockElement();
		}

	};

	private boolean nameMatches = false;

	public void doEndTag(String tag, Writer out, Parser parser)
			throws IOException {
		// Nothing to do
	}

	public void doStartTag(String tag, Writer out, Parser parser)
			throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		nameMatches = name.equals(parser.getAttribute("name"));
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) throws IOException {
		if (nameMatches)
			out.append(content, begin, end);
	}

	public boolean isClosed() {
		return false;
	}

}
