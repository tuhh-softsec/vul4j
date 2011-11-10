package org.esigate.esi;

import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class ChooseElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:choose", "</esi:choose") {
		public ChooseElement newInstance() {
			return new ChooseElement();
		}

	};

	private boolean condition;
	private boolean hasCondition;

	ChooseElement() {
		super(TYPE);
	}

	@Override
	protected void parseTag(Tag tag, Appendable parent, ElementStack stack) {
		condition = false;
		hasCondition = false;
	}

	public boolean hasCondition() {
		return hasCondition;
	}

	public boolean isCondition() {
		return condition;
	}

	public void setCondition(boolean condition) {
		this.condition = condition;
		hasCondition = true;
	}

	public void setHasCondition(boolean hasCondition) {
		this.hasCondition = hasCondition;
	}

}
