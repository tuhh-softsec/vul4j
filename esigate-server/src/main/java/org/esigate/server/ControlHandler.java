package org.esigate.server;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Gauge;
import com.yammer.metrics.core.Histogram;
import com.yammer.metrics.core.Metered;
import com.yammer.metrics.core.Metric;
import com.yammer.metrics.core.MetricName;
import com.yammer.metrics.core.MetricProcessor;
import com.yammer.metrics.core.Timer;

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

	private static boolean ensureCommand(Request serverRequest) {
		return "POST".equals(serverRequest.getMethod());
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

					final MetricProcessor<Writer> p = new MetricProcessor<Writer>() {

						@Override
						public void processCounter(MetricName name, Counter counter, Writer context) throws Exception {
							context.append(name.getName() + ": " + counter.count() + "\n");
						}

						@Override
						public void processGauge(MetricName name, Gauge gauge, Writer context) throws Exception {
							context.append(name.getName() + ": " + gauge.value() + "\n");
						}

						@Override
						public void processHistogram(MetricName name, Histogram histogram, Writer context)
								throws Exception {
							// TODO Auto-generated method stub

						}

						@Override
						public void processMeter(MetricName name, Metered meter, Writer context) throws Exception {
							context.append(name.getName() + ": " + meter.oneMinuteRate() + " per min\n");
						}

						@Override
						public void processTimer(MetricName name, Timer timer, Writer context) throws Exception {
							// TODO Auto-generated method stub

						}

					};
					Writer sos = response.getWriter();
					Map<MetricName, Metric> a = Metrics.defaultRegistry().allMetrics();
					for (MetricName name : a.keySet()) {

						Metric metric = a.get(name);
						try {
							metric.processWith(p, name, sos);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

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