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

package org.esigate.impl;

import static org.apache.commons.lang3.StringUtils.stripEnd;
import static org.apache.commons.lang3.StringUtils.stripStart;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.esigate.Parameters;
import org.esigate.util.UriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * "fixes" links to resources, images and pages in pages retrieved by esigate :
 * <ul>
 * <li>Current-path-relative urls are converted to full path relative urls ( img/test.img ->
 * /myapp/curentpath/img/test.img)</li>
 * <li>All relative urls can be converted to absolute urls (including server name)</li>
 * </ul>
 * 
 * This enables use of esigate without any special modifications of the generated urls on the provider side.
 * 
 * All href and src attributes are processed, except javascript links.
 * 
 * @author Nicolas Richeton
 * 
 */
public final class UrlRewriter {
    private static final Logger LOG = LoggerFactory.getLogger(UrlRewriter.class);

    public static final int ABSOLUTE = 0;
    public static final int RELATIVE = 1;

    private static final Pattern URL_PATTERN = Pattern.compile(
            "<([^\\!][^>]+)(src|href|action|background)\\s*=\\s*('[^<']*'|\"[^<\"]*\")([^>]*)>",
            Pattern.CASE_INSENSITIVE);

    private String visibleBaseUrlParameter;
    private int mode;

    /**
     * Creates a renderer which fixes urls. The domain name and the url path are computed from the full url made of
     * baseUrl + pageFullPath.
     * 
     * If mode is ABSOLUTE, all relative urls will be replaced by the full urls :
     * <ul>
     * <li>images/image.png is replaced by http://server/context/images/image.png</li>
     * <li>/context/images/image.png is replaced by http://server/context/images/image.png</li>
     * </ul>
     * 
     * If mode is RELATIVE, context will be added to relative urls :
     * <ul>
     * <li>images/image.png is replaced by /context/images/image.png</li>
     * </ul>
     * 
     * @param properties
     *            Configuration properties
     * 
     */
    public UrlRewriter(Properties properties) {
        if ("absolute".equalsIgnoreCase(Parameters.FIX_MODE.getValue(properties))) {
            mode = ABSOLUTE;
        } else {
            mode = RELATIVE;
        }
        visibleBaseUrlParameter = stripEnd(Parameters.VISIBLE_URL_BASE.getValue(properties), "/");
    }

    private String concatUrl(String begin, String end) {
        return stripEnd(begin, "/") + "/" + stripStart(end, "/");
    }

    /**
     * Fix an url according to the chosen mode.
     * 
     * @param url
     *            the url to fix.
     * @param requestUrl
     *            The request URL.
     * @param baseUrl
     *            The base URL selected for this request.
     * 
     * @return the fixed url.
     */
    public String rewriteUrl(String url, String requestUrl, String baseUrl) {
        if (url.isEmpty()) {
            LOG.debug("skip empty url");
            return url;
        }

        // Store the filename, if specified
        String fileName = null;
        if (!requestUrl.isEmpty() && !requestUrl.endsWith("/")) {
            fileName = requestUrl.substring(requestUrl.lastIndexOf('/') + 1);
        }

        // Build clean URI for further processing
        String cleanBaseUrl = stripEnd(baseUrl, "/");
        String visibleBaseUrl = visibleBaseUrlParameter;
        if (visibleBaseUrl == null) {
            visibleBaseUrl = cleanBaseUrl;
        }
        String visibleBaseUrlPath = UriUtils.getPath(visibleBaseUrl);
        String pagePath = concatUrl(visibleBaseUrlPath, requestUrl);
        if (pagePath != null) {
            int indexSlash = pagePath.lastIndexOf('/');
            if (indexSlash >= 0) {
                pagePath = pagePath.substring(0, indexSlash);
            }
        }

        String result = url;
        if (visibleBaseUrl != null && result.startsWith(cleanBaseUrl)) {
            result = visibleBaseUrl + result.substring(cleanBaseUrl.length());
            LOG.debug("fix absolute url: {} -> {} ", url, result);
            return result;
        }

        // Keep absolute, protocol-absolute and javascript urls untouched.
        if (result.startsWith("http://") || result.startsWith("https://") || result.startsWith("//")
                || result.startsWith("#") || result.startsWith("javascript:")) {
            LOG.debug("keeping absolute url: {}", result);
            return result;
        }

        HttpHost httpHost = UriUtils.extractHost(visibleBaseUrl);
        String server = httpHost.toURI();

        // Add domain to context absolute urls
        if (result.startsWith("/")) {

            // Check if we are going to replace context
            if (cleanBaseUrl != null && !cleanBaseUrl.equals(visibleBaseUrl)) {
                String baseUrlPath = UriUtils.getPath(cleanBaseUrl);
                if (result.startsWith(baseUrlPath)) {
                    result = result.substring(baseUrlPath.length());
                    result = concatUrl(visibleBaseUrlPath, result);
                }
            }

            if (mode == ABSOLUTE) {
                result = server + result;
            }
        } else {

            if (result.charAt(0) == '?' && fileName != null) {
                result = fileName + result;
            }

            // Process relative urls
            if (mode == ABSOLUTE) {
                result = server + pagePath + "/" + result;
            } else {
                result = pagePath + "/" + result;
            }
        }
        result = cleanUpPath(result);
        LOG.debug("url fixed: {} -> {}", url, result);
        return result;
    }

    private final static String REL_PATH = "../";

    /**
     * Cleanup url path to remove ../ when possible
     * 
     * /path/to/a/../page=/path/to/page /path/to/a/../../page=/path/page /path/to/a/../../=/path/
     * 
     */
    protected String cleanUpPath(String path) {

        String result = path;
        if (path.contains(REL_PATH)) {
            int postPro = path.indexOf("//");
            String protocol = "";
            if (postPro != -1) {
                protocol = path.substring(0, postPro + 2);
                result = result.substring(postPro + 2, result.length());
            }
            int nbRelPath = StringUtils.countMatches(result, REL_PATH);
            int nbSlash = StringUtils.countMatches(result, "/");
            // look if we can rewrite
            if (nbSlash - nbRelPath >= nbRelPath) {
                int pos;
                // While url contains ../
                while ((pos = result.indexOf(REL_PATH)) > 0) {
                    // Get url part after ../
                    String lastPart = result.substring(pos + REL_PATH.length(), result.length());
                    // Calculate url part before ../
                    String firstPart = result.substring(0, pos - 1);
                    firstPart = firstPart.substring(0, firstPart.lastIndexOf("/") + 1);
                    result = firstPart + lastPart;
                }
            }
            result = protocol + result;
            if (LOG.isDebugEnabled()) {
                LOG.debug("cleanup url [{}] to [{}]", path, result);
            }
        }
        return result;
    }

    /*
     * 
     * /** Fix all resources urls and return the result.
     * 
     * @param input The original charSequence to be processed.
     * 
     * @param requestUrl The request URL.
     * 
     * @param baseUrlParam The base URL selected for this request.
     * 
     * @return the result of this renderer.
     */
    public CharSequence rewriteHtml(CharSequence input, String requestUrl, String baseUrlParam) {
        StringBuffer result = new StringBuffer(input.length());
        Matcher m = URL_PATTERN.matcher(input);
        while (m.find()) {
            LOG.trace("found match: {}", m);
            String url = input.subSequence(m.start(3) + 1, m.end(3) - 1).toString();
            url = rewriteUrl(url, requestUrl, baseUrlParam);
            url = url.replaceAll("\\$", "\\\\\\$"); // replace '$' -> '\$' as it
                                                    // denotes group
            StringBuffer tagReplacement = new StringBuffer("<$1$2=\"").append(url).append("\"");
            if (m.groupCount() > 3) {
                tagReplacement.append("$4");
            }
            tagReplacement.append('>');
            LOG.trace("replacement: {}", tagReplacement);
            m.appendReplacement(result, tagReplacement.toString());
        }
        m.appendTail(result);

        return result;
    }

}
