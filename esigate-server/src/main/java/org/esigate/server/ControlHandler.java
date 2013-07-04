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

package org.esigate.server;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;

/**
 * Handle commands to the control port. Work in progress.
 * 
 * <p>
 * Commands
 * <ul>
 * <li>POST /shutdown</li>
 * <li>POST /status</li>
 * </ul>
 * 
 * @author Nicolas Richeton
 * 
 */
public class ControlHandler extends AbstractHandler {

	private final MetricRegistry registry;

	public ControlHandler(MetricRegistry registry) {
		this.registry = registry;
	}

	private static boolean ensureCommand(Request serverRequest) {
		return "GET".equals(serverRequest.getMethod());
	}

	private static boolean fromControlConnection(Request serverRequest) {
		return EsigateServer.controlPort == serverRequest.getLocalPort();
	}

	public static void shutdown(int port) {
		Http.POST("http://127.0.0.1:" + port + "/shutdown");
		return;
	}

	public static void status(int port) {
		Http.POST("http://127.0.0.1:" + port + "/status");
		return;
	}

	@Override
	public void handle(String target, Request serverRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (fromControlConnection(serverRequest)) {
			serverRequest.setHandled(true);

			if (ensureCommand(serverRequest)) {
				switch (target) {

				case "/shutdown":
					response.setStatus(HttpServletResponse.SC_OK);
					stopServer();
					break;

				case "/status":
					response.setStatus(HttpServletResponse.SC_OK);

					Writer sos = response.getWriter();
					Map<String, Counter> counters = registry.getCounters();
					for (Entry<String, Counter> c : counters.entrySet()) {
						sos.append(c.getKey() + " " + c.getValue().getCount() + "\n");
					}

					Map<String, Meter> meters = registry.getMeters();
					for (Entry<String, Meter> c : meters.entrySet()) {
						sos.append(c.getKey() + " " + c.getValue().getCount() + "\n");
					}

					Map<String, Gauge> gauges = registry.getGauges();
					for (Entry<String, Gauge> c : gauges.entrySet()) {
						sos.append(c.getKey() + " " + c.getValue().getValue() + "\n");
					}

					break;

				default:
					response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
					break;
				}

			}

		}
	}

	/**
	 * Start a new thread to shutdown the server
	 */
	private void stopServer() {
		// Get current server
		final Server targetServer = this.getServer();

		// Start a new thread in order to escape the destruction of this Handler
		// during the stop process.
		new Thread() {
			@Override
			public void run() {
				try {
					targetServer.stop();
				} catch (Exception e) {
					// ignore
				}
			}
		}.start();

	}
}