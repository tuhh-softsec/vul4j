package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.HttpErrorPage;
import org.esigate.parser.BodyTagElement;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class ExceptElement implements BodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:except", "</esi:except") {
		public ExceptElement newInstance() {
			return new ExceptElement();
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
		Tag exceptTag = Tag.create(tag);
		closed = exceptTag.isOpenClosed();
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

		Element current = stack.peek();
		if (current instanceof TryElement && ((TryElement) current).isIncludeInside()) {
			Element e = stack.pop();
			stack.getCurrentWriter().append(body);
			stack.push(e);
		}

		// Element e1 = stack.pop();
		// if (stack.isEmpty()) {
		// stack.push(e1);
		// return;
		// }
		// if (e1 instanceof ExceptElement) {
		// Element e2 = stack.pop();
		// if (e2 instanceof TryElement) {
		// stack.getCurrentWriter().append(body);
		// }
		// stack.push(e2);
		// }
		// stack.push(e1);

	}

}
