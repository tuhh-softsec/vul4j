package org.esigate.esi;

import java.io.IOException;
import java.util.Date;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.parser.Element;
import org.esigate.parser.ElementStack;
import org.esigate.parser.ElementType;
import org.esigate.vars.VariablesResolver;

public class IncludeElement implements Element {
	public final static ElementType TYPE = new ElementType() {

		public boolean isStartTag(String tag) {
			return tag.startsWith("<esi:include");
		}

		public boolean isEndTag(String tag) {
			return tag.startsWith("</esi:include");
		}

		public Element newInstance() {
			return new IncludeElement();
		}

	};

	private boolean closed = false;

	public boolean isClosed() {
		return closed;
	}

	public void doEndTag(String tag) {
		// Nothing to do
	}

	public void doStartTag(String tag, Appendable out, ElementStack stack) throws IOException, HttpErrorPage {
		Tag includeTag = new Tag(tag);
		String src = includeTag.getAttributes().get("src");
		String fragment = includeTag.getAttributes().get("fragment");
		closed = includeTag.isOpenClosed();
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
			if (ic != null && (ic.getOutdate() == null || ic.getOutdate().after(new Date()))) {
				getOut(out, stack).append(ic.getFragment());
			} else if (fragment != null) {
				driver.render(page, null, getOut(out, stack), esiRenderer.getRequest(), esiRenderer.getResponse(), new EsiFragmentRenderer(page, fragment), new EsiRenderer(esiRenderer.getRequest(),
						esiRenderer.getResponse(), driver));
			} else {
				driver.render(page, null, getOut(out, stack), esiRenderer.getRequest(), esiRenderer.getResponse(), new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			}
		} catch (Exception e) {
			TryElement tre = getTryElement(stack);
			if (tre != null) {
				tre.setIncludeInside(true);
			} else {
				throw new HttpErrorPage(404, "Not found", "The page: " + src + " does not exist");
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

	public ElementType getType() {
		return TYPE;
	}

	public Appendable append(CharSequence csq) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(char c) throws IOException {
		// Just ignore tag body
		return this;
	}

	public Appendable append(CharSequence csq, int start, int end) throws IOException {
		// Just ignore tag body
		return this;
	}

}
