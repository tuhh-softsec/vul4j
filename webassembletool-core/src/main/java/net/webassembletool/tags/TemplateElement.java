package net.webassembletool.tags;

import java.io.IOException;

import net.webassembletool.HttpErrorPage;
import net.webassembletool.aggregator.AggregationSyntaxException;
import net.webassembletool.parser.Element;
import net.webassembletool.parser.ElementStack;
import net.webassembletool.parser.ElementType;

public class TemplateElement implements Element {
	private TemplateRenderer templateRenderer;
	private boolean nameMatches;
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

	public void doEndTag(String tag) throws IOException {
		// Stop writing
		if (nameMatches)
			templateRenderer.setWrite(false);
	}

	public void doStartTag(String tag, Appendable parent, ElementStack stack)
			throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		this.templateRenderer = stack.findAncestorWithClass(this,
				TemplateRenderer.class);
		// If name matches, start writing
		nameMatches = name.equals(templateRenderer.getName());
		if (nameMatches)
			templateRenderer.setWrite(true);
	}

	public ElementType getType() {
		return TYPE;
	}

	public boolean isClosed() {
		return false;
	}

	public boolean isNameMatches() {
		return nameMatches;
	}

	public Appendable append(CharSequence csq) throws IOException {
		if (nameMatches)
			templateRenderer.append(csq);
		return this;
	}

	public Appendable append(char c) throws IOException {
		if (nameMatches)
			templateRenderer.append(c);
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end)
			throws IOException {
		if (nameMatches)
			templateRenderer.append(csq, start, end);
		return this;
	}

}
