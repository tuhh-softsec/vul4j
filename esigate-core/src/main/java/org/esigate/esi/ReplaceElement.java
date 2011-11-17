package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

// FIXME: [saber] NYI
abstract class ReplaceElement implements Element {

	public final static ElementType TYPE = new BaseElementType("<esi:replace", "</esi:replace") {
		public ReplaceElement newInstance() {
			return null;
			//return new ReplaceElement();
		}

	};

	private final boolean closed = false;

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void onTagStart(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		// TODO
	}

	public ElementType getType() {
		return TYPE;
	}

}
