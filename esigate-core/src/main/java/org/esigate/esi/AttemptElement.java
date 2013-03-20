package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class AttemptElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:attempt", "</esi:attempt") {
		@Override
		public AttemptElement newInstance() {
			return new AttemptElement();
		}

	};

	private StringBuilder buf = new StringBuilder();

	AttemptElement() {
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		buf.append(csq, start, end);
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		TryElement parent = ctx.findAncestor(TryElement.class);
		if (parent != null && !parent.hasErrors()) {
			ctx.getCurrent().characters(buf, 0, buf.length());
		}
		buf.delete(0, Integer.MAX_VALUE);
	}
}
