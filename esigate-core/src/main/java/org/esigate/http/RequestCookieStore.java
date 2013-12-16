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

package org.esigate.http;

import java.util.Date;
import java.util.List;

import org.apache.http.HttpRequest;
import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.esigate.cookie.CookieManager;

/**
 * A CookieStore that delegates to the CookieManager instance associated with this Driver instance. The CookieManager
 * will decide what to do with each cookie like forwarding it to the browser, storing it to session or just ignoring it.
 * 
 * @see CookieStore
 * 
 * @author Nicolas Richeton
 * 
 */
public class RequestCookieStore implements CookieStore {

    private final HttpRequest originalRequest;
    private final CookieManager cookieManager;

    public RequestCookieStore(CookieManager cookieManager, HttpRequest originalRequest) {
        this.originalRequest = originalRequest;
        this.cookieManager = cookieManager;
    }

    @Override
    public void addCookie(Cookie cookie) {
        this.cookieManager.addCookie(cookie, this.originalRequest);
    }

    @Override
    public List<Cookie> getCookies() {
        return this.cookieManager.getCookies(this.originalRequest);
    }

    @Override
    public boolean clearExpired(Date date) {
        return this.cookieManager.clearExpired(date, this.originalRequest);
    }

    @Override
    public void clear() {
        this.cookieManager.clear(this.originalRequest);
    }

}
