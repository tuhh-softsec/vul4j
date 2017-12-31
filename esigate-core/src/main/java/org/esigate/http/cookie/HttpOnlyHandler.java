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

import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.Args;

/**
 * Handler for HttpOnly attribute
 * 
 * @author Alexis Thaveau on 20/10/14.
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE_CONDITIONAL)
public class HttpOnlyHandler extends AbstractCookieAttributeHandler {

    public HttpOnlyHandler() {
        super();
    }

    public void parse(final SetCookie cookie, final String value) throws MalformedCookieException {

        Args.notNull(cookie, "Cookie");
        ((BasicClientCookie) cookie).setAttribute(CookieUtil.HTTP_ONLY_ATTR, "");

    }

}
