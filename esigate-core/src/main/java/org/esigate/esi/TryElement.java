package org.esigate.esi;

import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class TryElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:try", "</esi:try") {
		public TryElement newInstance() {
			return new TryElement();
		}

	};

	private boolean hasErrors;

	TryElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) {
		this.hasErrors = false;
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	@Override
	public boolean onError(Exception e, ParserContext ctx) {
		hasErrors = true;
		return true;
	}
}
