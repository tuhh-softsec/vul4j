package org.esigate.tags;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.aggregator.AggregationSyntaxException;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;


class TemplateElement implements Element {
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

	public boolean onError(Exception e, ParserContext ctx) {
		return false; // do not handle errors
	}

	public void onTagEnd(String tag, ParserContext ctx) throws IOException ,HttpErrorPage {
		// Stop writing
		if (nameMatches) {
			templateRenderer.setWrite(false);
		}
	}

	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		this.templateRenderer = ctx.findAncestor(TemplateRenderer.class);
		// If name matches, start writing
		nameMatches = name.equals(templateRenderer.getName());
		if (nameMatches) {
			templateRenderer.setWrite(true);
		}
	}

	public void characters(CharSequence csq, int start, int end) throws IOException {
		if (nameMatches) {
			templateRenderer.append(csq, start, end);
		}
	}

	public boolean isClosed() {
		return false;
	}

	public boolean isNameMatches() {
		return nameMatches;
	}

}
