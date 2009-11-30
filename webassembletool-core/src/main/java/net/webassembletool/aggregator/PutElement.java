package net.webassembletool.aggregator;

import java.io.IOException;
import java.io.Writer;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;
import net.webassembletool.parser.Parser;

public class PutElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$beginput$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endput$");
		}

		public Element newInstance() {
			return new PutElement();
		}

	};

	private IncludeTemplateElement includeTemplateElement;
	private StringBuilder body = new StringBuilder();
	private String name;

	public void doEndTag(String tag) {
		includeTemplateElement.addParam(name, body.toString());
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack)
			throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		name = parameters[2];
		includeTemplateElement = stack.findAncestorWithClass(this,
				IncludeTemplateElement.class);
		if (includeTemplateElement == null)
			throw new AggregationSyntaxException(tag
					+ " should be nested in an includetemplate tag");
	}

	public ElementType getType() {
		return TYPE;
	}

	public void write(CharSequence content, int begin, int end, Writer out,
			Parser parser) {
		body.append(content, begin, end);
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		body.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		body.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end) {
		body.append(csq, start, end);
		return this;
	}

}
