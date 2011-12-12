package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.vars.Operations;
import org.esigate.vars.VariablesResolver;

class WhenElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:when", "</esi:when") {
		public WhenElement newInstance() {
			return new WhenElement();
		}

	};

	private StringBuilder buf = new StringBuilder();
	private boolean active = false;

	WhenElement() {
	}

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String test = tag.getAttribute("test");
		ChooseElement parent = ctx.findAncestor(ChooseElement.class);
		if (test != null && parent != null) {
			// no other 'when' were active before
			active = !parent.hadConditionSet();
			parent.setCondition(Operations.processOperators(
					VariablesResolver.replaceAllVariables(test, ctx.getResourceContext().getOriginalRequest())));
			active &= parent.isCondition();
		}
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException {
		if (active) {
			String result = VariablesResolver.replaceAllVariables(buf.toString(), ctx.getResourceContext().getOriginalRequest());
			super.characters(result, 0, result.length());
		}
	}

	@Override
	public void characters(CharSequence csq, int start, int end) {
		if (active) {
			buf.append(csq, start, end);
		}
	}
}
