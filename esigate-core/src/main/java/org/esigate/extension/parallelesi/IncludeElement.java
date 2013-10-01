/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.extension.parallelesi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.regex.Pattern;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Renderer;
import org.esigate.parser.future.FutureElement;
import org.esigate.parser.future.FutureElementType;
import org.esigate.parser.future.FutureParserContext;
import org.esigate.parser.future.StringBuilderFutureAppendable;
import org.esigate.regexp.ReplaceRenderer;
import org.esigate.util.HttpRequestHelper;
import org.esigate.util.UriUtils;
import org.esigate.vars.VariablesResolver;
import org.esigate.xml.XpathRenderer;
import org.esigate.xml.XsltRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class IncludeElement extends BaseElement {
	private static final String PROVIDER_PATTERN = "$(PROVIDER{";
	private static final String LEGACY_PROVIDER_PATTERN = "$PROVIDER({";

	protected static final Logger LOG = LoggerFactory.getLogger(IncludeElement.class);

	public static class IncludeTask implements Callable<CharSequence> {
		String src;
		String alt;
		FutureParserContext ctx;
		private boolean ignoreError;
		FutureElement current;
		Tag includeTag;
		private Map<String, CharSequence> fragmentReplacements;
		private Map<String, CharSequence> regexpReplacements;

		public IncludeTask(Tag includeTag, String src, String alt, FutureParserContext ctx, FutureElement current,
				boolean ignoreError, Map<String, CharSequence> fragmentReplacements,
				Map<String, CharSequence> regexpReplacements) {
			this.src = src;
			this.alt = alt;
			this.ctx = ctx;
			this.ignoreError = ignoreError;
			this.current = current;
			this.includeTag = includeTag;
			this.fragmentReplacements = fragmentReplacements;
			this.regexpReplacements = regexpReplacements;
		}

		public CharSequence call() throws Exception {
			LOG.debug("Starting include task {}", this.src);
			StringWriter sw = new StringWriter();

			try {
				processPage(this.src, includeTag, this.ctx, sw);
			} catch (IOException e) {
				if (alt != null) {
					processPage(alt, includeTag, ctx, sw);
				} else if (!ignoreError && !ctx.reportError(current, e)) {
					throw e;
				}
			} catch (HttpErrorPage e) {
				if (alt != null) {
					processPage(alt, includeTag, ctx, sw);
				} else if (!ignoreError && !ctx.reportError(current, e)) {
					throw e;
				}
			}
			// apply regexp replacements
			String result = sw.toString();

			if (!regexpReplacements.isEmpty()) {
				for (Entry<String, CharSequence> entry : regexpReplacements.entrySet()) {

					result = Pattern.compile(entry.getKey()).matcher(result).replaceAll(entry.getValue().toString());
				}
			}

			return result;
		}

		void processPage(String src, Tag tag, FutureParserContext ctx, Appendable out) throws IOException,
				HttpErrorPage {
			String fragment = tag.getAttribute("fragment");
			String xpath = tag.getAttribute("xpath");
			String xslt = tag.getAttribute("stylesheet");
			boolean rewriteAbsoluteUrl = "true".equalsIgnoreCase(tag.getAttribute("rewriteabsoluteurl"));

			HttpEntityEnclosingRequest httpRequest = ctx.getHttpRequest();
			List<Renderer> rendererList = new ArrayList<Renderer>();
			Driver driver;
			String page;

			int idx = src.indexOf(PROVIDER_PATTERN);
			int idxLegacyPattern = src.indexOf(LEGACY_PROVIDER_PATTERN);
			if (idx < 0 && idxLegacyPattern < 0) {
				page = src;
				driver = HttpRequestHelper.getDriver(httpRequest);
			} else if (idx >= 0) {
				int startIdx = idx + PROVIDER_PATTERN.length();
				int endIndex = src.indexOf("})", startIdx);
				String provider = src.substring(startIdx, endIndex);
				page = src.substring(endIndex + "})".length());
				driver = DriverFactory.getInstance(provider);
			} else {
				int startIdx = idxLegacyPattern + PROVIDER_PATTERN.length();
				int endIndex = src.indexOf("})", startIdx);
				String provider = src.substring(startIdx, endIndex);
				page = src.substring(endIndex + "})".length());
				driver = DriverFactory.getInstance(provider);
			}

			if (rewriteAbsoluteUrl) {
				Map<String, String> replaceRules = new HashMap<String, String>();
				String baseUrl = HttpRequestHelper.getBaseUrl(httpRequest).toString();
				String visibleBaseUrl = driver.getConfiguration().getVisibleBaseURL(baseUrl);

				String contextBaseUrl;
				String contextVisibleBaseUrl;
				contextBaseUrl = UriUtils.createUri(baseUrl).getPath();
				if (visibleBaseUrl != null && !visibleBaseUrl.equals("") && !baseUrl.equals(visibleBaseUrl)) {
					contextVisibleBaseUrl = UriUtils.createUri(visibleBaseUrl).getPath();
					replaceRules.put("href=(\"|')" + visibleBaseUrl + "(.*)(\"|')", "href=$1" + contextVisibleBaseUrl
							+ "$2$3");
					replaceRules.put("src=(\"|')" + visibleBaseUrl + "(.*)(\"|')", "src=$1" + contextVisibleBaseUrl
							+ "$2$3");
					replaceRules.put("href=(\"|')" + baseUrl + "(.*)(\"|')", "href=$1" + contextBaseUrl + "$2$3");
					replaceRules.put("src=(\"|')" + baseUrl + "(.*)(\"|')", "src=$1" + contextBaseUrl + "$2$3");
				} else {
					contextBaseUrl = UriUtils.createUri(baseUrl).getPath();
					replaceRules.put("href=(\"|')" + baseUrl + "(.*)(\"|')", "href=$1" + contextBaseUrl + "$2$3");
					replaceRules.put("src=(\"|')" + baseUrl + "(.*)(\"|')", "src=$1" + contextBaseUrl + "$2$3");
				}

				rendererList.add(new ReplaceRenderer(replaceRules));
			}

			page = VariablesResolver.replaceAllVariables(page, httpRequest);
			InlineCache ic = InlineCache.getFragment(src);
			if (ic != null && !ic.isExpired()) {
				String cache = ic.getFragment();
				out.append(cache);
			} else {
				EsiRenderer esiRenderer;
				if (fragment != null)
					esiRenderer = new EsiRenderer(page, fragment);
				else
					esiRenderer = new EsiRenderer();
				if (fragmentReplacements != null && !fragmentReplacements.isEmpty())
					esiRenderer.setFragmentsToReplace(fragmentReplacements);
				rendererList.add(esiRenderer);
				if (xpath != null) {
					rendererList.add(new XpathRenderer(xpath));
				} else if (xslt != null) {
					rendererList.add(new XsltRenderer(xslt, driver, httpRequest));
				}
				driver.render(page, null, out, httpRequest, rendererList.toArray(new Renderer[rendererList.size()]));
			}
		}

	}

	public final static FutureElementType TYPE = new BaseElementType("<esi:include", "</esi:include") {
		public IncludeElement newInstance() {
			return new IncludeElement();
		}

	};

	private StringBuilderFutureAppendable buf;
	private Map<String, CharSequence> fragmentReplacements;
	private Map<String, CharSequence> regexpReplacements;
	private Tag includeTag;
	private boolean write = false;

	IncludeElement() {
	}

	@Override
	public void characters(Future<CharSequence> csq) throws IOException {
		if (write)
			buf.enqueueAppend(csq);
	}

	@Override
	public void onTagEnd(String tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		write = true;
		String src = includeTag.getAttribute("src");
		String alt = includeTag.getAttribute("alt");
		boolean ignoreError = "continue".equals(includeTag.getAttribute("onerror"));
		FutureElement current = ctx.getCurrent();
		// write accumulated data into parent
		RunnableFuture result = new FutureTask<CharSequence>(new IncludeTask(includeTag, src, alt, ctx, current,
				ignoreError, fragmentReplacements, regexpReplacements));
		EsiExecutor.run(result);
		ctx.getCurrent().characters(result);
	}

	@Override
	protected void parseTag(Tag tag, FutureParserContext ctx) throws IOException, HttpErrorPage {
		buf = new StringBuilderFutureAppendable();
		fragmentReplacements = new HashMap<String, CharSequence>();
		regexpReplacements = new HashMap<String, CharSequence>();
		includeTag = tag;
	}

	void addFragmentReplacement(String fragment, CharSequence replacement) {
		fragmentReplacements.put(fragment, replacement);
	}

	void addRegexpReplacement(String regexp, CharSequence replacement) {
		regexpReplacements.put(regexp, replacement);
	}

}
