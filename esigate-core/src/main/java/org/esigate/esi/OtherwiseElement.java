package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;
import org.esigate.vars.VariablesResolver;

class OtherwiseElement extends BaseBodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:otherwise", "</esi:otherwise") {
		public OtherwiseElement newInstance() {
			return new OtherwiseElement();
		}

	};

	private HttpServletRequest request;

	OtherwiseElement() {
		super(TYPE);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException {
		Element e = stack.pop();
		Appendable parent = stack.getCurrentWriter();

		if (e instanceof ChooseElement && !((ChooseElement) e).hasCondition()) {
			String result = VariablesResolver.replaceAllVariables(body, request);
			parent.append(result);
		}
		stack.push(e);
	}

}
