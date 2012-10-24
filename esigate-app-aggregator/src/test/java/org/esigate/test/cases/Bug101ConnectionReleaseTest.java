package org.esigate.test.cases;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import junit.framework.Assert;

import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.api.HttpRequest;
import org.esigate.tags.BlockRenderer;
import org.esigate.test.MockHttpRequest;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bug101ConnectionReleaseTest {
	private static final Logger LOG = LoggerFactory.getLogger(Bug101ConnectionReleaseTest.class);

	private void render(Driver driver, String page) throws IOException {
		StringWriter writer = new StringWriter();
		HttpRequest httpRequest = new MockHttpRequest();
		try {
			driver.render("/esigate-app-aggregated1/" + page, null, writer, httpRequest, null, new BlockRenderer(null, "/esigate-app-aggregated1/" + page));
		} catch (HttpErrorPage e) {
			LOG.info(page + " -> " + e.getStatusCode());
		}
	}

	/**
	 * This method while return immediately if no connections are leaked of will
	 * return after 20 or 30 seconds, (pool timeout)
	 * 
	 * @throws IOException
	 * @throws HttpErrorPage
	 */
	@Test
	public void testConnectionLeak() throws IOException, HttpErrorPage {
		Properties properties = new Properties();
		properties.put(Parameters.MAX_CONNECTIONS_PER_HOST.name, "1");
		properties.put(Parameters.REMOTE_URL_BASE.name, "http://localhost:8080/");
		properties.put(Parameters.SOCKET_TIMEOUT.name, "1000");
		properties.put(Parameters.USE_CACHE.name, "false");

		Driver driver = new Driver("test", properties);

		long start = System.currentTimeMillis();
		render(driver, "error500.jsp");
		render(driver, "error500.jsp");
		render(driver, "error404");
		render(driver, "error404");
		render(driver, "slow.jsp");
		render(driver, "slow.jsp");
		render(driver, "");
		render(driver, "");
		Assert.assertTrue("Connection pool timeout : ressource leaked", System.currentTimeMillis() - start < 3000);
	}

}
