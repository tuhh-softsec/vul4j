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

package org.esigate.esi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.esigate.Driver;
import org.esigate.DriverFactory;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.Renderer;
import org.esigate.http.HttpResponseUtils;
import org.esigate.impl.DriverRequest;
import org.esigate.parser.Adapter;
import org.esigate.parser.ElementType;
import org.esigate.parser.ParserContext;
import org.esigate.regexp.ReplaceRenderer;
import org.esigate.util.UriUtils;
import org.esigate.xml.XpathRenderer;
import org.esigate.xml.XsltRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class IncludeElement extends BaseElement {
    private static final String PROVIDER_PATTERN = "$(PROVIDER{";
    private static final String LEGACY_PROVIDER_PATTERN = "$PROVIDER({";
    private static final Logger LOG = LoggerFactory.getLogger(IncludeElement.class);
    public static final ElementType TYPE = new BaseElementType("<esi:include", "</esi:include") {
        @Override
        public IncludeElement newInstance() {
            return new IncludeElement();
        }

    };

    private final Appendable outAdapter = new Adapter(IncludeElement.this);
    private StringBuilder buf;
    private Map<String, CharSequence> fragmentReplacements;
    private Map<String, CharSequence> regexpReplacements;
    private Tag includeTag;
    private boolean write = false;

    IncludeElement() {
    }

    @Override
    public void characters(CharSequence csq, int start, int end) {
        if (write) {
            buf.append(csq, start, end);
        }
    }

    @Override
    public void onTagEnd(String tag, ParserContext ctx) throws IOException, HttpErrorPage {
        write = true;
        String src = includeTag.getAttribute("src");
        String alt = includeTag.getAttribute("alt");
        boolean ignoreError = "continue".equals(includeTag.getAttribute("onerror"));

        Exception currentException = null;
        // Handle src
        try {
            processPage(src, includeTag, ctx);
        } catch (IOException | HttpErrorPage e) {
            currentException = e;
        }

        // Handle Alt
        if (currentException != null && alt != null) {
            // Reset exception
            currentException = null;
            try {
                processPage(alt, includeTag, ctx);
            } catch (IOException | HttpErrorPage e) {
                currentException = e;
            }
        }

        // Handle onerror
        if (currentException != null && !ignoreError && !ctx.reportError(currentException)) {
            if (currentException instanceof IOException) {
                throw (IOException) currentException;
            } else if (currentException instanceof HttpErrorPage) {
                throw (HttpErrorPage) currentException;
            }
            throw new IllegalStateException(
                    "This type of exception is unexpected here. Should be IOException or HttpErrorPageException.",
                    currentException);

        }

        // apply regexp replacements
        if (!regexpReplacements.isEmpty()) {
            for (Entry<String, CharSequence> entry : regexpReplacements.entrySet()) {
                buf =
                        new StringBuilder(Pattern.compile(entry.getKey()).matcher(buf)
                                .replaceAll(entry.getValue().toString()));
            }
        }

        // write accumulated data into parent
        ctx.getCurrent().characters(buf, 0, buf.length());
    }

    @Override
    protected void parseTag(Tag tag, ParserContext ctx) {
        buf = new StringBuilder(Parameters.DEFAULT_BUFFER_SIZE);
        fragmentReplacements = new HashMap<String, CharSequence>();
        regexpReplacements = new HashMap<String, CharSequence>();
        includeTag = tag;
    }

    private void processPage(String src, Tag tag, ParserContext ctx) throws IOException, HttpErrorPage {
        String fragment = tag.getAttribute("fragment");
        String xpath = tag.getAttribute("xpath");
        String xslt = tag.getAttribute("stylesheet");
        boolean rewriteAbsoluteUrl = "true".equalsIgnoreCase(tag.getAttribute("rewriteabsoluteurl"));

        DriverRequest httpRequest = ctx.getHttpRequest();
        List<Renderer> rendererList = new ArrayList<Renderer>();
        Driver driver;
        String page;

        int idx = src.indexOf(PROVIDER_PATTERN);
        int idxLegacyPattern = src.indexOf(LEGACY_PROVIDER_PATTERN);
        if (idx < 0 && idxLegacyPattern < 0) {
            page = src;
            driver = httpRequest.getDriver();
        } else if (idx >= 0) {

            int startIdx = idx + PROVIDER_PATTERN.length();
            int endIndex = src.indexOf("})", startIdx);
            String provider = src.substring(startIdx, endIndex);
            page = src.substring(endIndex + "})".length());
            driver = DriverFactory.getInstance(provider);
            if (LOG.isWarnEnabled() && idx > 0) {
                LOG.warn(
                        "Invalid src attribute : [{}], src should start with [{}{}})]. First characters [{}] have been ignored",
                        src, PROVIDER_PATTERN, provider, src.substring(0, idx));
            }

        } else {
            int startIdx = idxLegacyPattern + PROVIDER_PATTERN.length();
            int endIndex = src.indexOf("})", startIdx);
            String provider = src.substring(startIdx, endIndex);
            page = src.substring(endIndex + "})".length());
            driver = DriverFactory.getInstance(provider);
            if (LOG.isWarnEnabled() && idxLegacyPattern > 0) {
                LOG.warn(
                        "Invalid src attribute : [{}], src should start with [{}{}})]. First characters [{}] have been ignored",
                        src, PROVIDER_PATTERN, provider, src.substring(0, idxLegacyPattern));
            }
        }

        if (rewriteAbsoluteUrl) {
            Map<String, String> replaceRules = new HashMap<String, String>();
            String baseUrl = httpRequest.getBaseUrl().toString();
            String visibleBaseUrl = driver.getConfiguration().getVisibleBaseURL();

            String contextBaseUrl;
            String contextVisibleBaseUrl;
            contextBaseUrl = UriUtils.getPath(baseUrl);
            if (visibleBaseUrl != null && !visibleBaseUrl.equals("") && !baseUrl.equals(visibleBaseUrl)) {
                contextVisibleBaseUrl = UriUtils.getPath(visibleBaseUrl);
                replaceRules.put("href=(\"|')" + visibleBaseUrl + "(.*)(\"|')", "href=$1" + contextVisibleBaseUrl
                        + "$2$3");
                replaceRules.put("src=(\"|')" + visibleBaseUrl + "(.*)(\"|')", "src=$1" + contextVisibleBaseUrl
                        + "$2$3");
                replaceRules.put("href=(\"|')" + baseUrl + "(.*)(\"|')", "href=$1" + contextBaseUrl + "$2$3");
                replaceRules.put("src=(\"|')" + baseUrl + "(.*)(\"|')", "src=$1" + contextBaseUrl + "$2$3");
            } else {
                contextBaseUrl = UriUtils.getPath(baseUrl);
                replaceRules.put("href=(\"|')" + baseUrl + "(.*)(\"|')", "href=$1" + contextBaseUrl + "$2$3");
                replaceRules.put("src=(\"|')" + baseUrl + "(.*)(\"|')", "src=$1" + contextBaseUrl + "$2$3");
            }

            rendererList.add(new ReplaceRenderer(replaceRules));
        }

        InlineCache ic = InlineCache.getFragment(src);
        if (ic != null && !ic.isExpired()) {
            String cache = ic.getFragment();
            characters(cache, 0, cache.length());
        } else {
            EsiRenderer esiRenderer;
            if (fragment != null) {
                esiRenderer = new EsiRenderer(page, fragment);
            } else {
                esiRenderer = new EsiRenderer();
            }
            if (fragmentReplacements != null && !fragmentReplacements.isEmpty()) {
                esiRenderer.setFragmentsToReplace(fragmentReplacements);
            }
            rendererList.add(esiRenderer);
            if (xpath != null) {
                rendererList.add(new XpathRenderer(xpath));
            } else if (xslt != null) {
                rendererList.add(new XsltRenderer(xslt, driver, httpRequest));
            }
            CloseableHttpResponse response =
                    driver.render(page, httpRequest.getOriginalRequest(),
                            rendererList.toArray(new Renderer[rendererList.size()]));
            outAdapter.append(HttpResponseUtils.toString(response));
        }
    }

    void addFragmentReplacement(String fragment, CharSequence replacement) {
        fragmentReplacements.put(fragment, replacement);
    }

    void addRegexpReplacement(String regexp, CharSequence replacement) {
        regexpReplacements.put(regexp, replacement);
    }

}
