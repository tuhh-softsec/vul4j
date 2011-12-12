package org.esigate.aggregator;

import java.io.IOException;

import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.tags.BlockRenderer;


class IncludeBlockElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<!--$includeblock$");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("<!--$endincludeblock$");
		}

		public Element newInstance() {
			return new IncludeBlockElement();
		}

	};

	public boolean onError(Exception e, org.esigate.parser.ParserContext ctx) {
		return false;
	}

	public void onTagEnd(String tag, org.esigate.parser.ParserContext ctx) {
		// Nothing to do
	}

	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		ElementAttributes tagAttributes = ElementAttributesFactory.createElementAttributes(tag);
		Driver driver = tagAttributes.getDriver();
		String page = tagAttributes.getPage();
		String name = tagAttributes.getName();

		driver.render(page, new Adapter(ctx.getCurrent()), ctx.getResourceContext(),
				new BlockRenderer(name, page), new AggregateRenderer());
	}

	public boolean isClosed() {
		return false;
	}

	public void characters(CharSequence csq, int start, int end) throws IOException {
		// Just ignore tag body
	}

}
