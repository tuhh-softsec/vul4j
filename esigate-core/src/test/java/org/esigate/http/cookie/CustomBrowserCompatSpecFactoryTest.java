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

package org.esigate.http.cookie;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.message.BasicHeader;
import org.esigate.http.Http;

public class CustomBrowserCompatSpecFactoryTest extends TestCase {

    private CookieSpec cookieSpec;

    @Override
    protected void setUp() throws Exception {
        cookieSpec = new CustomBrowserCompatSpecFactory().create(null);
    }

    public void testAcceptCookieWithLongerPathThanRequestPath() throws Exception {
        Header header = new BasicHeader("Set-Cookie", "wordpress_dce20=admin%7Ca; Path=/wp-content/plugins");
        CookieOrigin origin = new CookieOrigin("www.foo.com", Http.DEFAULT_HTTP_PORT, "/wp-login.php", false);
        Cookie cookie = cookieSpec.parse(header, origin).get(0);
        cookieSpec.validate(cookie, origin);
        // BrowserCompatSpec would throw an exception:
        // org.apache.http.cookie.CookieRestrictionViolationException: Illegal path attribute "/wp-content/plugins".
        // Path of origin: "/wp-login.php"
    }
}
