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

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.cookie.AbstractCookieSpec;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory.SecurityLevel;
import org.apache.http.protocol.HttpContext;

/**
 * A BrowserCompatSpecFactory that registers HttpOnly handler and disables path validation.
 * 
 * @author Alexis Thaveau on 21/10/14.
 */
@Immutable
public class CustomBrowserCompatSpecFactory implements CookieSpecProvider {

    /**
     * Constant for HttpClient configuration.
     */
    public static final String CUSTOM_BROWSER_COMPATIBILITY = "custom_browser_compatibility";

    @Override
    public CookieSpec create(final HttpContext context) {
        // SecurityLevel.SECURITYLEVEL_IE_MEDIUM disables path validation
        AbstractCookieSpec cookieSpec = new BrowserCompatSpec(null, SecurityLevel.SECURITYLEVEL_IE_MEDIUM);
        cookieSpec.registerAttribHandler(CookieUtil.HTTP_ONLY_ATTR, new HttpOnlyHandler());
        return cookieSpec;
    }

}
