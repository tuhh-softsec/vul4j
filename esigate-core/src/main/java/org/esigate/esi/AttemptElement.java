package org.esigate.esi;

import org.esigate.parser.ElementType;

class AttemptElement extends BaseBodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:attempt", "</esi:attempt") {
		public AttemptElement newInstance() {
			return new AttemptElement();
		}

	};

	AttemptElement() {
		super(TYPE);
	}

	// public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
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
	// }

}
