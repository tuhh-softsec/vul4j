package org.esigate.esi;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;
import org.esigate.vars.VariablesResolver;

public class VarsElement extends BaseBodyTagElement {
	public final static ElementType TYPE = new BaseElementType("<esi:vars", "</esi:vars") {
		public VarsElement newInstance() {
			return new VarsElement();
		}

	};

	private HttpServletRequest request;

	VarsElement() {
		super(TYPE);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void doAfterBody(String body, Appendable out, ElementStack stack) throws IOException {
		String result = VariablesResolver.replaceAllVariables(body, request);
		out.append(result);
	}

}
