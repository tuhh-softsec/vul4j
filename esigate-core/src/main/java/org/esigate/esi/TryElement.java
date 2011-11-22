package org.esigate.esi;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;

class TryElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:try", "</esi:try") {
		public TryElement newInstance() {
			return new TryElement();
		}

	};

	private boolean hasErrors;
	private boolean exceptProcessed;
	private int errorCode;

	TryElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) {
		this.hasErrors = false;
		this.errorCode = 0;
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public boolean exceptProcessed() {
		return exceptProcessed;
	}

	public void setExceptProcessed(boolean exceptProcessed) {
		this.exceptProcessed = exceptProcessed;
	}

	@Override
	public boolean onError(Exception e, ParserContext ctx) {
		hasErrors = true;
		if(e instanceof HttpErrorPage) {
			errorCode = ((HttpErrorPage) e).getStatusCode();
		}
		return true;
	}

}
