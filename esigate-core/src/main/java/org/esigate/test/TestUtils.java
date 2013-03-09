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

package org.esigate.test;

import java.io.IOException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.esigate.HttpErrorPage;
import org.esigate.http.HttpResponseUtils;
import org.esigate.util.HttpRequestHelper;

/**
 * @author Francois-Xavier Bonnet
 * 
 */
public class TestUtils {

	public final static HttpEntityEnclosingRequest createRequest() {
		return new MockMediator().getHttpRequest();
	}

	public final static HttpEntityEnclosingRequest createRequest(String uri) {
		return new MockMediator(uri).getHttpRequest();
	}

	public final static HttpResponse getResponse(HttpEntityEnclosingRequest request) {
		MockMediator mediator = (MockMediator) HttpRequestHelper.getMediator(request);
		return mediator.getHttpResponse();
	}

	public final static String getResponseBodyAsString(HttpEntityEnclosingRequest request) throws HttpErrorPage {
		MockMediator mediator = (MockMediator) HttpRequestHelper.getMediator(request);
		HttpResponse response = mediator.getHttpResponse();
		return HttpResponseUtils.toString(response, null);
	}

	public final static void sendHttpErrorPage(HttpErrorPage e, HttpEntityEnclosingRequest request) throws IOException {
		MockMediator mediator = (MockMediator) HttpRequestHelper.getMediator(request);
		mediator.sendResponse(e.getHttpResponse());
	}

	public final static void addCookie(Cookie cookie, HttpEntityEnclosingRequest request) {
		MockMediator mediator = (MockMediator) HttpRequestHelper.getMediator(request);
		mediator.addCookie(cookie);
	}
	
	public final static void setRemoteAddr(String remoteAddr, HttpEntityEnclosingRequest request){
		MockMediator mediator = (MockMediator) HttpRequestHelper.getMediator(request);
		mediator.setRemoteAddr(remoteAddr);
	}
}
