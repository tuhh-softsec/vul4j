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
import java.util.TreeMap;

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

	/**
	 * Human-readable status
	 */
	private static final String URL_STATUS = "/server-status";
	/**
	 * Machine-readable status.
	 * 
	 * <p>
	 * Sample :
	 * 
	 * <pre>
	 * Total Accesses: 157678
	 * Total kBytes: 176421
	 * CPULoad: .0190435
	 * Uptime: 2214828
	 * ReqPerSec: .071192
	 * BytesPerSec: 81.5662
	 * BytesPerReq: 1145.72
	 * BusyWorkers: 1
	 * IdleWorkers: 4
	 * </pre>
	 */
	private static final String URL_STATUS_AUTO = "/server-status?auto";
	private final MetricRegistry registry;

	public ControlHandler(MetricRegistry registry) {
		this.registry = registry;
	}

	private static boolean fromControlConnection(Request serverRequest) {
		return EsigateServer.controlPort == serverRequest.getLocalPort();
	}

	public static void shutdown(int port) {
		Http.POST("http://127.0.0.1:" + port + "/shutdown");
		return;
	}

	public static void status(int port) {
		Http.GET("http://127.0.0.1:" + port + URL_STATUS);
		return;
	}

	@Override
	public void handle(String target, Request serverRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (fromControlConnection(serverRequest)) {
			serverRequest.setHandled(true);

			switch (target) {

			case "/shutdown":
				if ("POST".equals(serverRequest.getMethod())) {
					response.setStatus(HttpServletResponse.SC_OK);
					stopServer();
				}
				break;

			case URL_STATUS:
				if ("GET".equals(serverRequest.getMethod())) {
					response.setStatus(HttpServletResponse.SC_OK);
					try (Writer sos = response.getWriter()) {
						Map<String, String> status = getServerStatus();
						for (String key : status.keySet()) {
							sos.append(key + ": " + status.get(key) + "\n");
						}
					}

				}
				break;

			case URL_STATUS_AUTO:
				response.setStatus(HttpServletResponse.SC_OK);
				try (Writer sos = response.getWriter()) {
					Map<String, String> status = getServerStatus();
					for (String key : status.keySet()) {
						sos.append(key + ": " + status.get(key) + "\n");
					}
				}
				break;

			default:
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				break;
			}

		}

	}

	private Map<String, String> getServerStatus() {
		Map<String, String> result = new TreeMap<>();

		Map<String, Counter> counters = this.registry.getCounters();
		for (Entry<String, Counter> c : counters.entrySet()) {
			result.put(c.getKey(), String.valueOf(c.getValue().getCount()));
		}

		Map<String, Meter> meters = this.registry.getMeters();
		for (Entry<String, Meter> c : meters.entrySet()) {
			result.put(c.getKey(), String.valueOf(c.getValue().getCount()));
		}

		Map<String, Gauge> gauges = this.registry.getGauges();
		for (Entry<String, Gauge> c : gauges.entrySet()) {
			result.put(c.getKey(), String.valueOf(c.getValue().getValue()));
		}

		return result;

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