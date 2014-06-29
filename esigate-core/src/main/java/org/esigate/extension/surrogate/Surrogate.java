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
package org.esigate.extension.surrogate;

import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.strip;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.esigate.Driver;
import org.esigate.Parameters;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FetchEvent;
import org.esigate.events.impl.FragmentEvent;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.extension.Extension;
import org.esigate.extension.parallelesi.Esi;
import org.esigate.extension.surrogate.http.Capability;
import org.esigate.extension.surrogate.http.SurrogateCapabilities;
import org.esigate.extension.surrogate.http.SurrogateCapabilitiesHeader;
import org.esigate.http.DeleteResponseHeader;
import org.esigate.http.MoveResponseHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Edge-Arch specification 1.0.
 * 
 * <p>
 * See :
 * <ul>
 * <li>http://www.w3.org/TR/edge-arch</li>
 * <li>http://docs.oracle.com/cd/E17904_01/web.1111/e10143/esi.htm</li>
 * </ul>
 * 
 * <p>
 * This extension allows control of esigate features from the provider application.
 * 
 * <p>
 * This extension cooperates with other extensions to get the currently supported capabilities. To participate to
 * capabilities collection, and extension should register to EVENT_SURROGATE_CAPABILITIES and provide capabilities when
 * the event is fired.
 * <p>
 * 
 * <pre>
 * driver.getEventManager().register(Surrogate.EVENT_SURROGATE_CAPABILITIES, new IEventListener() {
 *     public boolean event(EventDefinition id, Event event) {
 *         CapabilitiesEvent capEvent = (CapabilitiesEvent) event;
 *         capEvent.capabilities.add(&quot;ESI/1.0&quot;);
 *         capEvent.capabilities.add(&quot;ESI-Inline/1.0&quot;);
 *         capEvent.capabilities.add(&quot;ESIGATE/4.0&quot;);
 *         return true;
 *     }
 * });
 * </pre>
 * <p>
 * The provider may respond with control directive. In that case, the extension should ensure that its capability are
 * requested and do not process response otherwise.
 * <p>
 * 
 * <pre>
 *  if (renderEvent.httpResponse.containsHeader(Surrogate.H_X_ENABLED_CAPABILITIES)) {
 *      String capabilities = renderEvent.httpResponse.getFirstHeader(Surrogate.H_X_ENABLED_CAPABILITIES).getValue();
 * 
 *      if (!containsIgnoreCase(capabilities, "ESI/1.0") {
 *          // Cancel processing
 *      }
 *  }
 * </pre>
 * 
 * <p>
 * Targeting is supported.
 * 
 * <p>
 * NOTE:
 * <ul>
 * <li>no-store-remote is not honored since esigate is generally not used as a CDN.</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * 
 */
public class Surrogate implements Extension, IEventListener {

    private static final String H_SURROGATE_CONTROL = "Surrogate-Control";

    private static final String H_SURROGATE_CAPABILITIES = "Surrogate-Capabilities";

    private static final Logger LOG = LoggerFactory.getLogger(Surrogate.class);

    /**
     * This is an internal header, used to track the requested capabilities.
     * <p>
     * Value is the value of the content directive from the Surrogate-Control header.
     */
    public static final String H_X_ENABLED_CAPABILITIES = "X-Esigate-Internal-Enabled-Capabilities";
    /**
     * This header is used to flag the presence of another Surrogate in front of esigate.
     */
    private static final String H_X_SURROGATE = "X-Esigate-Internal-Surrogate";
    private static final String H_X_ORIGINAL_CACHE_CONTROL = "X-Esigate-Int-Surrogate-OCC";
    /**
     * This header is used to store the esigate instance id for the current request.
     */
    private static final String H_X_SURROGATE_ID = "X-Esigate-Int-Surrogate-Id";
    /**
     * This is an internal header used to store the value of the Surrogate-Control header which will be set to the next
     * surrogate. This is based on the original header, but with the removal of Capabilities processed on the current
     * hop.
     */
    private static final String H_X_NEXT_SURROGATE_CONTROL = "X-Esigate-Int-Surrogate-NSC";
    private String[] capabilities;
    /**
     * Pre-generated esigate token including all capabilities reported by extensions.
     */
    private String esigateToken;
    /**
     * This event is fired on startup to collect all installed capabilities.
     */
    public static final EventDefinition EVENT_SURROGATE_CAPABILITIES = new EventDefinition(
            "org.esigate.surrogate.capabilities", EventDefinition.TYPE_DEFAULT);
    private static final String CAP_SURROGATE = "Surrogate/1.0";

    @Override
    public void init(Driver driver, Properties properties) {
        CapabilitiesEvent capEvent = new CapabilitiesEvent();
        capEvent.getCapabilities().add(CAP_SURROGATE);

        // Build all supported capabilities
        driver.getEventManager().fire(EVENT_SURROGATE_CAPABILITIES, capEvent);
        this.capabilities = capEvent.getCapabilities().toArray(new String[] {});
        LOG.info("Surrogate capabilities: {}", join(this.capabilities, " "));

        // Build esigate token
        this.esigateToken = "=\"" + join(this.capabilities, " ") + "\"";

        // Register for events.
        driver.getEventManager().register(EventManager.EVENT_FETCH_PRE, this);
        driver.getEventManager().register(EventManager.EVENT_FETCH_POST, this);
        driver.getEventManager().register(EventManager.EVENT_PROXY_PRE, this);
        driver.getEventManager().register(EventManager.EVENT_PROXY_POST, this);
        driver.getEventManager().register(EventManager.EVENT_FRAGMENT_PRE, this);

        // Restore original Cache-Control header
        driver.getEventManager().register(EventManager.EVENT_FRAGMENT_POST,
                new MoveResponseHeader(H_X_ORIGINAL_CACHE_CONTROL, "Cache-Control"));

        // Delete internal header
        driver.getEventManager().register(EventManager.EVENT_PROXY_POST,
                new DeleteResponseHeader(H_X_ENABLED_CAPABILITIES));

    }

    /**
     * Return a new token, unique for the current Surrogate-Capability header.
     * <p>
     * Uses "esigate" and appends a number if necessary.
     * 
     * @param currentCapabilitiesHeader
     *            existing header which may contains tokens of other proxies (including other esigate instances).
     * @return unique token
     */
    private static String getUniqueToken(String currentCapabilitiesHeader) {
        String token = "esigate";

        if (currentCapabilitiesHeader != null && currentCapabilitiesHeader.contains(token + "=\"")) {
            int id = 2;
            while (currentCapabilitiesHeader.contains(token + id + "=\"")) {
                id++;
            }
            token = token + id;
        }

        return token;
    }

    @Override
    public boolean event(EventDefinition id, Event event) {

        if (EventManager.EVENT_FETCH_PRE.equals(id)) {
            FetchEvent e = (FetchEvent) event;

            // This header is used internally, and should not be forwarded.
            e.getHttpRequest().removeHeaders(H_X_SURROGATE);
        } else if (EventManager.EVENT_FETCH_POST.equals(id)) {
            onPostFetch(event);
        } else if (EventManager.EVENT_FRAGMENT_PRE.equals(id)) {

            // Add Surrogate-Capabilities or append to existing header.
            FragmentEvent e = (FragmentEvent) event;
            Header h = e.getHttpRequest().getFirstHeader(H_SURROGATE_CAPABILITIES);

            StringBuilder archCapabilities = new StringBuilder(Parameters.SMALL_BUFFER_SIZE);
            if (h != null && !isEmpty(h.getValue())) {
                archCapabilities.append(defaultString(h.getValue()));
                archCapabilities.append(", ");
            }

            String uniqueId = getUniqueToken(h == null ? null : h.getValue());
            e.getHttpRequest().setHeader(H_X_SURROGATE_ID, uniqueId);
            archCapabilities.append(uniqueId);
            archCapabilities.append(this.esigateToken);
            e.getHttpRequest().setHeader(H_SURROGATE_CAPABILITIES, archCapabilities.toString());

        } else if (EventManager.EVENT_PROXY_PRE.equals(id)) {
            ProxyEvent e = (ProxyEvent) event;
            // Do we have another surrogate in front of esigate
            if (e.getOriginalRequest().containsHeader(H_SURROGATE_CAPABILITIES)) {
                e.getOriginalRequest().setHeader(H_X_SURROGATE, "true");
            }
        } else if (EventManager.EVENT_PROXY_POST.equals(id)) {
            // Remove Surrogate Control content
            ProxyEvent e = (ProxyEvent) event;

            if (e.getResponse() != null) {
                processSurrogateControlContent(e.getResponse(), e.getOriginalRequest().containsHeader(H_X_SURROGATE));
                removeVarySurrogateCapabilities(e.getResponse());
            } else if (e.getErrorPage() != null) {
                processSurrogateControlContent(e.getErrorPage().getHttpResponse(), e.getOriginalRequest()
                        .containsHeader(H_X_SURROGATE));
                removeVarySurrogateCapabilities(e.getErrorPage().getHttpResponse());
            }

        }

        return true;
    }

    private void removeVarySurrogateCapabilities(HttpResponse response) {
        // Remove Vary: Surrogate-Capabilities
        Header[] varyHeaders = response.getHeaders("Vary");
        if (varyHeaders != null) {
            for (Header h : varyHeaders) {
                if (H_SURROGATE_CAPABILITIES.equals(h.getValue())) {
                    response.removeHeader(h);
                    break;
                }
            }
        }
    }

    /**
     * <ul>
     * <li>Inject H_X_ENABLED_CAPABILITIES into response.</li>
     * <li>Consume capabilities. Does not support targeting yet.</li>
     * <li>Update caching directives.</li>
     * </ul>
     * 
     * @param event
     *            Incoming fetch event.
     */
    private void onPostFetch(Event event) {
        // Update caching policies
        FetchEvent e = (FetchEvent) event;

        String ourSurrogateId = e.getHttpRequest().getFirstHeader(H_X_SURROGATE_ID).getValue();

        SurrogateCapabilitiesHeader surrogateCapabilitiesHeader = SurrogateCapabilitiesHeader.fromHeaderValue(e
                .getHttpRequest().getFirstHeader(H_SURROGATE_CAPABILITIES).getValue());

        if (!e.getHttpResponse().containsHeader(H_SURROGATE_CONTROL)
                && surrogateCapabilitiesHeader.getSurrogates().size() > 1) {

            // Ensure another proxy can process the request

            LinkedHashMap<String, List<String>> targetCapabilities = new LinkedHashMap<String, List<String>>();
            initSurrogateMap(targetCapabilities, surrogateCapabilitiesHeader);
            for (String c : this.capabilities) {

                // Ignore Surrogate/1.0
                if ("Surrogate/1.0".equals(c)) {
                    continue;
                }

                String firstSurrogate = getFirstSurrogateFor(surrogateCapabilitiesHeader, c);

                // firstSurrogate cannot be null since we are the last surrogate.
                targetCapabilities.get(firstSurrogate).add(c);
            }

            fixSurrogateMap(targetCapabilities, ourSurrogateId);

            StringBuilder sb = new StringBuilder();
            boolean firstDevice = true;
            for (String device : targetCapabilities.keySet()) {
                if (targetCapabilities.get(device).size() == 0) {
                    continue;
                }

                if (!firstDevice) {
                    sb.append(", ");
                } else {
                    firstDevice = false;
                }

                sb.append("content=\"");
                boolean firstCap = true;
                for (String cap : targetCapabilities.get(device)) {
                    if (!firstCap) {
                        sb.append(" ");
                    } else {
                        firstCap = false;

                    }
                    sb.append(cap);

                }
                sb.append("\";");
                sb.append(device);

            }

            e.getHttpResponse().addHeader(H_SURROGATE_CONTROL, sb.toString());
        }

        if (!e.getHttpResponse().containsHeader(H_SURROGATE_CONTROL)) {
            return;
        }

        // If there is a Surrogate-Control header, add a Vary header to ensure content is not reuse when using a
        // different set of Surrogates
        e.getHttpResponse().addHeader("Vary", H_SURROGATE_CAPABILITIES);

        List<String> enabledCapabilities = new ArrayList<String>();
        List<String> remainingCapabilities = new ArrayList<String>();
        List<String> newSurrogateControlL = new ArrayList<String>();
        List<String> newCacheContent = new ArrayList<String>();

        String controlHeader = e.getHttpResponse().getFirstHeader(H_SURROGATE_CONTROL).getValue();
        String[] control = split(controlHeader, ",");

        for (String directiveAndTarget : control) {
            String directive = strip(directiveAndTarget);

            // Is directive targeted
            int targetIndex = directive.lastIndexOf(';');
            String target = null;

            if (targetIndex > 0) {
                target = directive.substring(targetIndex + 1);
                directive = directive.substring(0, targetIndex);
            }

            if (target != null && !target.equals(ourSurrogateId)) {
                // If directive is not targeted to current instance.
                newSurrogateControlL.add(strip(directiveAndTarget));

            } else if (directive.startsWith("content=\"")) {
                // Handle content

                String[] content = split(directive.substring("content=\"".length(), directive.length() - 1), " ");

                for (String contentCap : content) {
                    contentCap = strip(contentCap);
                    if (contains(this.capabilities, contentCap)) {
                        enabledCapabilities.add(contentCap);
                    } else {
                        remainingCapabilities.add(contentCap);
                    }
                }
                if (remainingCapabilities.size() > 0) {
                    newSurrogateControlL.add("content=\"" + join(remainingCapabilities, " ") + "\"");
                }
            } else if (directive.startsWith("max-age=")) {
                String[] maxAge = split(directive, "+");
                newCacheContent.add(maxAge[0]);
                // Freshness extension
                if (maxAge.length > 1) {
                    newCacheContent.add("stale-while-revalidate=" + maxAge[1]);
                    newCacheContent.add("stale-if-error=" + maxAge[1]);
                }

                newSurrogateControlL.add(directive);
            } else if (directive.startsWith("no-store")) {
                newSurrogateControlL.add(directive);
                newCacheContent.add(directive);
            } else {
                newSurrogateControlL.add(directive);
            }
        }

        e.getHttpResponse().setHeader(H_X_ENABLED_CAPABILITIES, join(enabledCapabilities, " "));
        e.getHttpResponse().setHeader(H_X_NEXT_SURROGATE_CONTROL, join(newSurrogateControlL, ", "));

        // If cache control must be updated.
        if (newCacheContent.size() > 0) {
            MoveResponseHeader.moveHeader(e.getHttpResponse(), "Cache-Control", H_X_ORIGINAL_CACHE_CONTROL);
            e.getHttpResponse().setHeader("Cache-Control", join(newCacheContent, ", "));
        }
    }

    /**
     * The current implementation of ESI cannot execute rules partially. For instance if ESI-Inline is requested, ESI,
     * ESI-Inline, X-ESI-Fragment are executed.
     * 
     * <p>
     * This method handles this specific case : if one requested capability enables the Esi extension in this instance,
     * all other capabilities are moved to this instance. This prevents broken behavior.
     * 
     * 
     * @see Esi
     * @see org.esigate.extension.Esi
     * 
     * @param targetCapabilities
     * @param currentSurrogate
     *            the current surrogate id.
     */
    private void fixSurrogateMap(LinkedHashMap<String, List<String>> targetCapabilities, String currentSurrogate) {
        boolean esiEnabledInEsigate = false;

        // Check if Esigate will perform ESI.
        for (String c : Esi.CAPABILITIES) {
            if (targetCapabilities.get(currentSurrogate).contains(c)) {
                esiEnabledInEsigate = true;
                break;
            }
        }

        if (esiEnabledInEsigate) {
            // Ensure all Esi capabilities are executed by our instance.
            for (String c : Esi.CAPABILITIES) {
                for (String device : targetCapabilities.keySet()) {
                    if (device.equals(currentSurrogate)) {
                        if (!targetCapabilities.get(device).contains(c)) {
                            targetCapabilities.get(device).add(c);
                        }
                    } else {
                        targetCapabilities.get(device).remove(c);
                    }

                }
            }

        }

    }

   
    
    /**
     * Populate the Map with all current devices, with empty capabilities.
     * 
     * @param targetCapabilities
     * @param surrogateCapabilitiesHeader
     */
    private void initSurrogateMap(Map<String, List<String>> targetCapabilities,
            SurrogateCapabilitiesHeader surrogateCapabilitiesHeader) {

        for (SurrogateCapabilities sc : surrogateCapabilitiesHeader.getSurrogates()) {
            targetCapabilities.put(sc.getDeviceToken(), new ArrayList<String>());
        }
    }

    /**
     * Returns the first surrogate which supports the requested capability.
     * 
     * @param surrogateCapabilitiesHeader
     * @param capability
     * @return a Surrogate or null if the capability is not found.
     */
    private String getFirstSurrogateFor(SurrogateCapabilitiesHeader surrogateCapabilitiesHeader, String capability) {
        for (SurrogateCapabilities surrogate : surrogateCapabilitiesHeader.getSurrogates()) {

            for (Capability sc : surrogate.getCapabilities()) {
                if (capability.equals(sc.toString())) {
                    return surrogate.getDeviceToken();
                }
            }
        }
        return null;
    }

    /**
     * Remove Surrogate-Control header or replace by its new value.
     * 
     * @param response
     *            backend HTTP response.
     * @param keepHeader
     *            should the Surrogate-Control header be forwarded to the client.
     */
    private static void processSurrogateControlContent(HttpResponse response, boolean keepHeader) {
        if (!response.containsHeader(H_SURROGATE_CONTROL)) {
            return;
        }

        if (!keepHeader) {
            response.removeHeaders(H_SURROGATE_CONTROL);
            return;
        }

        MoveResponseHeader.moveHeader(response, H_X_NEXT_SURROGATE_CONTROL, H_SURROGATE_CONTROL);
    }
}
