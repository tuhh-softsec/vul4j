package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.HttpErrorPage;
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

	public void doStartTag(String tag, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
		super.doStartTag(tag, out, stack);
		Tag whenTag = Tag.create(tag);
		String test = whenTag.getAttribute("test");
		if (out instanceof ChooseElement) {
			if (test == null) {
				if (tag.indexOf("test") == -1) {
					return;
				}
				test = tag.substring(tag.indexOf('"') + 1, tag.lastIndexOf('"'));
				// whenTag.getAttributes().put("test", test);
				((ChooseElement) out).setCondition(Operations.processOperators(
						VariablesResolver.replaceAllVariables(test, request)));
			}
		}
	}

//	@Override
//	protected void parseTag(Tag tag, Appendable out, ElementStack stack) {
//		String test = tag.getAttribute("test");
//		if (test != null && out instanceof ChooseElement) {
//			// FIXME: [saber] something strange is here
//			((ChooseElement) out).setCondition(Operations.processOperators(
//					VariablesResolver.replaceAllVariables(test, request)));
//		}
//
//	}

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
