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

import junit.framework.TestCase;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.esigate.http.BasicCloseableHttpResponse;
import org.esigate.servlet.MockHttpServletResponse;

import java.io.PrintWriter;

public class ResponseSenderTest extends TestCase {

    private ResponseSender renderer;

    @Override
    protected void setUp() throws Exception {
        renderer = new ResponseSender();
        super.setUp();
    }

    public void testSendResponseAlreadySent() throws Exception {
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        PrintWriter writer = httpServletResponse.getWriter();
        writer.write("Test");
        writer.close();
        CloseableHttpResponse httpClientResponse =
                BasicCloseableHttpResponse.adapt(new BasicHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1,
                        HttpStatus.SC_OK, "OK")));
        httpClientResponse.setEntity(new StringEntity("Abcdefg"));
        renderer.sendResponse(httpClientResponse, null, httpServletResponse);
    }

}
