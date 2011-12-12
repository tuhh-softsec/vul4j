package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class InlineElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:inline", "</esi:inline") {
		public InlineElement newInstance() {
			return new InlineElement();
		}

	};

	private String uri;
	private boolean fetchable;
	private StringBuilder buf = new StringBuilder();

	InlineElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) {
		this.uri = tag.getAttribute("name");
		this.fetchable = "yes".equalsIgnoreCase(tag.getAttribute("fetchable"));
	}

	@Override
	public void characters(CharSequence csq, int start, int end) {
		buf.append(csq, start, end);
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String originalUrl = ctx.getResourceContext().getOriginalRequest().getRequestURL().toString();
		InlineCache.storeFragment(uri, null, fetchable, originalUrl, buf.toString());
	}
}
