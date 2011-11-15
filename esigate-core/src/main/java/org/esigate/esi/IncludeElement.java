package org.esigate.esi;

import java.io.IOException;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;
import org.esigate.vars.VariablesResolver;
import org.esigate.xml.XpathRenderer;

class IncludeElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:include", "</esi:include") {
		public IncludeElement newInstance() {
			return new IncludeElement();
		}

	};

	IncludeElement() {
		super(TYPE);
	}

	@Override
	protected void parseTag(Tag tag, Appendable parent, ElementStack stack) throws IOException, HttpErrorPage {
		String src = tag.getAttribute("src");
		String fragment = tag.getAttribute("fragment");
		String xpath = tag.getAttribute("xpath");
		EsiRenderer esiRenderer = stack.findAncestorWithClass(this, EsiRenderer.class);
		Driver driver;
		String page;
		int idx = src.indexOf("$PROVIDER({");
		if (idx < 0) {
			page = src;
			driver = esiRenderer.getDriver();
		} else {
			int startIdx = idx + "$PROVIDER({".length();
			int endIndex = src.indexOf("})", startIdx);
			String provider = src.substring(startIdx, endIndex);
			page = src.substring(endIndex + "})".length());
			driver = DriverFactory.getInstance(provider);
		}
		page = VariablesResolver.replaceAllVariables(page, esiRenderer.getRequest());
		try {
			InlineCache ic = InlineCache.getFragment(src);
			if (ic != null && !ic.isExpired()) {
				getOut(parent, stack).append(ic.getFragment());
			} else if (fragment != null) {
				driver.render(page, null, getOut(parent, stack), esiRenderer.getRequest(), esiRenderer.getResponse(),
						new EsiFragmentRenderer(page, fragment),
						new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			} else if (xpath != null) {
				driver.render(page, null, getOut(parent, stack), esiRenderer.getRequest(), esiRenderer.getResponse(),
						new XpathRenderer(xpath),
						new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			} else {
				driver.render(page, null, getOut(parent, stack), esiRenderer.getRequest(), esiRenderer.getResponse(),
						new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			}
		} catch (Exception e) {
			TryElement tre = getTryElement(stack);
			if (tre != null) {
				tre.setIncludeInside(true);
			} else {
				HttpErrorPage httpErrorPage = new HttpErrorPage(404, "Not found", "The page: " + src + " does not exist");
				httpErrorPage.initCause(e);
				throw httpErrorPage;
			}
		}
	}

	private TryElement getTryElement(ElementStack stack) {
		TryElement res = null;
		Element e3 = stack.pop();
		if (stack.isEmpty()) {
			stack.push(e3);
			return res;
		}
		Element e1 = stack.pop();
		if (stack.isEmpty()) {
			stack.push(e1);
			stack.push(e3);
			return res;
		}
		if (e1 instanceof AttemptElement) {
			Element e2 = stack.pop();
			if (e2 instanceof TryElement) {
				res = (TryElement) e2;
			}
			stack.push(e2);
		}
		stack.push(e1);
		stack.push(e3);
		return res;
	}

	private Appendable getOut(Appendable out, ElementStack stack) {
		Appendable res = out;
		getTryElement(stack);

		Element e3 = stack.pop();
		if (stack.isEmpty()) {
			stack.push(e3);
			return res;
		}
		Element e1 = stack.pop();
		if (stack.isEmpty()) {
			stack.push(e1);
			stack.push(e3);
			return res;
		}
		if (e1 instanceof AttemptElement) {
			Element e2 = stack.pop();
			if (e2 instanceof TryElement) {
				res = stack.getCurrentWriter();
			}
			stack.push(e2);
		}
		stack.push(e1);
		stack.push(e3);
		return res;
	}

}
