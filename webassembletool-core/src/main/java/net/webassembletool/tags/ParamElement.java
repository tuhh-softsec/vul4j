package net.webassembletool.tags;

import java.io.IOException;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;

public class ParamElement implements Element {
	private Appendable parent;
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

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable parent, ElementStack stack)
			throws IOException, HttpErrorPage {
		this.parent = parent;
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		TemplateElement templateElement = stack.findAncestorWithClass(this,
				TemplateElement.class);
		TemplateRenderer templateRenderer = stack.findAncestorWithClass(this,
				TemplateRenderer.class);
		if (templateElement == null || templateElement.isNameMatches()) {
			String value = templateRenderer.getParam(name);
			if (value != null) {
				parent.append(value);
				valueFound = true;
			}
		}
	}

	public ElementType getType() {
		return TYPE;
	}

	public boolean isClosed() {
		return false;
	}

	public Appendable append(CharSequence csq) throws IOException {
		if (!valueFound)
			parent.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		if (!valueFound)
			parent.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		if (!valueFound)
			parent.append(csq, start, end);
		return this;
	}

}
