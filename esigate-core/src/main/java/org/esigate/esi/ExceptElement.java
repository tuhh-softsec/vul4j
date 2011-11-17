package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class ExceptElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:except", "</esi:except") {
		public ExceptElement newInstance() {
			return new ExceptElement();
		}

	};

	private boolean ignoreContent;

	ExceptElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		TryElement parent = ctx.findAncestor(TryElement.class);
		ignoreContent = !parent.hasErrors();
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		if (!ignoreContent) {
			super.characters(csq, start, end);
		}
	}

}
