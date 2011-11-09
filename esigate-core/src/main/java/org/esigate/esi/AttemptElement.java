package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.HttpErrorPage;
import org.esigate.parser.BodyTagElement;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class AttemptElement implements BodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:attempt", "</esi:attempt") {
		public AttemptElement newInstance() {
			return new AttemptElement();
		}

	};

	private boolean closed = false;

	public void setRequest(HttpServletRequest request) {
		// Not used
	}

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
		Tag attemptTag = new Tag(tag);
		closed = attemptTag.isOpenClosed();
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

	public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {

		// Element e = stack.pop();
		// Appendable parent = stack.getCurrentWriter();
		//
		// if (e instanceof ChooseElement && ((ChooseElement) e).isCondition())
		// {
		// String result = VariablesResolver
		// .replaceAllVariables(body, request);
		// parent.append(result);
		// }
		// stack.push(e);

	}

}
