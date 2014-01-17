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

package org.esigate.servlet.impl;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.esigate.api.Session;

/**
 * Encapsulates servlet containes HttpSession mechanism.
 * 
 * @author Francois-Xavier Bonnet
 * 
 */
public class HttpServletSession implements Session {

    private final HttpServletRequest request;

    public HttpServletSession(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setAttribute(String key, Serializable value) {
        HttpSession session = this.request.getSession();
        // Session may be null on some servlet engines if the response has been sent already. This may happen for
        // background revalidations.
        if (session != null) {
            session.setAttribute(key, value);
        }
    }

    @Override
    public Serializable getAttribute(String key) {
        HttpSession session = this.request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Serializable) session.getAttribute(key);
    }

}
