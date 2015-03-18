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

public class ChooseElementTest extends AbstractElementTest {

    public void testChoose() throws IOException, HttpErrorPage {
        String page = "begin <esi:choose>inside choose</esi:choose> end";
        String result = render(page);
        assertEquals("begin inside choose end", result);
    }

    public void testSingleWhen() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">inside when</esi:when>"
                        + "</esi:choose> end";
        getRequestBuilder().addCookie(new BasicClientCookie("group", "Advanced"));
        String result = render(page);
        assertEquals("begin inside when end", result);
    }

    public void testMultipleWhen() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">"
                        + "<esi:vars>unexpected cookie '$(HTTP_COOKIE{group})'</esi:vars>" + "</esi:when>"
                        + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">"
                        + "<esi:vars>expected cookie '$(HTTP_COOKIE{group})'</esi:vars>" + "</esi:when>"
                        + "</esi:choose> end";
        getRequestBuilder().addCookie(new BasicClientCookie("group", "Beginner"));
        String result = render(page);
        assertEquals("begin expected cookie 'Beginner' end", result);
    }

    public void testMultipleWhenEvaluated() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:choose>"
                        + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">expected cookie</esi:when>"
                        + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie</esi:when>"
                        + "</esi:choose> end";
        getRequestBuilder().addCookie(new BasicClientCookie("group", "Beginner"));
        String result = render(page);
        assertEquals("begin expected cookie end", result);
    }

    public void testMultipleWhenOtherwise1() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:choose>"
                        + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">expected cookie</esi:when>"
                        + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">unexpected cookie</esi:when>"
                        + "<esi:otherwise>inside otherwise</esi:otherwise>" + "</esi:choose> end";
        getRequestBuilder().addCookie(new BasicClientCookie("group", "Intermediate"));
        String result = render(page);
        assertEquals("begin inside otherwise end", result);
    }

    public void testMultipleWhenOtherwise2() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Advanced'\">"
                        + "<esi:vars>expected cookie '$(HTTP_COOKIE{group})'</esi:vars>" + "</esi:when>"
                        + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">"
                        + "<esi:vars>unexpected cookie '$(HTTP_COOKIE{group})'</esi:vars>" + "</esi:when>"
                        + "<esi:otherwise>inside otherwise</esi:otherwise>" + "</esi:choose> end";
        getRequestBuilder().addCookie(new BasicClientCookie("group", "Advanced"));
        String result = render(page);
        assertEquals("begin expected cookie 'Advanced' end", result);
    }

    public void testOtherwise() throws IOException, HttpErrorPage {
        String page =
                "begin <esi:choose>" + "<esi:when test=\"'$(HTTP_COOKIE{group})'=='Beginner'\">inside when</esi:when>"
                        + "<esi:otherwise>"
                        + "<esi:vars>inside otherwise with '$(HTTP_COOKIE{group})' cookie</esi:vars>"
                        + "</esi:otherwise>" + "</esi:choose> end";
        getRequestBuilder().addCookie(new BasicClientCookie("group", "Advanced"));
        String result = render(page);
        assertEquals("begin inside otherwise with 'Advanced' cookie end", result);
    }

}
