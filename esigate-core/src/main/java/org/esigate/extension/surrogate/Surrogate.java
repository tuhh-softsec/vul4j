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
import java.util.List;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.esigate.Driver;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.EventManager;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FetchEvent;
import org.esigate.events.impl.ProxyEvent;
import org.esigate.extension.Extension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of Edge-Arch specification 1.0. (Work in progress)
 * 
 * <p>
 * See :
 * <ul>
 * <li>http://www.w3.org/TR/edge-arch</li>
 * <li>http://docs.oracle.com/cd/E17904_01/web.1111/e10143/esi.htm</li>
 * </ul>
 * 
 * <p>
 * This extension allows control of esigate features from the provider
 * application.
 * 
 * <p>
 * This extension cooperates with other extensions to get the currently
 * supported capabilities. To participate to capabilities collection, and
 * extension should register to EVENT_SURROGATE_CAPABILITIES and provide
 * capabilities when the event is fired.
 * <p>
 * 
 * <pre>
 * driver.getEventManager().register(Surrogate.EVENT_SURROGATE_CAPABILITIES, new IEventListener() {
 * 	public boolean event(EventDefinition id, Event event) {
 * 		CapabilitiesEvent capEvent = (CapabilitiesEvent) event;
 * 		capEvent.capabilities.add(&quot;ESI/1.0&quot;);
 * 		capEvent.capabilities.add(&quot;ESI-Inline/1.0&quot;);
 * 		capEvent.capabilities.add(&quot;ESIGATE/4.0&quot;);
 * 		return true;
 * 	}
 * });
 * </pre>
 * <p>
 * The provider may respond with control directive. In that case, the extension
 * should ensure that its capability are requested and do not process response
 * otherwise.
 * <p>
 * 
 * <pre>
 *  if (renderEvent.httpResponse.containsHeader(Surrogate.HEADER_ENABLED_CAPABILITIES)) {
 * 		String capabilities = renderEvent.httpResponse.getFirstHeader(Surrogate.HEADER_ENABLED_CAPABILITIES)
 * 			.getValue();
 * 
 * 		if (!containsIgnoreCase(capabilities, "ESI/1.0") {
 * 		    // Cancel processing
 * 		}
 * 	}
 * </pre>
 * <p>
 * TODO:
 * <ul>
 * <li>Process caching directives</li>
 * <li>Implement targeting</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * 
 */
public class Surrogate implements Extension, IEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(Surrogate.class);

	/**
	 * This is an internal header, used to track the requested capabilities.
	 * <p>
	 * Value is the value of the content directive from the Surrogate-Control
	 * header.
	 */
	public static String HEADER_ENABLED_CAPABILITIES = "X-Esigate-Internal-Enabled-Capabilities";
	/**
	 * This header is used to flag the presence of another Surrogate in front of
	 * esigate.
	 */
	private static final String HEADER_SURROGATE = "X-Esigate-Internal-Surrogate";
	private String[] capabilities;
	private String esigateToken;
	/**
	 * This event is fired on startup to collect all installed capabilities.
	 */
	public static EventDefinition EVENT_SURROGATE_CAPABILITIES = new EventDefinition(
			"org.esigate.surrogate.capabilities", EventDefinition.TYPE_DEFAULT);
	private static String CAP_SURROGATE = "Surrogate/1.0";

	@Override
	public void init(Driver driver, Properties properties) {
		CapabilitiesEvent capEvent = new CapabilitiesEvent();
		capEvent.capabilities.add(CAP_SURROGATE);

		// Build all supported capabilities
		driver.getEventManager().fire(EVENT_SURROGATE_CAPABILITIES, capEvent);
		this.capabilities = capEvent.capabilities.toArray(new String[] {});
		LOG.info("Surrogate capabilities: {}", join(this.capabilities, " "));
		// Build esigate token
		this.esigateToken = "=\"" + join(this.capabilities, " ") + "\"";

		// Register for events.
		driver.getEventManager().register(EventManager.EVENT_FETCH_PRE, this);
		driver.getEventManager().register(EventManager.EVENT_FETCH_POST, this);
		driver.getEventManager().register(EventManager.EVENT_PROXY_PRE, this);
		driver.getEventManager().register(EventManager.EVENT_PROXY_POST, this);

	}

	/**
	 * Return a new token, unique for the current Surrogate-Capability header.
	 * <p>
	 * Uses "esigate" and appends a number if necessary.
	 * 
	 * @param currentCapabilitiesHeader
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
			// Add Surrogate-Capabilities header.
			FetchEvent e = (FetchEvent) event;

			// This header is used internally, and should not be forwarded.
			e.httpRequest.removeHeaders(HEADER_SURROGATE);

			Header h = e.httpRequest.getFirstHeader("Surrogate-Capabilities");

			StringBuilder archCapabilities = new StringBuilder();
			if (h != null && !isEmpty(h.getValue())) {
				archCapabilities.append(defaultString(h.getValue()));
				archCapabilities.append(", ");
			}
			archCapabilities.append(getUniqueToken(h == null ? null : h.getValue()));
			archCapabilities.append(this.esigateToken);
			e.httpRequest.setHeader("Surrogate-Capabilities", archCapabilities.toString());
		} else if (EventManager.EVENT_FETCH_POST.equals(id)) {
			onPostFetch(event);
		} else if (EventManager.EVENT_PROXY_PRE.equals(id)) {
			ProxyEvent e = (ProxyEvent) event;
			if (e.originalRequest.containsHeader("Surrogate-Capabilities")) {
				e.originalRequest.setHeader(HEADER_SURROGATE, "true");
			}
		} else if (EventManager.EVENT_PROXY_POST.equals(id)) {
			// Remove Surrogate Control content
			ProxyEvent e = (ProxyEvent) event;

			if (e.response != null) {
				processSurrogateControlContent(e.response, e.originalRequest.containsHeader(HEADER_SURROGATE));
			} else if (e.errorPage != null) {
				processSurrogateControlContent(e.errorPage.getHttpResponse(),
						e.originalRequest.containsHeader(HEADER_SURROGATE));
			}

		}

		return true;
	}

	/**
	 * Inject HEADER_ENABLED_CAPABILITIES into response.
	 * <p/>
	 * Update caching directives.
	 * 
	 * @param event
	 */
	private void onPostFetch(Event event) {
		// Update caching policies
		FetchEvent e = (FetchEvent) event;

		if (!e.httpResponse.containsHeader("Surrogate-Control"))
			return;

		List<String> enabledCapabilities = new ArrayList<String>();
		List<String> remainingCapabilities = new ArrayList<String>();

		String controlHeader = e.httpResponse.getFirstHeader("Surrogate-Control").getValue();
		String[] control = split(controlHeader, ",");

		for (String directive : control) {
			directive = strip(directive);
			if (directive.startsWith("content=\"")) {
				String[] content = split(directive.substring("content=\"".length(), directive.length() - 1), " ");

				for (String contentCap : content) {
					contentCap = strip(contentCap);
					if (contains(this.capabilities, contentCap)) {
						enabledCapabilities.add(contentCap);
					} else {
						remainingCapabilities.add(contentCap);
					}

				}

			} else if (directive.startsWith("max-age=")) {
				// TODO;
			}

		}

		e.httpResponse.addHeader(HEADER_ENABLED_CAPABILITIES, join(enabledCapabilities, " "));
	}

	/**
	 * Consume capabilities. Does not support targeting yet.
	 * 
	 * @param response
	 */
	private void processSurrogateControlContent(HttpResponse response, boolean keepHeader) {
		if (!response.containsHeader("Surrogate-Control"))
			return;

		if (!keepHeader) {
			response.removeHeaders("Surrogate-Control");
			return;
		}

		String controlHeader = response.getFirstHeader("Surrogate-Control").getValue();
		String[] control = split(controlHeader, ",");
		StringBuilder newControlValue = new StringBuilder();
		boolean first = true;
		for (String directive : control) {
			directive = strip(directive);
			if (directive.startsWith("content=\"")) {
				// Remove esigate capabilities
				StringBuilder newCap = new StringBuilder();
				String[] content = split(directive.substring("content=\"".length(), directive.length() - 1), " ");

				for (String contentCap : content) {
					contentCap = strip(contentCap);
					if (!contains(this.capabilities, contentCap)) {
						newCap.append(contentCap);
						newCap.append(" ");
					}
				}
				// Append new control
				String newCapString = newCap.toString();
				if (!isEmpty(newCapString)) {

					if (!first) {
						newControlValue.append(", ");
					}
					newControlValue.append("content=\"");
					newControlValue.append(strip(newCapString));
					newControlValue.append("\"");
					first = false;
				}

			} else {
				// Other directives.
				if (!first) {
					newControlValue.append(", ");
				}
				newControlValue.append(directive);
				first = false;
			}

		}
		response.setHeader("Surrogate-Control", newControlValue.toString());
	}
}
