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

package org.esigate.test.cases;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import junit.framework.TestCase;
import junitx.framework.AssertionFailedError;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.esigate.http.HttpResponseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Performance test
 * 
 * @author Alexis Thaveau
 */
public class PerformanceTestCase extends TestCase {
	/**
	 * Runnable that perform a GET request. This object is reusable and thread
	 * safe.
	 */
	private class HttpGetRequestRunnable implements Runnable {
		long count = 0;
		Throwable exception;
		String lastResult;
		final String url;

		private HttpGetRequestRunnable(String url) {
			this.url = url;
		}

		public void run() {
			if (exception == null) {
				count++;
				try {
					HttpUriRequest request = new HttpGet(url);
					HttpResponse response = httpClient.execute(request);
					String result = HttpResponseUtils.toString(response, null);
					if (lastResult == null) {
						lastResult = result;
					}
					assertEquals("Status should be 200", HttpServletResponse.SC_OK, response.getStatusLine().getStatusCode());
					assertEquals("Result should always be the same", lastResult, result);
				} catch (Throwable e) {
					exception = e;
				}
			}
		}
	}

	private final static String AGGREGATED1 = "http://localhost:8080/esigate-app-aggregated1/";
	private final static String AGGREGATED2 = "http://localhost:8080/esigate-app-aggregated2/";
	private final static String AGGREGATOR = "http://localhost:8080/esigate-app-aggregator/";
	private final static String AGGREGATOR_NO_CACHE = "http://localhost:8080/esigate-app-aggregator/nocache/ag1/";

	private static final Logger LOG = LoggerFactory.getLogger(PerformanceTestCase.class);
	private PoolingClientConnectionManager connectionManager;

	private HttpClient httpClient;

	/**
	 * Execute la tache avec plusieurs Threads
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private long execute(HttpGetRequestRunnable request, int numberOfRequests, int threads) throws Exception {
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
		connectionManager = new PoolingClientConnectionManager(schemeRegistry);
		connectionManager.setMaxTotal(threads);
		connectionManager.setDefaultMaxPerRoute(threads);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		httpClient = new DefaultHttpClient(connectionManager, httpParams);
		// Warm up
		request.run();

		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(threads, threads, 5, TimeUnit.SECONDS, queue);

		long start = System.currentTimeMillis();
		threadPool.prestartAllCoreThreads();
		for (int i = 0; i < numberOfRequests; i++) {
			threadPool.submit(request);
		}
		threadPool.shutdown();

		// wait maximum 20 s
		threadPool.awaitTermination(200, TimeUnit.SECONDS);
		connectionManager.shutdown();

		if (request.exception != null) {
			throw new AssertionFailedError("Exception for request " + request.url + " after " + request.count + " requests", request.exception);
		}
		if (threadPool.getCompletedTaskCount() < threadPool.getTaskCount()) {
			// All task were not executed
			String msg = request.url + " : Only " + threadPool.getCompletedTaskCount() + "/" + threadPool.getTaskCount() + " have been renderered " + " => Maybe a performance issue";
			threadPool.shutdownNow();
			fail(msg);
		}

		long end = System.currentTimeMillis();
		long execTime = end - start;
		LOG.debug("Executed request " + request.url + " " + numberOfRequests + " times with " + threads + " threads in " + execTime + "ms");
		return execTime;

	}

	/**
	 * Performance test
	 * 
	 * @throws Exception
	 */
	public void testAggregator() throws Exception {
		HttpGetRequestRunnable runAggregated1 = new HttpGetRequestRunnable(AGGREGATED1 + "templateslow.jsp");
		HttpGetRequestRunnable runAggregated2 = new HttpGetRequestRunnable(AGGREGATED2 + "template.html");
		HttpGetRequestRunnable runAggregator = new HttpGetRequestRunnable(AGGREGATOR + "templateslow.jsp");
		HttpGetRequestRunnable runAggregatorNoCache = new HttpGetRequestRunnable(AGGREGATOR_NO_CACHE + "templateslow.jsp");

		long execTimeAggregated1 = execute(runAggregated1, 5000, 50);
		long execTimeAggregated2 = execute(runAggregated2, 5000, 50);
		long execTimeDirectAccess = execTimeAggregated1 + execTimeAggregated2;
		LOG.debug("Total execution time direct access: " + execTimeDirectAccess + "ms");

		long execTimeAggregator = execute(runAggregator, 5000, 50);
		LOG.debug("Total execution time with aggregator (cache): " + execTimeAggregator + "ms");

		long execTimeAggregatorNoCache = execute(runAggregatorNoCache, 5000, 50);
		LOG.debug("Total execution time with aggregator (no cache): " + execTimeAggregatorNoCache + "ms");

		/*
		 * Expected time : 10% of total time to retrieve resources without
		 * aggregator
		 */
		long expectedExecTime = Math.round((execTimeDirectAccess) * 1.10);
		LOG.debug("Maximum expected :" + expectedExecTime + "ms");
		if (execTimeAggregator > expectedExecTime) {
			fail("Performance issue :" + execTimeAggregator + "ms to render template.html with aggregator," + execTimeDirectAccess + "ms without aggregator- Expected " + expectedExecTime + "ms");
		}

		if (execTimeAggregatorNoCache > expectedExecTime) {
			fail("Performance issue :" + execTimeAggregatorNoCache + "ms to render template.html with aggregator(without cache)," + execTimeDirectAccess + "ms without aggregator- Expected "
					+ expectedExecTime + "ms");
		}

	}

}
