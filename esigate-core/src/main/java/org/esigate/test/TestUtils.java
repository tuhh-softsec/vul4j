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

import org.apache.http.HttpResponse;
import org.apache.http.cookie.Cookie;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.http.HttpResponseUtils;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.DriverRequest;

/**
 * @author Francois-Xavier Bonnet
 * 
 */
public final class TestUtils {

    private TestUtils() {

    }

    public static IncomingRequest createRequest() {
        return new MockMediator().getHttpRequest();
    }

    public static DriverRequest createRequest(Driver driver) throws HttpErrorPage {
        IncomingRequest request = new MockMediator().getHttpRequest();
        return new DriverRequest(request, driver, null, false);
    }

    public static IncomingRequest createRequest(String uri) {
        return new MockMediator(uri).getHttpRequest();
    }

    public static DriverRequest createRequest(String uri, Driver driver) throws HttpErrorPage {
        IncomingRequest request = new MockMediator(uri).getHttpRequest();
        return new DriverRequest(request, driver, null, false);
    }

    public static HttpResponse getResponse(IncomingRequest request) {
        MockMediator mediator = (MockMediator) request.getMediator();
        return mediator.getHttpResponse();
    }

    public static String getResponseBodyAsString(IncomingRequest request) throws HttpErrorPage {
        MockMediator mediator = (MockMediator) request.getMediator();
        HttpResponse response = mediator.getHttpResponse();
        return HttpResponseUtils.toString(response, null);
    }

    public static void sendHttpErrorPage(HttpErrorPage e, IncomingRequest request) throws IOException {
        MockMediator mediator = (MockMediator) request.getMediator();
        mediator.sendResponse(e.getHttpResponse());
    }

    public static void addCookie(Cookie cookie, DriverRequest request) {
        MockMediator mediator = (MockMediator) request.getMediator();
        mediator.addCookie(cookie);
    }

    public static void addCookie(Cookie cookie, IncomingRequest request) {
        MockMediator mediator = (MockMediator) request.getMediator();
        mediator.addCookie(cookie);
    }

    public static void setRemoteAddr(String remoteAddr, IncomingRequest request) {
        MockMediator mediator = (MockMediator) request.getMediator();
        mediator.setRemoteAddr(remoteAddr);
    }
}
