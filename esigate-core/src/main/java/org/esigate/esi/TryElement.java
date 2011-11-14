package org.esigate.esi;

import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

class TryElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:try", "</esi:try") {
		public TryElement newInstance() {
			return new TryElement();
		}

	};

	private boolean includeInside;

	TryElement() {
		super(TYPE);
	}

	public boolean isIncludeInside() {
		return includeInside;
	}

	public void setIncludeInside(boolean includeInside) {
		this.includeInside = includeInside;
	}

	@Override
	protected void parseTag(Tag tag, Appendable parent, ElementStack stack) {
		this.includeInside = false;
	}

}
