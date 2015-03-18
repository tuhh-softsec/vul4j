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

import org.apache.commons.lang3.StringUtils;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.http.IncomingRequest;
import org.esigate.test.TestUtils;

public class IpHashBaseUrlRetrieveStrategyTest extends TestCase {

    public void testGetBaseURLRandom() {
        String[] baseUrls =
                new String[] {"http://example.com/test/", "http://example1.com/test/", "http://example2.com/test/"};
        BaseUrlRetrieveStrategy strategy = new IpHashBaseUrlRetrieveStrategy(baseUrls);
        IncomingRequest request = TestUtils.createIncomingRequest().build();
        strategy.getBaseURL(request);
    }

    public void testGetBaseURLInvalidIp() {
        String[] baseUrls =
                new String[] {"http://example.com/test/", "http://example1.com/test/", "http://example2.com/test/"};
        BaseUrlRetrieveStrategy strategy = new IpHashBaseUrlRetrieveStrategy(baseUrls);
        IncomingRequest request = TestUtils.createIncomingRequest().setRemoteAddr("").build();
        strategy.getBaseURL(request);
        request = TestUtils.createIncomingRequest().setRemoteAddr(null).build();
        strategy.getBaseURL(request);
        request = TestUtils.createIncomingRequest().setRemoteAddr("not_ip").build();
        strategy.getBaseURL(request);
        request = TestUtils.createIncomingRequest().setRemoteAddr("a.b.c.d").build();
        strategy.getBaseURL(request);
    }

    public void testGetBaseURLSameIpSameBaseUrl() {
        String[] baseUrls =
                new String[] {"http://example.com/test/", "http://example1.com/test/", "http://example2.com/test/"};
        BaseUrlRetrieveStrategy strategy = new IpHashBaseUrlRetrieveStrategy(baseUrls);
        int times = 100;
        for (int i = 0; i < times; i++) {
            String ip = getRandomIp();
            IncomingRequest request = TestUtils.createIncomingRequest().setRemoteAddr(ip).build();
            String baseURL1 = strategy.getBaseURL(request);
            String baseURL2 = strategy.getBaseURL(request);
            String baseURL3 = strategy.getBaseURL(request);
            assertEquals(baseURL1, baseURL2);
            assertEquals(baseURL1, baseURL3);
        }

    }

    private String getRandomIp() {
        String[] arr = new String[4];
        for (int i = 0; i < 4; i++) {
            arr[i] = Integer.toString((int) (Math.random() * 256));
        }
        return StringUtils.join(arr, ".");
    }
}
