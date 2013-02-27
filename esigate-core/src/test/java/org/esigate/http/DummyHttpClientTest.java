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

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class DummyHttpClientTest {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static void main(String[] args) throws ClientProtocolException, IOException {
		CloseableHttpClient httpClient = HttpClients.custom().build();

		HttpGet request = new HttpGet("http://localhost:8080/esigate-app-aggregated1/slow.jsp");
		request.setConfig(RequestConfig.custom().setSocketTimeout(1000).build());

		long start = System.currentTimeMillis();
		// Should fail with socket timeout!
		EntityUtils.consume(httpClient.execute(request).getEntity());
		System.out.println("First request executed in " + (System.currentTimeMillis() - start) + " ms");

		request = new HttpGet("http://localhost:8080/esigate-app-aggregated1/slow.jsp");
		request.setConfig(RequestConfig.custom().setSocketTimeout(1000).build());

		start = System.currentTimeMillis();
		// Should fail with socket timeout!
		EntityUtils.consume(httpClient.execute(request).getEntity());
		System.out.println("Second request executed in " + (System.currentTimeMillis() - start) + " ms");
	}

}
