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

package org.esigate.extension.parallelesi;

import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.Executors;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.HttpErrorPage;
import org.esigate.MockRequestExecutor;
import org.esigate.test.TestUtils;

public class ChooseElementTest extends TestCase {

	private HttpEntityEnclosingRequest request;
	private EsiRenderer tested;

	@Override
	protected void setUp() throws Exception {
		MockRequestExecutor provider = MockRequestExecutor.createMockDriver();
		request = TestUtils.createRequest();
		tested = new EsiRenderer(Executors.newCachedThreadPool());
		provider.initHttpRequestParams(request, null);
	}

	public void testChoose() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>inside choose</esi:choose> end";
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("begin inside choose end", out.toString());
	}

	public void testSingleWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">inside when</esi:when>" + "</esi:choose> end";
		TestUtils.addCookie(new BasicClientCookie("group", "Advanced"), request);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("begin inside when end", out.toString());
	}

	public void testMultipleWhen() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">unexpected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">expected cookie '$(HTTP_COOKIE{group})'</esi:when>" + "</esi:choose> end";
		TestUtils.addCookie(new BasicClientCookie("group", "Beginner"), request);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("begin expected cookie 'Beginner' end", out.toString());
	}

	public void testMultipleWhenEvaluated() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">expected cookie</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie</esi:when>" + "</esi:choose> end";
		TestUtils.addCookie(new BasicClientCookie("group", "Beginner"), request);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("begin expected cookie end", out.toString());
	}

	public void testMultipleWhenOtherwise1() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">expected cookie</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie</esi:when>" + "<esi:otherwise>inside otherwise</esi:otherwise>" + "</esi:choose> end";
		TestUtils.addCookie(new BasicClientCookie("group", "Intermediate"), request);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("begin inside otherwise end", out.toString());
	}

	public void testMultipleWhenOtherwise2() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">expected cookie '$(HTTP_COOKIE{group})'</esi:when>"
				+ "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie '$(HTTP_COOKIE{group})'</esi:when>" + "<esi:otherwise>inside otherwise</esi:otherwise>"
				+ "</esi:choose> end";
		TestUtils.addCookie(new BasicClientCookie("group", "Advanced"), request);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("begin expected cookie 'Advanced' end", out.toString());
	}

	public void testOtherwise() throws IOException, HttpErrorPage {
		String page = "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">inside when</esi:when>"
				+ "<esi:otherwise>inside otherwise with '$(HTTP_COOKIE{group})' cookie</esi:otherwise>" + "</esi:choose> end";
		TestUtils.addCookie(new BasicClientCookie("group", "Advanced"), request);
		StringWriter out = new StringWriter();
		tested.render(request, page, out);
		assertEquals("begin inside otherwise with 'Advanced' cookie end", out.toString());
	}

}
