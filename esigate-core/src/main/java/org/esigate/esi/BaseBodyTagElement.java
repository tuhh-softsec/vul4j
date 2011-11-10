package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.HttpErrorPage;
import org.esigate.parser.BodyTagElement;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

abstract class BaseBodyTagElement extends BaseElement implements BodyTagElement {

	protected BaseBodyTagElement(ElementType type) {
		super(type);
	}

	public void setRequest(HttpServletRequest request) {
	}

	public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
	}

}
