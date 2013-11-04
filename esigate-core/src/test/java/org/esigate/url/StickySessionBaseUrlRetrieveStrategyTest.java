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

package org.esigate.url;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.test.TestUtils;

public class StickySessionBaseUrlRetrieveStrategyTest extends TestCase {

    public void testGetBaseURL() {
        final String[] baseUrls = new String[] { "http://example.com/test/", "http://example1.com/test/",
                "http://example2.com/test/" };
        BaseUrlRetrieveStrategy strategy = new StickySessionBaseUrlRetrieveStrategy(baseUrls);
        int times = 100;
        for (int i = 0; i < times; i++) {
            HttpEntityEnclosingRequest request = TestUtils.createRequest();
            Cookie cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME,
                    Integer.toString(i % baseUrls.length));
            TestUtils.addCookie(cookie, request);
            assertEquals(baseUrls[i % baseUrls.length], strategy.getBaseURL(request));
        }
    }

    public void testGetBaseURLWithWrongIndex() {
        final String[] baseUrls = new String[] { "http://example.com/test/", "http://example1.com/test/",
                "http://example2.com/test/" };
        BaseUrlRetrieveStrategy strategy = new StickySessionBaseUrlRetrieveStrategy(baseUrls);

        HttpEntityEnclosingRequest request = TestUtils.createRequest();
        Cookie cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, "-1");
        TestUtils.addCookie(cookie, request);
        strategy.getBaseURL(request);

        request = TestUtils.createRequest();
        cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, "5");
        TestUtils.addCookie(cookie, request);
        strategy.getBaseURL(request);

        request = TestUtils.createRequest();
        cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, null);
        TestUtils.addCookie(cookie, request);
        strategy.getBaseURL(request);

        request = TestUtils.createRequest();
        cookie = new BasicClientCookie(StickySessionBaseUrlRetrieveStrategy.ESI_SESSION_COOKIE_NAME, "a");
        TestUtils.addCookie(cookie, request);
        strategy.getBaseURL(request);
    }
}
