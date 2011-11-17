package org.esigate.tags;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.aggregator.AggregationSyntaxException;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;


class ParamElement implements Element {
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

	private Element parent;
	private boolean valueFound = false;

	public boolean onError(Exception e, ParserContext ctx) {
		return false;
	}

	public void onTagEnd(String tag, ParserContext ctx) {
		// Nothing to do
	}

	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		this.parent = ctx.getCurrent();
		String[] parameters = tag.split("\\$");
		if (parameters.length != 4)
			throw new AggregationSyntaxException("Invalid syntax: " + tag);
		String name = parameters[2];
		TemplateElement templateElement = ctx.findAncestor(TemplateElement.class);
		TemplateRenderer templateRenderer = ctx.findAncestor(TemplateRenderer.class);
		if (templateElement == null || templateElement.isNameMatches()) {
			String value = templateRenderer.getParam(name);
			if (value != null) {
				parent.characters(value, 0 , value.length());
				valueFound = true;
			}
		}
	}

	public boolean isClosed() {
		return false;
	}

	public void characters(CharSequence csq, int start, int end) throws IOException {
		if (!valueFound) {
			parent.characters(csq, start, end);
		}
	}

}
