package org.esigate.esi;

import java.io.IOException;

import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class ExceptElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:except", "</esi:except") {
		@Override
		public ExceptElement newInstance() {
			return new ExceptElement();
		}

	};

	private boolean processContent;

	ExceptElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) {
		TryElement parent = ctx.findAncestor(TryElement.class);
		int code = (tag.getAttribute("code") != null) ? Integer.parseInt(tag.getAttribute("code")) : -1;
		processContent = (parent.hasErrors() 
				&& !parent.exceptProcessed() 
				&& (code == -1 || code == parent.getErrorCode()));
		if (processContent) {
			parent.setExceptProcessed(processContent);
		}
	}

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		if (processContent) {
			super.characters(csq, start, end);
		}
	}

}
