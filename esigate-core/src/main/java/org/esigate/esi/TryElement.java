package org.esigate.esi;

import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class TryElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:try", "</esi:try") {
		public TryElement newInstance() {
			return new TryElement();
		}

	};

	private boolean condition;
	private boolean hasCondition;
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
