package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.HttpErrorPage;
import org.esigate.parser.BodyTagElement;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;
import org.esigate.vars.VariablesResolver;

public class VarsElement implements BodyTagElement {
	public final static ElementType TYPE = new BaseElementType("<esi:vars", "</esi:vars") {
		public VarsElement newInstance() {
			return new VarsElement();
		}

	};

	private boolean closed = false;
	private HttpServletRequest request;

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
		Tag varsTag = new Tag(tag);
		closed = varsTag.isOpenClosed();
		// out.append("This is var tag");
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

		String result = VariablesResolver.replaceAllVariables(body, request);
		out.append(result);

	}

}
