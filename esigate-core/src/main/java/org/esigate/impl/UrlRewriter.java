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

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.esigate.Parameters;
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
public class UrlRewriter {
    private static final Logger LOG = LoggerFactory.getLogger(UrlRewriter.class);

    public static final int ABSOLUTE = 0;
    public static final int RELATIVE = 1;

    private static final Pattern URL_PATTERN = Pattern
            .compile("<([^\\!:>]+)(src|href|action|background)\\s*=\\s*('[^<']*'|\"[^<\"]*\")([^>]*)>",
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

    /**
     * Fix an url according to the chosen mode.
     * 
     * @param url
     *            the url to fix (can be anything found in an html page, relative, absolute, empty...)
     * @param requestUrl
     *            The relative incoming request URL (relative to visible base url).
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

        // TODO temporary hack
        if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }
        if (visibleBaseUrlParameter != null && !visibleBaseUrlParameter.endsWith("/")) {
            visibleBaseUrlParameter = visibleBaseUrlParameter + "/";
        }

        UriBuilder baseUriBuilder = new UriBuilder(baseUrl);

        // If no visible url base is defined, use base url as visible base url
        UriBuilder visibleBaseUriBuilder;
        if (visibleBaseUrlParameter == null) {
            visibleBaseUriBuilder = baseUriBuilder;
        } else {
            visibleBaseUriBuilder = new UriBuilder(visibleBaseUrlParameter);
        }

        // Build the absolute Uri of the request sent to the backend
        // TODO extract concatenation method
        UriBuilder requestUriBuilder = new UriBuilder(requestUrl);
        requestUriBuilder.setScheme(baseUriBuilder.getScheme());
        requestUriBuilder.setHost(baseUriBuilder.getHost());
        requestUriBuilder.setPort(baseUriBuilder.getPort());
        requestUriBuilder.setPath(baseUriBuilder.getPath(), requestUriBuilder.getPath());

        // Interpret the url relatively to the request url (may be relative)
        UriBuilder uriBuilder = requestUriBuilder.resolve(url);
        // Normalize the path (remove . or .. if possible)
        uriBuilder.normalize();

        // Try to relativize url to base url
        UriBuilder relativeUriBuilder = uriBuilder.relativize(baseUriBuilder);
        // If the url is unchanged do nothing
        if (relativeUriBuilder.equals(uriBuilder)) {
            LOG.debug("url kept unchanged: [{}]", url);
            return url;
        }
        // Else rewrite replacing baseUrl by visibleBaseUrl
        UriBuilder result = visibleBaseUriBuilder.resolve(relativeUriBuilder);
        // If mode relative, remove all the scheme://host:port to keep only a url relative to server root (starts with
        // /)
        if (mode == RELATIVE) {
            result.setScheme(null);
            result.setHost(null);
            result.setPort(-1);
        }
        LOG.debug("url fixed: [{}] -> [{}]", url, result);
        return result.toString();
    }

    /**
     * Fix all resources urls and return the result.
     * 
     * @param input
     *            The original charSequence to be processed.
     * 
     * @param requestUrl
     *            The request URL.
     * 
     * @param baseUrlParam
     *            The base URL selected for this request.
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
