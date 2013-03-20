package org.esigate.esi;

import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class ChooseElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:choose", "</esi:choose") {
		@Override
		public ChooseElement newInstance() {
			return new ChooseElement();
		}

	};

	private boolean condition;
	private boolean hasConditionSet;

	ChooseElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) {
		condition = false;
		hasConditionSet = false;
	}

	public boolean hadConditionSet() {
		return hasConditionSet;
	}

	public boolean isCondition() {
		return condition;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
		this.hasConditionSet |= condition; // set to true if anyone of conditions are true
	}

}
