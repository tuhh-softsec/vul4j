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

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicRequestLine;
import org.esigate.Driver;
import org.esigate.HttpErrorPage;
import org.esigate.api.ContainerRequestContext;
import org.esigate.http.IncomingRequest;
import org.esigate.impl.DriverRequest;
import org.esigate.util.UriUtils;

/**
 * @author Francois-Xavier Bonnet
 * 
 */
public final class TestUtils {

    private TestUtils() {

    }

    public static IncomingRequest.Builder createIncomingRequest(String uri) {
        HttpHost httpHost = UriUtils.extractHost(uri);
        String scheme = httpHost.getSchemeName();
        String host = httpHost.getHostName();
        int port = httpHost.getPort();
        RequestLine requestLine = new BasicRequestLine("GET", uri, HttpVersion.HTTP_1_1);
        IncomingRequest.Builder builder = IncomingRequest.builder(requestLine);
        builder.setContext(new ContainerRequestContext() {
        });
        // Remove default ports
        if (port == -1 || (port == 80 && "http".equals(scheme)) || (port == 443 && "https".equals(scheme))) {
            builder.addHeader("Host", host);
        } else {
            builder.addHeader("Host", host + ":" + port);
        }
        builder.setSession(new MockSession());
        return builder;
    }

    public static IncomingRequest.Builder createIncomingRequest() {
        return createIncomingRequest("http://localhost:8080");
    }

    public static DriverRequest createDriverRequest(String uri, Driver driver) throws HttpErrorPage {
        IncomingRequest request = createIncomingRequest(uri).build();
        return new DriverRequest(request, driver, "/");
    }

    public static DriverRequest createDriverRequest(Driver driver) throws HttpErrorPage {
        IncomingRequest request = createIncomingRequest().build();
        return new DriverRequest(request, driver, "/");
    }

}
