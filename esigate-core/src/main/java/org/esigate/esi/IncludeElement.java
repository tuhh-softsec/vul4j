package org.esigate.esi;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.parser.ElementType;
import org.esigate.parser.Parser;
import org.esigate.parser.ParserContext;
import org.esigate.regexp.ReplaceRenderer;
import org.esigate.vars.VariablesResolver;
import org.esigate.xml.XpathRenderer;
import org.esigate.xml.XsltRenderer;

class IncludeElement extends BaseElement {
	private final static Pattern FRAGMENT_REPLACEMENT_PATTERN = Pattern.compile("(<esi:fragment[^>]*>)|(</esi:fragment[^>]*>)");

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
	private StringBuilder buf;
	private Map<String, CharSequence> fragmentRepacements;
	private Map<String, CharSequence> regexpRepacements;

	IncludeElement() { }

	@Override
	public void characters(CharSequence csq, int start, int end) {
		buf.append(csq, start, end);
	}

	@Override
	public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
		// apply fragment replacements
		if (!fragmentRepacements.isEmpty()) {
			StringBuilder tmp = new StringBuilder(buf.length());
			Parser fragmentReplacementParser = new Parser(FRAGMENT_REPLACEMENT_PATTERN,
					FragmentReplacementElement.createType(fragmentRepacements));
			fragmentReplacementParser.parse(buf, tmp);
			buf = tmp;
		}
		// apply regexp replacements
		if (!regexpRepacements.isEmpty()) {
			for (Entry<String, CharSequence> entry : regexpRepacements.entrySet()) {
				buf = new StringBuilder(Pattern.compile(entry.getKey())
					.matcher(buf)
					.replaceAll(entry.getValue().toString()));
			}
		}

		// TODO Auto-generated method stub
		ctx.getCurrent().characters(buf, 0, buf.length());

		buf = null;
		fragmentRepacements = null;
		regexpRepacements = null;
	}

	@Override
	protected void parseTag(Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
		buf = new StringBuilder();
		fragmentRepacements = new HashMap<String, CharSequence>();
		regexpRepacements = new HashMap<String, CharSequence>();

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
			
		} else {
			int startIdx = idx + "$PROVIDER({".length();
			int endIndex = src.indexOf("})", startIdx);
			String provider = src.substring(startIdx, endIndex);
			page = src.substring(endIndex + "})".length());
			driver = DriverFactory.getInstance(provider);
		}
		
		if(rewriteAbsoluteUrl)
		{
			Map<String, String> replaceRules = new HashMap<String, String>();
			String baseUrl = driver.getConfiguration().getBaseURL();
			String visibleBaseUrl =  driver.getConfiguration().getVisibleBaseURL();
			
			String contextBaseUrl;
			String contextVisibleBaseUrl;
			
			if(visibleBaseUrl != null && !visibleBaseUrl.equals("") && !baseUrl.equals(visibleBaseUrl)){
				contextBaseUrl = new URL(baseUrl).getPath();
				contextVisibleBaseUrl = new URL(visibleBaseUrl).getPath();
				replaceRules.put("href=(\"|')"+ visibleBaseUrl +"(.*)(\"|')", "href=$1"+ contextVisibleBaseUrl +"$2$3");
				replaceRules.put("src=(\"|')"+ visibleBaseUrl +"(.*)(\"|')", "src=$1"+ contextVisibleBaseUrl +"$2$3");
				replaceRules.put("href=(\"|')"+ baseUrl +"(.*)(\"|')", "href=$1"+ contextBaseUrl +"$2$3");
				replaceRules.put("src=(\"|')"+ baseUrl +"(.*)(\"|')", "src=$1"+ contextBaseUrl +"$2$3");
			}
			else{
				contextBaseUrl = new URL(baseUrl).getPath();
				replaceRules.put("href=(\"|')"+ baseUrl +"(.*)(\"|')", "href=$1"+ contextBaseUrl +"$2$3");
				replaceRules.put("src=(\"|')"+ baseUrl +"(.*)(\"|')", "src=$1"+ contextBaseUrl +"$2$3");
			}
				
			rendererList.add(new ReplaceRenderer(replaceRules));
		}
		
		page = VariablesResolver.replaceAllVariables(page, esiRenderer.getRequest());
		InlineCache ic = InlineCache.getFragment(src);
		if (ic != null && !ic.isExpired()) {
			String cache = ic.getFragment();
			characters(cache, 0, cache.length());
		} else {
			if (fragment != null) {
				rendererList.add(new EsiFragmentRenderer(page, fragment));
			} else if (xpath != null) {
				rendererList.add(new XpathRenderer(xpath));
			} else if (xslt != null) {
				// FIXME: need to move local/external resource loading into XsltRenderer
				Renderer xsltRenderer;
				try {
					xsltRenderer = new XsltRenderer(xslt , esiRenderer.getRequest().getSession().getServletContext());
				} catch (Exception e) {
					String currentValue = driver.getResourceAsString(xslt, null, esiRenderer.getRequest(), esiRenderer.getResponse());
					xsltRenderer = new XsltRenderer(currentValue);
				}
				rendererList.add(xsltRenderer);
			}
			rendererList.add(new EsiRenderer(esiRenderer.getRequest(), esiRenderer.getResponse(), driver));
			driver.render(page, parameters, outAdapter, esiRenderer.getRequest(), esiRenderer.getResponse(),
				rendererList.toArray(new Renderer[rendererList.size()]));
		}
	}

	void addFragmentReplacement(String fragment, CharSequence replacement) {
		fragmentRepacements.put(fragment, replacement);
	}

	void addRegexpReplacement(String regexp, CharSequence replacement) {
		regexpRepacements.put(regexp, replacement);
	}

}
