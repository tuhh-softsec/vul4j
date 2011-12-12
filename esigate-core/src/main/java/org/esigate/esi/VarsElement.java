package org.esigate.esi;

import java.io.IOException;

import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.vars.VariablesResolver;

class VarsElement extends BaseElement {
	public final static ElementType TYPE = new BaseElementType("<esi:vars", "</esi:vars") {
		public VarsElement newInstance() {
			return new VarsElement();
		}

	};

	private StringBuilder buf = new StringBuilder();

	VarsElement() { }

	@Override
	public void characters(CharSequence csq, int start, int end) throws IOException {
		buf.append(csq, start, end);
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String result = VariablesResolver.replaceAllVariables(buf.toString(), ctx.getResourceContext().getOriginalRequest());
		ctx.getCurrent().characters(result, 0 , result.length());
	}

}
