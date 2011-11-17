package org.esigate.esi;

import java.io.IOException;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.vars.VariablesResolver;
import org.esigate.xml.XpathRenderer;

class IncludeElement extends BaseElement {

	public final static ElementType TYPE = new BaseElementType("<esi:include", "</esi:include") {
		public IncludeElement newInstance() {
			return new IncludeElement();
		}

	};

	private final Appendable outAdapter = new Appendable() {

		public Appendable append(CharSequence csq, int start, int end) throws IOException {
			IncludeElement.this.characters(csq, start, end);
			return this;
		}

		public Appendable append(char c) throws IOException {
			return append(new StringBuilder(1).append(c), 0, 1);
		}

		public Appendable append(CharSequence csq) throws IOException {
			return append(csq, 0, csq.length());
		}
	};

	IncludeElement() { }

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String src = tag.getAttribute("src");
		String fragment = tag.getAttribute("fragment");
		String xpath = tag.getAttribute("xpath");
		EsiRenderer esiRenderer = ctx.findAncestor(EsiRenderer.class);
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
				String cache = ic.getFragment();
				super.characters(cache, 0, cache.length());
			} else if (fragment != null) {
				driver.render(page, null, outAdapter, esiRenderer.getRequest(), esiRenderer.getResponse(),
						new EsiFragmentRenderer(page, fragment),
						new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			} else if (xpath != null) {
				driver.render(page, null, outAdapter, esiRenderer.getRequest(), esiRenderer.getResponse(),
						new XpathRenderer(xpath),
						new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			} else {
				driver.render(page, null, outAdapter, esiRenderer.getRequest(), esiRenderer.getResponse(),
						new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			}
		} catch (Exception e) {
			if (!ctx.reportError(e)) {
				HttpErrorPage httpErrorPage = new HttpErrorPage(404, "Not found", "The page: " + src + " does not exist");
				httpErrorPage.initCause(e);
				throw httpErrorPage;
			}
		}
	}

}
