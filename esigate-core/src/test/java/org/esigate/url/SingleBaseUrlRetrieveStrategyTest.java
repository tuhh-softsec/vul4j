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
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.test.TestUtils;

public class SingleBaseUrlRetrieveStrategyTest extends TestCase {

    public void testGetBaseURL() {
        String baseUrl = "http://example.com/test/";
        BaseUrlRetrieveStrategy strategy = new SingleBaseUrlRetrieveStrategy(baseUrl);
        HttpEntityEnclosingRequest request = TestUtils.createRequest();
        String baseURL2 = strategy.getBaseURL(request);
        assertEquals(baseUrl, baseURL2);
    }
}
