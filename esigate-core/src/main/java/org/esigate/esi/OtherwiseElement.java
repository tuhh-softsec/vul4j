package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.vars.VariablesResolver;

class OtherwiseElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:otherwise", "</esi:otherwise") {
		public OtherwiseElement newInstance() {
			return new OtherwiseElement();
		}

	};

	private boolean active;
	private StringBuilder buf = new StringBuilder();

	OtherwiseElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		ChooseElement parent = ctx.findAncestor(ChooseElement.class);
		active = (parent != null) && !parent.hadConditionSet();
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
