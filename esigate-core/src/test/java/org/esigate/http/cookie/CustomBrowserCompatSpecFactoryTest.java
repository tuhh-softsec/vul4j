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

public class CustomBrowserCompatSpecFactoryTest extends TestCase {

    private CookieSpec cookieSpec;

    @Override
    protected void setUp() throws Exception {
        cookieSpec = new CustomBrowserCompatSpecFactory().newInstance(null);
    }

    public void testAcceptCookieWithLongerPathThanRequestPath() throws Exception {
        Header header =
                new BasicHeader(
                        "Set-Cookie",
                        "wordpress_dce2080bc042b2e639e4f5b3b704aa43=admin%7C1243786064%7C4c56aef46b1210d3d43d8b829fdf4d9a; Path=/wp-content/plugins");
        CookieOrigin origin = new CookieOrigin("www.foo.com", 80, "/wp-login.php", false);
        Cookie cookie = cookieSpec.parse(header, origin).get(0);
        cookieSpec.validate(cookie, origin);
        // BrowserCompatSpec would throw an exception:
        // org.apache.http.cookie.CookieRestrictionViolationException: Illegal path attribute "/wp-content/plugins".
        // Path of origin: "/wp-login.php"
    }
}
