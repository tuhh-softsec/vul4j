package org.esigate.esi;

import java.io.IOException;

import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;

class ExceptElement extends BaseBodyTagElement {

	public final static ElementType TYPE = new BaseElementType("<esi:except", "</esi:except") {
		public ExceptElement newInstance() {
			return new ExceptElement();
		}

	};

	ExceptElement() {
		super(TYPE);
	}

	@Override
	public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException {

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
