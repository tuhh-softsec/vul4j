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
import org.apache.http.impl.cookie.AbstractCookieSpec;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * A BrowserCompatSpecFactory that register HttpOnly handler
 * 
 * @author Alexis Thaveau on 21/10/14.
 */
@Immutable
@SuppressWarnings("deprecation")
public class CustomBrowserCompatSpecFactory extends BrowserCompatSpecFactory {

    /**
     *
     */
    public static final String CUSTOM_BROWSER_COMPATIBILITY = "custom_browser_compatibility";

    public CustomBrowserCompatSpecFactory() {
        // Remove path validation
        super(null, SecurityLevel.SECURITYLEVEL_IE_MEDIUM);
    }

    @Override
    public CookieSpec newInstance(final HttpParams params) {
        AbstractCookieSpec cookieSpec = (AbstractCookieSpec) super.newInstance(params);
        cookieSpec.registerAttribHandler(CookieUtil.HTTP_ONLY_ATTR, new HttpOnlyHandler());
        return cookieSpec;
    }

    @Override
    public CookieSpec create(final HttpContext context) {
        AbstractCookieSpec cookieSpec = (AbstractCookieSpec) super.create(context);
        cookieSpec.registerAttribHandler(CookieUtil.HTTP_ONLY_ATTR, new HttpOnlyHandler());
        return cookieSpec;
    }

}
