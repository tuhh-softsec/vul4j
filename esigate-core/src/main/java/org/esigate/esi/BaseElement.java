package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ParserContext;

abstract class BaseElement implements Element {
	private boolean closed = false;
	private Element parent = null;

	protected BaseElement() {
	}

	/** Additional tag initialization callback. */
	@SuppressWarnings("unused")
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		// Default implementation does nothing
	}

	@Override
	public boolean isClosed() {
		return closed;
	}

	@Override
	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		Tag tagObj = Tag.create(tag);
		closed = tagObj.isOpenClosed();
		parent = ctx.getCurrent();
		parseTag(tagObj, ctx);
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
	}

	@Override
	public boolean onError(Exception e, ParserContext ctx) {
		return false;
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		parent.characters(csq, start, end);
	}

}
