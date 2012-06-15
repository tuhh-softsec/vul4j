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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

/**
 * Performance test
 * 
 * @author Alexis Thaveau
 */
public class PerformanceTestCase extends TestCase {
	/**
	 * Runnable that perform GET
	 * 
	 * @author altha
	 * 
	 */
	class GetRunnable implements Runnable {
		private final String app;
		private final String page;

		public GetRunnable(String app, String page) {
			this.app = app;
			this.page = page;
		}

		public void run() {
			try {
				doGet(app, page);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private final static String AGGREGATED1_PATH = "http://localhost:8080/esigate-app-aggregated1/";
	private final static String AGGREGATED2_PATH = "http://localhost:8080/esigate-app-aggregated2/";
	private final static String AGGREGATOR = "http://localhost:8080/esigate-app-aggregator/";
	private final static String AGGREGATOR_NO_CACHE = "http://localhost:8080/esigate-app-aggregator/nocache/ag1/";
	private static final Logger LOG = LoggerFactory
			.getLogger(PerformanceTestCase.class);

	/**
	 * Execute a get request
	 * 
	 * @param path
	 * @param page
	 * @throws Exception
	 */
	private void doGet(String path, String page) throws Exception {
		WebConversation webConversation = new WebConversation();
		webConversation.getClientProperties().setAutoRedirect(false);
		WebRequest req = new GetMethodWebRequest(path + page);
		WebResponse resp = webConversation.getResponse(req);

		assertEquals("Status should be 200", HttpServletResponse.SC_OK,
				resp.getResponseCode());

	}

	/**
	 * Execute la tache avec plusieurs Threads
	 * 
	 * @param run
	 * @return
	 * @throws Exception
	 */
	private long executePool(GetRunnable run) throws Exception {
		BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

		ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 800, 5000,
				TimeUnit.MILLISECONDS, queue);

		long start = System.currentTimeMillis();
		threadPool.prestartAllCoreThreads();
		for (int i = 0; i < 500; i++) {
			threadPool.submit(run);
		}
		threadPool.shutdown();

		// wait maximum 20 s
		threadPool.awaitTermination(20000, TimeUnit.MILLISECONDS);
		if (threadPool.getCompletedTaskCount() < threadPool.getTaskCount()) {
			// All task were not executed
			String msg = run.app + run.page + " : Only "
					+ threadPool.getCompletedTaskCount() + "/"
					+ threadPool.getTaskCount() + " have been renderered "
					+ " => Maybe a performance issue";
			threadPool.shutdownNow();
			fail(msg);
		}

		long end = System.currentTimeMillis();
		long execTime = end - start;
		return execTime;

	}

	@Override
	public void setUp() throws Exception {
		super.setUp();

	}

	/**
	 * Performance test
	 */
	public void test() throws Exception {
		// Get page to compile jsp and init driver
		doGet(AGGREGATOR, "templateslow.jsp");
		doGet(AGGREGATOR_NO_CACHE, "templateslow.jsp");
		doGet(AGGREGATED1_PATH, "templateslow.jsp");
		doGet(AGGREGATED2_PATH, "template.html");

		GetRunnable runAggregated1 = new GetRunnable(AGGREGATED1_PATH,
				"templateslow.jsp");
		GetRunnable runAggregated2 = new GetRunnable(AGGREGATED2_PATH,
				"template.html");
		GetRunnable runAggregator = new GetRunnable(AGGREGATOR,
				"templateslow.jsp");
		GetRunnable runAggregatorNoCache = new GetRunnable(AGGREGATOR_NO_CACHE,
				"templateslow.jsp");

		long execTimeAggregated1 = executePool(runAggregated1);
		LOG.debug("execTimeAggregated1 :" + execTimeAggregated1 + "ms");

		long execTimeAggregated2 = executePool(runAggregated2);
		long execTimeDirectAccess = execTimeAggregated1 + execTimeAggregated2;
		LOG.debug("execTimeAggregated2 :" + execTimeAggregated2 + "ms");
		LOG.debug("execTimeDirectAccess :" + execTimeDirectAccess + "ms");

		long execTimeAggregator = executePool(runAggregator);
		LOG.debug("execTimeAggregator :" + execTimeAggregator + "ms");

		long execTimeAggregatorNoCache = executePool(runAggregatorNoCache);
		LOG.debug("execTimeAggregatorNoCache :" + execTimeAggregatorNoCache
				+ "ms");

		/*
		 * Expected time : 10% of total time to retrieve resources without
		 * aggregator
		 */
		long expectedExecTime = Math.round((execTimeDirectAccess) * 1.20);
		LOG.debug("Maximum expected :" + expectedExecTime + "ms");
		if (execTimeAggregator > expectedExecTime) {
			fail("Performance issue :" + execTimeAggregator
					+ "ms to render template.html with aggregator,"
					+ execTimeDirectAccess + "ms without aggregator- Expected "
					+ expectedExecTime + "ms");
		}

		if (execTimeAggregatorNoCache > expectedExecTime) {
			fail("Performance issue :"
					+ execTimeAggregatorNoCache
					+ "ms to render template.html with aggregator(without cache),"
					+ execTimeDirectAccess + "ms without aggregator- Expected "
					+ expectedExecTime + "ms");
		}

	}
}
