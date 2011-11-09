package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.HttpErrorPage;
import org.esigate.parser.BodyTagElement;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class InlineElement implements BodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:inline", "</esi:inline") {
		public InlineElement newInstance() {
			return new InlineElement();
		}

	};

	private boolean closed = false;
	private HttpServletRequest request;
	private String uri;
	private String fetchable;

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
		Tag inlineTag = new Tag(tag);
		closed = inlineTag.isOpenClosed();
		this.uri = inlineTag.getAttributes().get("name");
		this.fetchable = inlineTag.getAttributes().get("fetchable");
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

		String originalUrl = request.getRequestURL().toString();

		InlineCache.storeFragment(uri, null, fetchable.indexOf("yes") != -1, originalUrl, body);
	}

}
