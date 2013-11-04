/* 
 * Licensed under the import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.test.TestUtils;
se is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.esigate.url;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import org.apache.http.HttpEntityEnclosingRequest;
import org.esigate.api.BaseUrlRetrieveStrategy;
import org.esigate.test.TestUtils;

public class RoundRobinBaseUrlRetrieveStrategyTest extends TestCase {

    public void testGetBaseURL() {
        String[] baseUrls = new String[] { "http://example.com/test/", "http://example1.com/test/",
                "http://example2.com/test/" };
        BaseUrlRetrieveStrategy strategy = new RoundRobinBaseUrlRetrieveStrategy(baseUrls);
        HttpEntityEnclosingRequest request = TestUtils.createRequest();
        int times = 5;
        int requestsCount = baseUrls.length * times;
        ConcurrentMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<String, AtomicInteger>();
        for (int i = 0; i < requestsCount; i++) {
            String baseUrl = strategy.getBaseURL(request);
            counterMap.putIfAbsent(baseUrl, new AtomicInteger(0));
            counterMap.get(baseUrl).incrementAndGet();
        }
        for (String baseUrl : baseUrls) {
            assertEquals(times, counterMap.get(baseUrl).get());
        }
    }
}
