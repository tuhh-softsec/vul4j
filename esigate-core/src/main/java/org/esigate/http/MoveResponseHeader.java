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

package org.esigate.http;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.esigate.events.Event;
import org.esigate.events.EventDefinition;
import org.esigate.events.IEventListener;
import org.esigate.events.impl.FragmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fragment event listener which moves an header to another one.
 * <p>
 * Can only be registered to a fragment event.
 * 
 * @author Nicolas Richeton
 * 
 */
public class MoveResponseHeader implements IEventListener {
	private static final Logger LOG = LoggerFactory.getLogger(MoveResponseHeader.class);

	private final String srcName;
	private final String targetName;

	/**
	 * Move header srcName to targetName.
	 * 
	 * @param srcName
	 *            source header name
	 * @param targetName
	 *            target header name
	 */
	public MoveResponseHeader(String srcName, String targetName) {
		this.srcName = srcName;
		this.targetName = targetName;
	}

	@Override
	public boolean event(EventDefinition id, Event event) {
		FragmentEvent fEvent = (FragmentEvent) event;

		moveHeader(fEvent.httpResponse, this.srcName, this.targetName);

		return true;
	}

	/**
	 * This method can be used directly to move an header.
	 * 
	 * @param response
	 *            HTTP response
	 * @param srcName
	 *            source header name
	 * @param targetName
	 *            target header name
	 */
	public static void moveHeader(HttpResponse response, String srcName, String targetName) {
		if (response.containsHeader(srcName)) {
			LOG.info("Moving header {} to {}", srcName, targetName);

			Header[] headers = response.getHeaders(srcName);
			response.removeHeaders(targetName);
			for (Header h : headers) {
				response.addHeader(targetName, h.getValue());
			}
			response.removeHeaders(srcName);
		}
	}

}
