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

import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.HttpErrorPage;

public class VarsElementTest extends AbstractElementTest {

    public void testHttpHost() throws IOException, HttpErrorPage {
        String page = "begin <esi:vars>$(HTTP_HOST)</esi:vars> end";
        incomingRequest("http://www.foo.com");
        String result = render(page);
        assertEquals("begin www.foo.com end", result);
    }

    public void testCookie() throws IOException, HttpErrorPage {
        String page = "begin <esi:vars>"
                + "<img src=\"http://www.example.com/$(HTTP_COOKIE{cookieName})/hello.gif\"/ >" + "</esi:vars> end";
        getRequestBuilder().addCookie(new BasicClientCookie("cookieName", "value"));
        String result = render(page);
        assertEquals("begin <img src=\"http://www.example.com/value/hello.gif\"/ > end", result);
    }

    public void testQueryString() throws IOException, HttpErrorPage {
        String page = "begin <esi:vars>" + "<img src=\"http://www.example.com/$(QUERY_STRING{param1})/hello.gif\"/ >"
                + "</esi:vars> end";
        incomingRequest("http://localhost/?param1=param1value");
        String result = render(page);
        assertEquals("begin <img src=\"http://www.example.com/param1value/hello.gif\"/ > end", result);
    }

    public void testHttpReferer() throws IOException, HttpErrorPage {
        String page = "begin <esi:vars>" + "$(HTTP_REFERER)" + "</esi:vars> end";
        getRequestBuilder().addHeader("Referer", "http://www.example.com");
        String result = render(page);
        assertEquals("begin http://www.example.com end", result);
    }

    public void testUserAgent() throws IOException, HttpErrorPage {
        String page = "begin <esi:vars>" + "$(HTTP_USER_AGENT{os})" + "</esi:vars> end";
        getRequestBuilder().addHeader(
                "User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.10) Gecko/20100914 Firefox/3.6.10 GTB7.1 "
                        + "( .NET CLR 3.5.30729)");
        String result = render(page);
        assertEquals("begin WIN end", result);
    }

    public void testAcceptLanguage() throws IOException, HttpErrorPage {
        String page = "begin <esi:vars>" + "$(HTTP_ACCEPT_LANGUAGE{en-us})" + "</esi:vars> end";
        getRequestBuilder().addHeader("Accept-Language", "en-us,en;q=0.5");
        String result = render(page);
        assertEquals("begin true end", result);
    }

}
