package org.esigate.test.cases;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;

import junit.framework.Assert;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.Parameters;
import org.esigate.tags.BlockRenderer;
import org.esigate.test.TestUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bug101ConnectionReleaseTest {
	private static final Logger LOG = LoggerFactory.getLogger(Bug101ConnectionReleaseTest.class);

	private void render(Driver driver, String page) throws IOException {
		StringWriter writer = new StringWriter();
		HttpEntityEnclosingRequest httpRequest = TestUtils.createRequest();
		try {
			driver.render("/esigate-app-aggregated1/" + page, null, writer, httpRequest, new BlockRenderer(null, "/esigate-app-aggregated1/" + page));
		} catch (HttpErrorPage e) {
			LOG.info(page + " -> " + e.getHttpResponse().getStatusLine().getStatusCode());
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
		properties.put(Parameters.SOCKET_TIMEOUT.name, "4000");
		properties.put(Parameters.USE_CACHE.name, "false");

		Driver driver = Driver.builder().setName("test").setProperties(properties).build();

		long start = System.currentTimeMillis();
		// Should take less than 500ms each
		render(driver, "error500.jsp");
		render(driver, "error500.jsp");
		render(driver, "error404");
		render(driver, "error404");
		// Should take 4000ms each
		render(driver, "slow.jsp");
		render(driver, "slow.jsp");
		// Should take less than 500ms each
		render(driver, "");
		render(driver, "");
		Assert.assertTrue("Connection pool timeout : ressource leaked", System.currentTimeMillis() - start < 11000);
	}

}
