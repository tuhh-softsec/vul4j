package org.esigate.server.metrics;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.annotation.Name;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import com.yammer.metrics.Metrics;
import com.yammer.metrics.core.Counter;
import com.yammer.metrics.core.Meter;
import com.yammer.metrics.core.MetricsRegistry;

public class InstrumentedServerConnector extends ServerConnector {
	private Meter accepts, connects, disconnects;
	private Counter connections;

	/**
	 * Jetty 9 ServerConnector instrumented with Metrics.
	 * 
	 * @param id
	 *            connector id, will be used as prefix for metrics.
	 * @param port
	 *            this port will be set with ServerConnector#setPort().
	 * @param server
	 *            Jetty server.
	 */
	public InstrumentedServerConnector(String id, int port, @Name("server") Server server) {
		super(server);
		instrument(id, port, Metrics.defaultRegistry());
	}

	public InstrumentedServerConnector(String id, int port, @Name("server") Server server,
			@Name("factories") ConnectionFactory... factories) {
		super(server, factories);
		instrument(id, port, Metrics.defaultRegistry());
	}

	public InstrumentedServerConnector(String id, int port, @Name("server") Server server,
			@Name("sslContextFactory") SslContextFactory sslContextFactory) {
		super(server, sslContextFactory);
		instrument(id, port, Metrics.defaultRegistry());
	}

	@Override
	public void accept(int acceptorID) throws IOException {
		super.accept(acceptorID);
		this.accepts.mark();
	}

	@Override
	public void close() {
		super.close();
		this.disconnects.mark();
		this.connections.dec();
	}

	/**
	 * Create metrics.
	 * 
	 * @param id
	 * @param port
	 * @param registry
	 */
	private void instrument(String id, int port, MetricsRegistry registry) {
		this.setPort(port);
		this.accepts = registry.newMeter(InstrumentedServerConnector.class, id + "-accepts",
				Integer.toString(this.getPort()), "connections", TimeUnit.SECONDS);
		this.connects = registry.newMeter(InstrumentedServerConnector.class, id + "-connects",
				Integer.toString(this.getPort()), "connections", TimeUnit.SECONDS);
		this.disconnects = registry.newMeter(InstrumentedServerConnector.class, id + "-disconnects",
				Integer.toString(this.getPort()), "connections", TimeUnit.SECONDS);
		this.connections = registry.newCounter(InstrumentedServerConnector.class, id + "-active-connections",
				Integer.toString(this.getPort()));
	}

	@Override
	public void open() throws IOException {
		this.connections.inc();
		super.open();
		this.connects.mark();
	}
}
