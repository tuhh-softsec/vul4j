package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;
import org.esigate.vars.Operations;
import org.esigate.vars.VariablesResolver;

public class WhenElement extends BaseBodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:when", "</esi:when") {
		public WhenElement newInstance() {
			return new WhenElement();
		}

	};

	private HttpServletRequest request;

	WhenElement() {
		super(TYPE);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	protected void parseTag(Tag tag, Appendable out, ElementStack stack) {
		String test = tag.getAttribute("test");
		if (test != null && out instanceof ChooseElement) {
			((ChooseElement) out).setCondition(Operations.processOperators(
					VariablesResolver.replaceAllVariables(test, request)));
		}

	}

	@Override
	public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException {
		Element e = stack.pop();
		Appendable parent = stack.getCurrentWriter();

		if (e instanceof ChooseElement && ((ChooseElement) e).isCondition()) {
			String result = VariablesResolver.replaceAllVariables(body, request);
			parent.append(result);
		}
		stack.push(e);

		// Element parent = stack.peek();
		// if (parent instanceof ChooseElement
		// && ((ChooseElement) parent).isCondition()) {
		// String result = VariablesResolver
		// .replaceAllVariables(body, request);
		// out.append(result);
		// }
	}

}
