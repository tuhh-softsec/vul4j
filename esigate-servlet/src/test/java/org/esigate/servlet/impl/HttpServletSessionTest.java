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

package org.esigate.servlet.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.http.client.utils.DateUtils;
import org.esigate.http.IncomingRequest;
import org.esigate.servlet.MockHttpServletRequestBuilder;
import org.esigate.test.http.HttpResponseBuilder;

public class HttpServletSessionTest extends TestCase {
    private SimpleDateFormat format;

    @Override
    protected void setUp() throws Exception {
        format = new SimpleDateFormat(DateUtils.PATTERN_RFC1123, Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        super.setUp();
    }

    /**
     * Ensure there is no exception when trying to create a session outside of a request (during background
     * revalidation). Expected behavior is no exception, but value not set.
     * 
     * @see "https://sourceforge.net/apps/mantisbt/webassembletool/view.php?id=229"
     * @throws Exception
     */
    public void testSetAttributeNoSession() throws Exception {
        HttpServletRequest request = new MockHttpServletRequestBuilder().protocolVersion("HTTP/1.0").method("GET")
                .session(null).build();
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = new ServletOutputStream() {

            private ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

            @Override
            public void write(int b) {
                this.byteOutputStream.write(b);
            }

        };
        when(response.getOutputStream()).thenReturn(outputStream);
        ServletContext context = mock(ServletContext.class);

        RequestFactory requestFactory = new RequestFactory(context);
        IncomingRequest incomingRequest = requestFactory.create(request, response, null);
        ResponseSender renderer = new ResponseSender();
        renderer.sendResponse(new HttpResponseBuilder().entity("Response").build(), incomingRequest, response);

        incomingRequest.getSession().setAttribute("test", "value");

        // Previous method should have no effect since session cannot be
        // created.
        Assert.assertNull(incomingRequest.getSession().getAttribute("test"));
    }

}
