package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class TryElement implements Element {

	public final static ElementType TYPE = new BaseElementType("<esi:try", "</esi:try") {
		public TryElement newInstance() {
			return new TryElement();
		}

	};

	private boolean closed = false;
	private boolean condition;
	private boolean hasCondition;
	private boolean includeInside;

	public boolean isIncludeInside() {
		return includeInside;
	}

	public void setIncludeInside(boolean includeInside) {
		this.includeInside = includeInside;
	}

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
		Tag tryTag = new Tag(tag);
		closed = tryTag.isOpenClosed();
		condition = false;
		hasCondition = false;
	}

	public ElementType getType() {
		return TYPE;
	}

	public Appendable append(CharSequence csq) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(char c) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		// Just ignore tag body
		return this;
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
