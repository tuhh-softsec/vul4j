package org.esigate.esi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.regexp.ReplaceRenderer;
import org.esigate.vars.VariablesResolver;
import org.esigate.xml.XpathRenderer;
import org.esigate.xml.XsltRenderer;

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
		String alt = tag.getAttribute("alt");
		
		boolean ignoreError = "continue".equals(tag.getAttribute("onerror"));
		try {
			try {
				processPage(src, tag, ctx);
			} catch (Exception e) {
				if (alt != null) {
					processPage(alt, tag, ctx);
				} else {
					throw e;
				}
			}
		} catch (Exception e) {
			if (!ignoreError && !ctx.reportError(e)) {
				HttpErrorPage httpErrorPage = new HttpErrorPage(404, "Not found", "The page: " + src + " does not exist");
				httpErrorPage.initCause(e);
				throw httpErrorPage;
			}
		}
	}

	void processPage(String src, Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		String fragment = tag.getAttribute("fragment");
		String xpath = tag.getAttribute("xpath");
		String xslt = tag.getAttribute("stylesheet");
		boolean rewriteAbsoluteUrl = "true".equalsIgnoreCase(tag.getAttribute("rewriteabsoluteurl"));
		
		Map<String, String> parameters = null;
		List<Renderer> rendererList = new ArrayList<Renderer>();
		EsiRenderer esiRenderer = ctx.findAncestor(EsiRenderer.class);
		Driver driver;
		String page;
			
		int idx = src.indexOf("$PROVIDER({");
		if (idx < 0) {
			page = src;
			driver = esiRenderer.getDriver();
			if (rewriteAbsoluteUrl) {
				Map<String, String> replaceRules = new HashMap<String, String>();
				replaceRules.put("href=(\"|')(http://.*?/)(.*)(\"|')", "href=$1/$3$4");
				replaceRules.put("src=(\"|')(http://.*?/)(.*)(\"|')", "src=$1/$3$4");
				rendererList.add(new ReplaceRenderer(replaceRules));
			}
		} else {
			int startIdx = idx + "$PROVIDER({".length();
			int endIndex = src.indexOf("})", startIdx);
			String provider = src.substring(startIdx, endIndex);
			page = src.substring(endIndex + "})".length());
			driver = DriverFactory.getInstance(provider);
		}
		page = VariablesResolver.replaceAllVariables(page, esiRenderer.getRequest());
		InlineCache ic = InlineCache.getFragment(src);
		if (ic != null && !ic.isExpired()) {
			String cache = ic.getFragment();
			super.characters(cache, 0, cache.length());
		} else {
			if (fragment != null) {
				rendererList.add(new EsiFragmentRenderer(page, fragment));
			} else if (xpath != null) {
				rendererList.add(new XpathRenderer(xpath));
			} else if (xslt != null) {
				try {
					rendererList.add(new XsltRenderer(xslt , esiRenderer.getRequest().getSession().getServletContext()));		
				} catch (Exception e) {
					String currentValue = driver.getResourceAsString(xslt, null, esiRenderer.getRequest(), esiRenderer.getResponse());
					rendererList.add(new XsltRenderer(currentValue));
				}
			}
			rendererList.add(new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			driver.render(page, parameters, outAdapter, esiRenderer.getRequest(), esiRenderer.getResponse(),
				rendererList.toArray(new Renderer[rendererList.size()]));
		}
	}

}
