package org.esigate.esi;

import javax.servlet.http.HttpServletRequest;

import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

public class InlineElement extends BaseBodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:inline", "</esi:inline") {
		public InlineElement newInstance() {
			return new InlineElement();
		}

	};

	private HttpServletRequest request;
	private String uri;
	private String fetchable;

	InlineElement() {
		super(TYPE);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	protected void parseTag(Tag tag, Appendable parent, ElementStack stack) {
		this.uri = tag.getAttribute("name");
		this.fetchable = tag.getAttribute("fetchable");
	}

	@Override
	public void doAfterBody(String body, Appendable out, ElementStack stack) {
		String originalUrl = request.getRequestURL().toString();
		InlineCache.storeFragment(uri, null, fetchable.indexOf("yes") != -1, originalUrl, body);
	}

}
